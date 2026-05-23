package com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Finanzas.VentaLeche;

@Repository
public interface IRepositoryVentaLeche extends JpaRepository<VentaLeche, UUID> {
    @Modifying
    @Query(value = "DELETE FROM ventaleche WHERE id_registro IN (SELECT id_registro FROM registrofinanciero WHERE id_hato = :idHato)", nativeQuery = true)
    void deleteByHatoId(@Param("idHato") UUID idHato);

    @Query(value = """
        SELECT v.* FROM ventaleche v
        INNER JOIN registrofinanciero r ON v.id_registro = r.id_registro
        WHERE r.id_hato = :idHato
        ORDER BY v.fecha DESC
    """, nativeQuery = true)
    List<VentaLeche> findByHatoId(@Param("idHato") UUID idHato);

    @Query(value = """
        SELECT COALESCE(SUM(v.litros_vendidos), 0)
        FROM ventaleche v
        INNER JOIN registrofinanciero r ON v.id_registro = r.id_registro
        WHERE r.id_hato = :idHato
        AND TO_CHAR(v.fecha, 'YYYY-MM') = :mes
    """, nativeQuery = true)
    Float sumLitrosVendidosByHatoAndMes(
        @Param("idHato") UUID idHato,
        @Param("mes") String mes
    );
}
