package com.hathor.hathorback.Servicios.Usuario.Reporte.Repository;

import com.hathor.hathorback.Entities.Reportes.ReporteHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRepositoryReporteHistorial
        extends JpaRepository<ReporteHistorial, Integer> {

    // Todos los reportes de un hato ordenados por fecha desc
    @Query("""
        SELECT r FROM ReporteHistorial r
        WHERE r.hato.idHato = :idHato
        ORDER BY r.fechaGeneracion DESC
        """)
    List<ReporteHistorial> findByHatoOrderByFechaDesc(
            @Param("idHato") UUID idHato
    );

    // Reportes por tipo — para filtrar MENSUAL, MANUAL, TRIMESTRAL
    @Query("""
        SELECT r FROM ReporteHistorial r
        WHERE r.hato.idHato = :idHato
          AND r.tipo = :tipo
        ORDER BY r.fechaGeneracion DESC
        """)
    List<ReporteHistorial> findByHatoAndTipo(
            @Param("idHato") UUID  idHato,
            @Param("tipo")   String tipo
    );

    // Verificar si ya existe un reporte mensual para un período
    @Query("""
        SELECT COUNT(r) > 0 FROM ReporteHistorial r
        WHERE r.hato.idHato   = :idHato
          AND r.tipo          = 'MENSUAL'
          AND r.periodoDesde  = :periodoDesde
        """)
    boolean existeReporteMensual(
            @Param("idHato")      UUID   idHato,
            @Param("periodoDesde") String periodoDesde
    );

    // Cantidad de reportes por hato — para límite de historial
    @Query("""
        SELECT COUNT(r) FROM ReporteHistorial r
        WHERE r.hato.idHato = :idHato
        """)
    long countByHato(@Param("idHato") UUID idHato);
}