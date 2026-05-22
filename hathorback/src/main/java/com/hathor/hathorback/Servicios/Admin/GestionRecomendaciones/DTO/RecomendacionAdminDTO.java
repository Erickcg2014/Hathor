package com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionAdminDTO {
    private Integer idRecomendacion;
    private String  idHato;
    private String  nombreHato;
    private String  tipo;
    private String  mensaje;
    private String  indicador;
    private Float   valorActual;
    private Float   valorReferencia;
    private Boolean leida;
    private String  prioridad;
    private String  tipoEstado;
    private String  fechaCreacion;
    private String  escalaHato;
    private String  tropicoHato;
    private String  regionHato;
    // Regla que la originó
    private Integer idRegla;
    private String  codigoKpiRegla;
    private String  nombreKpiRegla;
}