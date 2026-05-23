package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Service;

import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Entities.Inventarios.InventarioGeneral;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO.ActualizarInventarioGeneralDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO.RegistroInventarioGeneralDTO;

public interface IServiceInventarioGeneral {
    public InventarioGeneral create(RegistroInventarioGeneralDTO dto);
    public List<InventarioGeneral> findAll();
    public InventarioGeneral findById(UUID id);
    public List<InventarioGeneral> findByHato(UUID idHato);
    public InventarioGeneral update(UUID id, RegistroInventarioGeneralDTO dto);
    public void delete(UUID id);
    InventarioGeneral actualizarParcial(UUID id, ActualizarInventarioGeneralDTO dto);
}
