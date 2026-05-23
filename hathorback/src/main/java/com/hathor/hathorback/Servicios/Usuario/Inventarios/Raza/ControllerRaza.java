package com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hathor.hathorback.Entities.Inventarios.Raza;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.DTO.RegistroRazaDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Service.IServiceRaza;

@RestController
@RequestMapping("/Raza")
public class ControllerRaza {

    @Autowired
    IServiceRaza serviceRaza;

    @PostMapping
    public ResponseEntity<Raza> create(@RequestBody RegistroRazaDTO dto) {
        return new ResponseEntity<>(serviceRaza.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Raza>> findAll() {
        return new ResponseEntity<>(serviceRaza.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Raza> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(serviceRaza.findById(id), HttpStatus.OK);
    }
    @GetMapping("/tipos")
    public ResponseEntity<List<String>> getTipos(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(serviceRaza.getTiposRaza());
    }

    @GetMapping("/por-tipo/{tipoRaza}")
    public ResponseEntity<List<Raza>> getRazasPorTipo(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable String tipoRaza
    ) {
        return ResponseEntity.ok(serviceRaza.getRazasPorTipo(tipoRaza));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Raza> update(@PathVariable Integer id, @RequestBody RegistroRazaDTO dto) {
        return new ResponseEntity<>(serviceRaza.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        serviceRaza.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
