package com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;

import java.util.UUID;
import java.util.List;

public interface IRepositoryRecomendacionHato extends JpaRepository<RecomendacionHato, Integer> {

    // Elimina FÍSICAMENTE las recomendaciones ACTIVAS de un hato
    // antes de generar las nuevas en cada recálculo de KPIs.
    // @Modifying requiere @Transactional en el service que lo llame.
    @Modifying
    @Query("""
        UPDATE RecomendacionHato r
        SET r.tipoEstado = 'INACTIVA'
        WHERE r.hato.idHato = :idHato
        AND r.tipoEstado  = 'ACTIVA'
        """)
    void desactivarActivasByHato(@Param("idHato") UUID idHato);

    // Recomendaciones activas del hato ordenadas por prioridad,
    // paginadas para el frontend (lista de alertas del dashboard).
    // Prioridad: ALTA primero → se ordena por texto con CASE en JPQL
    // o simplemente por el campo prioridad si es numérico en BD.
    // Como prioridad es Integer en la entidad Regla pero String
    // en RecomendacionHato, ordenamos alfabéticamente invertido
    // (ALTA > BAJA > MEDIA en orden DESC) — o usamos ORDER BY
    // CASE para control exacto.
    @Query("""
        SELECT rh FROM RecomendacionHato rh
        WHERE rh.hato.idHato = :idHato
          AND rh.tipoEstado = 'ACTIVA'
        ORDER BY
            CASE rh.prioridad
                WHEN 'ALTA'  THEN 1
                WHEN 'MEDIA' THEN 2
                WHEN 'BAJA'  THEN 3
                ELSE 4
            END ASC,
            rh.fechaCreacion DESC
        """)
    Page<RecomendacionHato> findActivasByHatoPaginado(
            @Param("idHato") UUID idHato,
            Pageable pageable
    );

    // Cuenta recomendaciones activas no leídas — para el badge
    // de notificaciones en el sidebar del frontend.
    @Query("""
        SELECT COUNT(rh) FROM RecomendacionHato rh
        WHERE rh.hato.idHato = :idHato
          AND rh.tipoEstado = 'ACTIVA'
          AND rh.leida = false
        """)
    long countNoLeidasByHato(@Param("idHato") UUID idHato);

    // Todas las recomendaciones de un hato sin paginación — para admin
    @Query("""
        SELECT rh FROM RecomendacionHato rh
        WHERE rh.hato.idHato = :idHato
        ORDER BY
            CASE rh.prioridad
                WHEN 'ALTA'  THEN 1
                WHEN 'MEDIA' THEN 2
                WHEN 'BAJA'  THEN 3
                ELSE 4
            END ASC,
            rh.fechaCreacion DESC
        """)
    List<RecomendacionHato> findAllByHato(
            @Param("idHato") UUID idHato
    );

    // Todas las recomendaciones con filtros — para panel admin
    @Query("""
        SELECT rh FROM RecomendacionHato rh
        WHERE (:idHato     IS NULL OR rh.hato.idHato    = :idHato)
        AND (:tipoEstado IS NULL OR rh.tipoEstado     = :tipoEstado)
        AND (:prioridad  IS NULL OR rh.prioridad      = :prioridad)
        ORDER BY
            CASE rh.prioridad
                WHEN 'ALTA'  THEN 1
                WHEN 'MEDIA' THEN 2
                WHEN 'BAJA'  THEN 3
                ELSE 4
            END ASC,
            rh.fechaCreacion DESC
        """)
    List<RecomendacionHato> findAllFiltrado(
            @Param("idHato")     UUID   idHato,
            @Param("tipoEstado") String tipoEstado,
            @Param("prioridad")  String prioridad
    );
}