package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Service;

import java.util.List;

import com.hathor.hathorback.Entities.Inventarios.CategoriaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.DTO.RegistroCategoriaGanadoDTO;

public interface IServiceCategoriaGanado {
    public CategoriaGanado create(RegistroCategoriaGanadoDTO dto);
    public List<CategoriaGanado> findAll();
    public CategoriaGanado findById(Integer id);
    public CategoriaGanado update(Integer id, RegistroCategoriaGanadoDTO dto);
    public void delete(Integer id);
}
