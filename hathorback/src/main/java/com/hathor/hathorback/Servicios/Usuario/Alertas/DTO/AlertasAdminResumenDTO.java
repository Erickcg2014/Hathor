package com.hathor.hathorback.Servicios.Usuario.Alertas.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertasAdminResumenDTO {
    // Hatos distintos con alertas críticas - PARA ADMINISTRADOR
    private long                 hatosCriticos;
    private List<AlertaHatoDTO>  alertasCriticas;
}