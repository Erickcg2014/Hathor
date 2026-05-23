package com.hathor.hathorback.Entities.Practicas;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Kpi.Kpi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regla")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Regla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regla")
    private Integer idRegla;

    // Operador de comparación:
    // MENOR_QUE | MAYOR_QUE | ENTRE |
    // MENOR_PCT_PROMEDIO | MAYOR_PCT_PROMEDIO | MAYOR_PCT_TOP
    @Column(name = "operador", nullable = false, length = 30)
    private String operador;

    // Factor o valor absoluto del umbral principal
    @Column(name = "umbral_1")
    private Double umbral1;

    // Segundo umbral, solo usado cuando operador = ENTRE
    @Column(name = "umbral_2")
    private Double umbral2;

    // ABSOLUTO | PCT_PROMEDIO | PCT_TOP
    @Column(name = "umbral_tipo", nullable = false, length = 20)
    private String umbralTipo;

    // CRITICO | ACEPTABLE
    @Column(name = "estado_kpi_objetivo", nullable = false, length = 20)
    private String estadoKpiObjetivo;

    // PEQUEÑA | MEDIANA | GRANDE | EMPRESARIAL | TODAS
    @Column(name = "escala_aplicable", nullable = false, length = 20)
    private String escalaAplicable;

    // ACTIVA | INACTIVA
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "prioridad")
    private Integer prioridad;

    // Relación con KPI 
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_kpi", nullable = false)
    private Kpi kpi;

    // Prácticas vinculadas a esta regla 
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "regla", fetch = FetchType.LAZY)
    private List<ReglaPractica> practicas;
}