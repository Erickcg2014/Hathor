package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionesResumenDTO {
    private long                         totalNoLeidas;
    private List<RecomendacionGeneralDTO> altas;
    private List<RecomendacionGeneralDTO> medias;
    private List<RecomendacionGeneralDTO> bajas;
}