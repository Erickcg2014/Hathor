package com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.DTO;

import lombok.*;
import java.util.List;

import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO.InversionResumenDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProyeccionMensualDTO {
    private String periodo;        
    private String periodoLabel;   

    private Double ingresoMin;
    private Double ingresoMax;
    private Double ingresoProyectado;

    private Double egresoMin;
    private Double egresoMax;
    private Double egresoProyectado;

    // Margen neto 
    private Double margenMin;
    private Double margenMax;
    private Double margenProyectado;

    private String estadoMargen;

    // Alertas contextuales del mes
    private List<String> alertas;

    private List<InversionResumenDTO> inversionesDelMes;
    private List<InversionResumenDTO> retornosDelMes;
    private boolean tieneInversion;
    private boolean tieneRetorno;
}