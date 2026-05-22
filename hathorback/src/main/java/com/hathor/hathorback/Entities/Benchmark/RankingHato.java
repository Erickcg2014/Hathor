package com.hathor.hathorback.Entities.Benchmark;

import java.time.LocalDate;
import java.util.UUID;

import com.hathor.hathorback.Entities.Hato.Hato;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ranking_hato")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingHato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ranking")
    private UUID idRanking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato", nullable = false, unique = true)
    private Hato hato;

    @Column(name = "score_compuesto")
    private Float scoreCompuesto;

    @Column(name = "posicion_nacional")
    private Integer posicionNacional;

    @Column(name = "posicion_regional")
    private Integer posicionRegional;

    @Column(name = "total_nacional")
    private Integer totalNacional;

    @Column(name = "total_regional")
    private Integer totalRegional;

    @Column(name = "fecha_calculo")
    private LocalDate fechaCalculo;
}