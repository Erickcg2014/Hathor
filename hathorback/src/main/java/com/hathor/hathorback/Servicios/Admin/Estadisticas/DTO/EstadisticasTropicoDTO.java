package com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasTropicoDTO {
    private String tropico;
    private long   totalHatos;
    private double porcentajeDelTotal;
    private Double promedioLitrosVacaDia;
    private Double promedioCargarAnimal;
}