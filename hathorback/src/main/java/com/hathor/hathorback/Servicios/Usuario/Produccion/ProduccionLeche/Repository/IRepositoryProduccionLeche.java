package com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRepositoryProduccionLeche extends JpaRepository<ProduccionLeche, UUID> {
    List<ProduccionLeche> findByHato_IdHato(UUID idHato);
    @Modifying
    @Query("DELETE FROM ProduccionLeche p WHERE p.hato.idHato = :idHato")
    void deleteByHatoId(@Param("idHato") UUID idHato);

    void deleteByHato_IdHato(UUID idHato);


    List<ProduccionLeche> findByHato_IdHatoOrderByFechaDesc(UUID idHato);

    // Producción total por mes
    @Query("""
        SELECT
            FUNCTION('TO_CHAR', p.fecha, 'YYYY-MM') as mes,
            SUM(p.litrosProducidos) as total
        FROM ProduccionLeche p
        WHERE p.hato.idHato = :idHato
        AND p.fecha       >= :fechaDesde
        GROUP BY FUNCTION('TO_CHAR', p.fecha, 'YYYY-MM')
        ORDER BY mes ASC
        """)
    List<Object[]> findProduccionMensualUltimosMeses(
            @Param("idHato")     UUID      idHato,
            @Param("fechaDesde") LocalDate fechaDesde);

    @Query(value = """
        SELECT COALESCE(SUM(p.litros_producidos), 0)
        FROM produccion_leche p
        WHERE p.id_hato = :idHato
        AND TO_CHAR(p.fecha, 'YYYY-MM') = :mes
    """, nativeQuery = true)
    Float sumLitrosProducidosByHatoAndMes(
        @Param("idHato") UUID idHato,
        @Param("mes") String mes
    );
}