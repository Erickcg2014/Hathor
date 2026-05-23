package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoAnonimizadoDTO {

    private String  alias;
    private Double  latitudDifuminada;
    private Double  longitudDifuminada;
    private String  tropico;
    private String  escala;
    private String  departamento;
    private Float   valorKpiPrincipal;
    private String  interpretacion;
    private boolean esMiHato;
}