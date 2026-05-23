package com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Inventarios.Raza;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.DTO.RegistroRazaDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Repository.IRepositoryRaza;

@Service
public class ServiceRaza implements IServiceRaza {

    @Autowired
    IRepositoryRaza repositoryRaza;

    @Override
    public Raza create(RegistroRazaDTO dto) {
        Raza raza = Raza.builder()
                .tipoRaza(dto.getTipoRaza())
                .nombre(dto.getNombre())
                .build();
        return repositoryRaza.save(raza);
    }

    @Override
    public List<Raza> findAll() {
        return repositoryRaza.findAll();
    }

    @Override
    public Raza findById(Integer id) {
        return repositoryRaza.findById(id)
                .orElseThrow(() -> new RuntimeException("Raza no encontrada con id: " + id));
    }

    @Override
    public Raza update(Integer id, RegistroRazaDTO dto) {
        Raza raza = findById(id);
        raza.setTipoRaza(dto.getTipoRaza());
        raza.setNombre(dto.getNombre());
        return repositoryRaza.save(raza);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        repositoryRaza.deleteById(id);
    }

    @Override
    public List<String> getTiposRaza() {
        return repositoryRaza.findDistinctTipoRaza();
    }

    @Override
    public List<Raza> getRazasPorTipo(String tipoRaza) {
        return repositoryRaza.findByTipoRaza(tipoRaza);
    }
}
