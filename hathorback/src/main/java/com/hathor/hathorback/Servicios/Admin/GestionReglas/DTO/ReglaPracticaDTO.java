package com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReglaPracticaDTO {
    private Integer id;
    private Integer idPractica;
    private String  nombrePractica;
    private String  categoriaPractica;
    private String  dificultadPractica;
    private Short   orden;
}