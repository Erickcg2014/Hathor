package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service;

import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkGlobalDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.BenchmarkHatoResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.ComparativaHatosDTO;

public interface IServiceBenchmarking {

    public List<BenchmarkHato> calcularBenchConReferencia(UUID idHato, String nivel);
    public List<BenchmarkHato> calcularBenchConHatos (UUID idHato);
    List<BenchmarkHatoResultadoDTO> obtenerBenchHato(UUID idHato, String modo, String nivel, String categoria);
    public void calcularTodo(UUID idHato);
    ComparativaHatosDTO getComparativaHatos(UUID idHato, String codigoKpi, int top);
    BenchmarkGlobalDTO calcularBenchmarkGlobal(UUID idHato,boolean filtroTropico, boolean filtroEscala, boolean filtroRegion, int cantidad);
}
