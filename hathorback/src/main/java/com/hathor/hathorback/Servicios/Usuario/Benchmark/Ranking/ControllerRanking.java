package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service.IServiceRanking;

import java.util.UUID;

@RestController
@RequestMapping("/Benchmarking")
public class ControllerRanking {

    @Autowired
    private IServiceRanking serviceRanking;

    @GetMapping("/{idHato}/ranking/resumen")
    public ResponseEntity<RankingResumenDTO> getResumenRanking(
            @PathVariable UUID idHato) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl
                .maxAge(10, java.util.concurrent.TimeUnit.MINUTES)
                .cachePrivate())
            .body(serviceRanking.getResumenRanking(idHato));
    }

    @GetMapping("/{idHato}/ranking/compuesto")
    public ResponseEntity<RankingCompuestoDTO> getRankingCompuesto(
            @PathVariable UUID idHato,
            @RequestParam(required = false) String region) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl
            .maxAge(10, java.util.concurrent.TimeUnit.MINUTES)
            .cachePrivate())
            .body(serviceRanking.getRankingCompuesto(idHato, region));
    }

    @GetMapping("/{idHato}/ranking/kpi/{codigoKpi}")
    public ResponseEntity<RankingPorKpiDTO> getRankingPorKpi(
            @PathVariable UUID   idHato,
            @PathVariable String codigoKpi,
            @RequestParam(required = false) String region) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl
            .maxAge(10, java.util.concurrent.TimeUnit.MINUTES)
            .cachePrivate())
            .body(serviceRanking.getRankingPorKpi(idHato, codigoKpi, region));
    }

    @GetMapping("/{idHato}/ranking/evolucion")
    public ResponseEntity<EvolucionPosicionDTO> getEvolucionPosicion(
            @PathVariable UUID   idHato,
            @RequestParam        String codigoKpi,
            @RequestParam(defaultValue = "6") int meses) {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceRanking.getEvolucionPosicion(idHato, codigoKpi, meses));
    }
}