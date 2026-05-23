package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO;

import lombok.Data;

@Data
public class ActualizarInventarioGanadoDTO {
    private Integer cantidad;
    private Integer edadPromedioMeses;
    private Float valorUnitario;
}