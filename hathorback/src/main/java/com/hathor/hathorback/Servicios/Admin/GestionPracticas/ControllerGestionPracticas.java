package com.hathor.hathorback.Servicios.Admin.GestionPracticas;

import com.hathor.hathorback.Servicios.Admin.GestionPracticas.DTO.*;
import com.hathor.hathorback.Servicios.Admin.GestionPracticas.Service.IServiceGestionPracticas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Admin/GestionPracticas")
public class ControllerGestionPracticas {

    @Autowired
    private IServiceGestionPracticas serviceGestionPracticas;

    @GetMapping
    public ResponseEntity<List<PracticaAdminDTO>> getPracticas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String escala,
            @RequestParam(required = false) String dificultad) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceGestionPracticas.getPracticas(
                estado, categoria, escala, dificultad));
    }

    @GetMapping("/{idPractica}")
    public ResponseEntity<?> getPracticaById(
            @PathVariable Integer idPractica) {
        try {
            return ResponseEntity.ok(
                serviceGestionPracticas.getPracticaById(idPractica));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearPractica(
            @RequestBody CrearPracticaDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceGestionPracticas.crearPractica(dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @PutMapping("/{idPractica}")
    public ResponseEntity<?> editarPractica(
            @PathVariable Integer         idPractica,
            @RequestBody  EditarPracticaDTO dto) {
        try {
            return ResponseEntity.ok(
                serviceGestionPracticas.editarPractica(idPractica, dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    @DeleteMapping("/{idPractica}")
    public ResponseEntity<?> desactivarPractica(
            @PathVariable Integer idPractica) {
        try {
            serviceGestionPracticas.desactivarPractica(idPractica);
            return ResponseEntity.ok(
                Map.of("mensaje", "Práctica desactivada correctamente"));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    private ResponseEntity<?> manejarError(RuntimeException e) {
        return switch (e.getMessage() != null ? e.getMessage() : "") {
            case "PRACTICA_NO_ENCONTRADA" ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Práctica no encontrada"));
            default ->
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        };
    }
}