package com.hathor.hathorback.Servicios.Admin.Estadisticas;

import com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO.*;
import com.hathor.hathorback.Servicios.Admin.Estadisticas.Service.IServiceEstadisticas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Admin/Estadisticas")
public class ControllerEstadisticas {

    @Autowired
    private IServiceEstadisticas serviceEstadisticas;

    @GetMapping("/globales")
    public ResponseEntity<EstadisticasGlobalesDTO> getGlobales() {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceEstadisticas.getEstadisticasGlobales());
    }

    @GetMapping("/por-departamento")
    public ResponseEntity<List<EstadisticasDepartamentoDTO>> getPorDepartamento() {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceEstadisticas.getEstadisticasPorDepartamento());
    }

    @GetMapping("/por-escala")
    public ResponseEntity<List<EstadisticasEscalaDTO>> getPorEscala() {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceEstadisticas.getEstadisticasPorEscala());
    }

    @GetMapping("/por-tropico")
    public ResponseEntity<List<EstadisticasTropicoDTO>> getPorTropico() {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceEstadisticas.getEstadisticasPorTropico());
    }
}