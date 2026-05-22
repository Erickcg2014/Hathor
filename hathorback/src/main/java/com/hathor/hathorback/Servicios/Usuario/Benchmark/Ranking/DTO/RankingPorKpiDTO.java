package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingPorKpiDTO {
    private String             codigoKpi;
    private String             nombreKpi;
    private String             unidadKpi;
    private String             categoria;
    private Float              valorMiHato;
    private Integer            posicionMiHato;
    private Integer            totalHatos;
    private String             regionFiltrada;    
    private List<HatoRankingItem> ranking;
}