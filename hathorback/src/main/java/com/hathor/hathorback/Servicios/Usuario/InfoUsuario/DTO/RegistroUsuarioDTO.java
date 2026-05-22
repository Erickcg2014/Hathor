package com.hathor.hathorback.Servicios.Usuario.InfoUsuario.DTO;

import java.util.UUID;
import lombok.Data;

@Data
public class RegistroUsuarioDTO {
    private UUID idAuth;
    private String nombre;
    private String apellido;
    private String correo;
    private String celular;
}