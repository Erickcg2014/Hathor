package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Service;

import com.hathor.hathorback.Entities.Inventarios.InventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO.ActualizarInventarioGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO.RegistroInventarioGanadoDTO;

import java.util.List;
import java.util.UUID;

public interface IServiceInventarioGanado {
    InventarioGanado create(RegistroInventarioGanadoDTO dto);
    List<InventarioGanado> findAll();
    InventarioGanado findById(UUID id);
    List<InventarioGanado> findByHato(UUID idHato);
    InventarioGanado update(UUID id, RegistroInventarioGanadoDTO dto);
    InventarioGanado actualizarParcial(UUID id, ActualizarInventarioGanadoDTO dto);
    void delete(UUID id);
}