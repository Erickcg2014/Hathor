package com.hathor.hathorback.Servicios.Usuario.Alertas.Service;

import com.hathor.hathorback.Entities.Alertas.AlertaHato;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Alertas.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Alertas.Repository.IRepositoryAlertaHato;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpiHato;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceAlertas implements IServiceAlertas {

    @Autowired private IRepositoryAlertaHato      repoAlerta;
    @Autowired private IRepositoryHato            repoHato;
    @Autowired private IRepositoryKpiHato         repoKpiHato;
    @Autowired private IRepositoryRegistroFinanciero repoFinanciero;
    @Autowired private IRepositoryProduccionLeche repoProduccion;
    @Autowired private IRepositoryHatoPractica    repoHatoPractica;
    @Autowired private IRepositoryPerfilProductivo repoPerfilProductivo;

    // ── Usuario ───────────────────────────────────────────────────────────

    @Override
    public AlertasResumenDTO getResumen(UUID idHato) {
        List<AlertaHato> activas = repoAlerta.findActivasByHato(idHato);
        long noLeidas = activas.stream().filter(a -> !a.getLeida()).count();
        String severidadMax = repoAlerta.getSeveridadMaximaNoLeida(idHato);

        List<AlertaHatoDTO> criticas = activas.stream()
            .filter(a -> "CRITICA".equals(a.getSeveridad()))
            .map(this::toDTO).collect(Collectors.toList());
        List<AlertaHatoDTO> preventivas = activas.stream()
            .filter(a -> "PREVENTIVA".equals(a.getSeveridad()))
            .map(this::toDTO).collect(Collectors.toList());
        List<AlertaHatoDTO> oportunidades = activas.stream()
            .filter(a -> "OPORTUNIDAD".equals(a.getSeveridad()))
            .map(this::toDTO).collect(Collectors.toList());

        return AlertasResumenDTO.builder()
            .totalNoLeidas(noLeidas)
            .severidadMaxima(severidadMax)
            .criticas(criticas)
            .preventivas(preventivas)
            .oportunidades(oportunidades)
            .build();
    }

    @Override
    @Transactional
    public void marcarLeida(Long idAlerta) {
        repoAlerta.findById(idAlerta).ifPresent(a -> {
            a.setLeida(true);
            repoAlerta.save(a);
        });
    }

    @Override
    @Transactional
    public void marcarTodasLeidas(UUID idHato) {
        repoAlerta.marcarTodasLeidasByHato(idHato);
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @Override
    public AlertasAdminResumenDTO getResumenAdmin() {
        long hatosCriticos = repoAlerta.countHatosConCriticas();
        List<AlertaHatoDTO> criticas = repoAlerta.findCriticasActivasGlobal()
            .stream().map(this::toDTO).collect(Collectors.toList());

        return AlertasAdminResumenDTO.builder()
            .hatosCriticos(hatosCriticos)
            .alertasCriticas(criticas)
            .build();
    }

    // ── Evaluación de alertas ─────────────────────────────────────────────

    @Override
    @Transactional (propagation = Propagation.REQUIRES_NEW)
    public void evaluarAlertas(UUID idHato) {
        Hato hato = repoHato.findById(idHato).orElse(null);
        if (hato == null) return;

        evaluarTendenciaKpiNegativa(hato);
        evaluarMargenNetoNegativo(hato);
        evaluarCaidaProduccion(hato);
        evaluarCostoAlimentacionAlto(hato);
        evaluarVacasOrdenoBajo(hato);
        evaluarKpiCriticoSinPractica(hato);
        evaluarMejoraPercentil(hato);
        evaluarPracticaDisponible(hato);
        evaluarKpiAlcanzóOptimo(hato);
    }

    // ── Detectores individuales ───────────────────────────────────────────

    private void evaluarTendenciaKpiNegativa(Hato hato) {
        // Obtener KPIs con histórico de los últimos 3 meses
        LocalDate fechaDesde = LocalDate.now().minusMonths(3);
        List<Object[]> historico = repoKpiHato
            .findHistoricoUltimosMeses(hato.getIdHato(), fechaDesde);

        // Agrupar por código KPI
        Map<String, List<Float>> porCodigo = new LinkedHashMap<>();
        for (Object[] row : historico) {
            String codigo = (String) row[0];
            Float  valor  = (Float)  row[1];
            porCodigo.computeIfAbsent(codigo, k -> new ArrayList<>()).add(valor);
        }

        for (Map.Entry<String, List<Float>> entry : porCodigo.entrySet()) {
            String      codigo  = entry.getKey();
            List<Float> valores = entry.getValue();
            if (valores.size() < 2) continue;

            // Verificar tendencia negativa consecutiva
            boolean bajando = true;
            for (int i = 1; i < valores.size(); i++) {
                if (valores.get(i) >= valores.get(i - 1)) {
                    bajando = false;
                    break;
                }
            }

            if (bajando) {
                String tipo = "TENDENCIA_KPI_NEGATIVA_" + codigo;
                if (repoAlerta.existeAlertaActivaDelTipo(
                        hato.getIdHato(), tipo)) continue;

                float inicial = valores.get(0);
                float actual  = valores.get(valores.size() - 1);
                int   meses   = valores.size();

                crearAlerta(hato, tipo, "CRITICA",
                    "KPI bajando: " + codigo,
                    String.format(
                        "Tu KPI %s lleva %d meses bajando consecutivamente. " +
                        "Pasó de %.2f a %.2f.",
                        codigo, meses, inicial, actual),
                    codigo,
                    (float) meses);
            }
        }
    }

    private void evaluarMargenNetoNegativo(Hato hato) {
        String tipo = "MARGEN_NETO_NEGATIVO";
        if (repoAlerta.existeAlertaActivaDelTipo(
                hato.getIdHato(), tipo)) return;

        // Obtener ingresos y egresos de los últimos 3 meses
        LocalDate fechaDesde = LocalDate.now().minusMonths(3)
            .withDayOfMonth(1);
        List<Object[]> resumenMeses = repoFinanciero
            .findResumenMensualUltimosMeses(hato.getIdHato(), fechaDesde);

        long mesesNegativos = resumenMeses.stream()
            .filter(row -> {
                Double ingresos = (Double) row[1];
                Double egresos  = (Double) row[2];
                return ingresos != null && egresos != null
                    && ingresos < egresos;
            }).count();

        if (mesesNegativos >= 2) {
            crearAlerta(hato, tipo, "CRITICA",
                "Margen neto negativo",
                String.format(
                    "Tu margen neto fue negativo en %d de los " +
                    "últimos 3 meses. Revisa tus costos e ingresos.",
                    mesesNegativos),
                null,
                (float) mesesNegativos);
        }
    }

    private void evaluarCaidaProduccion(Hato hato) {
        String tipo = "CAIDA_PRODUCCION";
        if (repoAlerta.existeAlertaActivaDelTipo(
                hato.getIdHato(), tipo)) return;

        // En evaluarCaidaProduccion:
        LocalDate fechaDesde = LocalDate.now().minusMonths(2)
            .withDayOfMonth(1);
        List<Object[]> produccionMeses = repoProduccion
            .findProduccionMensualUltimosMeses(hato.getIdHato(), fechaDesde);

        if (produccionMeses.size() < 2) return;

        Object[] filaAnterior = produccionMeses.get(0);
        Object[] filaActual   = produccionMeses.get(1);

        if (filaAnterior == null || filaActual == null) return;

        Double mesAnterior = filaAnterior[1] != null 
            ? ((Number) filaAnterior[1]).doubleValue() : null;
        Double mesActual   = filaActual[1] != null   
            ? ((Number) filaActual[1]).doubleValue()   : null;

        if (mesAnterior == null || mesActual == null || mesAnterior == 0) return;

        float pctCaida = (float) ((mesAnterior - mesActual)
            / mesAnterior * 100);

        if (pctCaida >= 20) {
            crearAlerta(hato, tipo, "CRITICA",
                "Caída en producción",
                String.format(
                    "Tu producción cayó un %.1f%% respecto al " +
                    "mes anterior (de %.0f a %.0f litros).",
                    pctCaida, mesAnterior, mesActual),
                null,
                pctCaida);
        }
    }

    private void evaluarCostoAlimentacionAlto(Hato hato) {
        String tipo = "COSTO_ALIMENTACION_ALTO";
        if (repoAlerta.existeAlertaActivaDelTipo(
                hato.getIdHato(), tipo)) return;

        // Verificar KPI de costo alimentación vs benchmark
        repoKpiHato.findUltimoByHatoAndCodigo(
                hato.getIdHato(), "COSTO_ALIM_LITRO")
            .ifPresent(kpiHato -> {
                if (kpiHato.getEstado() != null &&
                    kpiHato.getEstado().equals("CRITICO")) {
                    crearAlerta(hato, tipo, "PREVENTIVA",
                        "Costo de alimentación alto",
                        "Tu costo de alimentación por litro supera " +
                        "el promedio del sector. Considera optimizar " +
                        "la dieta del hato.",
                        "COSTO_ALIM_LITRO",
                        kpiHato.getValor());
                }
            });
    }

    private void evaluarVacasOrdenoBajo(Hato hato) {
        String tipo = "VACAS_ORDENIO_BAJO";
        if (repoAlerta.existeAlertaActivaDelTipo(
                hato.getIdHato(), tipo)) return;

        repoPerfilProductivo.findByHato_IdHato(hato.getIdHato())
            .ifPresent(perfil -> {
                if (perfil.getVacasEnOrdenio() == null) return;

                // Obtener total vacas del inventario
                Integer totalVacas = repoKpiHato
                    .findUltimoByHatoAndCodigo(
                        hato.getIdHato(), "TOTAL_VACAS")
                    .map(k -> k.getValor() != null
                        ? k.getValor().intValue() : null)
                    .orElse(null);

                if (totalVacas == null || totalVacas == 0) return;

                float pct = (float) perfil.getVacasEnOrdenio()
                    / totalVacas * 100;

                if (pct < 60) {
                    crearAlerta(hato, tipo, "PREVENTIVA",
                        "Pocas vacas en ordeño",
                        String.format(
                            "Solo el %.0f%% de tus vacas están en " +
                            "ordeño. El mínimo recomendado es 60%%.",
                            pct),
                        null,
                        pct);
                }
            });
    }

    private void evaluarKpiCriticoSinPractica(Hato hato) {
        String tipo = "KPI_CRITICO_SIN_PRACTICA";

        boolean yaExiste = repoAlerta.existeAlertaActivaDelTipo(
            hato.getIdHato(), tipo);
        System.out.println("🔍 KPI_CRITICO_SIN_PRACTICA yaExiste: "
            + yaExiste);

        if (yaExiste) return;

        List<String> kpisCriticos = repoKpiHato
            .findCodigosEnEstadoByHato(hato.getIdHato(), "CRITICO");
        System.out.println("🔍 KPIs críticos encontrados: "
            + kpisCriticos);

        if (kpisCriticos.isEmpty()) return;

        List<String> kpisConPractica = repoHatoPractica
            .findKpisEnCursoByHato(hato.getIdHato());
        System.out.println("🔍 KPIs con práctica EN_CURSO: "
            + kpisConPractica);

        List<String> sinPractica = kpisCriticos.stream()
            .filter(k -> !kpisConPractica.contains(k))
            .collect(Collectors.toList());
        System.out.println("🔍 KPIs críticos SIN práctica: "
            + sinPractica);

        if (!sinPractica.isEmpty()) {
            crearAlerta(hato, tipo, "CRITICA",
                "KPIs críticos sin práctica",
                String.format(
                    "Tienes %d KPI(s) en estado crítico sin una " +
                    "práctica activa asignada: %s.",
                    sinPractica.size(),
                    String.join(", ", sinPractica)),
                null,
                (float) sinPractica.size());
        }
    }

    private void evaluarMejoraPercentil(Hato hato) {
        String tipo = "MEJORA_PERCENTIL";
        if (repoAlerta.existeAlertaActivaDelTipo(
                hato.getIdHato(), tipo)) return;

        // Comparar percentil promedio actual vs hace 1 mes
        Float percentilActual = repoKpiHato.findPercentilPromedioByHato(
            hato.getIdHato(),
            LocalDate.now().withDayOfMonth(1));
        Float percentilAnterior = repoKpiHato.findPercentilPromedioByHato(
            hato.getIdHato(),
            LocalDate.now().minusMonths(1).withDayOfMonth(1));

        if (percentilActual == null || percentilAnterior == null) return;

        float mejora = percentilActual - percentilAnterior;
        if (mejora >= 10) {
            crearAlerta(hato, tipo, "OPORTUNIDAD",
                "¡Tu posición mejoró!",
                String.format(
                    "¡Tu posición en el ranking mejoró! Pasaste del " +
                    "percentil %.0f al %.0f este mes.",
                    percentilAnterior, percentilActual),
                null,
                mejora);
        }
    }

    private void evaluarPracticaDisponible(Hato hato) {
        String tipo = "PRACTICA_DISPONIBLE";
        if (repoAlerta.existeAlertaActivaDelTipo(
                hato.getIdHato(), tipo)) return;

        long practicasPendientes = repoHatoPractica
            .countByHatoAndEstado(hato.getIdHato(), "PENDIENTE");

        if (practicasPendientes > 0) {
            crearAlerta(hato, tipo, "OPORTUNIDAD",
                "Prácticas disponibles",
                String.format(
                    "Tienes %d práctica(s) recomendadas pendientes " +
                    "de iniciar. ¡Comienza hoy para mejorar tus KPIs!",
                    practicasPendientes),
                null,
                (float) practicasPendientes);
        }
    }

    private void evaluarKpiAlcanzóOptimo(Hato hato) {
        // Obtener KPIs que pasaron a OPTIMO hoy
        List<String> kpisOptimos = repoKpiHato
            .findCodigosNuevosOptimosByHato(
                hato.getIdHato(), LocalDate.now());

        for (String codigo : kpisOptimos) {
            String tipo = "KPI_ALCANZO_OPTIMO_" + codigo;
            if (repoAlerta.existeAlertaActivaDelTipo(
                    hato.getIdHato(), tipo)) continue;

            crearAlerta(hato, tipo, "OPORTUNIDAD",
                "¡KPI alcanzó nivel óptimo!",
                String.format(
                    "¡Felicitaciones! Tu KPI %s alcanzó nivel " +
                    "óptimo. Sigue aplicando buenas prácticas.",
                    codigo),
                codigo,
                null);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void crearAlerta(Hato hato, String tipo,
            String severidad, String titulo,
            String mensaje, String codigoKpi,
            Float valorReferencia) {
        AlertaHato alerta = AlertaHato.builder()
            .hato(hato)
            .tipo(tipo)
            .severidad(severidad)
            .titulo(titulo)
            .mensaje(mensaje)
            .codigoKpi(codigoKpi)
            .valorReferencia(valorReferencia)
            .leida(false)
            .estado("ACTIVA")
            .fechaCreacion(LocalDateTime.now())
            .build();
        repoAlerta.save(alerta);
    }

    private AlertaHatoDTO toDTO(AlertaHato a) {
        return AlertaHatoDTO.builder()
            .idAlerta(a.getIdAlerta())
            .tipo(a.getTipo())
            .severidad(a.getSeveridad())
            .titulo(a.getTitulo())
            .mensaje(a.getMensaje())
            .leida(a.getLeida())
            .fechaCreacion(a.getFechaCreacion() != null
                ? a.getFechaCreacion().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null)
            .fechaExpiracion(a.getFechaExpiracion() != null
                ? a.getFechaExpiracion().toString() : null)
            .codigoKpi(a.getCodigoKpi())
            .valorReferencia(a.getValorReferencia())
            .estado(a.getEstado())
            .tiempoRelativo(calcularTiempoRelativo(
                a.getFechaCreacion()))
            .build();
    }

    private String calcularTiempoRelativo(LocalDateTime fecha) {
        if (fecha == null) return "—";
        long minutos = ChronoUnit.MINUTES.between(
            fecha, LocalDateTime.now());
        if (minutos < 60)
            return "hace " + minutos + " min";
        long horas = ChronoUnit.HOURS.between(
            fecha, LocalDateTime.now());
        if (horas < 24)
            return "hace " + horas + " h";
        long dias = ChronoUnit.DAYS.between(
            fecha, LocalDateTime.now());
        return "hace " + dias + " día" + (dias > 1 ? "s" : "");
    }
}