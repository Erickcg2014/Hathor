package com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.Service;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Inventarios.ValorReferenciaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Service.IServiceCategoriaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Service.IServiceRaza;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.DTO.RegistroValorReferenciaGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.Repository.IRepositoryValorReferenciaGanado;

@Service
public class ServiceValorReferenciaGanado implements IServiceValorReferenciaGanado {

    @Autowired
    IRepositoryValorReferenciaGanado repositoryValorReferenciaGanado;

    @Autowired
    IServiceRaza serviceRaza;

    @Autowired
    IServiceCategoriaGanado serviceCategoriaGanado;

    @Override
    public ValorReferenciaGanado create(RegistroValorReferenciaGanadoDTO dto) {
        ValorReferenciaGanado valor = ValorReferenciaGanado.builder()
                .raza(serviceRaza.findById(dto.getIdRaza()))
                .categoriaGanado(serviceCategoriaGanado.findById(dto.getIdCategoria()))
                .valorPromedio(dto.getValorPromedio())
                .region(dto.getRegion())
                .anio(dto.getAnio())
                .build();
        return repositoryValorReferenciaGanado.save(valor);
    }

    @Override
    public List<ValorReferenciaGanado> findAll() {
        return repositoryValorReferenciaGanado.findAll();
    }

    @Override
    public ValorReferenciaGanado findById(Integer id) {
        return repositoryValorReferenciaGanado.findById(id)
                .orElseThrow(() -> new RuntimeException("ValorReferenciaGanado no encontrado con id: " + id));
    }

    @Override
    public ValorReferenciaGanado update(Integer id, RegistroValorReferenciaGanadoDTO dto) {
        ValorReferenciaGanado valor = findById(id);
        valor.setRaza(serviceRaza.findById(dto.getIdRaza()));
        valor.setCategoriaGanado(serviceCategoriaGanado.findById(dto.getIdCategoria()));
        valor.setValorPromedio(dto.getValorPromedio());
        valor.setRegion(dto.getRegion());
        valor.setAnio(dto.getAnio());
        return repositoryValorReferenciaGanado.save(valor);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        repositoryValorReferenciaGanado.deleteById(id);
    }

    @Override
    public Optional<ValorReferenciaGanado> findByRazaYCategoria(
            Integer idRaza, Integer idCategoria) {
        return repositoryValorReferenciaGanado
            .findByRaza_IdRazaAndCategoriaGanado_IdCategoria(
                idRaza, idCategoria);
    }
}
