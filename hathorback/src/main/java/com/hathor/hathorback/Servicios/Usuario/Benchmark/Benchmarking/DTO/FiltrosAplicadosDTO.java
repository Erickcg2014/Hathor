package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiltrosAplicadosDTO {

    private String  tropico;
    private String  escala;
    private String  region;
    private int     cantidad;
    private boolean filtroTropicoActivo;
    private boolean filtroEscalaActivo;
    private boolean filtroRegionActivo;
}