package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class HatoPracticaDTO {

    private UUID idHatoPractica;

    // PENDIENTE | EN_CURSO | COMPLETADA
    private String estado;

    private Float porcentajeAvance;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Integer idPractica;
    private String  nombrePractica;
    private String  categoria;
    private String  dificultad;
    private String  kpiImpactado;
    private Integer duracionDias;

    private Integer idRecomendacion;
}