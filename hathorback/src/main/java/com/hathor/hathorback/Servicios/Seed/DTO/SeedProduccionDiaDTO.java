package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;

@Data
public class SeedProduccionDiaDTO {
    private String fecha;
    private Float  litrosProducidos;
    private Integer vacasOrdenadas;
}