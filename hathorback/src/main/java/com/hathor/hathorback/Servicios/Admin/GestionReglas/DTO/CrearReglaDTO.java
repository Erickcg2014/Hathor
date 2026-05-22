package com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearReglaDTO {
    private Integer idKpi;          
    private String  operador;       
    private Double  umbral1;
    private Double  umbral2;        
    private String  umbralTipo;     
    private String  estadoKpiObjetivo; 
    private String  escalaAplicable;   
    private String  mensaje;
    private Integer prioridad;
    // IDs de prácticas a vincular con su orden
    private List<VincularPracticaDTO> practicas;
}