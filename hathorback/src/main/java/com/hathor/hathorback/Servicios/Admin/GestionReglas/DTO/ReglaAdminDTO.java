package com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReglaAdminDTO {
    private Integer idRegla;
    private Integer idKpi;
    private String  codigoKpi;
    private String  nombreKpi;
    private String  operador;
    private Double  umbral1;
    private Double  umbral2;
    private String  umbralTipo;
    private String  estadoKpiObjetivo;
    private String  escalaAplicable;
    private String  estado;
    private String  mensaje;
    private Integer prioridad;
    // Prácticas vinculadas ordenadas
    private List<ReglaPracticaDTO> practicas;
}