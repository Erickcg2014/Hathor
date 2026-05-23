package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hathor.hathorback.Entities.Inventarios.CategoriaInventario;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.DTO.RegistroCategoriaInventarioDTO;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Service.IServiceCategoriaInventario;

@RestController
@RequestMapping("/CategoriaInventario")
public class ControllerCategoriaInventario {

    @Autowired
    IServiceCategoriaInventario serviceCategoriaInventario;

    @PostMapping
    public ResponseEntity<CategoriaInventario> create(@RequestBody RegistroCategoriaInventarioDTO dto) {
        return new ResponseEntity<>(serviceCategoriaInventario.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaInventario>> findAll() {
        return new ResponseEntity<>(serviceCategoriaInventario.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaInventario> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(serviceCategoriaInventario.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaInventario> update(@PathVariable Integer id, @RequestBody RegistroCategoriaInventarioDTO dto) {
        return new ResponseEntity<>(serviceCategoriaInventario.update(id, dto), HttpStatus.OK);
    }
    @GetMapping("/padres")
    public ResponseEntity<List<CategoriaInventario>> getCategoriasParent(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(serviceCategoriaInventario.getCategoriasParent());
    }

    @GetMapping("/{idPadre}/subcategorias")
    public ResponseEntity<List<CategoriaInventario>> getSubcategorias(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable Integer idPadre
    ) {
        return ResponseEntity.ok(serviceCategoriaInventario.getSubcategorias(idPadre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        serviceCategoriaInventario.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
