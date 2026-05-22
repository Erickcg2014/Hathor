package com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO.ActualizarPerfilRapidoDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO.RegistroPerfilProductivoDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Service.IServicePerfilProductivo;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/PerfilProductivo")
public class ControllerPerfilProductivo {

    @Autowired
    IServicePerfilProductivo servicePerfilProductivo;

    @PostMapping
    public ResponseEntity<?> crearPerfil(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody RegistroPerfilProductivoDTO dto
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicePerfilProductivo.crearPerfil(dto, email));
        } catch (RuntimeException e) {
            if ("PERFIL_YA_EXISTE".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un perfil productivo para este hato"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{idHato}")
    public ResponseEntity<?> actualizarPerfil(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato,
        @RequestBody RegistroPerfilProductivoDTO dto
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.ok(
                servicePerfilProductivo.actualizarPerfil(idHato, dto, email)
            );
        } catch (RuntimeException e) {
            if ("PERFIL_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Perfil productivo no encontrado"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{idHato}")
    public ResponseEntity<?> getPerfilByHato(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato
    ) {
        try {
            return ResponseEntity.ok(servicePerfilProductivo.getPerfilByHato(idHato));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Perfil productivo no encontrado"));
        }
    }

    @PatchMapping("/{idHato}")
    public ResponseEntity<?> actualizarParcial(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID idHato,
            @RequestBody ActualizarPerfilRapidoDTO dto) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.ok(
                servicePerfilProductivo.actualizarParcial(
                    idHato, dto, email));
        } catch (RuntimeException e) {
            if ("PERFIL_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Perfil no encontrado"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}