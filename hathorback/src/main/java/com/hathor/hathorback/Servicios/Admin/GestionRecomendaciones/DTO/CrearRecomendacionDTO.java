package com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.DTO;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearRecomendacionDTO {
    private UUID   idHato;
    private String tipo;
    private String mensaje;
    private String indicador;       
    private Float  valorActual;
    private Float  valorReferencia;
    private String prioridad;
    private Integer idRegla;
}