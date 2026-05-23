package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PracticaGeneradaDTO {
    private String nombre;
    private String descripcion;
    private String objetivo;
    private String categoria;
    private String impactoEsperado;
    private List<String> pasos;
    private String kpiImpactado;
    private String dificultad;
    private Integer duracionDias;
    private String escala;
    private String tropicaAplicable;
}