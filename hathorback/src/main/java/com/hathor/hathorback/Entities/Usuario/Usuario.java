package com.hathor.hathorback.Entities.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.enums.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Column(name = "id_auth", nullable = false, unique = true)
    private UUID idAuth;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo", nullable = false)
    private String correo;

    @Column(name = "celular", nullable = false)
    private String celular;

    @Column(name = "estado", insertable = false)
    private Boolean estado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDate fecha_creacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Hato> hatos;

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = true;
        }
        if (rol == null) {
            rol = Rol.USER;
        }
        this.fecha_creacion = LocalDate.now();
    }

    public Usuario(UUID idAuth, String nombre, String apellido, String correo, String celular, Boolean estado, LocalDate fecha_creacion, Rol rol) {
        this.idAuth = idAuth;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.celular = celular;
        this.estado = estado;
        this.fecha_creacion = fecha_creacion;
        this.rol = rol;
    }

    public Usuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public UUID getIdUsuario() {
        return this.idUsuario;
    }
}
