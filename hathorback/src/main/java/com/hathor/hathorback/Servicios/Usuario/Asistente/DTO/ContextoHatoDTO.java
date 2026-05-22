package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextoHatoDTO {

    // Info básica del hato
    private String nombreHato;
    private String departamento;
    private String ciudad;
    private String tropico;
    private String escala;
    private float altitud;
    private float areaHato;
    private float areaPastoreo;
    private int totalEmpleados;
    private float capacidadAlmacenamiento;
    private String tipoHato;
    private int porcentajeCompletitud;

    // Perfil productivo
    private Double produccionDiariaLitros;
    private Integer vacasEnOrdenio;
    private Double precioLitroPromedio;
    private String sistemasOrdenio;
    private Integer frecuenciaOrdenio;
    private Integer periodoLactancia;
    private String razaPredominante;

    // Totales financieros
    private Double totalIngresos;
    private Double totalGastos;
    private Double balanceNeto;
    private String capacidadInversion;

    // KPIs — lista de los calculados con su estado
    private List<KpiContextoDTO> kpis;

    // Benchmarking resumen — solo los críticos y aceptables
    private List<KpiBenchContextoDTO> kpisBenchmark;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KpiContextoDTO {
        private String codigo;
        private String nombre;
        private Float valor;
        private String unidad;
        private String estado;      // CRITICO | ACEPTABLE | OPTIMO | SIN_DATOS
        private String categoria;
        private Float benchmarkPromedio;
        private Float diferenciaPct;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KpiBenchContextoDTO {
        private String codigo;
        private String nombre;
        private Float valorHato;
        private Float promedioGrupo;
        private Float percentilEnGrupo;
        private String interpretacion;
    }
}