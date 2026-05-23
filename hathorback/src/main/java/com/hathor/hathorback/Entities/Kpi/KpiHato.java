package com.hathor.hathorback.Entities.Kpi;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;

import java.time.LocalDate;

@Entity
@Table(name = "kpi_hato")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiHato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kpi_hato")
    private Integer idKpiHato;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @ManyToOne
    @JoinColumn(name = "id_kpi", nullable = false)
    private Kpi kpi;

    @Column(name = "valor")
    private Float valor;

    @Column(name = "fecha_calculo", nullable = false)
    private LocalDate fechaCalculo;

    @Column(name = "periodo")
    private String periodo;

    @Column(name = "estado")
    private String estado; // OPTIMO, ACEPTABLE, CRITICO, SIN_DATOS
}