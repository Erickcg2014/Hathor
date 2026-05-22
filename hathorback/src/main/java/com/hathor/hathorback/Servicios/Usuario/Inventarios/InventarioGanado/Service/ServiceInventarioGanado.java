package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Inventarios.InventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Service.IServiceCategoriaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO.ActualizarInventarioGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO.RegistroInventarioGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Repository.IRepositoryInventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Service.IServiceRaza;

@Service
public class ServiceInventarioGanado implements IServiceInventarioGanado {

    @Autowired
    IRepositoryInventarioGanado repositoryInventarioGanado;

    @Autowired
    IRepositoryHato repositoryHato;

    @Autowired
    IServiceRaza serviceRaza;

    @Autowired
    IServiceCategoriaGanado serviceCategoriaGanado;

    @Override
    public InventarioGanado create(RegistroInventarioGanadoDTO dto) {
        InventarioGanado inventario = InventarioGanado.builder()
            .hato(repositoryHato.findById(dto.getIdHato())
                    .orElseThrow(() -> new RuntimeException("Hato no encontrado con id: " + dto.getIdHato())))
            .raza(serviceRaza.findById(dto.getIdRaza()))
            .categoriaGanado(serviceCategoriaGanado.findById(dto.getIdCategoria()))
            .cantidad(dto.getCantidad())
            .edadPromedioMeses(dto.getEdadPromedioMeses())
            .valorUnitario(dto.getValorUnitario())
            .fechaRegistro(LocalDate.now()) 
            .build();
        return repositoryInventarioGanado.save(inventario);
    }

    @Override
    public List<InventarioGanado> findAll() {
        return repositoryInventarioGanado.findAll();
    }

    @Override
    public InventarioGanado findById(UUID id) {
        return repositoryInventarioGanado.findById(id)
                .orElseThrow(() -> new RuntimeException("InventarioGanado no encontrado con id: " + id));
    }

    @Override
    public List<InventarioGanado> findByHato(UUID idHato) {
        return repositoryInventarioGanado.findByHato_IdHato(idHato);
    }

    @Override
    public InventarioGanado update(UUID id, RegistroInventarioGanadoDTO dto) {
        InventarioGanado inventario = findById(id);
        inventario.setHato(repositoryHato.findById(dto.getIdHato())
                .orElseThrow(() -> new RuntimeException("Hato no encontrado con id: " + dto.getIdHato())));
        inventario.setRaza(serviceRaza.findById(dto.getIdRaza()));
        inventario.setCategoriaGanado(serviceCategoriaGanado.findById(dto.getIdCategoria()));
        inventario.setCantidad(dto.getCantidad());
        inventario.setEdadPromedioMeses(dto.getEdadPromedioMeses());
        inventario.setValorUnitario(dto.getValorUnitario());
        inventario.setValorTotal(dto.getCantidad() * dto.getValorUnitario());
        return repositoryInventarioGanado.save(inventario);
    }
    
    @Override
    public InventarioGanado actualizarParcial(UUID id,
            ActualizarInventarioGanadoDTO dto) {

        InventarioGanado inventario = findById(id);

        if (dto.getCantidad() != null)
            inventario.setCantidad(dto.getCantidad());

        if (dto.getValorUnitario() != null)
            inventario.setValorUnitario(dto.getValorUnitario());

        if (dto.getEdadPromedioMeses() != null) {
            inventario.setEdadPromedioMeses(dto.getEdadPromedioMeses());
            inventario.setFechaRegistro(LocalDate.now());
        }

        // Recalcular valor total
        inventario.setValorTotal(
            inventario.getCantidad() * inventario.getValorUnitario());

        return repositoryInventarioGanado.save(inventario);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        repositoryInventarioGanado.deleteById(id);
    }
}
