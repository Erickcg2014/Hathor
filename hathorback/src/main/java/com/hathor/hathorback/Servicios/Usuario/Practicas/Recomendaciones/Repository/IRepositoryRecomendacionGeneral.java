package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.Repository;

import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IRepositoryRecomendacionGeneral
        extends JpaRepository<RecomendacionGeneral, Long> {

    // Todas las activas de un hato + las globales (id_hato null)
    @Query("""
        SELECT r FROM RecomendacionGeneral r
        WHERE r.estado = 'ACTIVA'
          AND (r.hato.idHato = :idHato OR r.hato IS NULL)
        ORDER BY
          CASE r.prioridad
            WHEN 'ALTA'  THEN 1
            WHEN 'MEDIA' THEN 2
            ELSE 3
          END ASC,
          r.fechaCreacion DESC
        """)
    List<RecomendacionGeneral> findActivasByHato(
            @Param("idHato") UUID idHato);

    // Solo las de un tipo específico
    @Query("""
        SELECT r FROM RecomendacionGeneral r
        WHERE r.estado    = 'ACTIVA'
          AND r.tipo      = :tipo
          AND (r.hato.idHato = :idHato OR r.hato IS NULL)
        ORDER BY r.fechaCreacion DESC
        """)
    List<RecomendacionGeneral> findActivasByHatoAndTipo(
            @Param("idHato") UUID   idHato,
            @Param("tipo")   String tipo);

    // Todas las globales — para el admin
    @Query("""
        SELECT r FROM RecomendacionGeneral r
        WHERE r.estado   = 'ACTIVA'
          AND r.hato IS NULL
        ORDER BY r.fechaCreacion DESC
        """)
    List<RecomendacionGeneral> findGlobalesActivas();

    // Todas las del admin — con o sin hato
    @Query("""
        SELECT r FROM RecomendacionGeneral r
        WHERE r.tipo = 'ADMIN'
        ORDER BY r.fechaCreacion DESC
        """)
    List<RecomendacionGeneral> findAllAdmin();

    // Verificar si ya existe activa del mismo tipo y subtipo
    @Query("""
        SELECT COUNT(r) > 0 FROM RecomendacionGeneral r
        WHERE r.estado  = 'ACTIVA'
          AND r.tipo    = :tipo
          AND r.subtipo = :subtipo
          AND (r.hato.idHato = :idHato OR r.hato IS NULL)
        """)
    boolean existeActivaDelTipo(
            @Param("idHato")  UUID   idHato,
            @Param("tipo")    String tipo,
            @Param("subtipo") String subtipo);

    // Expirar las vencidas
    @Modifying
    @Query("""
        UPDATE RecomendacionGeneral r
        SET r.estado = 'EXPIRADA'
        WHERE r.estado          = 'ACTIVA'
          AND r.fechaExpiracion < :hoy
        """)
    int expirarVencidas(@Param("hoy") LocalDate hoy);

    // Marcar todas como leídas para un hato
    @Modifying
    @Query("""
        UPDATE RecomendacionGeneral r
        SET r.leida = true
        WHERE r.estado = 'ACTIVA'
          AND (r.hato.idHato = :idHato OR r.hato IS NULL)
        """)
    void marcarTodasLeidasByHato(@Param("idHato") UUID idHato);

    // Count no leídas para badge
    @Query("""
        SELECT COUNT(r) FROM RecomendacionGeneral r
        WHERE r.estado = 'ACTIVA'
          AND r.leida  = false
          AND (r.hato.idHato = :idHato OR r.hato IS NULL)
        """)
    long countNoLeidasByHato(@Param("idHato") UUID idHato);

    // Buscar plantillas por subtipo
    @Query("""
        SELECT r FROM RecomendacionGeneral r
        WHERE r.tipo    = 'CLIMA'
        AND r.subtipo = :subtipo
        AND r.estado  = 'PLANTILLA'
        """)
    List<RecomendacionGeneral> findPlantillasBySubtipo(
            @Param("subtipo") String subtipo);
}