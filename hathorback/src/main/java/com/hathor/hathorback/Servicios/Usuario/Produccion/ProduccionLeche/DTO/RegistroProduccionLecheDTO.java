package com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegistroProduccionLecheDTO {
    private UUID idHato;
    private LocalDate fecha;
    private Float litrosProducidos;
    private Integer vacasOrdenadas;
}