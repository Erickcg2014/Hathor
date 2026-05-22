package com.hathor.hathorback.Servicios.Usuario.Kpi.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiHistoricoDTO {
    private String periodo;
    private String fechaCalculo;
    private Float valor;
    private String estado;
}