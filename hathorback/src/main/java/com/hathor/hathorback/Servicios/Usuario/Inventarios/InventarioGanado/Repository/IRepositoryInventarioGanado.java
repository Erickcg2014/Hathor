package com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Inventarios.InventarioGanado;

@Repository
public interface IRepositoryInventarioGanado extends JpaRepository<InventarioGanado, UUID> {
    List<InventarioGanado> findByHato_IdHato(UUID idHato);
    @Modifying
    @Query("DELETE FROM InventarioGanado i WHERE i.hato.idHato = :idHato")
    void deleteByHatoId(@Param("idHato") UUID idHato);

    void deleteByHato_IdHato(UUID idHato);

}
