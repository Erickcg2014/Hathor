package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO {
    private String respuesta;
    private boolean historialLargo;
    private int tokensEstimados;
    private String conversationId;
}