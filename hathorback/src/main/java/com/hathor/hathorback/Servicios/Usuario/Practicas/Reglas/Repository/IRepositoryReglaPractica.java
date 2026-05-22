package com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository;

import com.hathor.hathorback.Entities.Practicas.ReglaPractica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRepositoryReglaPractica extends JpaRepository<ReglaPractica, Integer> {

    // Prácticas de una regla ordenadas por prioridad —
    // el motor las recorre en este orden para crear HatoPractica.
    @Query("""
        SELECT rp FROM ReglaPractica rp
        JOIN FETCH rp.practica
        WHERE rp.regla.idRegla = :idRegla
        ORDER BY rp.orden ASC
        """)
    List<ReglaPractica> findByReglaOrdenadas(@Param("idRegla") Integer idRegla);

    // Eliminar todos los vínculos de una regla
    @Modifying
    @Query("DELETE FROM ReglaPractica rp WHERE rp.regla.idRegla = :idRegla")
    void deleteByRegla_IdRegla(@Param("idRegla") Integer idRegla);

    // Verificar si ya existe el vínculo
    @Query("""
        SELECT COUNT(rp) > 0 FROM ReglaPractica rp
        WHERE rp.regla.idRegla       = :idRegla
        AND rp.practica.idPractica = :idPractica
        """)
    boolean existsByRegla_IdReglaAndPractica_IdPractica(
            @Param("idRegla")    Integer idRegla,
            @Param("idPractica") Integer idPractica
    );

    // Eliminar vínculo específico regla-práctica
    @Modifying
    @Query("""
        DELETE FROM ReglaPractica rp
        WHERE rp.regla.idRegla       = :idRegla
        AND rp.practica.idPractica = :idPractica
        """)
    void deleteByRegla_IdReglaAndPractica_IdPractica(
            @Param("idRegla")    Integer idRegla,
            @Param("idPractica") Integer idPractica
    );

    // ADMIN
    // Reglas vinculadas a una práctica específica
    @Query("""
        SELECT rp FROM ReglaPractica rp
        JOIN FETCH rp.regla r
        JOIN FETCH r.kpi
        WHERE rp.practica.idPractica = :idPractica
        ORDER BY r.prioridad ASC
        """)
    List<ReglaPractica> findByPractica_IdPractica(
            @Param("idPractica") Integer idPractica
    );
}