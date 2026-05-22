package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistorialDTO {

    private List<MensajeHistorialDTO> mensajes;
    private boolean tieneHistorial;
    private String conversationId;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MensajeHistorialDTO {
        private String role;       // "user" | "assistant"
        private String content;
    }
}