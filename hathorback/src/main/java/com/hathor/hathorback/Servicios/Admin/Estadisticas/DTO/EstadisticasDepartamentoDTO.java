package com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasDepartamentoDTO {
    private String departamento;
    private long   totalHatos;
    private double promedioCompletitud;
    private long   hatosConKpis;
    // KPI más representativo del departamento
    private String kpiDestacado;
    private Double promedioKpiDestacado;
}