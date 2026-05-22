package com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRepositoryBenchmarkReferencia extends JpaRepository<BenchmarkReferencia, Integer> {

    // Buscar por código KPI y trópico específico
    List<BenchmarkReferencia> findByKpi_CodigoAndTropico(String codigo, String tropico);

    // Buscar el benchmark nacional (tropico = null)
    List<BenchmarkReferencia> findByKpi_CodigoAndTropicoIsNull(String codigo);

    // Todos los benchmarks de un KPI (para listar opciones)
    List<BenchmarkReferencia> findByKpi_Codigo(String codigo);

    // Todos los benchmarks de un trópico (FUTURO)
    List<BenchmarkReferencia> findByTropico(String tropico);

        @Query("""
            SELECT b FROM BenchmarkReferencia b
            WHERE b.kpi.idKpi = :idKpi
            AND (
                (b.region = :region AND b.tropico = :tropico)
                OR (b.region = :region AND b.tropico IS NULL)
                OR (b.region IS NULL AND b.tropico = :tropico)
                OR (b.region = 'NACIONAL')
            )
        """)
        List<BenchmarkReferencia> findBenchmarkCombinado(
            @Param("idKpi") int idKpi,
            @Param("region") String region,
            @Param("tropico") String tropico
        );
    
    // Busca el benchmark MÁS ESPECÍFICO disponible para un KPI,
    @Query("""
        SELECT b FROM BenchmarkReferencia b
        WHERE b.kpi.codigo = :codigoKpi
          AND (b.tropico IS NULL OR b.tropico = :tropico)
          AND (b.escala  IS NULL OR b.escala  = :escala)
          AND (b.region  IS NULL OR b.region  = :region OR b.region = 'NACIONAL')
        ORDER BY
            CASE
                WHEN b.tropico = :tropico AND b.region = :region AND b.escala = :escala THEN 1
                WHEN b.tropico = :tropico AND b.escala = :escala AND b.region IS NULL   THEN 2
                WHEN b.tropico = :tropico AND b.region = :region AND b.escala IS NULL   THEN 3
                WHEN b.tropico = :tropico AND b.region IS NULL   AND b.escala IS NULL   THEN 4
                WHEN b.tropico IS NULL    AND b.escala = :escala                        THEN 5
                ELSE 6
            END ASC
        LIMIT 1
        """)
    Optional<BenchmarkReferencia> findMasEspecifico(
            @Param("codigoKpi") String codigoKpi,
            @Param("tropico")   String tropico,
            @Param("region")    String region,
            @Param("escala")    String escala
    );
}