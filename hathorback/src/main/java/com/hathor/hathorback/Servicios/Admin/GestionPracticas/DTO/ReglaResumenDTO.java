package com.hathor.hathorback.Servicios.Admin.GestionPracticas.DTO;

import lombok.*;

// Resumen de regla para mostrar dentro de una práctica
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReglaResumenDTO {
    private Integer idRegla;
    private String  codigoKpi;
    private String  nombreKpi;
    private String  operador;
    private String  escalaAplicable;
    private String  estado;
    private Short   orden;
}