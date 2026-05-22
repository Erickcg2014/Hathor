package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearCategoriaPersonalizadaDTO {
    private String nombre;
    private String tipo;
    private UUID idCategoriaPadre; 
}