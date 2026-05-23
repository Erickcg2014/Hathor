package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Service;

import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CategoriaFinancieraDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CategoriasAgrupadasDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CrearCategoriaPersonalizadaDTO;

public interface IServiceCategoriaFinanciera {
    public CategoriaFinanciera createCategoriaFinanciera(CategoriaFinanciera categoriaFinanciera);
    public CategoriaFinanciera getCategoriaFinancieraById(UUID id);
    CategoriaFinanciera getCategoriaByNombre(String nombre);
    List<CategoriaFinanciera> getCategorias();
    //devuelve categorías de primer nivel agrupadas por tipo
    CategoriasAgrupadasDTO getCategoriasAgrupadas();
    CategoriaFinancieraDTO crearCategoriaPersonalizada(CrearCategoriaPersonalizadaDTO dto, String email);
    public List<CategoriaFinanciera> getCategoriasAllAndMine(String emailUsuario);
}
