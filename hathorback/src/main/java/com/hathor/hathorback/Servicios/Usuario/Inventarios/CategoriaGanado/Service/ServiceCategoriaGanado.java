package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Inventarios.CategoriaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.DTO.RegistroCategoriaGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Repository.IRepositoryCategoriaGanado;

@Service
public class ServiceCategoriaGanado implements IServiceCategoriaGanado {

    @Autowired
    IRepositoryCategoriaGanado repositoryCategoriaGanado;

    @Override
    public CategoriaGanado create(RegistroCategoriaGanadoDTO dto) {
        CategoriaGanado categoria = CategoriaGanado.builder()
                .nombreCategoria(dto.getNombreCategoria())
                .descripcion(dto.getDescripcion())
                .build();
        return repositoryCategoriaGanado.save(categoria);
    }

    @Override
    public List<CategoriaGanado> findAll() {
        return repositoryCategoriaGanado.findAll();
    }

    @Override
    public CategoriaGanado findById(Integer id) {
        return repositoryCategoriaGanado.findById(id)
                .orElseThrow(() -> new RuntimeException("CategoriaGanado no encontrada con id: " + id));
    }

    @Override
    public CategoriaGanado update(Integer id, RegistroCategoriaGanadoDTO dto) {
        CategoriaGanado categoria = findById(id);
        categoria.setNombreCategoria(dto.getNombreCategoria());
        categoria.setDescripcion(dto.getDescripcion());
        return repositoryCategoriaGanado.save(categoria);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        repositoryCategoriaGanado.deleteById(id);
    }
}
