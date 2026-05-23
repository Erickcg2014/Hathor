package com.hathor.hathorback.Servicios.Admin.Hatos;

import com.hathor.hathorback.Servicios.Admin.Hatos.DTO.*;
import com.hathor.hathorback.Servicios.Admin.Hatos.Service.IServiceAdminHatos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/Admin/Hatos")
public class ControllerAdminHatos {

    @Autowired
    private IServiceAdminHatos serviceAdminHatos;

    // Hatos filtrados
    @GetMapping
    public ResponseEntity<List<HatoAdminDTO>> getHatosFiltrados(
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String tropico,
            @RequestParam(required = false) String escala,
            @RequestParam(required = false) String tipoHato) {

        FiltroHatoDTO filtro = FiltroHatoDTO.builder()
            .departamento(departamento)
            .region(region)
            .tropico(tropico)
            .escala(escala)
            .tipoHato(tipoHato)
            .build();

        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceAdminHatos.getHatosFiltrados(filtro));
    }

    // Detalle de un hato
    @GetMapping("/{idHato}")
    public ResponseEntity<?> getDetalleHato(@PathVariable UUID idHato) {
        try {
            return ResponseEntity.ok(serviceAdminHatos.getDetalleHato(idHato));
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Filtros disponibles
    @GetMapping("/filtros/departamentos")
    public ResponseEntity<List<String>> getDepartamentos() {
        return ResponseEntity.ok(serviceAdminHatos.getDepartamentos());
    }

    @GetMapping("/filtros/regiones")
    public ResponseEntity<List<String>> getRegiones(
            @RequestParam String departamento) {
        return ResponseEntity.ok(
            serviceAdminHatos.getRegionesPorDepartamento(departamento));
    }

    @GetMapping("/filtros/tropicos")
    public ResponseEntity<List<String>> getTropicos() {
        return ResponseEntity.ok(serviceAdminHatos.getTropicos());
    }

    @GetMapping("/filtros/escalas")
    public ResponseEntity<List<String>> getEscalas() {
        return ResponseEntity.ok(serviceAdminHatos.getEscalas());
    }
}