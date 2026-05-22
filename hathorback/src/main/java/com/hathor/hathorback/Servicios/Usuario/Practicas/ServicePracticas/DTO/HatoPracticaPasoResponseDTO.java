package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoPracticaPasoResponseDTO {
    private List<PasoDTO>   pasos;
    private Float           porcentajeAvance;
    private String          estado;
}