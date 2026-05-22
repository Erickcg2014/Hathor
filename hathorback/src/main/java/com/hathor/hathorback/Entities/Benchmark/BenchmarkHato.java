package com.hathor.hathorback.Entities.Benchmark;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Kpi.Kpi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benchmark_hato")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenchmarkHato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_benchmark_resultado")
    private Integer idBenchmarkHato;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_benchreferencia", nullable = true)
    private BenchmarkReferencia benchReferencia;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kpi", nullable = false)
    private Kpi kpi;

    @Column(name = "percentil")
    private Float percentil;

    @Column(name = "interpretacion")
    private String interpretacion;

    @Column(name = "nivel_benchmark", nullable = false, length = 20)
    private String nivelBenchmark;

    @Column(name = "valor_hato")
    private Float valorHato;

    @Column(name = "fecha_calculo")
    private LocalDate fechaCalculo;
}