package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones;

import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.Service.IServiceInversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/inversiones")
public class ControllerInversion {

    @Autowired
    private IServiceInversion serviceInversion;

    // Todas las inversiones de un hato
    @GetMapping("/{idHato}")
    public ResponseEntity<?> getByHato(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok()
                .cacheControl(
                    org.springframework.http.CacheControl
                        .noStore())
                .body(serviceInversion.getByHato(idHato));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Crear inversión planeada
    @PostMapping("/{idHato}")
    public ResponseEntity<?> crear(
            @PathVariable UUID idHato,
            @RequestBody CrearInversionDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceInversion.crear(idHato, dto));
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Actualizar inversión
    @PutMapping("/{idInversion}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long idInversion,
            @RequestBody ActualizarInversionDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(
                serviceInversion.actualizar(
                    idInversion, dto));
        } catch (RuntimeException e) {
            if ("INVERSION_NO_ENCONTRADA"
                    .equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Cancelar inversión
    @DeleteMapping("/{idInversion}")
    public ResponseEntity<?> cancelar(
            @PathVariable Long idInversion,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceInversion.cancelar(idInversion);
            return ResponseEntity.ok(
                Map.of("mensaje",
                    "Inversión cancelada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}