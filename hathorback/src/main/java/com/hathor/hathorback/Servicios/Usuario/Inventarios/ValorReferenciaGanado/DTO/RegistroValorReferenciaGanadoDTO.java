package com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.DTO;

import lombok.Data;

@Data
public class RegistroValorReferenciaGanadoDTO {
    private Integer idRaza;
    private Integer idCategoria;
    private Float valorPromedio;
    private String region;
    private Integer anio;
}
