package com.hathor.hathorback.Entities.Recomendaciones;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Practicas.Regla;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recomendacion_hato")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionHato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion_hato")
    private Integer idRecomendacionHato;

    @Column(name = "tipo", length = 30)
    private String tipo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    // Código del KPI que disparó esta recomendación
    @Column(name = "indicador", length = 50)
    private String indicador;

    // Valor calculado del KPI del hato en el momento de la evaluación
    @Column(name = "valor_actual")
    private Float valorActual;

    // Benchmark de referencia usado para la comparación
    @Column(name = "valor_referencia")
    private Float valorReferencia;

    @Column(name = "leida")
    private Boolean leida;

    // ALTA | MEDIA | BAJA
    @Column(name = "prioridad", nullable = false, length = 20)
    private String prioridad;

    // ACTIVA | DESCARTADA | COMPLETADA
    @Column(name = "tipo_estado", nullable = false, length = 20)
    private String tipoEstado;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    // Snapshot del contexto del hato al momento de la generación
    @Column(name = "escala_hato", length = 20)
    private String escalaHato;

    @Column(name = "tropico_hato", length = 20)
    private String tropicoHato;

    @Column(name = "region_hato", length = 100)
    private String regionHato;

    // Regla que disparó esta recomendación
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_regla")
    private Regla regla;

    // Hato al que pertenece esta recomendación
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;
}