package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CrearRecomendacionAdminDTO {
    private String    titulo;
    private String    mensaje;
    // ALTA | MEDIA | BAJA
    private String    prioridad;
    private String    icono;
    private String    urlAccion;
    private String    labelAccion;
    private UUID      idHato;
    private LocalDate fechaExpiracion;
}