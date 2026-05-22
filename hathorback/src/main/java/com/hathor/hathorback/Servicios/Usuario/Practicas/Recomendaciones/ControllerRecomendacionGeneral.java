package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones;

import com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.Service.IServiceRecomendacionGeneral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recomendaciones-generales")
public class ControllerRecomendacionGeneral {

    @Autowired
    private IServiceRecomendacionGeneral serviceRecomendacion;

    // ── Usuario ───────────────────────────────────────────────────────────

    // Resumen completo por prioridad
    @GetMapping("/{idHato}")
    public ResponseEntity<?> getResumen(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok()
                .cacheControl(
                    org.springframework.http.CacheControl.noStore())
                .body(serviceRecomendacion.getResumen(idHato));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Marcar una recomendación como leída
    @PatchMapping("/{idRecomendacion}/leida")
    public ResponseEntity<?> marcarLeida(
            @PathVariable Long idRecomendacion,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceRecomendacion.marcarLeida(idRecomendacion);
            return ResponseEntity.ok(
                Map.of("mensaje",
                    "Recomendación marcada como leída"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Marcar todas como leídas
    @PatchMapping("/{idHato}/leidas")
    public ResponseEntity<?> marcarTodasLeidas(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceRecomendacion.marcarTodasLeidas(idHato);
            return ResponseEntity.ok(
                Map.of("mensaje",
                    "Todas marcadas como leídas"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Clima ─────────────────────────────────────────────────────────────

    // Crear recomendación climática desde el frontend
    @PostMapping("/{idHato}/clima")
    public ResponseEntity<?> crearClimatica(
            @PathVariable UUID idHato,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            List<RecomendacionGeneralDTO> recomendaciones =
                serviceRecomendacion.crearRecomendacionClima(
                    idHato,
                    body.get("subtipo"));
            return ResponseEntity.ok(recomendaciones);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Limpiar recomendaciones climáticas cuando el clima mejora
    @DeleteMapping("/{idHato}/clima")
    public ResponseEntity<?> limpiarClimaticas(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceRecomendacion.limpiarRecomendacionesClima(
                idHato);
            return ResponseEntity.ok(
                Map.of("mensaje",
                    "Recomendaciones climáticas limpiadas"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    // Crear recomendación manual del admin
    @PostMapping("/admin")
    public ResponseEntity<?> crearAdmin(
            @RequestBody CrearRecomendacionAdminDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(
                serviceRecomendacion
                    .crearRecomendacionAdmin(dto));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Listar todas las del admin
    @GetMapping("/admin")
    public ResponseEntity<?> getAdmin(
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok()
                .cacheControl(
                    org.springframework.http.CacheControl.noStore())
                .body(serviceRecomendacion
                    .getRecomendacionesAdmin());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Descartar una recomendación
    @DeleteMapping("/{idRecomendacion}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long idRecomendacion,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceRecomendacion
                .eliminarRecomendacion(idRecomendacion);
            return ResponseEntity.ok(
                Map.of("mensaje",
                    "Recomendación descartada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}