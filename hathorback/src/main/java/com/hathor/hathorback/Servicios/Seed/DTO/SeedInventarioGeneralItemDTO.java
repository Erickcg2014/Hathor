package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;

@Data
public class SeedInventarioGeneralItemDTO {
    private String  nombre;
    private String  nombreCategoria;
    private Integer cantidad;
    private Float   valorUnitario;
    private String  descripcion;
}