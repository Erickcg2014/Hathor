package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeedResultadoDTO {

    private boolean exitoso;
    private String  idHato;
    private String  nombreHato;
    private String  perfil;
    private String  mensaje;

    private int     registrosFinancieros;
    private int     diasProduccion;
    private int     itemsInventarioGanado;
    private int     itemsInventarioGeneral;
    private int     practicasAsignadas;

    // Errores si los hay
    private List<String> advertencias;
}