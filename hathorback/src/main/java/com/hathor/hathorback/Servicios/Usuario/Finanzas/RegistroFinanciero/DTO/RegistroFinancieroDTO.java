package com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.DTO;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class RegistroFinancieroDTO {
    private UUID id_hato;
    private UUID id_categoria;
    private String titulo;
    private String tipo_movimiento;
    private LocalDate fecha;
    private String descripcion;
    private float monto;
    private Float precio_litro_leche;
    private Float litros_vendidos_leche;

    public UUID getId_hato() { return id_hato; }
    public UUID getId_categoria() { return id_categoria; }
    public String getTitulo() { return titulo; }
    public String getTipo_movimiento() { return tipo_movimiento; }
    public LocalDate getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public float getMonto() { return monto; }
    public Float getPrecio_litro_leche() { return precio_litro_leche; }
    public Float getLitros_vendidos_leche() { return litros_vendidos_leche; }
}
