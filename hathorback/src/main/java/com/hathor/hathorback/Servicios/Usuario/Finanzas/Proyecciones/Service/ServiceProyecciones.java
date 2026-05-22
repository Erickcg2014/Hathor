package com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.Service;

import com.hathor.hathorback.Entities.Finanzas.InversionPlaneada;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO.InversionResumenDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.Repository.IRepositoryInversionPlaneada;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceProyecciones implements IServiceProyecciones {

    @Autowired
    private IRepositoryRegistroFinanciero repoFinanciero;

    @Autowired
    private IRepositoryInversionPlaneada repoInversion;

    private static final int MESES_MINIMOS = 3;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public ProyeccionesResponseDTO getProyecciones(
            UUID idHato, int mesesProyectar) {

        List<Object[]> historial = repoFinanciero
            .findResumenMensualCompleto(idHato);

        int mesesDisponibles = historial.size();

        if (mesesDisponibles < MESES_MINIMOS) {
            return ProyeccionesResponseDTO.builder()
                .proyecciones(Collections.emptyList())
                .mesesHistorial(mesesDisponibles)
                .modelosAplicados(Collections.emptyList())
                .datosInsuficientes(true)
                .mensajeInsuficiente(String.format(
                    "Se necesitan al menos %d meses de registros " +
                    "financieros para generar proyecciones. " +
                    "Actualmente tienes %d mes%s registrado%s.",
                    MESES_MINIMOS, mesesDisponibles,
                    mesesDisponibles == 1 ? "" : "es",
                    mesesDisponibles == 1 ? "" : "s"))
                .build();
        }

        List<Double> ingresos = historial.stream()
            .map(r -> r[1] != null
                ? ((Number) r[1]).doubleValue() : 0.0)
            .collect(Collectors.toList());
        List<Double> egresos = historial.stream()
            .map(r -> r[2] != null
                ? ((Number) r[2]).doubleValue() : 0.0)
            .collect(Collectors.toList());

        List<String> modelos = new ArrayList<>();
        modelos.add("PROMEDIO_MOVIL");
        if (mesesDisponibles >= 4) modelos.add("TENDENCIA_LINEAL");
        if (mesesDisponibles >= 6) modelos.add("ESTACIONALIDAD");

        double avgIngresos = ingresos.stream()
            .mapToDouble(Double::doubleValue).average().orElse(0);
        double avgEgresos = egresos.stream()
            .mapToDouble(Double::doubleValue).average().orElse(0);

        double tendIngreso = calcularTendencia(ingresos);
        double tendEgreso  = calcularTendencia(egresos);

        // Cargar inversiones planeadas del rango proyectado
        YearMonth mesBase  = YearMonth.now();
        String mesDesde    = mesBase.plusMonths(1).format(FMT);
        String mesHasta    = mesBase.plusMonths(
            mesesProyectar).format(FMT);

        List<InversionPlaneada> inversionesRango =
            repoInversion.findPlaneadasByHatoAndRango(
                idHato, mesDesde, mesHasta);

        // Agrupar inversiones por mes
        Map<String, List<InversionPlaneada>> inversionesPorMes =
            inversionesRango.stream().collect(
                Collectors.groupingBy(
                    InversionPlaneada::getMesEjecucion));

        List<ProyeccionMensualDTO> proyecciones = new ArrayList<>();

        for (int i = 1; i <= mesesProyectar; i++) {
            YearMonth mes   = mesBase.plusMonths(i);
            String periodo  = mes.format(FMT);

            double[] rangoIngreso = proyectarRango(
                ingresos, i, modelos, mes);
            double[] rangoEgreso  = proyectarRango(
                egresos, i, modelos, mes);

            // Inversiones del mes — se suma al egreso proyectado
            List<InversionPlaneada> invMes =
                inversionesPorMes.getOrDefault(
                    periodo, Collections.emptyList());

            double totalInversionMes = invMes.stream()
                .mapToDouble(InversionPlaneada::getMonto)
                .sum();


            List<InversionPlaneada> retornosMes =
                inversionesRango.stream()
                    .filter(inv -> {
                        if (inv.getMesesRetorno() == null ||
                            inv.getRetornoEsperadoPct() == null)
                            return false;
                        YearMonth mesInv = YearMonth.parse(
                            inv.getMesEjecucion());
                        YearMonth finRetorno = mesInv.plusMonths(
                            inv.getMesesRetorno());
                        return mes.isAfter(mesInv) &&
                               !mes.isAfter(finRetorno);
                    })
                    .collect(Collectors.toList());

            double totalRetornoMes = retornosMes.stream()
                .mapToDouble(inv -> {
                    double retornoTotal = inv.getMonto() *
                        inv.getRetornoEsperadoPct() / 100;
                    return retornoTotal / inv.getMesesRetorno();
                }).sum();

            // Aplicar inversiones y retornos a los rangos
            double egresoMinConInv  =
                rangoEgreso[0] + totalInversionMes;
            double egresoMaxConInv  =
                rangoEgreso[1] + totalInversionMes;
            double egresoProyConInv =
                rangoEgreso[2] + totalInversionMes;

            double ingresoMinConRet  =
                rangoIngreso[0] + totalRetornoMes;
            double ingresoMaxConRet  =
                rangoIngreso[1] + totalRetornoMes;
            double ingresoProyConRet =
                rangoIngreso[2] + totalRetornoMes;

            double margenMin  =
                ingresoMinConRet  - egresoMaxConInv;
            double margenMax  =
                ingresoMaxConRet  - egresoMinConInv;
            double margenProy =
                ingresoProyConRet - egresoProyConInv;

            // Construir resúmenes de inversiones del mes
            List<InversionResumenDTO> invResumen = invMes.stream()
                .map(inv -> InversionResumenDTO.builder()
                    .idInversion(inv.getIdInversion())
                    .descripcion(inv.getDescripcion())
                    .monto(inv.getMonto())
                    .nombreCategoria(
                        inv.getCategoriaFinanciera() != null
                            ? inv.getCategoriaFinanciera()
                                .getNombre()
                            : "Sin categoría")
                    .icono("📦")
                    .build())
                .collect(Collectors.toList());

            List<InversionResumenDTO> retResumen =
                retornosMes.stream()
                    .map(inv -> InversionResumenDTO.builder()
                        .idInversion(inv.getIdInversion())
                        .descripcion(inv.getDescripcion())
                        .monto(inv.getMonto() *
                            inv.getRetornoEsperadoPct() / 100
                            / inv.getMesesRetorno())
                        .nombreCategoria(
                            inv.getCategoriaFinanciera() != null
                                ? inv.getCategoriaFinanciera()
                                    .getNombre()
                                : "Sin categoría")
                        .icono("📈")
                        .build())
                    .collect(Collectors.toList());

            // Alertas contextuales
            List<String> alertas = generarAlertasContextuales(
                mes, rangoIngreso, rangoEgreso,
                avgIngresos, avgEgresos, historial);

            // Alertas adicionales por inversión
            if (!invMes.isEmpty()) {
                alertas.add(String.format(
                    "📦 Este mes tienes %d inversión(es) " +
                    "planeada(s) por %s.",
                    invMes.size(),
                    formatCOP(totalInversionMes)));
            }
            if (!retornosMes.isEmpty()) {
                alertas.add(String.format(
                    "📈 Retorno estimado activo: +%s " +
                    "en ingresos este mes.",
                    formatCOP(totalRetornoMes)));
            }

            proyecciones.add(ProyeccionMensualDTO.builder()
                .periodo(periodo)
                .periodoLabel(formatearPeriodo(mes))
                .ingresoMin(Math.max(0, ingresoMinConRet))
                .ingresoMax(ingresoMaxConRet)
                .ingresoProyectado(ingresoProyConRet)
                .egresoMin(Math.max(0, egresoMinConInv))
                .egresoMax(egresoMaxConInv)
                .egresoProyectado(egresoProyConInv)
                .margenMin(margenMin)
                .margenMax(margenMax)
                .margenProyectado(margenProy)
                .estadoMargen(calcularEstadoMargen(
                    margenMin, margenMax))
                .alertas(alertas)
                .inversionesDelMes(invResumen)
                .retornosDelMes(retResumen)
                .tieneInversion(!invResumen.isEmpty())
                .tieneRetorno(!retResumen.isEmpty())
                .build());
        }

        return ProyeccionesResponseDTO.builder()
            .proyecciones(proyecciones)
            .mesesHistorial(mesesDisponibles)
            .modelosAplicados(modelos)
            .datosInsuficientes(false)
            .promedioIngresosMensual(avgIngresos)
            .promedioEgresosMensual(avgEgresos)
            .tendenciaIngresos(tendIngreso)
            .tendenciaEgresos(tendEgreso)
            .build();
    }

    // ── Modelos  ─────────────────────────────────────────────

    private double promedioMovilPonderado(List<Double> serie) {
        if (serie.isEmpty()) return 0;
        int n = Math.min(3, serie.size());
        double suma = 0, pesos = 0;
        for (int i = 0; i < n; i++) {
            double peso = n - i;
            suma  += serie.get(serie.size() - 1 - i) * peso;
            pesos += peso;
        }
        return suma / pesos;
    }

    private double calcularTendencia(List<Double> serie) {
        if (serie.size() < 2) return 0;
        int n = serie.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX  += i; sumY  += serie.get(i);
            sumXY += i * serie.get(i); sumX2 += i * i;
        }
        double denom = n * sumX2 - sumX * sumX;
        if (denom == 0) return 0;
        double pendiente = (n * sumXY - sumX * sumY) / denom;
        double promedio  = sumY / n;
        return promedio > 0
            ? Math.round((pendiente / promedio) * 1000.0) / 10.0
            : 0;
    }

    private double proyectarTendencia(
            List<Double> serie, int pasosFuturo) {
        if (serie.size() < 2)
            return promedioMovilPonderado(serie);
        int n = serie.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX  += i; sumY  += serie.get(i);
            sumXY += i * serie.get(i); sumX2 += i * i;
        }
        double denom = n * sumX2 - sumX * sumX;
        if (denom == 0) return promedioMovilPonderado(serie);
        double pendiente  = (n * sumXY - sumX * sumY) / denom;
        double intercepto = (sumY - pendiente * sumX) / n;
        return intercepto + pendiente * (n - 1 + pasosFuturo);
    }

    private double[] proyectarRango(
            List<Double> serie, int paso,
            List<String> modelos, YearMonth mes) {
        List<Double> valores = new ArrayList<>();
        valores.add(promedioMovilPonderado(serie));
        if (modelos.contains("TENDENCIA_LINEAL")) {
            double v = proyectarTendencia(serie, paso);
            if (v > 0) valores.add(v);
        }
        if (valores.isEmpty()) valores.add(0.0);
        double proyectado = valores.stream()
            .mapToDouble(Double::doubleValue).average().orElse(0);
        double incertidumbre = 0.15 + (paso - 1) * 0.05;
        return new double[]{
            proyectado * (1 - incertidumbre),
            proyectado * (1 + incertidumbre),
            proyectado
        };
    }

    private List<String> generarAlertasContextuales(
            YearMonth mes, double[] rangoIngreso,
            double[] rangoEgreso, double avgIngresos,
            double avgEgresos, List<Object[]> historial) {
        List<String> alertas = new ArrayList<>();
        double margenProy = rangoIngreso[2] - rangoEgreso[2];
        if (margenProy < 0)
            alertas.add("⚠️ Se proyecta margen neto negativo.");
        double pctEgreso = avgEgresos > 0
            ? (rangoEgreso[2] - avgEgresos) / avgEgresos * 100 : 0;
        if (pctEgreso > 20)
            alertas.add(String.format(
                "⚠️ Egresos proyectados %.0f%% sobre el promedio.",
                pctEgreso));
        int mesNum = mes.getMonthValue();
        long mesesAltos = historial.stream().filter(r -> {
            String p = (String) r[0];
            if (p == null) return false;
            int m = Integer.parseInt(p.substring(5));
            double eg = r[2] != null
                ? ((Number) r[2]).doubleValue() : 0;
            return m == mesNum && eg > avgEgresos * 1.2;
        }).count();
        if (mesesAltos > 0)
            alertas.add(String.format(
                "📅 Históricamente %s tiene gastos más altos.",
                mes.getMonth().getDisplayName(
                    java.time.format.TextStyle.FULL,
                    new java.util.Locale("es", "CO"))));
        return alertas;
    }

    private String calcularEstadoMargen(
            double margenMin, double margenMax) {
        if (margenMax < 0)  return "NEGATIVO";
        if (margenMin >= 0) return "POSITIVO";
        return "NEUTRO";
    }

    private String formatearPeriodo(YearMonth mes) {
        return mes.getMonth().getDisplayName(
            java.time.format.TextStyle.FULL,
            new java.util.Locale("es", "CO")) +
            " " + mes.getYear();
    }

    private String formatCOP(double valor) {
        if (valor >= 1_000_000)
            return String.format("$%.1fM", valor / 1_000_000);
        if (valor >= 1_000)
            return String.format("$%.0fK", valor / 1_000);
        return String.format("$%.0f", valor);
    }
}