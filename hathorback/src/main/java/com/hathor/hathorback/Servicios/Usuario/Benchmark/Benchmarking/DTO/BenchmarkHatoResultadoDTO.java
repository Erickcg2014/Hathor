package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class BenchmarkHatoResultadoDTO {

    private Integer idBenchmarkHato;
    private Float   percentil;
    private String  interpretacion;
    private String  nivelBenchmark;
    private Float   valorHato;
    private LocalDate fechaCalculo;

    private KpiDTO kpi;

    private BenchReferenciaDTO benchReferencia;

    @Data
    @Builder
    public static class KpiDTO {
        private Integer idKpi;
        private String  codigo;
        private String  nombre;
        private String  descripcion;
        private String  formula;
        private String  unidad;
        private String  categoria;
    }

    @Data
    @Builder
    public static class BenchReferenciaDTO {
        private Integer idBenchmark;
        private String  region;
        private Float   valorPromedio;
        private Float   valorTop;
        private Integer anio;
        private String  tropico;
        private String  sistemaOrdenio;
    }
}