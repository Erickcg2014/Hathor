package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingCompuestoDTO {
    private Integer            posicionMiHato;
    private Integer            totalHatos;
    private Float              scoreMiHato;       
    private Float              scorePromedio;     
    private Float              scoreTop;          
    private String             regionFiltrada;    
    private List<HatoRankingItem> ranking;
}