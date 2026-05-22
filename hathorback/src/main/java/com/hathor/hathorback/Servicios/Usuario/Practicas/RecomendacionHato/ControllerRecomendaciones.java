package com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.DTO.RecomendacionDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Service.IServiceRecomendacion;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recomendaciones")
public class ControllerRecomendaciones {

    @Autowired
    private IServiceRecomendacion serviceRecomendacion;

    // GET /api/recomendaciones/{idHato}?page=0&size=10
    @GetMapping("/{idHato}")
    public ResponseEntity<?> getActivasByHato(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID idHato,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        try {
            Page<RecomendacionDTO> resultado =
                serviceRecomendacion.getActivasByHato(idHato, pageable);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if ("RECOMENDACION_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Recomendación no encontrada"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/recomendaciones/{idHato}/contador
    @GetMapping("/{idHato}/contador")
    public ResponseEntity<Long> countNoLeidas(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID idHato
    ) {
        return ResponseEntity.ok(serviceRecomendacion.countNoLeidas(idHato));
    }

    // PATCH /api/recomendaciones/{id}/leer
    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer id
    ) {
        try {
            serviceRecomendacion.marcarComoLeida(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if ("RECOMENDACION_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Recomendación no encontrada"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH /api/recomendaciones/{id}/descartar
    @PatchMapping("/{id}/descartar")
    public ResponseEntity<?> descartar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer id
    ) {
        try {
            serviceRecomendacion.descartar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if ("RECOMENDACION_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Recomendación no encontrada"));
            if ("RECOMENDACION_NO_ACTIVA".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "La recomendación no está activa"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}