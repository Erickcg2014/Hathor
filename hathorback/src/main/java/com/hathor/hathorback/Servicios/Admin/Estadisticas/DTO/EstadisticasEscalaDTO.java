package com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasEscalaDTO {
    private String escala;
    private long   totalHatos;
    private double porcentajeDelTotal;
    // Promedio de KPI productividad principal por escala
    private Double promedioLitrosVacaDia;
    private Double promedioMargenNeto;
}