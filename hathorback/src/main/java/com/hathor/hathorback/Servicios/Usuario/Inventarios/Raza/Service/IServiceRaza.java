package com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Service;

import java.util.List;

import com.hathor.hathorback.Entities.Inventarios.Raza;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.DTO.RegistroRazaDTO;

public interface IServiceRaza {
    public Raza create(RegistroRazaDTO dto);
    public List<Raza> findAll();
    public Raza findById(Integer id);
    public Raza update(Integer id, RegistroRazaDTO dto);
    public void delete(Integer id);
    List<String> getTiposRaza();
    List<Raza> getRazasPorTipo(String tipoRaza);
}
