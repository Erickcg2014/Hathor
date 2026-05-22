package com.hathor.hathorback.Entities.Finanzas;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "perfil_financiero_detalle")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerfilFinancieroDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_detalle")
    private UUID idDetalle;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_perfil_financiero", nullable = false)
    private PerfilFinanciero perfilFinanciero;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaFinanciera categoriaFinanciera;

    @Column(name = "tipo", nullable = false)
    private String tipo; // INGRESO, GASTO, COSTO, INVERSION

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "monto_mensual", nullable = false)
    private Float montoMensual;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDate fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDate.now();
    }
}