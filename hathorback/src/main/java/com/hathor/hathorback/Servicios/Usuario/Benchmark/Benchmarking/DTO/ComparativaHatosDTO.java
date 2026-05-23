package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparativaHatosDTO {

    private String codigoKpi;
    private String nombreKpi;
    private String unidadKpi;
    private Float  valorHatoActual;
    private String nombreHatoActual;
    private List<HatoComparableItem> comparables;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HatoComparableItem {
        private Integer posicion;
        private Float   valor;
        private String  etiqueta;
    }
}