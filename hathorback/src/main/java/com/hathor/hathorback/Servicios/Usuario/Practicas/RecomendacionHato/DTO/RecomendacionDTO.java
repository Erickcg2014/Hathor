package com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RecomendacionDTO {

    private Integer idRecomendacion;

    private String mensaje;

    // ALTA | MEDIA | BAJA 
    private String prioridad;

    private String indicador;

    private Float valorActual;

    private Float valorReferencia;

    private LocalDate fechaCreacion;

    // ACTIVA | DESCARTADA | COMPLETADA
    private String tipoEstado;

    private Boolean leida;

    private String escalaHato;
    private String tropicoHato;

    private List<PracticaResumenDTO> practicas;

    @Data
    @Builder
    public static class PracticaResumenDTO {
        private Integer idHatoPractica;
        private String  nombre;
        private String  dificultad;
        private String  estado;       // PENDIENTE | EN_CURSO | COMPLETADA
        private Float   porcentajeAvance;
    }
}