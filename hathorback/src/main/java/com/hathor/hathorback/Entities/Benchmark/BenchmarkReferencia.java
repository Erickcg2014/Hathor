package com.hathor.hathorback.Entities.Benchmark;

import com.hathor.hathorback.Entities.Kpi.Kpi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benchmarkreferencia")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenchmarkReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_benchmark")
    private Integer idBenchmark;

    @ManyToOne
    @JoinColumn(name = "id_kpi", nullable = false)
    private Kpi kpi;

    @Column(name = "region")
    private String region;

    @Column(name = "valor_promedio")
    private Float valorPromedio;

    @Column(name = "valor_top")
    private Float valorTop;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "tropico")
    private String tropico;

    @Column(name = "sistema_ordenio")
    private String sistemaOrdenio;

    @Column(name = "escala")
    private String escala;
}