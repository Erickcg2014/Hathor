// Admin/GestionRecomendaciones/ControllerGestionRecomendaciones.java
package com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones;

import com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.DTO.*;
import com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.Service
    .IServiceGestionRecomendaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/Admin/GestionRecomendaciones")
public class ControllerGestionRecomendaciones {

    @Autowired
    private IServiceGestionRecomendaciones serviceGestionRecomendaciones;

    // Todas las recomendaciones de un hato
    @GetMapping("/{idHato}")
    public ResponseEntity<List<RecomendacionAdminDTO>> getByHato(
            @PathVariable UUID idHato) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceGestionRecomendaciones
                .getRecomendacionesPorHato(idHato));
    }

    // Filtradas — para panel general del admin
    @GetMapping
    public ResponseEntity<List<RecomendacionAdminDTO>> getFiltradas(
            @RequestParam(required = false) UUID   idHato,
            @RequestParam(required = false) String tipoEstado,
            @RequestParam(required = false) String prioridad) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceGestionRecomendaciones
                .getRecomendacionesFiltradas(idHato, tipoEstado, prioridad));
    }

    // Crear recomendación manual
    @PostMapping
    public ResponseEntity<?> crearRecomendacion(
            @RequestBody CrearRecomendacionDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceGestionRecomendaciones.crearRecomendacion(dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    // Cambiar estado
    @PutMapping("/{idRecomendacion}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Integer                     idRecomendacion,
            @RequestBody  CambiarEstadoRecomendacionDTO dto) {
        try {
            return ResponseEntity.ok(
                serviceGestionRecomendaciones
                    .cambiarEstado(idRecomendacion, dto));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    // Eliminar
    @DeleteMapping("/{idRecomendacion}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer idRecomendacion) {
        try {
            serviceGestionRecomendaciones
                .eliminarRecomendacion(idRecomendacion);
            return ResponseEntity.ok(
                Map.of("mensaje", "Recomendación eliminada correctamente"));
        } catch (RuntimeException e) {
            return manejarError(e);
        }
    }

    private ResponseEntity<?> manejarError(RuntimeException e) {
        return switch (e.getMessage() != null ? e.getMessage() : "") {
            case "HATO_NO_ENCONTRADO" ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Hato no encontrado"));
            case "REGLA_NO_ENCONTRADA" ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Regla no encontrada"));
            case "RECOMENDACION_NO_ENCONTRADA" ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Recomendación no encontrada"));
            default ->
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        };
    }
}