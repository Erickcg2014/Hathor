package com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Service;

import java.util.Optional;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;

import java.util.List;


public interface IServiceBenchmarkReferencia {

    Optional<BenchmarkReferencia> getBenchmark(String codigoKpi, String tropico);

    Optional<BenchmarkReferencia> getBenchmarkParaHato(String codigoKpi, String tropico);

    List<BenchmarkReferencia> benchmarkcombinado(int idKpi, String region, String tropico);
}