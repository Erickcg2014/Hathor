package com.hathor.hathorback.Servicios.Admin.GestionPracticas.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PracticaAdminDTO {
    private Integer idPractica;
    private String  nombre;
    private String  descripcion;
    private String  objetivo;
    private String  categoria;
    private String  impactoEsperado;
    private String  estado;
    private String  pasos;
    private String  kpiImpactado;
    private String  dificultad;
    private Integer duracionDias;
    private String  escala;
    private String  tropicaAplicable;
    private List<ReglaResumenDTO> reglasVinculadas;
}