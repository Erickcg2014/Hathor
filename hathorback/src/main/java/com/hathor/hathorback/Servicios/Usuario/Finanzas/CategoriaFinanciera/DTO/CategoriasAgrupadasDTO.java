package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriasAgrupadasDTO {
    private List<CategoriaFinancieraDTO> ingresos;
    private List<CategoriaFinancieraDTO> gastos;
    private List<CategoriaFinancieraDTO> inversiones;
}