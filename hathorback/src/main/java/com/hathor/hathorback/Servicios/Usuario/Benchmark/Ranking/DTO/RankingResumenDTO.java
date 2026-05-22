package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingResumenDTO {
    private Integer posicionNacional;
    private Integer posicionRegional;
    private Integer totalHatosNacional;
    private Integer totalHatosRegional;
    private Float   scoreCompuesto;      
    private Integer kpisCriticos;
    private Integer kpisAceptables;
    private Integer kpisBuenos;
    private Integer kpisOptimos;
    private String  fechaUltimoCalculo;
}