package com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO;

import lombok.*;

// DTO genérico para pares clave-valor de estadísticas
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntradaEstadisticaDTO {
    private String clave;
    private Long   cantidad;
    private Double porcentaje; 
}