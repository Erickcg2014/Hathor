package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO;

import java.util.UUID;
import lombok.Data;

@Data
public class RegistroInventarioGanadoDTO {
    private UUID idHato;
    private Integer idRaza;
    private Integer idCategoria;
    private Integer cantidad;
    private Integer edadPromedioMeses;
    private Float valorUnitario;
}