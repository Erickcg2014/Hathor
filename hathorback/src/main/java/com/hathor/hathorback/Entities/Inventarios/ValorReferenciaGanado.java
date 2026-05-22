package com.hathor.hathorback.Entities.Inventarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "valorreferenciaganado")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValorReferenciaGanado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valor_referencia")
    private Integer idValorReferencia;

    @ManyToOne
    @JoinColumn(name = "id_raza", nullable = false)
    private Raza raza;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaGanado categoriaGanado;

    @Column(name = "valor_promedio")
    private Float valorPromedio;

    @Column(name = "region")
    private String region;

    @Column(name = "anio")
    private Integer anio;
}
