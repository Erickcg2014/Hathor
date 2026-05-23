package com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.hathor.hathorback.Entities.Practicas.Regla;


import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository.IRepositoryRegla;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Repository.IRepositoryBenchmarkReferencia;

@Service
public class ServiceReglaCache {

    @Autowired private IRepositoryBenchmarkReferencia repoBenchmark;
    @Autowired private IRepositoryRegla repoRegla;

    @Cacheable(value = "benchmarkEspecifico",
               key = "#codigo + '_' + #tropico + '_' + #region + '_' + #escala")
    public Optional<BenchmarkReferencia> getBenchmarkEspecifico(
            String codigo, String tropico, String region, String escala) {
        return repoBenchmark.findMasEspecifico(codigo, tropico, region, escala);
    }

    @Cacheable(value = "reglasPorKpi", key = "#codigoKpi + '_' + #escala")
    public List<Regla> getReglasPorKpi(String codigoKpi, String escala) {
        return repoRegla.findActivasByKpiYEscala(codigoKpi, escala);
    }
}