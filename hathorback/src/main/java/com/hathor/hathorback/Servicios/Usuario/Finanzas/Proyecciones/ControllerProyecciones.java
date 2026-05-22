package com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones;

import com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.DTO.ProyeccionesResponseDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.Service.IServiceProyecciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/proyecciones")
public class ControllerProyecciones {

    @Autowired
    private IServiceProyecciones serviceProyecciones;

    @GetMapping("/{idHato}")
    public ResponseEntity<?> getProyecciones(
            @PathVariable UUID idHato,
            @RequestParam(defaultValue = "6") int meses,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            ProyeccionesResponseDTO resultado =
                serviceProyecciones.getProyecciones(idHato, meses);
            return ResponseEntity.ok()
                .cacheControl(
                    org.springframework.http.CacheControl.noStore())
                .body(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}