package com.hathor.hathorback.Entities.Finanzas;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hathor.hathorback.Entities.Usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categoria_financiera")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaFinanciera {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_categoria")
    private UUID idCategoriaFinanciera;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "id_categoria_padre", nullable = true)
    @JsonIgnoreProperties({"categoriaPadre", "usuario", "esPredefinida", "unidadProduccion"})
    private CategoriaFinanciera categoriaPadre;
    
    @Column(name = "es_predefinida", nullable = false)
    private Boolean esPredefinida;

    @Column(name = "unidad_produccion", nullable = true)
    private String unidadProduccion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonIgnore
    private Usuario usuario;
}
