package com.hathor.hathorback.Servicios.Usuario.Kpi.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleCalculoItem {
    private String variable;   
    private Double valor;      
    private String unidad;     
    private String tipo;       
}