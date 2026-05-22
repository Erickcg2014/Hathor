package com.hathor.hathorback.Entities.Practicas;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hatopractica")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoPractica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_hato_practica")
    private UUID idHatoPractica;

    // PENDIENTE | EN_CURSO | COMPLETADA
    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "porcentaje_avance")
    private Float porcentajeAvance;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // Recomendación que originó esta práctica asignada
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_recomendacion")
    private RecomendacionHato recomendacion;

    // Hato al que está asignada esta práctica
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    // Práctica del catálogo asignada a este hato
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_practica", nullable = false)
    private Practica practica;
}