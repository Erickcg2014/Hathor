package com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Service;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Repository.IRepositoryBenchmarkReferencia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public class ServiceBenchmarkReferencia implements IServiceBenchmarkReferencia  {

    @Autowired
    private IRepositoryBenchmarkReferencia repositoryBenchmark;


    @Override
    @Cacheable(value = "benchmarkRef", key = "#codigo + '_' + #tropico")
    public Optional<BenchmarkReferencia> getBenchmark(String codigoKpi, String tropico) {
        if (tropico != null && !tropico.isBlank()) {
            
            List<BenchmarkReferencia> porTropicoList =
                repositoryBenchmark.findByKpi_CodigoAndTropico(codigoKpi, tropico.toUpperCase());
            if (!porTropicoList.isEmpty()) return Optional.of(porTropicoList.get(0));
        }

        // Fallback nacional
        List<BenchmarkReferencia> nacionales =
            repositoryBenchmark.findByKpi_CodigoAndTropicoIsNull(codigoKpi);
        return nacionales.isEmpty() ? Optional.empty() : Optional.of(nacionales.get(0));
    }


    public Optional<BenchmarkReferencia> getBenchmarkParaHato(String codigoKpi, String tropico) {
        return getBenchmark(codigoKpi, tropico);
    }


    @Override
    public List<BenchmarkReferencia> benchmarkcombinado(int idKpi, String region, String tropico) {
        return repositoryBenchmark.findBenchmarkCombinado(idKpi, region, tropico);
    }
}