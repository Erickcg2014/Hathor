package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository;

import com.hathor.hathorback.Entities.Practicas.HatoPracticaPaso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRepositoryHatoPracticaPaso
        extends JpaRepository<HatoPracticaPaso, Long> {

    List<HatoPracticaPaso> findByHatoPractica_IdHatoPractica(
            UUID idHatoPractica);

    @Modifying
    @Query("DELETE FROM HatoPracticaPaso p " +
           "WHERE p.hatoPractica.idHatoPractica = :idHatoPractica")
    void deleteByHatoPractica(@Param("idHatoPractica") UUID idHatoPractica);
}