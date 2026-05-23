package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PracticaIARequestDTO {
    private UUID idHato;
    private String codigoKpi;
    private boolean confirmar;
}