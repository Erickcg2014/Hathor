package com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasGlobalesDTO {
    private long   totalHatos;
    private long   hatosConKpis;
    private double promedioCompletitud;
    private long   totalDepartamentos;
    // Distribución general
    private List<EntradaEstadisticaDTO> hatosPorEscala;
    private List<EntradaEstadisticaDTO> hatosPorTropico;
    private List<EntradaEstadisticaDTO> hatosPorDepartamento;
}