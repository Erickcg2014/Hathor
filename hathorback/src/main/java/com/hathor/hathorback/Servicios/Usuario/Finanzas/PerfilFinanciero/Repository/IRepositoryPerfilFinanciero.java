package com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hathor.hathorback.Entities.Finanzas.PerfilFinanciero;

import java.util.List;
import java.util.UUID;

public interface IRepositoryPerfilFinanciero
    extends JpaRepository<PerfilFinanciero, UUID> {

    List<PerfilFinanciero> findByHato_IdHato(UUID idHato);
    boolean existsByHato_IdHato(UUID idHato);

    // TODO: PRUEBA — eliminar antes de producción
    @Modifying
    @Query("DELETE FROM PerfilFinanciero p WHERE p.hato.idHato = :idHato")
    void deleteByHatoIdHato(@Param("idHato") UUID idHato);
}