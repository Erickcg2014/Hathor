package com.hathor.hathorback.Servicios.Usuario.Kpi.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiResultadoDTO {
    private Integer idKpi;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String formula;
    private String unidad;
    private String categoria;
    private Float valor;
    private String estado;       
    private String periodo;
    private String fechaCalculo;
    private Float benchmarkPromedio;
    private Float benchmarkTop;
    private Float diferenciaPct;  
    private String razonSinDatos;
    private List<DetalleCalculoItem> detalleCalculo;
}