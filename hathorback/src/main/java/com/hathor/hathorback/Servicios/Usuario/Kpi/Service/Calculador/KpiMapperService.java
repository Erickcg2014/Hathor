package com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;
import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Entities.Kpi.KpiHato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Service.IServiceBenchmarkReferencia;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.DetalleCalculoItem;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class KpiMapperService {

    @Autowired
    private IServiceBenchmarkReferencia benchmarkService;

    // MAPA DE CATEGORÍAS 
    public static final Map<String, String> CATEGORIA_MAP = Map.ofEntries(
        Map.entry("KPI_LITROS_VACA_DIA",       "PRODUCTIVIDAD"),
        Map.entry("KPI_LITROS_HA_ANIO",         "PRODUCTIVIDAD"),
        Map.entry("KPI_PRODUCCION_HA_DIA",      "PRODUCTIVIDAD"),
        Map.entry("KPI_CAP_ALMAC_UTILIZADA",    "PRODUCTIVIDAD"),
        Map.entry("KPI_LACTANCIA_VS_ESTANDAR",  "PRODUCTIVIDAD"),
        Map.entry("KPI_FRECUENCIA_ORDENIO",     "PRODUCTIVIDAD"),
        Map.entry("KPI_CARGA_ANIMAL",           "HATO"),
        Map.entry("KPI_PCT_VACAS_ORDENIO",      "HATO"),
        Map.entry("KPI_HEMBRAS_RECRIA_VACA",    "HATO"),
        Map.entry("KPI_MARGEN_NETO",            "FINANCIERO"),
        Map.entry("KPI_MARGEN_BRUTO_PCT",       "FINANCIERO"),
        Map.entry("KPI_RATIO_INGRESO_EGRESO",   "FINANCIERO"),
        Map.entry("KPI_INGRESO_VACA",           "FINANCIERO"),
        Map.entry("KPI_INGRESO_LITRO",          "FINANCIERO"),
        Map.entry("KPI_ROA",                    "FINANCIERO"),
        Map.entry("KPI_ROTACION_ACTIVOS",       "FINANCIERO"),
        Map.entry("KPI_COSTO_LITRO",            "FINANCIERO"),
        Map.entry("KPI_BALANCE_NETO",           "FINANCIERO"),
        Map.entry("KPI_INGRESO_HA_ANIO",        "FINANCIERO"),
        Map.entry("KPI_IOFC",                   "FINANCIERO"),
        Map.entry("KPI_COSTO_LABORAL_PCT",      "EFICIENCIA"),
        Map.entry("KPI_BREAKEVEN_LITRO",        "FINANCIERO"),
        Map.entry("KPI_EMPLEADOS_HA",           "EFICIENCIA"),
        Map.entry("KPI_LITROS_EMPLEADO",        "EFICIENCIA")
    );

    // KPIs donde MENOR es mejor 
    private static final Set<String> MENOR_ES_MEJOR = Set.of(
        "KPI_COSTO_LITRO",
        "KPI_EMPLEADOS_HA",
        "KPI_COSTO_LABORAL_PCT",
        "KPI_BREAKEVEN_LITRO"
    );

    // KPIs sin benchmark comparativo
    private static final Set<String> SIN_BENCHMARK_COMPARATIVO = Set.of(
        "KPI_BALANCE_NETO",
        "KPI_FRECUENCIA_ORDENIO"
        );

    // CALCULAR ESTADO 
    public String calcularEstado(String codigo, Float valor,
                              Float benchPromedio, Float benchTop) {
    if (valor == null) return "SIN_DATOS";

    // Caso especial: lactancia 
    if ("KPI_LACTANCIA_VS_ESTANDAR".equals(codigo)) {
        if (valor < -30f) return "CRITICO";
        if (valor <= 0f)  return "ACEPTABLE";
        if (valor <= 30f) return "OPTIMO";
        return "ACEPTABLE"; 
    }

    // KPIs sin benchmark comparativo
    if (SIN_BENCHMARK_COMPARATIVO.contains(codigo)) return "ACEPTABLE";

    // Sin benchmark en BD
    if (benchPromedio == null || benchTop == null) return "SIN_DATOS";

    if (MENOR_ES_MEJOR.contains(codigo)) {
        if (valor <= benchTop)             return "OPTIMO";
        if (valor <= benchPromedio * 1.1f) return "ACEPTABLE";
        return "CRITICO";
    }

    if (valor >= benchTop)             return "OPTIMO";
    if (valor >= benchPromedio * 0.7f) return "ACEPTABLE";
    return "CRITICO";
}

    // TO DTO — consulta BD para benchmark usando trópico del hato
    public KpiResultadoDTO toDTO(KpiHato kh, Map<String, String> razones, String tropico, Map<String, List<DetalleCalculoItem>> detalles) {
        Kpi kpi = kh.getKpi();
        String codigo = kpi.getCodigo();

        Float benchPromedio = null;
        Float benchTop = null;
        Float diferenciaPct = null;

        Optional<BenchmarkReferencia> benchmark =
            benchmarkService.getBenchmark(codigo, tropico);

        if (benchmark.isPresent()) {
            benchPromedio = benchmark.get().getValorPromedio();
            benchTop = benchmark.get().getValorTop();

            if (kh.getValor() != null && benchPromedio != null && benchPromedio > 0)
                diferenciaPct = ((kh.getValor() - benchPromedio) / benchPromedio) * 100;
        }

        String estado = calcularEstado(codigo, kh.getValor(), benchPromedio, benchTop);

        // Razón sin datos
        String razonSinDatos = null;
        if ("SIN_DATOS".equals(estado)) {
            razonSinDatos = razones.getOrDefault(
                codigo, "Datos insuficientes para calcular este indicador"
            );
        }

        return KpiResultadoDTO.builder()
            .idKpi(kpi.getIdKpi())
            .codigo(codigo)
            .nombre(kpi.getNombre())
            .descripcion(kpi.getDescripcion())
            .formula(kpi.getFormula())
            .unidad(kpi.getUnidad())
            .categoria(CATEGORIA_MAP.getOrDefault(codigo, "OTRO"))
            .valor(kh.getValor())
            .estado(estado)
            .periodo(kh.getPeriodo())
            .fechaCalculo(kh.getFechaCalculo() != null
                ? kh.getFechaCalculo().toString() : null)
            .benchmarkPromedio(benchPromedio)
            .benchmarkTop(benchTop)
            .diferenciaPct(diferenciaPct)
            .razonSinDatos(razonSinDatos)
            .detalleCalculo(detalles.getOrDefault(codigo, List.of()))
            .build();
    }
}