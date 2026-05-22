package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.Repository;

import com.hathor.hathorback.Entities.Finanzas.InversionPlaneada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRepositoryInversionPlaneada
        extends JpaRepository<InversionPlaneada, Long> {

    // Todas las inversiones de un hato ordenadas por mes
    @Query("""
        SELECT i FROM InversionPlaneada i
        LEFT JOIN FETCH i.categoriaFinanciera
        WHERE i.hato.idHato = :idHato
          AND i.estado != 'CANCELADA'
        ORDER BY i.mesEjecucion ASC
        """)
    List<InversionPlaneada> findActivasByHato(
            @Param("idHato") UUID idHato);

    // Inversiones planeadas para un mes específico
    @Query("""
        SELECT i FROM InversionPlaneada i
        LEFT JOIN FETCH i.categoriaFinanciera
        WHERE i.hato.idHato   = :idHato
          AND i.mesEjecucion  = :mes
          AND i.estado        = 'PLANEADA'
        """)
    List<InversionPlaneada> findPlaneadasByHatoAndMes(
            @Param("idHato") UUID   idHato,
            @Param("mes")    String mes);

    // Inversiones en un rango de meses
    @Query("""
        SELECT i FROM InversionPlaneada i
        LEFT JOIN FETCH i.categoriaFinanciera
        WHERE i.hato.idHato  = :idHato
          AND i.mesEjecucion >= :mesDesde
          AND i.mesEjecucion <= :mesHasta
          AND i.estado        = 'PLANEADA'
        ORDER BY i.mesEjecucion ASC
        """)
    List<InversionPlaneada> findPlaneadasByHatoAndRango(
            @Param("idHato")   UUID   idHato,
            @Param("mesDesde") String mesDesde,
            @Param("mesHasta") String mesHasta);

    // Total invertido por mes 
    @Query("""
        SELECT i.mesEjecucion, SUM(i.monto)
        FROM InversionPlaneada i
        WHERE i.hato.idHato  = :idHato
          AND i.estado        = 'PLANEADA'
        GROUP BY i.mesEjecucion
        ORDER BY i.mesEjecucion ASC
        """)
    List<Object[]> findTotalPorMes(
            @Param("idHato") UUID idHato);
}