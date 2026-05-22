package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;
import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Entities.Kpi.KpiHato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Repository.IRepositoryBenchmarkReferencia;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Service.IServiceBenchmarkReferencia;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkGlobalDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkHatoResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.ComparativaHatosDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.FiltrosAplicadosDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.HatoAnonimizadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.HatoValorDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.KpiResumenGlobalDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Repository.IRepositoryBenchmarking;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service.IServiceRanking;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpiHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;

import java.util.LinkedHashMap;
import java.util.HashSet;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
public class ServiceBenchmarking implements IServiceBenchmarking {

    @Autowired
    IServiceHato serviceHato;

    @Autowired
    IServiceRanking serviceRanking;

    @Autowired
    IServiceKpi serviceKpi;

    @Autowired
    IServiceBenchmarkReferencia serviceBenchReference;

    @Autowired
    IRepositoryBenchmarking repoBenchHato;

    @Autowired
    IRepositoryBenchmarkReferencia repoBenchReferencia;
    
    @Autowired
    IRepositoryKpiHato repositoryKpiHato;

    // === MAPEO PARA ANONIMIZAR LOS HATOS ===

    private static final Map<String, String> GENTILICIOS = Map.ofEntries(
        Map.entry("Amazonas",          "Amazónico"),
        Map.entry("Antioquia",         "Antioqueño"),
        Map.entry("Arauca",            "Araucano"),
        Map.entry("Atlántico",         "Atlanticense"),
        Map.entry("Bolívar",           "Bolivarense"),
        Map.entry("Boyacá",            "Boyacense"),
        Map.entry("Caldas",            "Caldense"),
        Map.entry("Caquetá",           "Caqueteño"),
        Map.entry("Casanare",          "Casanareño"),
        Map.entry("Cauca",             "Caucano"),
        Map.entry("Cesar",             "Cesarense"),
        Map.entry("Chocó",             "Chocoano"),
        Map.entry("Córdoba",           "Cordobés"),
        Map.entry("Cundinamarca",      "Cundinamarqués"),
        Map.entry("Guainía",           "Guainiano"),
        Map.entry("Guaviare",          "Guaviarense"),
        Map.entry("Huila",             "Huilense"),
        Map.entry("La Guajira",        "Guajiro"),
        Map.entry("Magdalena",         "Magdalenense"),
        Map.entry("Meta",              "Llanero"),
        Map.entry("Nariño",            "Nariñense"),
        Map.entry("Norte de Santander","Nortesantandereano"),
        Map.entry("Putumayo",          "Putumayense"),
        Map.entry("Quindío",           "Quindiano"),
        Map.entry("Risaralda",         "Risaraldense"),
        Map.entry("San Andrés y Providencia", "Sanandresano"),
        Map.entry("Santander",         "Santandereano"),
        Map.entry("Sucre",             "Sucreño"),
        Map.entry("Tolima",            "Tolimense"),
        Map.entry("Valle del Cauca",   "Vallecaucano"),
        Map.entry("Vaupés",            "Vaupesino"),
        Map.entry("Vichada",           "Vichadeño")
    );

    // Mapa para contar cuántos hatos hay por gentilicio en esta sesión
    private String generarAlias(Hato hato, Map<String, Integer> contadorPorGentilicio) {
        String depto     = hato.getDepartamento() != null ? hato.getDepartamento() : "Colombia";
        String gentilicio = GENTILICIOS.getOrDefault(depto, "Ganadero");

        int numero = contadorPorGentilicio.merge(gentilicio, 1, Integer::sum);
        return "Hato " + gentilicio + " #" + numero;
    }

    private Double difuminar(Double coordenada) {
        if (coordenada == null) return null;
        return Math.round(coordenada * 100.0) / 100.0;
    }

    private float calcularPercentilEnGrupo(float valorHato, List<KpiHato> grupo) {
        if (grupo.isEmpty()) return 0f;
        long menores = grupo.stream()
            .filter(kh -> kh.getValor() != null && kh.getValor() < valorHato)
            .count();
        return ((float) menores / grupo.size()) * 100f;
    }

    private String calcularInterpretacionGrupo(float valorHato, float promedio, float top) {
        if (menorEsMejor(null)) return "SIN_DATOS"; 
        if (valorHato < promedio * 0.7f) return "CRITICO";
        if (valorHato < promedio)        return "ACEPTABLE";
        if (valorHato <= top)            return "BUENO";
        return "OPTIMO";
    }

