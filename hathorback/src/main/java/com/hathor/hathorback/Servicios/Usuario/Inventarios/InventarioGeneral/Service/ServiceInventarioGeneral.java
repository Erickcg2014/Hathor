package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Inventarios.InventarioGeneral;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Service.IServiceCategoriaInventario;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO.ActualizarInventarioGeneralDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO.RegistroInventarioGeneralDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Repository.IRepositoryInventarioGeneral;

@Service
public class ServiceInventarioGeneral implements IServiceInventarioGeneral {

    @Autowired
    IRepositoryInventarioGeneral repositoryInventarioGeneral;

    @Autowired
    IRepositoryHato repositoryHato;

    @Autowired
    IServiceCategoriaInventario serviceCategoriaInventario;

    @Override
    public InventarioGeneral create(RegistroInventarioGeneralDTO dto) {
        InventarioGeneral inventario = InventarioGeneral.builder()
                .hato(repositoryHato.findById(dto.getIdHato())
                        .orElseThrow(() -> new RuntimeException("Hato no encontrado")))
                .categoriaInventario(serviceCategoriaInventario.findById(dto.getIdCategoriaInventario()))
                .nombreItem(dto.getNombreItem())
                .cantidad(dto.getCantidad())
                .valorUnitario(dto.getValorUnitario())
                .valorTotal(dto.getCantidad() * dto.getValorUnitario()) 
                .fechaRegistro(LocalDate.now())                         
                .descripcion(dto.getDescripcion())
                .build();
        return repositoryInventarioGeneral.save(inventario);
    }

    @Override
    public List<InventarioGeneral> findAll() {
        return repositoryInventarioGeneral.findAll();
    }

    @Override
    public InventarioGeneral findById(UUID id) {
        return repositoryInventarioGeneral.findById(id)
                .orElseThrow(() -> new RuntimeException("InventarioGeneral no encontrado con id: " + id));
    }

    @Override
    public List<InventarioGeneral> findByHato(UUID idHato) {
        return repositoryInventarioGeneral.findByHato_IdHato(idHato);
    }

    @Override
    public InventarioGeneral update(UUID id, RegistroInventarioGeneralDTO dto) {
        InventarioGeneral inventario = findById(id);
        inventario.setHato(repositoryHato.findById(dto.getIdHato())
                .orElseThrow(() -> new RuntimeException("Hato no encontrado con id: " + dto.getIdHato())));
        inventario.setCategoriaInventario(serviceCategoriaInventario.findById(dto.getIdCategoriaInventario()));
        inventario.setNombreItem(dto.getNombreItem());
        inventario.setCantidad(dto.getCantidad());
        inventario.setValorUnitario(dto.getValorUnitario());
        inventario.setValorTotal(dto.getCantidad() * dto.getValorUnitario());
        inventario.setDescripcion(dto.getDescripcion());
        return repositoryInventarioGeneral.save(inventario);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        repositoryInventarioGeneral.deleteById(id);
    }

    @Override
    public InventarioGeneral actualizarParcial(UUID id,
            ActualizarInventarioGeneralDTO dto) {

        InventarioGeneral inventario = findById(id);

        if (dto.getNombreItem() != null)
            inventario.setNombreItem(dto.getNombreItem());

        if (dto.getCantidad() != null)
            inventario.setCantidad(dto.getCantidad());

        if (dto.getValorUnitario() != null)
            inventario.setValorUnitario(dto.getValorUnitario());

        if (dto.getDescripcion() != null)
            inventario.setDescripcion(dto.getDescripcion());

        // Recalcular valor total
        inventario.setValorTotal(
            inventario.getCantidad() * inventario.getValorUnitario());

        return repositoryInventarioGeneral.save(inventario);
    }
}
