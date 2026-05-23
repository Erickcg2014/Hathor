package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionGeneralDTO {
    private Long    idRecomendacion;
    private String  tipo;
    private String  subtipo;
    private String  titulo;
    private String  mensaje;
    private String  prioridad;
    private String  estado;
    private Boolean leida;
    private String  fechaCreacion;
    private String  fechaExpiracion;
    private String  icono;
    private String  urlAccion;
    private String  labelAccion;
    private Boolean esGlobal;
    private String  tiempoRelativo;
}