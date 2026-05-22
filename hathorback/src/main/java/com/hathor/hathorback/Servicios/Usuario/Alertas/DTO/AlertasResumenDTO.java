package com.hathor.hathorback.Servicios.Usuario.Alertas.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertasResumenDTO {
    private long   totalNoLeidas;
    private String severidadMaxima;
    private List<AlertaHatoDTO> criticas;
    private List<AlertaHatoDTO> preventivas;
    private List<AlertaHatoDTO> oportunidades;
}