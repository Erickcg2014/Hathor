package com.hathor.hathorback.Entities.Practicas;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "hato_practica_paso")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoPracticaPaso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paso")
    private Long idPaso;

    @Column(name = "indice_paso", nullable = false)
    private Integer indicePaso;

    @Column(name = "completado", nullable = false)
    private Boolean completado;

    @Column(name = "fecha_completado")
    private LocalDate fechaCompletado;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato_practica", nullable = false)
    private HatoPractica hatoPractica;
}