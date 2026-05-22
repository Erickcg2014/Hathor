package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoValorDTO {

    private String  alias;
    private Float   valor;
    private int     posicion;
    private boolean esMiHato;
}