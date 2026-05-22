package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;

@Data
public class SeedInventarioGanadoItemDTO {
    private String  nombreCategoria;
    private String  nombreRaza;
    private Integer cantidad;
    private Integer edadPromedioMeses;
    private Float   valorUnitario;
}