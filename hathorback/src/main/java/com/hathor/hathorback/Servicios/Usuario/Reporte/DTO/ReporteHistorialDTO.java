package com.hathor.hathorback.Servicios.Usuario.Reporte.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteHistorialDTO {
    private Integer       idReporte;
    private String        tipo;           // MANUAL | MENSUAL | TRIMESTRAL
    private String        nombre;
    private String        fechaGeneracion;
    private String        periodoDesde;
    private String        periodoHasta;
    private String        urlArchivo;
    private Long          tamanioBytes;
    private String        estado;
    private String        tamanioFormateado;
    private ReporteConfigDTO configuracion;
}