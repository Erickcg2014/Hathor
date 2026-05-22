package com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hathor.hathorback.Entities.Finanzas.PerfilFinancieroDetalle;

import java.util.List;
import java.util.UUID;

public interface IRepositoryPerfilFinancieroDetalle
    extends JpaRepository<PerfilFinancieroDetalle, UUID> {

    List<PerfilFinancieroDetalle> findByPerfilFinanciero_IdPerfilFinanciero(UUID idPerfil);

    // TODO: PRUEBA — eliminar antes de producción
    @Modifying
    @Query("""
        DELETE FROM PerfilFinancieroDetalle d
        WHERE d.perfilFinanciero.hato.idHato = :idHato
    """)
    void deleteByHatoIdHato(@Param("idHato") UUID idHato);
}