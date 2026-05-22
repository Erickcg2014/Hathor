package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO;

import java.util.UUID;

import lombok.Data;

@Data
public class RegistroInventarioGeneralDTO {
    private UUID idHato;
    private Integer idCategoriaInventario;
    private String nombreItem;
    private Integer cantidad;
    private Float valorUnitario;
    private String descripcion;
}
