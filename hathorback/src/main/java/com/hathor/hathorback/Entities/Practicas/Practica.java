package com.hathor.hathorback.Entities.Practicas;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "practica")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Practica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_practica")
    private Integer idPractica;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "objetivo", columnDefinition = "TEXT")
    private String objetivo;

    // PRODUCTIVIDAD | HATO | FINANCIERO | EFICIENCIA
    @Column(name = "categoria", length = 30)
    private String categoria;

    @Column(name = "impacto_esperado", columnDefinition = "TEXT")
    private String impactoEsperado;

    // ACTIVA | INACTIVA
    @Column(name = "estado", length = 20)
    private String estado;

    // JSON array de strings con los pasos concretos
    @Column(name = "pasos", columnDefinition = "TEXT")
    private String pasos;

    // Código del KPI que mejora esta práctica
    @Column(name = "kpi_impactado", length = 50)
    private String kpiImpactado;

    // BAJA | MEDIA | ALTA
    @Column(name = "dificultad", nullable = false, length = 20)
    private String dificultad;

    // Tiempo estimado de implementación en días
    @Column(name = "duracion_dias")
    private Integer duracionDias;

    // PEQUEÑA | MEDIANA | GRANDE | EMPRESARIAL | TODAS
    @Column(name = "escala", nullable = false, length = 20)
    private String escala;

    // FRIO | TEMPLADO | CALIDO | TODOS
    @Column(name = "tropico_aplicable", nullable = false, length = 20)
    private String tropicaAplicable;

    // Reglas que apuntan a esta práctica (tabla pivote)
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "practica", fetch = FetchType.LAZY)
    private List<ReglaPractica> reglas;
}