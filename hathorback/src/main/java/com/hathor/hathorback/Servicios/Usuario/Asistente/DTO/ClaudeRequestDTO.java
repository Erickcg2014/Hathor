package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaudeRequestDTO {
    private String model;
    private int maxTokens;
    private String system;
    private List<ClaudeMensajeDTO> messages;
    private double temperature;
}