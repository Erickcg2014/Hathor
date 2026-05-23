package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PracticaIAResponseDTO {

    private PracticaGeneradaDTO practicaGenerada;

    private UUID idHatoPracticaCreada;

    private boolean pendienteConfirmacion;

    private String mensaje;
}