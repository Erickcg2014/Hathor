package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoRankingItem {
    private Integer posicion;
    private String  alias;
    private Float   valor;      
    private Boolean esMiHato;
}