package com.hathor.hathorback.Entities.Inventarios;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categoria_inventario")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoriaInventario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "tipo", nullable = false)
    private String tipo; // PADRE, HIJA

    @Column(name = "es_predefinida", nullable = false)
    private Boolean esPredefinida;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @Column(name = "orden")
    private Short orden;

    @Column(name = "activa")
    private Boolean activa;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_categoria_padre")
    private CategoriaInventario categoriaPadre;

    @Column(name = "id_usuario")
    private java.util.UUID idUsuario;
}