package com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Service;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Practicas.Regla;
import com.hathor.hathorback.Entities.Practicas.ReglaPractica;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador.KpiMapperService;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Repository.IRepositoryRecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository.IRepositoryReglaPractica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceMotorReglas implements IServiceMotorReglas {

    @Autowired private IRepositoryRecomendacionHato   repoRecomendacion;

    @Autowired private IRepositoryReglaPractica       repoReglaPractica;
    @Autowired private IRepositoryHatoPractica        repoHatoPractica;
    @Autowired private KpiMapperService kpiMapperService;
    @Autowired private ServiceReglaCache reglaCache;


    @Override
    @Transactional
    public void evaluar(Hato hato, Map<String, Float> kpisCalculados) {

        String escala  = hato.getEscala();
        String tropico = hato.getTropico();
        String ciudad  = hato.getCiudad();

        // 1. Eliminar todas las recomendaciones ACTIVAS anteriores del hato
        repoRecomendacion.desactivarActivasByHato(hato.getIdHato());

        // 2. Iterar sobre cada KPI calculado
        for (Map.Entry<String, Float> entrada : kpisCalculados.entrySet()) {
            String codigoKpi = entrada.getKey();
            Float  valorKpi  = entrada.getValue();

            // 2a. Benchmark más contextualizado disponible
            Optional<BenchmarkReferencia> benchOpt =
                reglaCache.getBenchmarkEspecifico(codigoKpi, tropico, ciudad, escala);

            BenchmarkReferencia benchmark = benchOpt.orElse(null);

            Float benchPromedio = benchmark != null ? benchmark.getValorPromedio() : null;
            Float benchTop      = benchmark != null ? benchmark.getValorTop()      : null;

            // 2b. Reglas activas para este KPI y esta escala
            List<Regla> reglas = reglaCache.getReglasPorKpi(codigoKpi, escala);

            // 2c. Evaluar cada regla
            for (Regla regla : reglas) {

                String estadoKpiObjetivo = regla.getEstadoKpiObjetivo();
                if (estadoKpiObjetivo != null && !estadoKpiObjetivo.isBlank()
                        && !"TODOS".equalsIgnoreCase(estadoKpiObjetivo)) {

                    String estadoActual = kpiMapperService.calcularEstado(
                        codigoKpi,
                        valorKpi,
                        benchPromedio,
                        benchTop
                    );

                    if (!estadoKpiObjetivo.equalsIgnoreCase(estadoActual)) {
                        continue; 
                    }
                }
                // ── FIN NUEVO ──────────────────────────────────────────

                boolean cumple = cumpleCondicion(
                    regla.getOperador(),
                    regla.getUmbral1(),
                    regla.getUmbral2(),
                    valorKpi,
                    benchPromedio,
                    benchTop
                );

                if (!cumple) continue;

                // Crear y persistir la recomendación
                RecomendacionHato recomendacion = RecomendacionHato.builder()
                    .hato(hato)
                    .regla(regla)
                    .mensaje(regla.getMensaje())
                    .prioridad(mapearPrioridad(regla.getPrioridad()))
                    .tipoEstado("ACTIVA")
                    .leida(false)
                    .fechaCreacion(LocalDate.now())
                    .indicador(codigoKpi)
                    .valorActual(valorKpi)
                    .valorReferencia(benchPromedio)
                    // Snapshot del contexto del hato
                    .escalaHato(escala)
                    .tropicoHato(tropico)
                    .regionHato(ciudad)
                    .build();

                recomendacion = repoRecomendacion.save(recomendacion);

                // Asignar prácticas de la regla al hato si no existen ya
                List<ReglaPractica> practicasOrdenadas =
                    repoReglaPractica.findByReglaOrdenadas(regla.getIdRegla());

                for (ReglaPractica rp : practicasOrdenadas) {
                    Integer idPractica = rp.getPractica().getIdPractica();

                    boolean yaExiste = repoHatoPractica
                        .existsPendienteOEnCurso(hato.getIdHato(), idPractica);

                    if (!yaExiste) {
                        repoHatoPractica.save(HatoPractica.builder()
                            .hato(hato)
                            .practica(rp.getPractica())
                            .recomendacion(recomendacion)
                            .estado("PENDIENTE")
                            .porcentajeAvance(0f)
                            .build());
                    }
                }
            }
        }
    }

    @Override
    public boolean cumpleCondicion(String operador, Double umbral1, Double umbral2,
                                   Float valorKpi, Float benchmarkPromedio,
                                   Float benchmarkTop) {
        if (valorKpi == null || operador == null) return false;

        switch (operador) {
            case "MENOR_QUE":
                if (umbral1 == null) return false;
                return valorKpi < umbral1;

            case "MAYOR_QUE":
                if (umbral1 == null) return false;
                return valorKpi > umbral1;

            case "ENTRE":
                if (umbral1 == null || umbral2 == null) return false;
                return valorKpi >= umbral1 && valorKpi <= umbral2;

            case "MENOR_PCT_PROMEDIO":
                if (umbral1 == null || benchmarkPromedio == null) return false;
                return valorKpi < (benchmarkPromedio * umbral1);

            case "MAYOR_PCT_PROMEDIO":
                if (umbral1 == null || benchmarkPromedio == null) return false;
                return valorKpi > (benchmarkPromedio * umbral1);

            case "MAYOR_PCT_TOP":
                if (umbral1 == null || benchmarkTop == null) return false;
                return valorKpi > (benchmarkTop * umbral1);

            default:
                return false;
        }
    }

    // Convierte prioridad numérica a texto
    private String mapearPrioridad(Integer prioridad) {
        if (prioridad == null) return "BAJA";
        return switch (prioridad) {
            case 1  -> "ALTA";
            case 2  -> "MEDIA";
            default -> "BAJA";
        };
    }
}