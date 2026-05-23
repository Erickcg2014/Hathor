package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {
    private UUID idHato;
    private String mensaje;
    // kpis | benchmarking | practicas | finanzas | general
    private String contextoActivo;
}