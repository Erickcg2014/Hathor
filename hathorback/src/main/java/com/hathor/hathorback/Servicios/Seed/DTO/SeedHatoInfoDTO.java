package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;

@Data
public class SeedHatoInfoDTO {
    private String  nombreHato;
    private String  tipoHato;
    private String  departamento;
    private String  ciudad;
    private String  direccion;
    private String  tropico;
    private String  escala;
    private Double  areaHato;
    private Double  areaPastoreo;
    private Double  altitud;
    private Double  latitud;
    private Double  longitud;
    private Integer cantCorrales;
    private Integer cantSalasOrdenio;
    private Double  capacidadAlmacenarLeche;
    private Integer cantEmpleadosPermanentes;
    private Integer cantEmpleadosTemporales;
    private Double  gastoMensualNomina;
    private Double  gastoMensualAlimentacion;
}