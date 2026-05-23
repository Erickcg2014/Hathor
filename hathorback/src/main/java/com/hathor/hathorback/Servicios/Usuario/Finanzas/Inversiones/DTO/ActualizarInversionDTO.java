package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO;

import lombok.Data;

@Data
public class ActualizarInversionDTO {
    private String  descripcion;
    private Double  monto;
    private String  mesEjecucion;
    private Double  retornoEsperadoPct;
    private Integer mesesRetorno;
    private String idCategoria;
    // PLANEADA | EJECUTADA | CANCELADA
    private String  estado;
}