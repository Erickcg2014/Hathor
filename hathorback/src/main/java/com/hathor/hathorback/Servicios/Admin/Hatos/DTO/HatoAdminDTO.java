package com.hathor.hathorback.Servicios.Admin.Hatos.DTO;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoAdminDTO {
    private UUID   idHato;
    private String nombreHato;
    private String departamento;
    private String ciudad;
    private String tropico;
    private String escala;
    private String tipoHato;
    private Float areaHato;
    private Float  altitud;
    private Integer porcentajeCompletitud;
    // Info del usuario dueño
    private String nombreUsuario;
    private String apellidoUsuario;
    private String correoUsuario;
    private Double latitud;
    private Double longitud;
}