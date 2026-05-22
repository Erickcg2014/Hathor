package com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Repository;

import com.hathor.hathorback.Entities.Benchmark.RankingHato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRepositoryRankingHato 
    extends JpaRepository<RankingHato, UUID> {

    Optional<RankingHato> findByHato_IdHato(UUID idHato);

    List<RankingHato> findAllByOrderByPosicionNacionalAsc();

    @Query("""
        SELECT r FROM RankingHato r
        WHERE r.hato.departamento = :departamento
        ORDER BY r.posicionRegional ASC
        """)
    List<RankingHato> findAllByDepartamentoOrderByPosicion(
        @Param("departamento") String departamento
    );
}