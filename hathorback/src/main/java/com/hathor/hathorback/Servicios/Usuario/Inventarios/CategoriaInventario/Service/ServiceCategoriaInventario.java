package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Inventarios.CategoriaInventario;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.DTO.RegistroCategoriaInventarioDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Repository.IRepositoryCategoriaInventario;

@Service
public class ServiceCategoriaInventario implements IServiceCategoriaInventario {

    @Autowired
    IRepositoryCategoriaInventario repositoryCategoriaInventario;

    @Override
    public CategoriaInventario create(RegistroCategoriaInventarioDTO dto) {
        CategoriaInventario categoria = CategoriaInventario.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
        return repositoryCategoriaInventario.save(categoria);
    }

    @Override
    public List<CategoriaInventario> findAll() {
        return repositoryCategoriaInventario.findAll();
    }

    @Override
    public CategoriaInventario findById(Integer id) {
        return repositoryCategoriaInventario.findById(id)
                .orElseThrow(() -> new RuntimeException("CategoriaInventario no encontrada con id: " + id));
    }

    @Override
    public CategoriaInventario update(Integer id, RegistroCategoriaInventarioDTO dto) {
        CategoriaInventario categoria = findById(id);
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return repositoryCategoriaInventario.save(categoria);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        repositoryCategoriaInventario.deleteById(id);
    }

    @Override
    public List<CategoriaInventario> getCategoriasParent() {
        return repositoryCategoriaInventario.findCategoriasParent();
    }

    @Override
    public List<CategoriaInventario> getSubcategorias(Integer idPadre) {
        return repositoryCategoriaInventario
            .findByCategoriaPadre_IdCategoriaInventarioAndActivaTrue(idPadre);
    }
}