    private boolean menorEsMejor(String codigoKpi) {
        return codigoKpi != null && (
            codigoKpi.equals("KPI_COSTO_LITRO")       ||
            codigoKpi.equals("KPI_BREAKEVEN_LITRO")    ||
            codigoKpi.equals("KPI_EMPLEADOS_HA")       ||
            codigoKpi.equals("KPI_COSTO_LABORAL_PCT")
        );
    }

    // MAPPER ENTIDAD A DTO
    private BenchmarkHatoResultadoDTO toDTO(BenchmarkHato b) {
        BenchmarkHatoResultadoDTO.KpiDTO kpiDTO = null;
        if (b.getKpi() != null) {
            kpiDTO = BenchmarkHatoResultadoDTO.KpiDTO.builder()
                .idKpi(b.getKpi().getIdKpi())
                .codigo(b.getKpi().getCodigo())
                .nombre(b.getKpi().getNombre())
                .descripcion(b.getKpi().getDescripcion())
                .formula(b.getKpi().getFormula())
                .unidad(b.getKpi().getUnidad())
                .categoria(b.getKpi().getCategoria())
                .build();
        }

        BenchmarkHatoResultadoDTO.BenchReferenciaDTO benchDTO = null;
        if (b.getBenchReferencia() != null) {
            benchDTO = BenchmarkHatoResultadoDTO.BenchReferenciaDTO.builder()
                .idBenchmark(b.getBenchReferencia().getIdBenchmark())
                .region(b.getBenchReferencia().getRegion())
                .valorPromedio(b.getBenchReferencia().getValorPromedio())
                .valorTop(b.getBenchReferencia().getValorTop())
                .anio(b.getBenchReferencia().getAnio())
                .tropico(b.getBenchReferencia().getTropico())
                .sistemaOrdenio(b.getBenchReferencia().getSistemaOrdenio())
                .build();
        }

        return BenchmarkHatoResultadoDTO.builder()
            .idBenchmarkHato(b.getIdBenchmarkHato())
            .percentil(b.getPercentil())
            .interpretacion(b.getInterpretacion())
            .nivelBenchmark(b.getNivelBenchmark())
            .valorHato(b.getValorHato())
            .fechaCalculo(b.getFechaCalculo())
            .kpi(kpiDTO)
            .benchReferencia(benchDTO)
            .build();
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void calcularTodo(UUID idHato) {
        repoBenchHato.deleteByHato_IdHatoAndNivelBenchmark(idHato, "NACIONAL");
        repoBenchHato.deleteByHato_IdHatoAndNivelBenchmark(idHato, "TROPICO");
        repoBenchHato.deleteByHato_IdHatoAndNivelBenchmark(idHato, "TROPICO_ESCALA");

        calcularBenchConReferencia(idHato, "NACIONAL");
        calcularBenchConReferencia(idHato, "TROPICO");
        calcularBenchConReferencia(idHato, "TROPICO_ESCALA");
    }

    @Transactional
    @Override
    public List<BenchmarkHato> calcularBenchConReferencia(UUID idHato, String nivel) {
        Hato hato = serviceHato.findHatoById(idHato);
        List<KpiResultadoDTO> kpis = serviceKpi.getKpisDelHato(hato.getIdHato());
        List<BenchmarkHato> listReturn = new ArrayList<>();

        String tropico = null;
        String region  = null;
        String escala  = null;

        if ("TROPICO".equals(nivel) || "TROPICO_ESCALA".equals(nivel)) {
            tropico = hato.getTropico();
        }
        if ("TROPICO_ESCALA".equals(nivel)) {
            region = hato.getCiudad();
            escala = hato.getEscala();
        }

        for (KpiResultadoDTO kpi : kpis) {

            if (kpi.getValor() == null) continue;

            Optional<BenchmarkReferencia> benchOpt =
                repoBenchReferencia.findMasEspecifico(
                    kpi.getCodigo(),
                    tropico,
                    region,
                    escala
                );

            if (benchOpt.isEmpty()) continue;

            BenchmarkReferencia benchRef = benchOpt.get();

            float promedio = benchRef.getValorPromedio();
            float top      = benchRef.getValorTop();
            float valor    = kpi.getValor();

            float percentil;

            if (menorEsMejor(kpi.getCodigo())) {
                if (top == promedio) {
                    percentil = valor <= top ? 100f : 0f;
                } else if (valor <= top) {
                    percentil = 100f;
                } else if (valor <= promedio) {
                    float rango = promedio - top;
                    percentil = 50f + ((promedio - valor) / rango) * 50f;
                } else {
                    float minEst = promedio * 1.5f;
                    float rango  = minEst - promedio;
                    percentil = rango == 0 ? 0f
                        : Math.max(0f, ((minEst - valor) / rango) * 50f);
                }
            } else {
                if (top == promedio) {
                    percentil = valor >= top ? 100f : 0f;
                } else if (valor <= promedio) {
                    float rangoInferior = promedio; 
                    percentil = rangoInferior == 0 ? 0f
                        : Math.max(0f, Math.min(50f, (valor / rangoInferior) * 50f));
                } else if (valor <= top) {
                    float rangoSuperior = top - promedio;
                    percentil = 50f + ((valor - promedio) / rangoSuperior) * 50f;
                } else {
                    percentil = 100f;
                }
            }
            percentil = Math.max(0f, Math.min(100f, percentil));

            String interpretacion;
            if (menorEsMejor(kpi.getCodigo())) {
                if (valor <= top * 0.9f) {
                    interpretacion = "OPTIMO";
                } else if (valor <= promedio) {
                    interpretacion = "BUENO";
                } else if (valor <= promedio * 1.3f) {
                    interpretacion = "ACEPTABLE";
                } else {
                    interpretacion = "CRITICO";
                }
            } else {
                if (valor < promedio * 0.7f) {
                    interpretacion = "CRITICO";
                } else if (valor < promedio) {
                    interpretacion = "ACEPTABLE";
                } else if (valor <= top) {
                    interpretacion = "BUENO";
                } else {
                    interpretacion = "OPTIMO";
                }
            }

            Kpi kpiRef = serviceKpi.getKpiById(kpi.getIdKpi());

            BenchmarkHato benchmark = BenchmarkHato.builder()
                .hato(hato)
                .benchReferencia(benchRef)
                .kpi(kpiRef)
                .percentil(percentil)
                .interpretacion(interpretacion)
                .valorHato(valor)
                .fechaCalculo(LocalDate.now())
                .nivelBenchmark(nivel)
                .build();

            listReturn.add(benchmark);
        }

        repoBenchHato.saveAll(listReturn);
        return listReturn;
    }

    @Transactional
    @Override
    public List<BenchmarkHato> calcularBenchConHatos(UUID idHato) {

        Hato hato = serviceHato.findHatoById(idHato);
        List<KpiResultadoDTO> kpis = serviceKpi.getKpisDelHato(idHato);
        List<BenchmarkHato> resultados = new ArrayList<>();

        for (KpiResultadoDTO kpi : kpis) {

            if (kpi.getValor() == null) continue;

            List<KpiHato> comparables = repositoryKpiHato.findKpiHatosComparables(
                kpi.getIdKpi(),
                idHato,
                hato.getDepartamento(),
                hato.getTropico()
            );

            if (comparables.isEmpty()) continue;

            float suma = 0f;
            float max  = Float.MIN_VALUE;

            for (KpiHato kh : comparables) {
                if (kh.getValor() == null) continue;
                suma += kh.getValor();
                if (kh.getValor() > max) max = kh.getValor();
            }

            float promedio = suma / comparables.size();
            float top      = max;
            float rango    = top - promedio;

            float percentil = (rango == 0)
                ? 0
                : ((kpi.getValor() - promedio) / rango) * 100;

            String interpretacion;
            if (kpi.getValor() < promedio * 0.7f) {
                interpretacion = "CRITICO";
            } else if (kpi.getValor() < promedio) {
                interpretacion = "ACEPTABLE";
            } else if (kpi.getValor() <= top) {
                interpretacion = "BUENO";
            } else {
                interpretacion = "OPTIMO";
            }

            Kpi kpiRef = serviceKpi.getKpiById(kpi.getIdKpi());

            BenchmarkHato bench = BenchmarkHato.builder()
                .hato(hato)
                .benchReferencia(null)
                .kpi(kpiRef)
                .percentil(percentil)
                .interpretacion(interpretacion)
                .valorHato(kpi.getValor())
                .fechaCalculo(LocalDate.now())
                .nivelBenchmark("PLATAFORMA")
                .build();

            resultados.add(bench);
        }

        repoBenchHato.saveAll(resultados);
        return resultados;
    }
    
    @Override
    public List<BenchmarkHatoResultadoDTO> obtenerBenchHato(
            UUID idHato, String modo, String nivel, String categoria) {
        return repoBenchHato
            .findByHatoFiltrado(idHato, modo, nivel, categoria)
            .stream()
            .map(this::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public ComparativaHatosDTO getComparativaHatos(UUID idHato, String codigoKpi, int top) {
        Hato hato = serviceHato.findHatoById(idHato);

        KpiHato kpiActual = repositoryKpiHato
            .findUltimoByHatoYKpi(idHato, codigoKpi)
            .orElseThrow(() -> new RuntimeException("KPI_NO_ENCONTRADO"));

        List<KpiHato> topHatos = repositoryKpiHato.findTopHatosComparables(
            codigoKpi, idHato, hato.getTropico(), hato.getDepartamento(), top
        );

        List<ComparativaHatosDTO.HatoComparableItem> comparables = new ArrayList<>();
        for (int i = 0; i < topHatos.size(); i++) {
            comparables.add(ComparativaHatosDTO.HatoComparableItem.builder()
                .posicion(i + 1)
                .valor(topHatos.get(i).getValor())
                .etiqueta("Hato #" + (i + 1))
                .build());
        }

        return ComparativaHatosDTO.builder()
            .codigoKpi(codigoKpi)
            .nombreKpi(kpiActual.getKpi().getNombre())
            .unidadKpi(kpiActual.getKpi().getUnidad())
            .valorHatoActual(kpiActual.getValor())
            .nombreHatoActual(hato.getNombreHato())
            .comparables(comparables)
            .build();
    }

    @Override
    public BenchmarkGlobalDTO calcularBenchmarkGlobal(UUID idHato, boolean filtroTropico, boolean filtroEscala, boolean filtroRegion, int cantidad) {

        Hato hato = serviceHato.findHatoById(idHato);
        List<KpiResultadoDTO> kpis = serviceKpi.getKpisDelHato(idHato);

        String tropico = filtroTropico ? hato.getTropico()      : null;
        String escala  = filtroEscala  ? hato.getEscala()       : null;
        String region  = filtroRegion  ? hato.getDepartamento() : null;

        List<KpiResumenGlobalDTO> kpisResumen         = new ArrayList<>();
        List<HatoAnonimizadoDTO>  hatosEnMapa         = new ArrayList<>();
        List<String>              faltantes           = new ArrayList<>();
        int                       totalHatos          = 0;

        Map<UUID, String>    aliasMap              = new HashMap<>();
        Map<String, Integer> contadorPorGentilicio = new HashMap<>();

        // Acumular hatos únicos encontrados en el loop para el mapa
        Map<UUID, Hato> hatosEncontrados = new LinkedHashMap<>();

        for (KpiResultadoDTO kpi : kpis) {
            if (kpi.getValor() == null) continue;

            List<KpiHato> comparables = repositoryKpiHato.findHatosComparablesDinamico(
                kpi.getCodigo(), idHato, tropico, escala, region, cantidad
            );

            if (comparables.size() < 3) {
                faltantes.add(kpi.getNombre());
                continue;
            }

            totalHatos = Math.max(totalHatos, comparables.size());

            float suma = 0f;
            float max  = Float.MIN_VALUE;
            float min  = Float.MAX_VALUE;

            for (KpiHato kh : comparables) {
                if (kh.getValor() == null) continue;
                suma += kh.getValor();
                if (kh.getValor() > max) max = kh.getValor();
                if (kh.getValor() < min) min = kh.getValor();
            }

            float promedio = suma / comparables.size();
            float top      = max;

            float percentilEnGrupo = calcularPercentilEnGrupo(kpi.getValor(), comparables);

            String interpretacion;
            if (kpi.getValor() < promedio * 0.7f)  interpretacion = "CRITICO";
            else if (kpi.getValor() < promedio)     interpretacion = "ACEPTABLE";
            else if (kpi.getValor() <= top)         interpretacion = "BUENO";
            else                                    interpretacion = "OPTIMO";

            List<HatoValorDTO> ranking = new ArrayList<>();
            for (int i = 0; i < comparables.size(); i++) {
                KpiHato kh     = comparables.get(i);
                UUID    hatoId = kh.getHato().getIdHato();
                Hato    hatoC  = kh.getHato();

                // Generar alias (ANONIMIZAR) y acumular para el mapa
                aliasMap.computeIfAbsent(hatoId, id ->
                    generarAlias(hatoC, contadorPorGentilicio)
                );
                hatosEncontrados.putIfAbsent(hatoId, hatoC);

                ranking.add(HatoValorDTO.builder()
                    .alias(aliasMap.get(hatoId))
                    .valor(kh.getValor())
                    .posicion(i + 1)
                    .esMiHato(false)
                    .build());
            }

            long posicionMiHato = comparables.stream()
                .filter(kh -> kh.getValor() != null && kh.getValor() > kpi.getValor())
                .count() + 1;

            ranking.add((int) posicionMiHato - 1, HatoValorDTO.builder()
                .alias("Tu hato")
                .valor(kpi.getValor())
                .posicion((int) posicionMiHato)
                .esMiHato(true)
                .build());

            kpisResumen.add(KpiResumenGlobalDTO.builder()
                .codigoKpi(kpi.getCodigo())
                .nombreKpi(kpi.getNombre())
                .unidadKpi(kpi.getUnidad())
                .categoria(kpi.getCategoria())
                .valorHatoActual(kpi.getValor())
                .promedioGrupo(promedio)
                .topGrupo(top)
                .percentilEnGrupo(percentilEnGrupo)
                .totalHatosGrupo(comparables.size())
                .interpretacion(interpretacion)
                .rankingHatos(ranking)
                .build());
        }

        // Construir mapa desde hatos acumulados en el loop 
        Set<UUID> hatosAgregadosAlMapa = new HashSet<>();

        for (Map.Entry<UUID, Hato> entry : hatosEncontrados.entrySet()) {
            UUID hatoId = entry.getKey();
            Hato hatoC  = entry.getValue();

            if (hatosAgregadosAlMapa.contains(hatoId)) continue;
            if (hatoC.getLatitud()  == null) continue;
            if (hatoC.getLongitud() == null) continue;

            String alias = aliasMap.getOrDefault(hatoId,
                generarAlias(hatoC, contadorPorGentilicio)
            );

            hatosEnMapa.add(HatoAnonimizadoDTO.builder()
                .alias(alias)
                .latitudDifuminada(difuminar(hatoC.getLatitud()))
                .longitudDifuminada(difuminar(hatoC.getLongitud()))
                .tropico(hatoC.getTropico())
                .escala(hatoC.getEscala())
                .departamento(hatoC.getDepartamento())
                .valorKpiPrincipal(null)
                .interpretacion("BUENO")
                .esMiHato(false)
                .build());

            hatosAgregadosAlMapa.add(hatoId);
        }

        // Agregar mi hato al mapa 
        hatosEnMapa.add(HatoAnonimizadoDTO.builder()
            .alias("Tu hato")
            .latitudDifuminada(hato.getLatitud())
            .longitudDifuminada(hato.getLongitud())
            .tropico(hato.getTropico())
            .escala(hato.getEscala())
            .departamento(hato.getDepartamento())
            .valorKpiPrincipal(null)
            .interpretacion(null)
            .esMiHato(true)
            .build());

        // Mensaje de filtros faltantes
        String mensajeFaltante = null;
        if (!faltantes.isEmpty()) {
            List<String> caracteristicas = new ArrayList<>();
            if (filtroTropico) caracteristicas.add("trópico " + hato.getTropico());
            if (filtroEscala)  caracteristicas.add("escala " + hato.getEscala());
            if (filtroRegion)  caracteristicas.add("región " + hato.getDepartamento());
            mensajeFaltante = "No hay suficientes hatos con " +
                String.join(", ", caracteristicas) +
                " para comparar " + faltantes.size() + " KPI(s). " +
                "Prueba desactivando algún filtro.";
        }

        return BenchmarkGlobalDTO.builder()
            .kpisResumen(kpisResumen)
            .hatosEnMapa(hatosEnMapa)
            .filtrosAplicados(FiltrosAplicadosDTO.builder()
                .tropico(tropico)
                .escala(escala)
                .region(region)
                .cantidad(cantidad)
                .filtroTropicoActivo(filtroTropico)
                .filtroEscalaActivo(filtroEscala)
                .filtroRegionActivo(filtroRegion)
                .build())
            .totalHatosEncontrados(totalHatos)
            .datosInsuficientes(totalHatos < 3)
            .mensajeFaltante(mensajeFaltante)
            .build();
    }
}
