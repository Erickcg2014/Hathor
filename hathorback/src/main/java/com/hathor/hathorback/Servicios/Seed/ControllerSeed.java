package com.hathor.hathorback.Servicios.Seed;

import com.hathor.hathorback.Servicios.Seed.DTO.*;
import com.hathor.hathorback.Servicios.Seed.Service.IServiceSeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/seed")
public class ControllerSeed {

    @Autowired
    private IServiceSeed serviceSeed;

    // ── Orquestador completo ──────────────────────────────────────────────

    // Recibe el JSON completo de un hato y crea todo
    @PostMapping("/hato")
    public ResponseEntity<?> seedHatoCompleto(
            @RequestBody SeedHatoCompletoDTO dto) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.seedHatoCompleto(dto);
            if (!resultado.isExitoso()) {
                return ResponseEntity.badRequest()
                    .body(resultado);
            }
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Endpoints modulares ───────────────────────────────────────────────

    // Agregar inventario a un hato existente
    @PostMapping("/hato/{idHato}/inventario")
    public ResponseEntity<?> seedInventario(
            @PathVariable UUID idHato,
            @RequestBody SeedInventarioDTO dto) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.seedInventario(idHato, dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO"
                    .equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Agregar finanzas a un hato existente
    @PostMapping("/hato/{idHato}/finanzas")
    public ResponseEntity<?> seedFinanzas(
            @PathVariable UUID idHato,
            @RequestBody SeedFinanzasDTO dto) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.seedFinanzas(idHato, dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO"
                    .equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Agregar producción a un hato existente
    @PostMapping("/hato/{idHato}/produccion")
    public ResponseEntity<?> seedProduccion(
            @PathVariable UUID idHato,
            @RequestBody SeedProduccionDTO dto) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.seedProduccion(idHato, dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO"
                    .equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Calcular KPIs, benchmarking y alertas
    @PostMapping("/hato/{idHato}/kpis")
    public ResponseEntity<?> seedKpis(
            @PathVariable UUID idHato) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.seedKpis(idHato);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO"
                    .equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Asignar una práctica a un hato existente
    @PostMapping("/hato/{idHato}/practica")
    public ResponseEntity<?> seedPractica(
            @PathVariable UUID idHato,
            @RequestBody SeedPracticaDTO dto) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.seedPracticas(idHato, dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO"
                    .equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/hato/{idUsuarioAuth}")
    public ResponseEntity<?> eliminarHatoPorUsuario(
            @PathVariable String idUsuarioAuth) {
        try {
            SeedResultadoDTO resultado =
                serviceSeed.eliminarHatoPorUsuario(idUsuarioAuth);
            if (!resultado.isExitoso()) {
                return ResponseEntity.badRequest().body(resultado);
            }
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Estado ────────────────────────────────────────────────────────────

    // Ver cuántos hatos y usuarios existen en el sistema
    @GetMapping("/estado")
    public ResponseEntity<?> estado() {
        return ResponseEntity.ok(
            Map.of("mensaje",
       
            "Endpoint de seed activo"));
    }
    // Recalcular KPIs, benchmarking y alertas para TODOS los hatos
    @PostMapping("/recalcular-todos")
    public ResponseEntity<?> recalcularTodos() {
        try {
            SeedResultadoDTO resultado = serviceSeed.recalcularKpisTodos();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/recalcular-benchmarking-todos")
    public ResponseEntity<?> recalcularBenchmarkingTodos() {
        try {
            SeedResultadoDTO resultado = serviceSeed.recalcularBenchmarkingTodos();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}