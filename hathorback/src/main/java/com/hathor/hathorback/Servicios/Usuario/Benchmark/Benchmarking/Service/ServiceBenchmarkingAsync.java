package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
public class ServiceBenchmarkingAsync {
    @Lazy
    @Autowired
    private IServiceBenchmarking serviceBenchmarking;

    @Async("benchmarkingExecutor")
    public void calcularEnSegundoPlano(UUID idHato) {
        log.info("Benchmarking async iniciado para hato: {}", idHato);
        try {
            serviceBenchmarking.calcularTodo(idHato);
            log.info("Benchmarking async completado para hato: {}", idHato);
        } catch (Exception e) {
            log.warn("Error en benchmarking async para {}: {}", idHato, e.getMessage());
        }
    }
}