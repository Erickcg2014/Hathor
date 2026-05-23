package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CategoriaFinancieraDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CategoriasAgrupadasDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CrearCategoriaPersonalizadaDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Service.IServiceCategoriaFinanciera;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/CategoriaFinanciera")
public class ControllerCategoriaFinanciera {

    @Autowired
    IServiceCategoriaFinanciera servicioCategoriaFinanciera;

    @GetMapping
    public ResponseEntity<List<CategoriaFinanciera>> getCategorias(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(servicioCategoriaFinanciera.getCategorias());
    }

    @GetMapping("/me")
    public ResponseEntity<List<CategoriaFinanciera>> getCategoriasAllAndMine(
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(servicioCategoriaFinanciera.getCategoriasAllAndMine(email));
    }
    

    // Categorías de primer nivel agrupadas por tipo
    @GetMapping("/agrupadas")
    public ResponseEntity<CategoriasAgrupadasDTO> getCategoriasAgrupadas(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(servicioCategoriaFinanciera.getCategoriasAgrupadas());
    }

    @PostMapping("/")
    public ResponseEntity<CategoriaFinanciera> createCategoriaFinanciera(
        @RequestBody CategoriaFinanciera categoria
    ) {
        return new ResponseEntity<>(
            servicioCategoriaFinanciera.createCategoriaFinanciera(categoria),
            HttpStatus.CREATED
        );
    }
    @PostMapping("/personalizada")
    public ResponseEntity<CategoriaFinancieraDTO> crearCategoriaPersonalizada(
        @RequestBody CrearCategoriaPersonalizadaDTO dto,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return new ResponseEntity<>(
            servicioCategoriaFinanciera.crearCategoriaPersonalizada(dto, email),
            HttpStatus.CREATED
        );
    }
}