package com.hathor.hathorback.Servicios.Usuario.Alertas.Repository;

import com.hathor.hathorback.Entities.Alertas.AlertaHato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRepositoryAlertaHato
        extends JpaRepository<AlertaHato, Long> {

    // Todas las alertas activas de un hato ordenadas por severidad
    @Query("""
        SELECT a FROM AlertaHato a
        WHERE a.hato.idHato = :idHato
          AND a.estado = 'ACTIVA'
        ORDER BY
          CASE a.severidad
            WHEN 'CRITICA'     THEN 1
            WHEN 'PREVENTIVA'  THEN 2
            WHEN 'OPORTUNIDAD' THEN 3
            ELSE 4
          END ASC,
          a.fechaCreacion DESC
        """)
    List<AlertaHato> findActivasByHato(
            @Param("idHato") UUID idHato);

    // Alertas no leídas de un hato
    @Query("""
        SELECT a FROM AlertaHato a
        WHERE a.hato.idHato = :idHato
          AND a.estado = 'ACTIVA'
          AND a.leida  = false
        ORDER BY a.fechaCreacion DESC
        """)
    List<AlertaHato> findNoLeidasByHato(
            @Param("idHato") UUID idHato);

    // Count de no leídas — para el badge del sidebar
    @Query("""
        SELECT COUNT(a) FROM AlertaHato a
        WHERE a.hato.idHato = :idHato
          AND a.estado = 'ACTIVA'
          AND a.leida  = false
        """)
    long countNoLeidasByHato(@Param("idHato") UUID idHato);

    // Severidad más alta con alertas no leídas
    @Query("""
        SELECT COALESCE(
          CASE
            WHEN COUNT(CASE WHEN a.severidad = 'CRITICA'    THEN 1 END) > 0
              THEN 'CRITICA'
            WHEN COUNT(CASE WHEN a.severidad = 'PREVENTIVA' THEN 1 END) > 0
              THEN 'PREVENTIVA'
            WHEN COUNT(CASE WHEN a.severidad = 'OPORTUNIDAD' THEN 1 END) > 0
              THEN 'OPORTUNIDAD'
            ELSE 'NINGUNA'
          END, 'NINGUNA')
        FROM AlertaHato a
        WHERE a.hato.idHato = :idHato
          AND a.estado = 'ACTIVA'
          AND a.leida  = false
        """)
    String getSeveridadMaximaNoLeida(@Param("idHato") UUID idHato);

    // Verificar si ya existe alerta activa del mismo tipo
    @Query("""
        SELECT COUNT(a) > 0 FROM AlertaHato a
        WHERE a.hato.idHato = :idHato
          AND a.tipo         = :tipo
          AND a.estado       = 'ACTIVA'
        """)
    boolean existeAlertaActivaDelTipo(
            @Param("idHato") UUID   idHato,
            @Param("tipo")   String tipo);

    // Para el admin — hatos con alertas críticas activas
    @Query("""
        SELECT a FROM AlertaHato a
        WHERE a.estado    = 'ACTIVA'
          AND a.severidad = 'CRITICA'
          AND a.leida     = false
        ORDER BY a.fechaCreacion DESC
        """)
    List<AlertaHato> findCriticasActivasGlobal();

    // Count de hatos distintos con alertas críticas
    @Query("""
        SELECT COUNT(DISTINCT a.hato.idHato)
        FROM AlertaHato a
        WHERE a.estado    = 'ACTIVA'
          AND a.severidad = 'CRITICA'
          AND a.leida     = false
        """)
    long countHatosConCriticas();

    // Expirar alertas vencidas — usado en el job semanal
    @Modifying
    @Query("""
        UPDATE AlertaHato a
        SET a.estado = 'EXPIRADA'
        WHERE a.estado           = 'ACTIVA'
          AND a.fechaExpiracion  < :hoy
        """)
    int expirarAlertas(@Param("hoy") LocalDate hoy);

    // Marcar todas como leídas para un hato
    @Modifying
    @Query("""
        UPDATE AlertaHato a
        SET a.leida = true
        WHERE a.hato.idHato = :idHato
          AND a.estado       = 'ACTIVA'
        """)
    void marcarTodasLeidasByHato(@Param("idHato") UUID idHato);
}