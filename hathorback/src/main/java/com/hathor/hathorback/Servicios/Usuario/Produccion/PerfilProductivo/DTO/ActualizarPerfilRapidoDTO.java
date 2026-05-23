package com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO;

import lombok.Data;

@Data
public class ActualizarPerfilRapidoDTO {
    private Integer vacasEnOrdenio;
    private Double produccionDiariaLitros;
    private Double precioLitroPromedio;
    private Integer periodoLactanciaPromedio;
    private Integer frecuenciaOrdenio;
}