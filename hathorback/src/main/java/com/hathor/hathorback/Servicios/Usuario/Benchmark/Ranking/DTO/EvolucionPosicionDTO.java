package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvolucionPosicionDTO {
    private String              codigoKpi;
    private String              nombreKpi;
    private String              unidadKpi;
    private Integer             mesesConsultados;
    private List<PuntoEvolucion> puntos;
    private Boolean             datosInsuficientes;
}