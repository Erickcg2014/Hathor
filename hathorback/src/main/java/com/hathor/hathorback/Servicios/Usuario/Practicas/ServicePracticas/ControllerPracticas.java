package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas;

import com.hathor.hathorback.Entities.Practicas.Practica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.ActualizarPasosDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.HatoPracticaDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.PracticaDetalleDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Service.IServicePractica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Service.HatoPractica.IServiceHatoPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/practicas")
public class ControllerPracticas {

    @Autowired
    private IServicePractica servicePractica;

    @Autowired
    private IServiceHatoPractica serviceHatoPractica;

    // GET /api/practicas/{idHato} — prácticas asignadas al hato con estado
    @GetMapping("/{idHato}")
    public ResponseEntity<?> getPracticasByHato(
            @PathVariable UUID idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            List<HatoPracticaDTO> result = serviceHatoPractica.getByHato(idHato);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("HATO_PRACTICA_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.status(500).build();
        }
    }

    // GET /api/practicas/catalogo/{escala} — catálogo filtrado por escala
    @GetMapping("/catalogo/{escala}")
    public ResponseEntity<?> getCatalogo(
            @PathVariable String escala,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            List<Practica> result = servicePractica.getCatalogoByEscala(escala);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET /api/practicas/detalle/{idPractica} — detalle completo con pasos parseados
    @GetMapping("/detalle/{idPractica}")
    public ResponseEntity<?> getDetalle(
            @PathVariable Integer idPractica,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            PracticaDetalleDTO result = servicePractica.getDetalle(idPractica);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("PRACTICA_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.status(500).build();
        }
    }

    // PATCH /api/practicas/hato/{idHatoPractica}/estado — cambia PENDIENTE→EN_CURSO→COMPLETADA
    @PatchMapping("/hato/{idHatoPractica}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable UUID idHatoPractica,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String nuevoEstado = body.get("estado");
            HatoPracticaDTO result = serviceHatoPractica.actualizarEstado(idHatoPractica, nuevoEstado);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("HATO_PRACTICA_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            if ("TRANSICION_INVALIDA".equals(e.getMessage()))
                return ResponseEntity.status(409).build();
            return ResponseEntity.status(500).build();
        }
    }

    // PATCH /api/practicas/hato/{idHatoPractica}/avance — actualiza porcentaje de avance
    @PatchMapping("/hato/{idHatoPractica}/avance")
    public ResponseEntity<?> actualizarAvance(
            @PathVariable UUID idHatoPractica,
            @RequestBody Map<String, Float> body,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            Float porcentaje = body.get("porcentaje");
            HatoPracticaDTO result = serviceHatoPractica.actualizarAvance(idHatoPractica, porcentaje);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("HATO_PRACTICA_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            if ("HATO_PRACTICA_NO_EN_CURSO".equals(e.getMessage()))
                return ResponseEntity.status(409).build();
            return ResponseEntity.status(500).build();
        }
    }

    // GET pasos de una práctica
    @GetMapping("/hato/{idHatoPractica}/pasos")
    public ResponseEntity<?> getPasos(
            @PathVariable UUID idHatoPractica,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(
                serviceHatoPractica.getPasos(idHatoPractica));
        } catch (RuntimeException e) {
            if ("HATO_PRACTICA_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.status(500).build();
        }
    }

    // PUT actualizar pasos
    @PutMapping("/hato/{idHatoPractica}/pasos")
    public ResponseEntity<?> actualizarPasos(
            @PathVariable UUID idHatoPractica,
            @RequestBody ActualizarPasosDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(
                serviceHatoPractica.actualizarPasos(idHatoPractica, dto));
        } catch (RuntimeException e) {
            if ("HATO_PRACTICA_NO_ENCONTRADA".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.status(500).build();
        }
    }
}
