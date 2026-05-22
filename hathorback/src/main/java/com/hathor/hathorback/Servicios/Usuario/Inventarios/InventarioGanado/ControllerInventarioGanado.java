package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado;

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

import com.hathor.hathorback.Entities.Inventarios.InventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO.ActualizarInventarioGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.DTO.RegistroInventarioGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Service.IServiceInventarioGanado;

@RestController
@RequestMapping("/InventarioGanado")
public class ControllerInventarioGanado {

    @Autowired
    IServiceInventarioGanado serviceInventarioGanado;

    @PostMapping
    public ResponseEntity<InventarioGanado> create(@RequestBody RegistroInventarioGanadoDTO dto) {
        return new ResponseEntity<>(serviceInventarioGanado.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InventarioGanado>> findAll() {
        return new ResponseEntity<>(serviceInventarioGanado.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioGanado> findById(@PathVariable UUID id) {
        return new ResponseEntity<>(serviceInventarioGanado.findById(id), HttpStatus.OK);
    }

    @GetMapping("/hato/{idHato}")
    public ResponseEntity<List<InventarioGanado>> findByHato(@PathVariable UUID idHato) {
        return new ResponseEntity<>(serviceInventarioGanado.findByHato(idHato), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioGanado> update(@PathVariable UUID id, @RequestBody RegistroInventarioGanadoDTO dto) {
        return new ResponseEntity<>(serviceInventarioGanado.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceInventarioGanado.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InventarioGanado> actualizarParcial(
            @PathVariable UUID id,
            @RequestBody ActualizarInventarioGanadoDTO dto) {
        try {
            return ResponseEntity.ok(
                serviceInventarioGanado.actualizarParcial(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
