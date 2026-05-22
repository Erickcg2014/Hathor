package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkGlobalDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkHatoResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.ComparativaHatosDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service.IServiceBenchmarking;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/Benchmarking")
public class ControllerBenchmarking {

    @Autowired
    IServiceBenchmarking serviceBenchmarking;

    @GetMapping("/{id_hato}")
    public ResponseEntity<List<BenchmarkHatoResultadoDTO>> obtenerBenchHato(
            @PathVariable UUID id_hato,
            @RequestParam(required = false, defaultValue = "REFERENCIA") String modo,
            @RequestParam(required = false, defaultValue = "NACIONAL") String nivel,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceBenchmarking.obtenerBenchHato(
                id_hato, modo, nivel, categoria));
    }
    
    @GetMapping("/{id_hato}/comparativa-hatos")
    public ResponseEntity<ComparativaHatosDTO> getComparativaHatos(
            @PathVariable UUID id_hato,
            @RequestParam String codigoKpi,
            @RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(
            serviceBenchmarking.getComparativaHatos(id_hato, codigoKpi, top)
        );
    }

    @GetMapping("/{id_hato}/global")
    public ResponseEntity<BenchmarkGlobalDTO> getBenchmarkGlobal(
            @PathVariable UUID    id_hato,
            @RequestParam(defaultValue = "true")  boolean filtroTropico,
            @RequestParam(defaultValue = "false") boolean filtroEscala,
            @RequestParam(defaultValue = "false") boolean filtroRegion,
            @RequestParam(defaultValue = "10")    int     cantidad) {

        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceBenchmarking.calcularBenchmarkGlobal(
                id_hato, filtroTropico, filtroEscala, filtroRegion, cantidad
            ));
    }
    
    @PostMapping("/calcularBenchmarking/{id_hato}")
    public ResponseEntity<?> calcularBenchmarking(@PathVariable UUID id_hato) {
        serviceBenchmarking.calcularTodo(id_hato);
        return ResponseEntity.ok("Benchmarking calculado correctamente");
    } 
}
