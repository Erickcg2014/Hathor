
package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository;

import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IRepositoryHatoPractica extends JpaRepository<HatoPractica, UUID> {

    // Verifica si una práctica ya está asignada al hato como
    // PENDIENTE o EN_CURSO — evita duplicar prácticas en cada
    // recálculo del motor de reglas.
    @Query("""
        SELECT COUNT(hp) > 0 FROM HatoPractica hp
        WHERE hp.hato.idHato     = :idHato
          AND hp.practica.idPractica = :idPractica
          AND hp.estado IN ('PENDIENTE', 'EN_CURSO')
        """)
    boolean existsPendienteOEnCurso(
            @Param("idHato")      UUID    idHato,
            @Param("idPractica")  Integer idPractica
    );

    // Prácticas asignadas al hato con su estado — para el
    // módulo de prácticas del frontend, ordenadas por estado
    // (EN_CURSO primero) y fecha de inicio descendente.
    @Query("""
        SELECT hp FROM HatoPractica hp
        WHERE hp.hato.idHato = :idHato
        ORDER BY
            CASE hp.estado
                WHEN 'EN_CURSO'   THEN 1
                WHEN 'PENDIENTE'  THEN 2
                WHEN 'COMPLETADA' THEN 3
                ELSE 4
            END ASC,
            hp.fechaInicio DESC NULLS LAST
        """)
    List<HatoPractica> findByHatoOrdenadas(@Param("idHato") UUID idHato);

    // Prácticas vinculadas a una recomendación específica,
    // ordenadas por estado (EN_CURSO primero) y orden de creación.
    @Query("""
        SELECT hp FROM HatoPractica hp
        WHERE hp.recomendacion.idRecomendacionHato = :idRecomendacion
        ORDER BY
            CASE hp.estado
                WHEN 'EN_CURSO'   THEN 1
                WHEN 'PENDIENTE'  THEN 2
                WHEN 'COMPLETADA' THEN 3
                ELSE 4
            END ASC
        """)
    List<HatoPractica> findByRecomendacion(@Param("idRecomendacion") Integer idRecomendacion);

    // KPIs impactados por prácticas EN_CURSO del hato
    @Query("""
        SELECT DISTINCT hp.practica.kpiImpactado
        FROM HatoPractica hp
        WHERE hp.hato.idHato = :idHato
        AND hp.estado       = 'EN_CURSO'
        AND hp.practica.kpiImpactado IS NOT NULL
        """)
    List<String> findKpisEnCursoByHato(
            @Param("idHato") UUID idHato);

    // Count prácticas por estado
    @Query("""
        SELECT COUNT(hp) FROM HatoPractica hp
        WHERE hp.hato.idHato = :idHato
        AND hp.estado       = :estado
        """)
    long countByHatoAndEstado(
            @Param("idHato") UUID   idHato,
            @Param("estado") String estado);

    void deleteByHato_IdHato(UUID idHato);
}