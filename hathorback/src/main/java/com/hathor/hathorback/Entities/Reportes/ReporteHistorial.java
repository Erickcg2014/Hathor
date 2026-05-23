package com.hathor.hathorback.Entities.Reportes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reporte_historial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer idReporte;

    // MANUAL | MENSUAL | TRIMESTRAL
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "configuracion_json", columnDefinition = "TEXT")
    private String configuracionJson;

    @Column(name = "periodo_desde")
    private String periodoDesde;

    @Column(name = "periodo_hasta")
    private String periodoHasta;

    @Column(name = "url_archivo", columnDefinition = "TEXT")
    private String urlArchivo;

    @Column(name = "tamanio_bytes")
    private Long tamanioBytes;

    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "GENERADO";

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;
}