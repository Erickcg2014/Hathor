package com.hathor.hathorback.Entities.Finanzas;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "perfil_financiero")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerfilFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_perfil_financiero")
    private UUID idPerfilFinanciero;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @Column(name = "metodo_registro", nullable = false)
    private String metodoRegistro; // EXCEL, MANUAL, OMITIDO

    @Column(name = "periodo")
    private String periodo; 

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDate fechaRegistro;

    @ToString.Exclude
    @OneToMany(mappedBy = "perfilFinanciero", cascade = CascadeType.ALL)
    private List<PerfilFinancieroDetalle> detalles;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDate.now();
    }
}