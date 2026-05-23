package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InversionResumenDTO {
    private Long   idInversion;
    private String descripcion;
    private Double monto;
    private String nombreCategoria;
    private String icono;
}