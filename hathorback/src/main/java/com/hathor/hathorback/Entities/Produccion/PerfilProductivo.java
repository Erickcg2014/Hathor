package com.hathor.hathorback.Entities.Produccion;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "perfil_productivo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerfilProductivo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_perfil")
    private UUID idPerfil;

    @ToString.Exclude
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id_hato", nullable = false, unique = true)
    private Hato hato;

    @Column(name = "raza_predominante", nullable = false)
    private String razaPredominante;

    @Column(name = "produccion_diaria_litros", nullable = false)
    private double produccionDiariaLitros;

    @Column(name = "precio_litro_promedio", nullable = false)
    private double precioLitroPromedio;

    @Column(name = "vacas_en_ordenio")
    private Integer vacasEnOrdenio;

    @Column(name = "frecuencia_ordenio")
    private Integer frecuenciaOrdenio;

    @Column(name = "sistema_ordenio")
    private String sistemaOrdenio;

    @Column(name = "destino_leche")
    private String destinoLeche;

    @Column(name = "periodo_lactancia_promedio")
    private Integer periodoLactanciaPromedio;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDate fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDate.now();
    }
}