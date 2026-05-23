package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado;

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

import com.hathor.hathorback.Entities.Inventarios.CategoriaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.DTO.RegistroCategoriaGanadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Service.IServiceCategoriaGanado;

@RestController
@RequestMapping("/CategoriaGanado")
public class ControllerCategoriaGanado {

    @Autowired
    IServiceCategoriaGanado serviceCategoriaGanado;

    @PostMapping
    public ResponseEntity<CategoriaGanado> create(@RequestBody RegistroCategoriaGanadoDTO dto) {
        return new ResponseEntity<>(serviceCategoriaGanado.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaGanado>> findAll() {
        return new ResponseEntity<>(serviceCategoriaGanado.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaGanado> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(serviceCategoriaGanado.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaGanado> update(@PathVariable Integer id, @RequestBody RegistroCategoriaGanadoDTO dto) {
        return new ResponseEntity<>(serviceCategoriaGanado.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        serviceCategoriaGanado.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
