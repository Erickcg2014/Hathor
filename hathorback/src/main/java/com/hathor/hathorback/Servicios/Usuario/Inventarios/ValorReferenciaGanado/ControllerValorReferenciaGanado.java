package com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hathor.hathorback.Entities.Inventarios.ValorReferenciaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.DTO.RegistroValorReferenciaGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.Service.IServiceValorReferenciaGanado;

@RestController
@RequestMapping("/ValorReferenciaGanado")
public class ControllerValorReferenciaGanado {

    @Autowired
    IServiceValorReferenciaGanado serviceValorReferenciaGanado;

    @PostMapping
    public ResponseEntity<ValorReferenciaGanado> create(@RequestBody RegistroValorReferenciaGanadoDTO dto) {
        return new ResponseEntity<>(serviceValorReferenciaGanado.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ValorReferenciaGanado>> findAll() {
        return new ResponseEntity<>(serviceValorReferenciaGanado.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValorReferenciaGanado> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(serviceValorReferenciaGanado.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ValorReferenciaGanado> update(@PathVariable Integer id, @RequestBody RegistroValorReferenciaGanadoDTO dto) {
        return new ResponseEntity<>(serviceValorReferenciaGanado.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        serviceValorReferenciaGanado.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/raza/{idRaza}/categoria/{idCategoria}")
    public ResponseEntity<ValorReferenciaGanado> findByRazaYCategoria(
            @PathVariable Integer idRaza,
            @PathVariable Integer idCategoria) {
        return serviceValorReferenciaGanado
            .findByRazaYCategoria(idRaza, idCategoria)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
