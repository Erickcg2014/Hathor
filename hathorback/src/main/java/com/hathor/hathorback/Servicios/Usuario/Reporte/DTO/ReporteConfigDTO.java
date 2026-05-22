package com.hathor.hathorback.Servicios.Usuario.Reporte.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteConfigDTO {

    // Secciones que se incluyen
    private boolean incluirResumenEjecutivo;
    private boolean incluirKpis;
    private boolean incluirBenchmarking;
    private boolean incluirRanking;
    private boolean incluirFinanzas;
    private boolean incluirProduccion;
    private boolean incluirPracticas;
    private String nivelBenchmark;
    // Período del reporte 
    private String periodoDesde;  
    private String periodoHasta;
    private String tituloPersonalizado;
    private String  tipo;

}