package com.hathor.hathorback.Entities.Practicas;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regla_practica")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReglaPractica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Orden de prioridad dentro de la regla 
    @Column(name = "orden", nullable = false)
    private Short orden;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_regla", nullable = false)
    private Regla regla;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_practica", nullable = false)
    private Practica practica;
}