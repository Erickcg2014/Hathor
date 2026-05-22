package com.hathor.hathorback.Entities.Alertas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerta_hato")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertaHato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long idAlerta;

    // TENDENCIA_KPI_NEGATIVA | MARGEN_NETO_NEGATIVO |
    // CAIDA_PRODUCCION | COSTO_ALIMENTACION_ALTO |
    // VACAS_ORDENIO_BAJO | KPI_CRITICO_SIN_PRACTICA |
    // MEJORA_PERCENTIL | PRACTICA_DISPONIBLE |
    // KPI_ALCANZO_OPTIMO
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    // CRITICA | PREVENTIVA | OPORTUNIDAD
    @Column(name = "severidad", nullable = false, length = 20)
    private String severidad;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "leida", nullable = false)
    @Builder.Default
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Column(name = "codigo_kpi", length = 50)
    private String codigoKpi;

    // ej: meses consecutivos,
    // porcentaje de caída, etc.
    @Column(name = "valor_referencia")
    private Float valorReferencia;

    // ACTIVA | EXPIRADA | RESUELTA
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVA";

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaExpiracion == null) {
            fechaExpiracion = switch (severidad) {
                case "CRITICA"     -> LocalDate.now().plusDays(30);
                case "PREVENTIVA"  -> LocalDate.now().plusDays(15);
                case "OPORTUNIDAD" -> LocalDate.now().plusDays(7);
                default            -> LocalDate.now().plusDays(15);
            };
        }
    }
}