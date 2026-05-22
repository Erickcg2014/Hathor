package com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class RegistroPerfilProductivoDTO {
    private UUID idHato;
    private String razaPredominante;
    private double produccionDiariaLitros;
    private double precioLitroPromedio;
    private Integer vacasEnOrdenio;
    private Integer frecuenciaOrdenio;
    private String sistemaOrdenio;
    private String destinoLeche;
    private Integer periodoLactanciaPromedio;
}