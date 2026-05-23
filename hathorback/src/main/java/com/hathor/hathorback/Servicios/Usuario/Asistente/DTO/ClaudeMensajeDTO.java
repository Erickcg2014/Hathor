package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaudeMensajeDTO {
    private String role;    
    private String content;
}