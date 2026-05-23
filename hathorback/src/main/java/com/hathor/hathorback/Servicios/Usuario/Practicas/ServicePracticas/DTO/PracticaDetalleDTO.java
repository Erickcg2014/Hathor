package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PracticaDetalleDTO {

    private Integer idPractica;

    private String nombre;

    private String descripcion;

    private String objetivo;

    // PRODUCTIVIDAD | HATO | FINANCIERO | EFICIENCIA
    private String categoria;

    private String impactoEsperado;

    private List<String> pasos;


    private String kpiImpactado;

    // BAJA | MEDIA | ALTA
    private String dificultad;

    private Integer duracionDias;

    // PEQUEÑA | MEDIANA | GRANDE | EMPRESARIAL | TODAS
    private String escala;

    // FRIO | TEMPLADO | CALIDO | TODOS
    private String tropicaAplicable;
}