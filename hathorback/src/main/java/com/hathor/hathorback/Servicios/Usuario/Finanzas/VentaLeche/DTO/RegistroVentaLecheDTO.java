package com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegistroVentaLecheDTO {
    private UUID      idHato;
    private LocalDate fecha;
    private float     precioLitro;
    private float     litrosVendidos;
}