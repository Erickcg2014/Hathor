package com.hathor.hathorback.Entities.Finanzas;

import java.time.LocalDate;
import java.util.UUID;

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
@Table(name = "ventaleche")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class VentaLeche {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ventaleche")
    private UUID idVentaLeche;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "precio_litro", nullable = false)
    private float precioLitro;

    @Column(name = "litros_vendidos", nullable = true)
    private float litrosVendidos;

    @OneToOne
    @JoinColumn(name = "id_registro")
    private RegistroFinanciero registroFinanciero;
}
