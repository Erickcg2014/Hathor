package com.hathor.hathorback.Entities.Hato;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.RegistroHatoDTO;

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
@Table(name = "hato")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_hato")
    private UUID idHato;

    @Column(name = "nombre", nullable = false)
    private String nombreHato;

    @Column(name = "departamento", nullable = false)
    private String departamento;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "direccion", nullable = true)
    private String direccion;

    @Column(name = "altitud", nullable = false)
    private float altitud;

    @Column(name = "tropico", nullable = false)
    private String tropico;

    @Column(name = "area_hato", nullable = false)
    private float areaHato;

    @Column(name = "area_pastoreo", nullable = false)
    private float areaPastoreo;

    @Column(name = "cant_corrales", nullable = false)
    private int cantCorrales;

    @Column(name = "cant_salasordenio", nullable = false)
    private int cantSalasOrdenio;

    @Column(name = "capacidad_almacenarleche", nullable = false)
    private float capacidadAlmacenarLeche;

    @Column(name = "cant_empleadospermanentes", nullable = false)
    private int cantEmpleadosPermanentes;

    @Column(name = "cant_empleadostemporales", nullable = false)
    private int cantEmpleadosTemporales;

    @Column(name = "tipo_hato", nullable = false)
    private String tipoHato;

    @Column(name = "porcentaje_completitud", nullable = false)
    private int porcentajeCompletitud;
    
    @Column(name = "gasto_mensual_nomina")
    private Double gastoMensualNomina;

    @Column(name = "gasto_mensual_alimentacion")
    private Double gastoMensualAlimentacion;

    @Column(name = "escala")
    private String escala;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (porcentajeCompletitud == 0) {
            porcentajeCompletitud = 25; 
        }
    }

}
