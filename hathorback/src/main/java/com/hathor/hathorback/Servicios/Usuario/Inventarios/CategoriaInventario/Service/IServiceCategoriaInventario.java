package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Service;

import java.util.List;

import com.hathor.hathorback.Entities.Inventarios.CategoriaInventario;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.DTO.RegistroCategoriaInventarioDTO;

public interface IServiceCategoriaInventario {
    public CategoriaInventario create(RegistroCategoriaInventarioDTO dto);
    public List<CategoriaInventario> findAll();
    public CategoriaInventario findById(Integer id);
    public CategoriaInventario update(Integer id, RegistroCategoriaInventarioDTO dto);
    public void delete(Integer id);
    List<CategoriaInventario> getCategoriasParent();
    List<CategoriaInventario> getSubcategorias(Integer idPadre);
}
