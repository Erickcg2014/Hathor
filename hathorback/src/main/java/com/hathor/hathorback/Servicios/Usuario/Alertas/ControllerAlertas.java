package com.hathor.hathorback.Servicios.Usuario.Alertas;

import com.hathor.hathorback.Servicios.Usuario.Alertas.DTO.AlertasAdminResumenDTO;
import com.hathor.hathorback.Servicios.Usuario.Alertas.DTO.AlertasResumenDTO;
import com.hathor.hathorback.Servicios.Usuario.Alertas.Service.IServiceAlertas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/alertas")
public class ControllerAlertas {

    @Autowired
    private IServiceAlertas serviceAlertas;

    // ── Usuario ───────────────────────────────────────────────────────────

    // Resumen completo con listas por severidad
    @GetMapping("/{idHato}")
    public ResponseEntity<?> getResumen(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            AlertasResumenDTO resumen =
                serviceAlertas.getResumen(idHato);
            return ResponseEntity.ok()
                .cacheControl(
                    org.springframework.http.CacheControl.noStore())
                .body(resumen);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Marcar una alerta como leída
    @PatchMapping("/{idAlerta}/leida")
    public ResponseEntity<?> marcarLeida(
            @PathVariable Long idAlerta,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceAlertas.marcarLeida(idAlerta);
            return ResponseEntity.ok(
                Map.of("mensaje", "Alerta marcada como leída"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Marcar todas las alertas de un hato como leídas
    @PatchMapping("/{idHato}/leidas")
    public ResponseEntity<?> marcarTodasLeidas(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceAlertas.marcarTodasLeidas(idHato);
            return ResponseEntity.ok(
                Map.of("mensaje", "Todas las alertas marcadas como leídas"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Evaluar alertas manualmente para un hato
    @PostMapping("/{idHato}/evaluar")
    public ResponseEntity<?> evaluarAlertas(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            serviceAlertas.evaluarAlertas(idHato);
            return ResponseEntity.ok(
                Map.of("mensaje",
                    "Alertas evaluadas correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    // Resumen global para el badge del admin
    @GetMapping("/admin/resumen")
    public ResponseEntity<?> getResumenAdmin(
            @AuthenticationPrincipal Jwt jwt) {
        try {
            AlertasAdminResumenDTO resumen =
                serviceAlertas.getResumenAdmin();
            return ResponseEntity.ok()
                .cacheControl(
                    org.springframework.http.CacheControl.noStore())
                .body(resumen);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}