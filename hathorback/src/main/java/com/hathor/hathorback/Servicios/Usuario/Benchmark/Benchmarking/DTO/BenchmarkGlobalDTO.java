package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenchmarkGlobalDTO {

    private List<KpiResumenGlobalDTO> kpisResumen;
    private List<HatoAnonimizadoDTO>  hatosEnMapa;
    private FiltrosAplicadosDTO       filtrosAplicados;
    private int                       totalHatosEncontrados;
    private boolean                   datosInsuficientes;
    private String                    mensajeFaltante;
}