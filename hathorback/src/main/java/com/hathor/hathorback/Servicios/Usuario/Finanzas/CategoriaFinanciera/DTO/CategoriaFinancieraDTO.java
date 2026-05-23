package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaFinancieraDTO {
    private UUID idCategoria;
    private String nombre;
    private String descripcion;
    private String tipo;
}