package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PuntoEvolucion {
    private String  fecha;         
    private Integer posicion;
    private Integer totalHatos;
    private Float   valorHato;
    private Float   percentilEnFecha;
}