package com.hathor.hathorback.Servicios.Usuario.Alertas.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertaHatoDTO {
    private Long    idAlerta;
    private String  tipo;
    private String  severidad;
    private String  titulo;
    private String  mensaje;
    private Boolean leida;
    private String  fechaCreacion;
    private String  fechaExpiracion;
    private String  codigoKpi;
    private Float   valorReferencia;
    private String  estado;
    private String  tiempoRelativo;
}