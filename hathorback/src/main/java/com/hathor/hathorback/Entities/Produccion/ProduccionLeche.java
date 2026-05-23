package com.hathor.hathorback.Entities.Produccion;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "produccion_leche")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduccionLeche {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_produccion")
    private UUID idProduccion;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "litros_producidos")
    private Float litrosProducidos;

    @Column(name = "vacas_ordenadas")
    private Integer vacasOrdenadas;
}