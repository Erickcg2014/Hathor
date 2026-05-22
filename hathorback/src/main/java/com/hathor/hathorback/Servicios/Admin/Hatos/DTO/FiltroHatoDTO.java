package com.hathor.hathorback.Servicios.Admin.Hatos.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiltroHatoDTO {
    private String departamento;
    private String region;      
    private String tropico;
    private String escala;
    private String tipoHato;
}