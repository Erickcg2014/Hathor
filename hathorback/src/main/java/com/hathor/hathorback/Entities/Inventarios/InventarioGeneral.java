package com.hathor.hathorback.Entities.Inventarios;

import java.time.LocalDate;
import java.util.UUID;

import com.hathor.hathorback.Entities.Hato.Hato;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventario_general")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_inventario_general")
    private UUID idInventarioGeneral;

    @ManyToOne
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @ManyToOne
    @JoinColumn(name = "id_categoria_inventario", nullable = false)
    private CategoriaInventario categoriaInventario;

    @Column(name = "nombre_item", nullable = false)
    private String nombreItem;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "valor_unitario", nullable = false)
    private Float valorUnitario;

    @Column(name = "valor_total")
    private Float valorTotal;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "descripcion")
    private String descripcion;

    @PrePersist
    public void prePersist() {
        this.valorTotal = this.cantidad * this.valorUnitario;
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();  
        }
    }
}
