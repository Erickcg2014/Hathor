package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO;

import lombok.Data;

@Data
public class ActualizarInventarioGeneralDTO {
    private String nombreItem;
    private Integer cantidad;
    private Float valorUnitario;
    private String descripcion;
}