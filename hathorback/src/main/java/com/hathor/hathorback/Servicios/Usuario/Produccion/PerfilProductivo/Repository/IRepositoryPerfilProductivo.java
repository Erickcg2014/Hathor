package com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;

import java.util.Optional;
import java.util.UUID;

public interface IRepositoryPerfilProductivo extends JpaRepository<PerfilProductivo, UUID> {
    Optional<PerfilProductivo> findByHato_IdHato(UUID idHato);
    @Modifying
    @Query("DELETE FROM PerfilProductivo p WHERE p.hato.idHato = :idHato")
    void deleteByHatoId(@Param("idHato") UUID idHato);

    void deleteByHato_IdHato(UUID idHato);

}