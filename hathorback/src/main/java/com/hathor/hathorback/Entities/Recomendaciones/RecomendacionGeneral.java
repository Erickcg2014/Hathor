package com.hathor.hathorback.Entities.Recomendaciones;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recomendacion_general")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion")
    private Long idRecomendacion;

    // CLIMA | ADMIN | SISTEMA | ESTACIONAL
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "subtipo", length = 50)
    private String subtipo;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "mensaje",
            nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "prioridad", nullable = false, length = 10)
    @Builder.Default
    private String prioridad = "MEDIA";

    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVA";

    @Column(name = "leida", nullable = false)
    @Builder.Default
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Column(name = "icono", length = 10)
    private String icono;

    @Column(name = "url_accion", length = 200)
    private String urlAccion;

    @Column(name = "label_accion", length = 50)
    private String labelAccion;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato", nullable = true)
    private Hato hato;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}