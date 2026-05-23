package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;
import com.hathor.hathorback.Entities.Benchmark.RankingHato;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Kpi.KpiHato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Repository.IRepositoryBenchmarking;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Repository.IRepositoryRankingHato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpiHato;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceRanking implements IServiceRanking {

    @Autowired private IServiceHato            serviceHato;
    @Autowired private IRepositoryKpiHato      repositoryKpiHato;
    @Autowired private IRepositoryRankingHato  repoRankingHato;
    @Autowired private IRepositoryBenchmarking repoBenchHato;

    private static final Map<String, String> GENTILICIOS = Map.ofEntries(
        Map.entry("Amazonas",                 "Amazónico"),
        Map.entry("Antioquia",                "Antioqueño"),
        Map.entry("Arauca",                   "Araucano"),
        Map.entry("Atlántico",                "Atlanticense"),
        Map.entry("Bolívar",                  "Bolivarense"),
        Map.entry("Boyacá",                   "Boyacense"),
        Map.entry("Caldas",                   "Caldense"),
        Map.entry("Caquetá",                  "Caqueteño"),
        Map.entry("Casanare",                 "Casanareño"),
        Map.entry("Cauca",                    "Caucano"),
        Map.entry("Cesar",                    "Cesarense"),
        Map.entry("Chocó",                    "Chocoano"),
        Map.entry("Córdoba",                  "Cordobés"),
        Map.entry("Cundinamarca",             "Cundinamarqués"),
        Map.entry("Guainía",                  "Guainiano"),
        Map.entry("Guaviare",                 "Guaviarense"),
        Map.entry("Huila",                    "Huilense"),
        Map.entry("La Guajira",               "Guajiro"),
        Map.entry("Magdalena",                "Magdalenense"),
        Map.entry("Meta",                     "Llanero"),
        Map.entry("Nariño",                   "Nariñense"),
        Map.entry("Norte de Santander",       "Nortesantandereano"),
        Map.entry("Putumayo",                 "Putumayense"),
        Map.entry("Quindío",                  "Quindiano"),
        Map.entry("Risaralda",                "Risaraldense"),
        Map.entry("San Andrés y Providencia", "Sanandresano"),
        Map.entry("Santander",                "Santandereano"),
        Map.entry("Sucre",                    "Sucreño"),
        Map.entry("Tolima",                   "Tolimense"),
        Map.entry("Valle del Cauca",          "Vallecaucano"),
        Map.entry("Vaupés",                   "Vaupesino"),
        Map.entry("Vichada",                  "Vichadeño")
    );

    private static final Set<String> MENOR_ES_MEJOR = Set.of(
        "KPI_COSTO_LITRO",
        "KPI_BREAKEVEN_LITRO",
        "KPI_EMPLEADOS_HA",
        "KPI_COSTO_LABORAL_PCT"
    );

    private String generarAlias(Hato hato, Map<String, Integer> contador) {
        String depto     = hato.getDepartamento() != null ? hato.getDepartamento() : "Colombia";
        String gentilicio = GENTILICIOS.getOrDefault(depto, "Ganadero");
        int numero = contador.merge(gentilicio, 1, Integer::sum);
        return "Hato " + gentilicio + " #" + numero;
    }

    // ── 1. Resumen  ──────
    @Override
    public RankingResumenDTO getResumenRanking(UUID idHato) {
        RankingHato ranking = repoRankingHato
            .findByHato_IdHato(idHato)
            .orElse(null);

        List<BenchmarkHato> benchmarks = repoBenchHato
            .findByHatoFiltrado(idHato, "REFERENCIA", "NACIONAL", null);

        float  scoreCompuesto     = ranking != null ? ranking.getScoreCompuesto() : 0f;
        int    kpisCriticos       = 0;
        int    kpisAceptables     = 0;
        int    kpisBuenos         = 0;
        int    kpisOptimos        = 0;
        String fechaUltimoCalculo = null;

        for (BenchmarkHato b : benchmarks) {
            if (b.getInterpretacion() != null) {
                switch (b.getInterpretacion()) {
                    case "CRITICO"   -> kpisCriticos++;
                    case "ACEPTABLE" -> kpisAceptables++;
                    case "BUENO"     -> kpisBuenos++;
                    case "OPTIMO"    -> kpisOptimos++;
                }
            }
            if (fechaUltimoCalculo == null && b.getFechaCalculo() != null)
                fechaUltimoCalculo = b.getFechaCalculo().toString();
        }

        return RankingResumenDTO.builder()
            .posicionNacional(ranking != null ? ranking.getPosicionNacional() : null)
            .posicionRegional(ranking != null ? ranking.getPosicionRegional() : null)
            .totalHatosNacional(ranking != null ? ranking.getTotalNacional()  : 0)
            .totalHatosRegional(ranking != null ? ranking.getTotalRegional()  : 0)
            .scoreCompuesto(scoreCompuesto)
            .kpisCriticos(kpisCriticos)
            .kpisAceptables(kpisAceptables)
            .kpisBuenos(kpisBuenos)
            .kpisOptimos(kpisOptimos)
            .fechaUltimoCalculo(fechaUltimoCalculo)
            .build();
    }

    // ── 2. Ranking compuesto ──────────────────────────────────────────────────
    @Override
    public RankingCompuestoDTO getRankingCompuesto(UUID idHato, String region) {

        RankingHato miRanking = repoRankingHato
            .findByHato_IdHato(idHato)
            .orElse(null);

        List<RankingHato> todosRankings = region != null
            ? repoRankingHato.findAllByDepartamentoOrderByPosicion(region)
            : repoRankingHato.findAllByOrderByPosicionNacionalAsc();

        Map<String, Integer> contador = new HashMap<>();
        List<HatoRankingItem> items   = new ArrayList<>();

        for (RankingHato r : todosRankings) {
            boolean esMiHato = r.getHato().getIdHato().equals(idHato);
            String  alias    = esMiHato
                ? "Tu hato"
                : generarAlias(r.getHato(), contador);

            int posicion = region != null
                ? (r.getPosicionRegional()  != null ? r.getPosicionRegional()  : 0)
                : (r.getPosicionNacional()  != null ? r.getPosicionNacional()  : 0);

            items.add(HatoRankingItem.builder()
                .posicion(posicion)
                .alias(alias)
                .valor(r.getScoreCompuesto() != null
                    ? r.getScoreCompuesto(): null)
                .esMiHato(esMiHato)
                .build());
        }

        boolean miHatoEnLista = todosRankings.stream()
            .anyMatch(r -> r.getHato().getIdHato().equals(idHato));

        float scoreMiHato = miRanking != null && miRanking.getScoreCompuesto() != null
            ? miRanking.getScoreCompuesto() : 0f;

        if (!miHatoEnLista) {
            items.add(HatoRankingItem.builder()
                .posicion(items.size() + 1)
                .alias("Tu hato")
                .valor(scoreMiHato)
                .esMiHato(true)
                .build());
        }

        int posicionMiHato = miRanking != null
            ? (region != null
                ? (miRanking.getPosicionRegional() != null ? miRanking.getPosicionRegional() : items.size())
                : (miRanking.getPosicionNacional() != null ? miRanking.getPosicionNacional() : items.size()))
            : items.size();

        int totalHatos = miRanking != null
            ? (region != null
                ? (miRanking.getTotalRegional()   != null ? miRanking.getTotalRegional()   : items.size())
                : (miRanking.getTotalNacional()   != null ? miRanking.getTotalNacional()   : items.size()))
            : items.size();

        float scorePromedio = (float) todosRankings.stream()
            .filter(r -> r.getScoreCompuesto() != null)
            .mapToDouble(RankingHato::getScoreCompuesto)
            .average().orElse(0.0);

        float scoreTop = (float) todosRankings.stream()
            .filter(r -> r.getScoreCompuesto() != null)
            .mapToDouble(RankingHato::getScoreCompuesto)
            .max().orElse(0.0);
        scoreTop = Math.max(scoreTop, scoreMiHato);

        return RankingCompuestoDTO.builder()
            .posicionMiHato(posicionMiHato)
            .totalHatos(totalHatos)
            .scoreMiHato(scoreMiHato)
            .scorePromedio(scorePromedio)
            .scoreTop(scoreTop)
            .regionFiltrada(region)
            .ranking(items)
            .build();
    }

    // ── 3. Ranking por KPI ────────────────────────────────────────────────────
    @Override
    public RankingPorKpiDTO getRankingPorKpi(UUID idHato, String codigoKpi, String region) {
        KpiHato miKpi = repositoryKpiHato
            .findUltimoByHatoYKpi(idHato, codigoKpi)
            .orElse(null);

        float  valorMiHato = miKpi != null && miKpi.getValor() != null ? miKpi.getValor() : 0f;
        String nombreKpi   = miKpi != null ? miKpi.getKpi().getNombre()    : codigoKpi;
        String unidadKpi   = miKpi != null ? miKpi.getKpi().getUnidad()    : "";
        String categoria   = miKpi != null ? miKpi.getKpi().getCategoria() : "";

        List<KpiHato> todos = repositoryKpiHato
            .findTodosHatosOrdenadosPorKpi(codigoKpi, idHato, region);

        Map<String, Integer> contador = new HashMap<>();
        List<HatoRankingItem> items   = new ArrayList<>();

        for (KpiHato kh : todos) {
            String alias = generarAlias(kh.getHato(), contador);
            items.add(HatoRankingItem.builder()
                .alias(alias)
                .valor(kh.getValor())
                .esMiHato(false)
                .build());
        }

        int posicionMiHato = (int) todos.stream()
            .filter(kh -> kh.getValor() != null && kh.getValor() > valorMiHato)
            .count() + 1;

        int insertIdx = Math.min(posicionMiHato - 1, items.size());
        items.add(insertIdx, HatoRankingItem.builder()
            .alias("Tu hato")
            .valor(valorMiHato)
            .esMiHato(true)
            .build());

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPosicion(i + 1);
        }

        return RankingPorKpiDTO.builder()
            .codigoKpi(codigoKpi)
            .nombreKpi(nombreKpi)
            .unidadKpi(unidadKpi)
            .categoria(categoria)
            .valorMiHato(valorMiHato)
            .posicionMiHato(posicionMiHato)
            .totalHatos(items.size())
            .regionFiltrada(region)
            .ranking(items)
            .build();
    }

    // ── 4. Evolución temporal ─────────────────────────────────────────────────
    @Override
    public EvolucionPosicionDTO getEvolucionPosicion(UUID idHato, String codigoKpi, int meses) {
        LocalDate fechaDesde = LocalDate.now().minusMonths(meses);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<KpiHato> historico = repositoryKpiHato
            .findHistoricoKpiHato(idHato, codigoKpi, fechaDesde);

        String nombreKpi = historico.isEmpty() ? codigoKpi : historico.get(0).getKpi().getNombre();
        String unidadKpi = historico.isEmpty() ? ""        : historico.get(0).getKpi().getUnidad();

        if (historico.size() < 2) {
            return EvolucionPosicionDTO.builder()
                .codigoKpi(codigoKpi)
                .nombreKpi(nombreKpi)
                .unidadKpi(unidadKpi)
                .mesesConsultados(meses)
                .puntos(Collections.emptyList())
                .datosInsuficientes(true)
                .build();
        }

        // ← sacar fuera del loop — una sola query
        List<KpiHato> otrosHatos = repositoryKpiHato
            .findTodosHatosOrdenadosPorKpi(codigoKpi, idHato, null);
        int totalHatos = otrosHatos.size() + 1;

        List<PuntoEvolucion> puntos = new ArrayList<>();

        for (KpiHato punto : historico) {
            if (punto.getValor() == null) continue;

            float     valorHato = punto.getValor();
            LocalDate fecha     = punto.getFechaCalculo();

            // ← usar la lista ya cargada en vez de hacer otra query
            int mayores  = (int) otrosHatos.stream()
                .filter(kh -> kh.getValor() != null && kh.getValor() > valorHato)
                .count();
            int posicion = mayores + 1;

            float percentil = totalHatos <= 1
                ? 50f
                : ((float)(totalHatos - posicion) / (totalHatos - 1)) * 100f;

            puntos.add(PuntoEvolucion.builder()
                .fecha(fecha.format(fmt))
                .posicion(posicion)
                .totalHatos(totalHatos)
                .valorHato(valorHato)
                .percentilEnFecha(percentil)
                .build());
        }

        return EvolucionPosicionDTO.builder()
            .codigoKpi(codigoKpi)
            .nombreKpi(nombreKpi)
            .unidadKpi(unidadKpi)
            .mesesConsultados(meses)
            .puntos(puntos)
            .datosInsuficientes(puntos.size() < 2)
            .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private Map<UUID, Float> calcularScoresPorHato(
            List<KpiHato> todos, UUID idHatoExcluir, String region) {

        if (todos.isEmpty()) return new LinkedHashMap<>();

        Map<String, List<Float>> valoresPorKpi = new HashMap<>();
        for (KpiHato kh : todos) {
            if (kh.getValor() == null) continue;
            valoresPorKpi
                .computeIfAbsent(kh.getKpi().getCodigo(), k -> new ArrayList<>())
                .add(kh.getValor());
        }

        Map<String, Float> minPorKpi = new HashMap<>();
        Map<String, Float> maxPorKpi = new HashMap<>();
        for (Map.Entry<String, List<Float>> e : valoresPorKpi.entrySet()) {
            minPorKpi.put(e.getKey(), e.getValue().stream().min(Float::compareTo).orElse(0f));
            maxPorKpi.put(e.getKey(), e.getValue().stream().max(Float::compareTo).orElse(1f));
        }

        Map<UUID, List<Float>> scoresPorHato = new LinkedHashMap<>();
        for (KpiHato kh : todos) {
            if (kh.getValor() == null) continue;

            String codigo = kh.getKpi().getCodigo();
            float  valor  = kh.getValor();
            float  min    = minPorKpi.getOrDefault(codigo, 0f);
            float  max    = maxPorKpi.getOrDefault(codigo, 1f);
            float  rango  = max - min;

            float normalizado;
            if (rango == 0f) {
                normalizado = 50f;
            } else if (MENOR_ES_MEJOR.contains(codigo)) {
                normalizado = ((max - valor) / rango) * 100f;
            } else {
                normalizado = ((valor - min) / rango) * 100f;
            }
            normalizado = Math.max(0f, Math.min(100f, normalizado));

            scoresPorHato
                .computeIfAbsent(kh.getHato().getIdHato(), k -> new ArrayList<>())
                .add(normalizado);
        }

        Map<UUID, Float> resultado = new LinkedHashMap<>();
        for (Map.Entry<UUID, List<Float>> e : scoresPorHato.entrySet()) {
            float promedio = (float) e.getValue().stream()
                .mapToDouble(Float::doubleValue).average().orElse(0.0);
            resultado.put(e.getKey(), promedio);
        }
        return resultado;
    }

    private int calcularPosicion(float scoreMiHato, Map<UUID, Float> scores) {
        return (int) scores.values().stream()
            .filter(s -> s > scoreMiHato)
            .count() + 1;
    }
}