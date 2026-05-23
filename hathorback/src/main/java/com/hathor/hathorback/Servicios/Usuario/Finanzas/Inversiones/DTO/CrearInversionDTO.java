package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO;

import lombok.Data;

@Data
public class CrearInversionDTO {
    private String  descripcion;
    private Double  monto;
    private String  mesEjecucion;
    private Double  retornoEsperadoPct;
    private Integer mesesRetorno;
    private String idCategoria;
}