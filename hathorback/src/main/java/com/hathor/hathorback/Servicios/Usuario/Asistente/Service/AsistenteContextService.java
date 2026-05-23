package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ContextoHatoDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkGlobalDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.KpiResumenGlobalDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service.IServiceBenchmarking;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AsistenteContextService {

    @Autowired private IRepositoryHato repositoryHato;
    @Autowired private IServiceKpi serviceKpi;
    @Autowired private IRepositoryPerfilProductivo repositoryPerfilProductivo;
    @Autowired private IRepositoryRegistroFinanciero repositoryRegistroFinanciero;
    @Autowired private IServiceBenchmarking serviceBenchmarking;

    // Construye el contexto completo del hato para inyectar al system prompt
    public ContextoHatoDTO construirContexto(UUID idHato) {

        Hato hato = repositoryHato.findById(idHato)
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));

        PerfilProductivo pp = repositoryPerfilProductivo
            .findByHato_IdHato(idHato).orElse(null);

        List<RegistroFinanciero> registros =
            repositoryRegistroFinanciero.findByHato_IdHato(idHato);

        // == FINANZAS
        double totalIngresos = registros.stream()
            .filter(r -> "INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double totalGastos = registros.stream()
            .filter(r -> !"INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        // == KPIS
        List<KpiResultadoDTO> kpis = serviceKpi.getKpisDelHato(idHato);

        List<ContextoHatoDTO.KpiContextoDTO> kpisContexto = kpis.stream()
            .filter(k -> k.getValor() != null)
            .map(k -> ContextoHatoDTO.KpiContextoDTO.builder()
                .codigo(k.getCodigo())
                .nombre(k.getNombre())
                .valor(k.getValor())
                .unidad(k.getUnidad())
                .estado(k.getEstado())
                .categoria(k.getCategoria())
                .benchmarkPromedio(k.getBenchmarkPromedio())
                .diferenciaPct(k.getDiferenciaPct())
                .build())
            .collect(Collectors.toList());

        // Benchmarking global 
        List<ContextoHatoDTO.KpiBenchContextoDTO> kpisBench = List.of();
        try {
            BenchmarkGlobalDTO bench = serviceBenchmarking
                .calcularBenchmarkGlobal(idHato, true, true, false, 10);

            if (!bench.isDatosInsuficientes()) {
                kpisBench = bench.getKpisResumen().stream()
                    .filter(k -> "CRITICO".equals(k.getInterpretacion())
                        || "ACEPTABLE".equals(k.getInterpretacion()))
                    .map(k -> ContextoHatoDTO.KpiBenchContextoDTO.builder()
                        .codigo(k.getCodigoKpi())
                        .nombre(k.getNombreKpi())
                        .valorHato(k.getValorHatoActual())
                        .promedioGrupo(k.getPromedioGrupo())
                        .percentilEnGrupo(k.getPercentilEnGrupo())
                        .interpretacion(k.getInterpretacion())
                        .build())
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Benchmarking global no disponible: "
                + e.getMessage());
        }
        String capacidadInversion = calcularCapacidadInversion(kpis);
        return ContextoHatoDTO.builder()
            .nombreHato(hato.getNombreHato())
            .departamento(hato.getDepartamento())
            .ciudad(hato.getCiudad())
            .tropico(hato.getTropico())
            .escala(hato.getEscala())
            .altitud(hato.getAltitud())
            .areaHato(hato.getAreaHato())
            .areaPastoreo(hato.getAreaPastoreo())
            .totalEmpleados(hato.getCantEmpleadosPermanentes()
                + hato.getCantEmpleadosTemporales())
            .capacidadAlmacenamiento(hato.getCapacidadAlmacenarLeche())
            .tipoHato(hato.getTipoHato())
            .porcentajeCompletitud(hato.getPorcentajeCompletitud())
            // Perfil productivo
            .produccionDiariaLitros(pp != null
                ? pp.getProduccionDiariaLitros() : null)
            .vacasEnOrdenio(pp != null
                ? pp.getVacasEnOrdenio() : null)
            .precioLitroPromedio(pp != null
                ? pp.getPrecioLitroPromedio() : null)
            .sistemasOrdenio(pp != null
                ? pp.getSistemaOrdenio() : null)
            .frecuenciaOrdenio(pp != null
                ? pp.getFrecuenciaOrdenio() : null)
            .periodoLactancia(pp != null
                ? pp.getPeriodoLactanciaPromedio() : null)
            .razaPredominante(pp != null
                ? pp.getRazaPredominante() : null)
            // Financiero
            .totalIngresos(totalIngresos)
            .totalGastos(totalGastos)
            .balanceNeto(totalIngresos - totalGastos)
            // KPIs y benchmark
            .kpis(kpisContexto)
            .kpisBenchmark(kpisBench)
            .capacidadInversion(capacidadInversion)
            .build();
    }

    // Convierte el contexto a texto estructurado para el system prompt
    public String construirSystemPrompt(ContextoHatoDTO ctx,
                                         String corpusDocumental) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
            Eres Hathor AI, un asistente especializado en gestión financiera \
            y productiva de hatos lecheros colombianos. Respondes siempre en \
            español, con lenguaje claro y directo para productores ganaderos. \
            No inventes datos — basa tus respuestas exclusivamente en los datos \
            proporcionados. Si no tienes suficiente información para responder, \
            dilo claramente.
            
            """);

        sb.append("## Datos del hato en sesión\n");
        sb.append("- Nombre: ").append(ctx.getNombreHato()).append("\n");
        sb.append("- Ubicación: ").append(ctx.getCiudad())
            .append(", ").append(ctx.getDepartamento()).append("\n");
        sb.append("- Trópico: ").append(ctx.getTropico()).append("\n");
        sb.append("- Escala: ").append(ctx.getEscala()).append("\n");
        sb.append("- Altitud: ").append(ctx.getAltitud()).append(" msnm\n");
        sb.append("- Área total: ").append(ctx.getAreaHato()).append(" ha\n");
        sb.append("- Área pastoreo: ").append(ctx.getAreaPastoreo())
            .append(" ha\n");
        sb.append("- Empleados: ").append(ctx.getTotalEmpleados()).append("\n");
        sb.append("- Tipo hato: ").append(ctx.getTipoHato()).append("\n\n");

        if (ctx.getProduccionDiariaLitros() != null) {
            sb.append("## Perfil productivo\n");
            sb.append("- Producción diaria: ")
                .append(ctx.getProduccionDiariaLitros())
                .append(" litros\n");
            sb.append("- Vacas en ordeño: ")
                .append(ctx.getVacasEnOrdenio()).append("\n");
            sb.append("- Precio litro promedio: $")
                .append(ctx.getPrecioLitroPromedio()).append(" COP\n");
            sb.append("- Raza predominante: ")
                .append(ctx.getRazaPredominante()).append("\n");
            if (ctx.getSistemasOrdenio() != null)
                sb.append("- Sistema ordeño: ")
                    .append(ctx.getSistemasOrdenio()).append("\n");
            if (ctx.getFrecuenciaOrdenio() != null)
                sb.append("- Frecuencia ordeño: ")
                    .append(ctx.getFrecuenciaOrdenio())
                    .append(" veces/día\n");
            sb.append("\n");
        }

        sb.append("## Resumen financiero\n");
        sb.append("- Total ingresos: $")
            .append(String.format("%,.0f", ctx.getTotalIngresos()))
            .append(" COP\n");
        sb.append("- Total gastos: $")
            .append(String.format("%,.0f", ctx.getTotalGastos()))
            .append(" COP\n");
        sb.append("- Balance neto: $")
            .append(String.format("%,.0f", ctx.getBalanceNeto()))
            .append(" COP\n\n");

        if (!ctx.getKpis().isEmpty()) {
            sb.append("## KPIs actuales\n");
            // Primero los críticos
            ctx.getKpis().stream()
                .filter(k -> "CRITICO".equals(k.getEstado()))
                .forEach(k -> sb.append("- [CRÍTICO] ")
                    .append(k.getNombre()).append(": ")
                    .append(k.getValor()).append(" ")
                    .append(k.getUnidad() != null ? k.getUnidad() : "")
                    .append(" (promedio sector: ")
                    .append(k.getBenchmarkPromedio()).append(")\n"));
            // Luego aceptables
            ctx.getKpis().stream()
                .filter(k -> "ACEPTABLE".equals(k.getEstado()))
                .forEach(k -> sb.append("- [ACEPTABLE] ")
                    .append(k.getNombre()).append(": ")
                    .append(k.getValor()).append(" ")
                    .append(k.getUnidad() != null ? k.getUnidad() : "")
                    .append("\n"));
            // Luego óptimos
            ctx.getKpis().stream()
                .filter(k -> "OPTIMO".equals(k.getEstado()))
                .forEach(k -> sb.append("- [ÓPTIMO] ")
                    .append(k.getNombre()).append(": ")
                    .append(k.getValor()).append(" ")
                    .append(k.getUnidad() != null ? k.getUnidad() : "")
                    .append("\n"));
            sb.append("\n");
        }

        if (!ctx.getKpisBenchmark().isEmpty()) {
            sb.append("## Posición vs otros hatos similares\n");
            ctx.getKpisBenchmark().forEach(k ->
                sb.append("- ").append(k.getNombre())
                    .append(": percentil ").append(k.getPercentilEnGrupo())
                    .append(" del grupo (").append(k.getInterpretacion())
                    .append(")\n"));
            sb.append("\n");
        }

        // Bloque capacidad de inversión
        if (ctx.getCapacidadInversion() != null) {
            sb.append("## Capacidad de inversión del hato\n");
            sb.append("- Clasificación: ")
                .append(ctx.getCapacidadInversion()).append("\n");

            switch (ctx.getCapacidadInversion()) {
                case "ALTA" -> sb.append(
                    "- El hato tiene indicadores financieros sólidos. " +
                    "Puede evaluar inversiones de mediano y alto valor " +
                    "con financiamiento externo.\n");
                case "MEDIA" -> sb.append(
                    "- El hato tiene margen suficiente para inversiones " +
                    "selectivas. Priorizar las que reduzcan costos o " +
                    "aumenten producción directamente.\n");
                case "BAJA" -> sb.append(
                    "- El hato opera con margen ajustado. Solo considerar " +
                    "inversiones críticas de bajo costo con retorno rápido.\n");
                case "NO_RECOMENDADA" -> sb.append(
                    "- ALERTA: Los indicadores financieros no soportan " +
                    "nuevas inversiones. Priorizar estabilización financiera " +
                    "antes de comprometer capital.\n");
                default -> sb.append(
                    "- Datos insuficientes para clasificar capacidad " +
                    "de inversión.\n");
            }
            sb.append("\n");
        }

        if (corpusDocumental != null && !corpusDocumental.isBlank()) {
            sb.append("## Conocimiento de referencia\n");
            sb.append(corpusDocumental).append("\n\n");
        }

        sb.append("""
            ## Instrucciones de comportamiento
            - Responde siempre en español colombiano, lenguaje claro
            - Eres un asistente EXCLUSIVAMENTE especializado en ganadería \
            lechera y gestión financiera de hatos. Si el usuario pregunta \
            sobre temas que no están relacionados con ganadería, finanzas \
            de hatos, producción lechera o el sistema Hathor, responde \
            exactamente: "Solo estoy programado para ayudarte con temas \
            relacionados con tu hato lechero, finanzas ganaderas y el \
            sistema Hathor. ¿En qué te puedo ayudar con tu hato?"
            - Cuando hagas recomendaciones, sé específico con el contexto \
            del hato (trópico, escala, producción actual)
            - No repitas todos los datos del hato en cada respuesta
            - Máximo 3 recomendaciones por respuesta para no abrumar
            - Cuando el usuario pregunte sobre inversiones, usa SIEMPRE \
            la sección 'Capacidad de inversión del hato' para contextualizar \
            tu respuesta. Si la capacidad es NO_RECOMENDADA, explica \
            claramente qué KPIs están impidiendo la inversión antes de \
            dar cualquier recomendación. Si la capacidad es ALTA o MEDIA, \
            sugiere el tipo de inversión más adecuado según el trópico, \
            escala y KPIs productivos del hato, usando los criterios del \
            corpus documental de inversiones.
            """);

        return sb.toString();
    }

    private String calcularCapacidadInversion(List<KpiResultadoDTO> kpis) {
    Float roa = kpis.stream()
        .filter(k -> "KPI_ROA".equals(k.getCodigo()))
        .map(KpiResultadoDTO::getValor)
        .findFirst().orElse(null);

    Float margenNeto = kpis.stream()
        .filter(k -> "KPI_MARGEN_NETO".equals(k.getCodigo()))
        .map(KpiResultadoDTO::getValor)
        .findFirst().orElse(null);

    Float ratio = kpis.stream()
        .filter(k -> "KPI_RATIO_INGRESO_EGRESO".equals(k.getCodigo()))
        .map(KpiResultadoDTO::getValor)
        .findFirst().orElse(null);

    Float balanceNeto = kpis.stream()
        .filter(k -> "KPI_BALANCE_NETO".equals(k.getCodigo()))
        .map(KpiResultadoDTO::getValor)
        .findFirst().orElse(null);

    Float iofc = kpis.stream()
        .filter(k -> "KPI_IOFC".equals(k.getCodigo()))
        .map(KpiResultadoDTO::getValor)
        .findFirst().orElse(null);

    if ((balanceNeto != null && balanceNeto < 0)
            || (ratio != null && ratio < 1.0f)
            || (iofc != null && iofc < 0)) {
        return "NO_RECOMENDADA";
    }

    if (roa != null && margenNeto != null && ratio != null) {
        if (roa >= 8f && margenNeto >= 20f && ratio >= 1.5f)
            return "ALTA";
        if (roa >= 5f && margenNeto >= 10f && ratio >= 1.3f)
            return "MEDIA";
        if (roa >= 2f && margenNeto >= 5f && ratio >= 1.0f)
            return "BAJA";
        return "NO_RECOMENDADA";
    }

    return "SIN_DATOS";
}
}