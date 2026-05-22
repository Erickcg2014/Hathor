package com.hathor.hathorback.Entities.Kpi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kpi")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kpi")
    private Integer idKpi;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @Column(name = "formula", columnDefinition = "text")
    private String formula;

    @Column(name = "unidad")
    private String unidad;
    
    @Column(name = "categoria", length = 20)
    private String categoria;

    @Column(name = "codigo", unique = true)
    private String codigo;
}