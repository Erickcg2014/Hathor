package com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero;

import com.hathor.hathorback.Entities.Finanzas.PerfilFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.DTO.RegistroPerfilFinancieroDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Service.IServicePerfilFinanciero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/PerfilFinanciero")
public class ControllerPerfilFinanciero {

    @Autowired
    IServicePerfilFinanciero servicePerfilFinanciero;

    @PostMapping
    public ResponseEntity<?> crearPerfil(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody RegistroPerfilFinancieroDTO dto
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicePerfilFinanciero.crearPerfil(dto, email));
        } catch (RuntimeException e) {
            if ("METODO_INVALIDO".equals(e.getMessage()))
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Método de registro inválido"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/hato/{idHato}")
    public ResponseEntity<List<PerfilFinanciero>> getPerfilesByHato(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato
    ) {
        return ResponseEntity.ok(servicePerfilFinanciero.getPerfilesByHato(idHato));
    }
}