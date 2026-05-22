package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hathor.hathorback.Entities.Inventarios.InventarioGeneral;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO.ActualizarInventarioGeneralDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.DTO.RegistroInventarioGeneralDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Service.IServiceInventarioGeneral;

@RestController
@RequestMapping("/InventarioGeneral")
public class ControllerInventarioGeneral {

    @Autowired
    IServiceInventarioGeneral serviceInventarioGeneral;

    @PostMapping
    public ResponseEntity<InventarioGeneral> create(@RequestBody RegistroInventarioGeneralDTO dto) {
        return new ResponseEntity<>(serviceInventarioGeneral.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InventarioGeneral>> findAll() {
        return new ResponseEntity<>(serviceInventarioGeneral.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioGeneral> findById(@PathVariable UUID id) {
        return new ResponseEntity<>(serviceInventarioGeneral.findById(id), HttpStatus.OK);
    }

    @GetMapping("/hato/{idHato}")
    public ResponseEntity<List<InventarioGeneral>> findByHato(@PathVariable UUID idHato) {
        return new ResponseEntity<>(serviceInventarioGeneral.findByHato(idHato), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioGeneral> update(@PathVariable UUID id, @RequestBody RegistroInventarioGeneralDTO dto) {
        return new ResponseEntity<>(serviceInventarioGeneral.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceInventarioGeneral.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InventarioGeneral> actualizarParcial(
            @PathVariable UUID id,
            @RequestBody ActualizarInventarioGeneralDTO dto) {
        try {
            return ResponseEntity.ok(
                serviceInventarioGeneral.actualizarParcial(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
