package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;


public interface IRepositoryBenchmarking extends JpaRepository<BenchmarkHato, Integer>{
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM BenchmarkHato b WHERE b.hato.idHato = :idHato")
    void deleteByHato_IdHato(@Param("idHato") UUID idHato);
    void deleteByHato_IdHatoAndNivelBenchmark(UUID idHato, String nivelBenchmark);

    public List<BenchmarkHato> findAllByHato_IdHato(UUID idHato);

   @Query("""
        SELECT b FROM BenchmarkHato b
        JOIN FETCH b.kpi k
        LEFT JOIN FETCH b.benchReferencia br
        WHERE b.hato.idHato = :idHato
        AND ((:modo = 'REFERENCIA' AND b.benchReferencia IS NOT NULL)
            OR (:modo = 'PLATAFORMA' AND b.benchReferencia IS NULL))
        AND (:nivel IS NULL OR b.nivelBenchmark = :nivel)
        AND (:categoria IS NULL OR k.categoria = :categoria)
        ORDER BY k.nombre ASC
        """)
    List<BenchmarkHato> findByHatoFiltrado(
                @Param("idHato")    UUID   idHato,
                @Param("modo")      String modo,
                @Param("nivel")     String nivel,
                @Param("categoria") String categoria
    );

    /**
     * Trae los últimos benchmark_hato de todos los hatos
     * con nivel NACIONAL para calcular ranking.
     * Si region != null filtra por departamento del hato.
     */
    @Query("""
        SELECT b FROM BenchmarkHato b
        JOIN FETCH b.hato h
        WHERE b.nivelBenchmark = 'NACIONAL'
        AND b.percentil IS NOT NULL
        AND (:region IS NULL OR h.departamento = :region)
        AND b.fechaCalculo = (
            SELECT MAX(b2.fechaCalculo)
            FROM BenchmarkHato b2
            WHERE b2.hato.idHato = b.hato.idHato
            AND b2.nivelBenchmark = 'NACIONAL'
        )
        """)
    List<BenchmarkHato> findTodosParaRanking(
        @Param("region") String region
    );

    @Query("""
        SELECT b FROM BenchmarkHato b
        JOIN FETCH b.kpi k
        LEFT JOIN FETCH b.benchReferencia br
        WHERE b.hato.idHato = :idHato
        AND b.benchReferencia IS NOT NULL
        ORDER BY b.nivelBenchmark ASC, k.nombre ASC
        """)
    List<BenchmarkHato> findAllConReferenciaByHato(
            @Param("idHato") UUID idHato
    );
}
