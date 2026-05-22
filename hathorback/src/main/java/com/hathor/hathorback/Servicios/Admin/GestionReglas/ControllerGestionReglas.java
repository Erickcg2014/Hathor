package com.hathor.hathorback.Servicios.Admin.GestionReglas;

import com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO.*;
import com.hathor.hathorback.Servicios.Admin.GestionReglas.Service.IServiceGestionReglas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Admin/GestionReglas")
public class ControllerGestionReglas {

    @Autowired
    private IServiceGestionReglas serviceGestionReglas;

    @GetMapping
    public ResponseEntity<List<ReglaAdminDTO>> getReglas(
            @RequestParam(required = false) String  estado,
            @RequestParam(required = false) String  escala,
            @RequestParam(required = false) Integer idKpi) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceGestionReglas.getReglas(estado, escala, idKpi));
    }

    @GetMapping("/{idRegla}")
    public ResponseEntity<?> getReglaById(@PathVariable Integer idRegla) {
        try {
            return ResponseEntity.ok(serviceGestionReglas.getReglaById(idRegla));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearRegla(@RequestBody CrearReglaDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceGestionReglas.crearRegla(dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @PutMapping("/{idRegla}")
    public ResponseEntity<?> editarRegla(
            @PathVariable Integer    idRegla,
            @RequestBody  EditarReglaDTO dto) {
        try {
            return ResponseEntity.ok(serviceGestionReglas.editarRegla(idRegla, dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @DeleteMapping("/{idRegla}")
    public ResponseEntity<?> desactivarRegla(@PathVariable Integer idRegla) {
        try {
            serviceGestionReglas.desactivarRegla(idRegla);
            return ResponseEntity.ok(Map.of("mensaje", "Regla desactivada correctamente"));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    // ── Gestión de vínculos regla-práctica ────────────────────────────────

    @PostMapping("/{idRegla}/practicas")
    public ResponseEntity<?> vincularPractica(
            @PathVariable Integer           idRegla,
            @RequestBody  VincularPracticaDTO dto) {
        try {
            return ResponseEntity.ok(
                serviceGestionReglas.vincularPractica(idRegla, dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @DeleteMapping("/{idRegla}/practicas/{idPractica}")
    public ResponseEntity<?> desvincularPractica(
            @PathVariable Integer idRegla,
            @PathVariable Integer idPractica) {
        try {
            return ResponseEntity.ok(
                serviceGestionReglas.desvincularPractica(idRegla, idPractica));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @PutMapping("/{idRegla}/practicas/reordenar")
    public ResponseEntity<?> reordenarPracticas(
            @PathVariable Integer                 idRegla,
            @RequestBody  List<VincularPracticaDTO> practicas) {
        try {
            return ResponseEntity.ok(
                serviceGestionReglas.reordenarPracticas(idRegla, practicas));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    // ── Helper de errores ─────────────────────────────────────────────────

    private ResponseEntity<?> manejarError(RuntimeException e) {
        return switch (e.getMessage()) {
            case "REGLA_NO_ENCONTRADA"   ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Regla no encontrada"));
            case "KPI_NO_ENCONTRADO"     ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "KPI no encontrado"));
            case "PRACTICA_NO_ENCONTRADA" ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Práctica no encontrada"));
            case "PRACTICA_YA_VINCULADA" ->
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "La práctica ya está vinculada a esta regla"));
            default ->
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        };
    }
}