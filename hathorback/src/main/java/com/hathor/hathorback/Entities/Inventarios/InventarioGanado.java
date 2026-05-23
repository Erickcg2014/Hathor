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
@Table(name = "inventario_ganado")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioGanado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_inventario")
    private UUID idInventario;

    @ManyToOne
    @JoinColumn(name = "id_hato", nullable = false)
    private Hato hato;

    @ManyToOne
    @JoinColumn(name = "id_raza", nullable = false)
    private Raza raza;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaGanado categoriaGanado;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "edad_promedio_meses")
    private Integer edadPromedioMeses;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "valor_unitario", nullable = false)
    private Float valorUnitario;

    @Column(name = "valor_total")
    private Float valorTotal;

    @PrePersist
    public void prePersist() {
        this.valorTotal = this.cantidad * this.valorUnitario;
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now(); 
        }
    }
}
