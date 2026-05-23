package com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO;

import lombok.*;

// DTO para vincular una práctica a una regla con un orden
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VincularPracticaDTO {
    private Integer idPractica;
    private Short   orden;      
}