package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;

@Data
public class SeedPerfilProductivoDTO {
    private Integer vacasEnOrdenio;
    private Integer totalVacas;
    private Double  produccionDiariaLitros;
    private Double  precioLitroPromedio;
    private Integer diasLactancia;
    private Integer frecuenciaOrdenio;
    private String  destinoLeche;
    private String  razaPredominante;
    private String  sistemaOrdenio;
}