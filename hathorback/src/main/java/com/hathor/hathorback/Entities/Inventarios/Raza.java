package com.hathor.hathorback.Entities.Inventarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "raza")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Raza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raza")
    private Integer idRaza;

    @Column(name = "tipo_raza")
    private String tipoRaza;

    @Column(name = "nombre", nullable = false)
    private String nombre;
}
