package com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProyeccionesResponseDTO {

    // Proyecciones mes a mes
    private List<ProyeccionMensualDTO> proyecciones;
    private int mesesHistorial;
    // Modelos usados
    private List<String> modelosAplicados;
    private boolean datosInsuficientes;
    private String mensajeInsuficiente;
    private Double promedioIngresosMensual;
    private Double promedioEgresosMensual;
    private Double tendenciaIngresos; 
    private Double tendenciaEgresos;
}