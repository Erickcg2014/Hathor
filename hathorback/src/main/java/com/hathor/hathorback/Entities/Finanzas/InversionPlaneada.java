package com.hathor.hathorback.Entities.Finanzas;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import com.hathor.hathorback.Entities.Hato.Hato;

@Entity
@Table(name = "inversion_planeada")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class InversionPlaneada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inversion")
    private Long idInversion;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "mes_ejecucion", nullable = false, length = 7)
    private String mesEjecucion;

    // % mejora esperada en ingresos tras la inversión
    @Column(name = "retorno_esperado_pct")
    private Double retornoEsperadoPct;

    // Meses en que se materializa el retorno
    @Column(name = "meses_retorno")
    private Integer mesesRetorno;

    // PLANEADA | EJECUTADA | CANCELADA
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "PLANEADA";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_categoria")
    private CategoriaFinanciera categoriaFinanciera;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null)
            fechaCreacion = LocalDateTime.now();
    }
}