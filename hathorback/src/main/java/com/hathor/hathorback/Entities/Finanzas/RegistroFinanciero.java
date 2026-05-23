package com.hathor.hathorback.Entities.Finanzas;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.enums.Rol;

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
@Table(name = "registrofinanciero")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroFinanciero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_registro")
    private UUID idRegistro;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", nullable = true)
    private String descripcion;

    @Column(name = "monto", nullable = false)
    private float monto;

    @Column(name = "es_historico", nullable = false)
    private boolean esHistorico = false;

    @Column(name = "precision_fecha")
    private String precisionFecha = "EXACTA"; // ANUAL, MENSUAL, EXACTA

    @ManyToOne
    @JoinColumn(name = "id_hato")
    private Hato hato;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaFinanciera categoriaFinanciera;
}
