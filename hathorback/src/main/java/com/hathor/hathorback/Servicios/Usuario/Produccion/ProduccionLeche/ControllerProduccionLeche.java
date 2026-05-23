package com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.DTO.RegistroProduccionLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Service.IServiceProduccionLeche;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ProduccionLeche")
public class ControllerProduccionLeche {

    @Autowired
    IServiceProduccionLeche serviceProduccionLeche;

    @PostMapping
    public ResponseEntity<?> crearRegistro(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody RegistroProduccionLecheDTO dto
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceProduccionLeche.crearRegistro(dto, email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/hato/{idHato}")
    public ResponseEntity<?> getByHato(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato
    ) {
        try {
            return ResponseEntity.ok(serviceProduccionLeche.getByHato(idHato));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}