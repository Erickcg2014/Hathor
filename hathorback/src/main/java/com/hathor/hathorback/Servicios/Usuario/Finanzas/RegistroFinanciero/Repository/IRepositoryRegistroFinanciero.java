package com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;

@Repository
public interface IRepositoryRegistroFinanciero extends JpaRepository<RegistroFinanciero, UUID> {

    public List<RegistroFinanciero> findByHato_IdHato(UUID id_hato);

    List<RegistroFinanciero> findByHato_IdHatoAndEsHistoricoFalse(UUID idHato);
    List<RegistroFinanciero> findByHato_IdHatoAndEsHistoricoTrue(UUID idHato);

    // TODO: PRUEBA — eliminar antes de producción
    @Modifying
    @Query("DELETE FROM RegistroFinanciero r WHERE r.hato.idHato = :idHato")
    void deleteByHatoId(@Param("idHato") UUID idHato);

    void deleteByHato_IdHato(UUID idHato);


    // TODO: PRUEBA — eliminar antes de producción
    @Modifying
    @Query(value = "DELETE FROM ventaleche WHERE id_registro IN (SELECT id_registro FROM registrofinanciero WHERE id_hato = :idHato)", nativeQuery = true)
    void deleteVentaLecheByHatoId(@Param("idHato") UUID idHato);

    // TODO: PRUEBA — eliminar antes de producción
    List<RegistroFinanciero> findByHatoIdHato(UUID idHato);

    // Resumen mensual — ingresos y egresos por mes
    @Query("""
        SELECT
            FUNCTION('TO_CHAR', r.fecha, 'YYYY-MM') as mes,
            SUM(CASE WHEN r.tipoMovimiento = 'INGRESO'
                THEN r.monto ELSE 0 END) as ingresos,
            SUM(CASE WHEN r.tipoMovimiento != 'INGRESO'
                THEN r.monto ELSE 0 END) as egresos
        FROM RegistroFinanciero r
        WHERE r.hato.idHato  = :idHato
        AND r.fecha        >= :fechaDesde
        GROUP BY FUNCTION('TO_CHAR', r.fecha, 'YYYY-MM')
        ORDER BY mes ASC
        """)
    List<Object[]> findResumenMensualUltimosMeses(
            @Param("idHato")     UUID      idHato,
            @Param("fechaDesde") LocalDate fechaDesde);
            
    @Query("""
        SELECT
            FUNCTION('TO_CHAR', r.fecha, 'YYYY-MM') as mes,
            SUM(CASE WHEN r.tipoMovimiento = 'INGRESO'
                THEN r.monto ELSE 0 END) as ingresos,
            SUM(CASE WHEN r.tipoMovimiento != 'INGRESO'
                THEN r.monto ELSE 0 END) as egresos
        FROM RegistroFinanciero r
        WHERE r.hato.idHato = :idHato
        GROUP BY FUNCTION('TO_CHAR', r.fecha, 'YYYY-MM')
        ORDER BY mes ASC
        """)
    List<Object[]> findResumenMensualCompleto(
            @Param("idHato") UUID idHato);

    @Query("""
        SELECT r FROM RegistroFinanciero r
        WHERE r.hato.idHato = :idHato
        AND FUNCTION('TO_CHAR', r.fecha, 'YYYY-MM')
            >= :mesDesde
        AND FUNCTION('TO_CHAR', r.fecha, 'YYYY-MM')
            <= :mesHasta
        ORDER BY r.fecha ASC
        """)
    List<RegistroFinanciero> findByHatoAndRangoMeses(
            @Param("idHato")   UUID   idHato,
            @Param("mesDesde") String mesDesde,
            @Param("mesHasta") String mesHasta);
}