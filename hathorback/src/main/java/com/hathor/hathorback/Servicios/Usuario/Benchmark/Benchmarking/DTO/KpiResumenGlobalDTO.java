package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiResumenGlobalDTO {

    private String              codigoKpi;
    private String              nombreKpi;
    private String              unidadKpi;
    private String              categoria;
    private Float               valorHatoActual;
    private Float               promedioGrupo;
    private Float               topGrupo;
    private Float               percentilEnGrupo;
    private int                 totalHatosGrupo;
    private String              interpretacion;
    private List<HatoValorDTO>  rankingHatos;
}