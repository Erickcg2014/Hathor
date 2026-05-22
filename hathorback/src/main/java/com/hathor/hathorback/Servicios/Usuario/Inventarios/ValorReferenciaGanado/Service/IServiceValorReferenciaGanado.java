package com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.Service;

import java.util.List;
import java.util.Optional;

import com.hathor.hathorback.Entities.Inventarios.ValorReferenciaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.DTO.RegistroValorReferenciaGanadoDTO;

public interface IServiceValorReferenciaGanado {
    ValorReferenciaGanado create(RegistroValorReferenciaGanadoDTO dto);
    List<ValorReferenciaGanado> findAll();
    ValorReferenciaGanado findById(Integer id);
    ValorReferenciaGanado update(Integer id, RegistroValorReferenciaGanadoDTO dto);
    void delete(Integer id);
    Optional<ValorReferenciaGanado> findByRazaYCategoria(
        Integer idRaza, Integer idCategoria);  
}