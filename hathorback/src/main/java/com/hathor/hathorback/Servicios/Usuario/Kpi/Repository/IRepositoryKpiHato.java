package com.hathor.hathorback.Servicios.Usuario.Kpi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Kpi.KpiHato;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRepositoryKpiHato extends JpaRepository<KpiHato, Integer> {

    List<KpiHato> findByHato_IdHatoOrderByFechaCalculoDesc(UUID idHato);

    List<KpiHato> findByHato_IdHatoAndKpi_CodigoAndFechaCalculo(
            UUID idHato, String codigo, LocalDate fechaCalculo
    );

    List<KpiHato> findByHato_IdHatoAndFechaCalculo(UUID idHato, LocalDate fechaCalculo);


    List<KpiHato> findByHato_IdHatoAndKpi_CodigoOrderByFechaCalculoDesc(
            UUID idHato, String codigo
    );

    @Modifying
    @Query("DELETE FROM KpiHato k WHERE k.hato.idHato = :idHato")
    void deleteByHatoId(@Param("idHato") UUID idHato);

    // Query comparables por departamento o trópico
    @Query("""
        SELECT b FROM KpiHato b
        WHERE b.kpi.idKpi = :idKpi
        AND b.hato.idHato <> :idHato
        AND (
            b.hato.departamento = :departamento
            OR b.hato.tropico = :tropico
        )
    """)
    List<KpiHato> findKpiHatosComparables(
            @Param("idKpi")        int    idKpi,
            @Param("idHato")       UUID   idHato,
            @Param("departamento") String departamento,
            @Param("tropico")      String tropico
    );

    // Top N hatos comparables para el gráfico de líneas.
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.kpi.codigo = :codigoKpi
          AND kh.hato.idHato != :idHato
          AND kh.hato.tropico = :tropico
          AND kh.hato.departamento = :departamento
          AND kh.valor IS NOT NULL
          AND kh.fechaCalculo = (
              SELECT MAX(kh2.fechaCalculo)
              FROM KpiHato kh2
              WHERE kh2.hato.idHato = kh.hato.idHato
                AND kh2.kpi.codigo  = :codigoKpi
          )
        ORDER BY kh.valor DESC
        LIMIT :top
        """)
    List<KpiHato> findTopHatosComparables(
            @Param("codigoKpi")    String codigoKpi,
            @Param("idHato")       UUID   idHato,
            @Param("tropico")      String tropico,
            @Param("departamento") String departamento,
            @Param("top")          int    top
    );

    // Valor más reciente de un KPI específico para un hato 
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.hato.idHato = :idHato
          AND kh.kpi.codigo  = :codigoKpi
        ORDER BY kh.fechaCalculo DESC
        LIMIT 1
        """)
    java.util.Optional<KpiHato> findUltimoByHatoYKpi(
            @Param("idHato")    UUID   idHato,
            @Param("codigoKpi") String codigoKpi
    );

    // Ordena por valor DESC para tener los mejores primero.
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.kpi.codigo    = :codigoKpi
        AND kh.hato.idHato  != :idHato
        AND kh.valor         IS NOT NULL
        AND (:tropico  IS NULL OR kh.hato.tropico      = :tropico)
        AND (:escala   IS NULL OR kh.hato.escala        = :escala)
        AND (:region   IS NULL OR kh.hato.departamento  = :region)
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo)
            FROM KpiHato kh2
            WHERE kh2.hato.idHato = kh.hato.idHato
                AND kh2.kpi.codigo  = :codigoKpi
        )
        ORDER BY kh.valor DESC
        LIMIT :cantidad
        """)
    List<KpiHato> findHatosComparablesDinamico(
            @Param("codigoKpi") String codigoKpi,
            @Param("idHato")    UUID   idHato,
            @Param("tropico")   String tropico,
            @Param("escala")    String escala,
            @Param("region")    String region,
            @Param("cantidad")  int    cantidad
    );

    // Cuenta cuántos hatos existen con los filtros dados para un KPI —
    @Query("""
        SELECT COUNT(DISTINCT kh.hato.idHato) FROM KpiHato kh
        WHERE kh.kpi.codigo    = :codigoKpi
        AND kh.hato.idHato  != :idHato
        AND kh.valor         IS NOT NULL
        AND (:tropico  IS NULL OR kh.hato.tropico      = :tropico)
        AND (:escala   IS NULL OR kh.hato.escala        = :escala)
        AND (:region   IS NULL OR kh.hato.departamento  = :region)
        """)
    long countHatosComparablesDinamico(
            @Param("codigoKpi") String codigoKpi,
            @Param("idHato")    UUID   idHato,
            @Param("tropico")   String tropico,
            @Param("escala")    String escala,
            @Param("region")    String region
    );

    // Trae todos los hatos comparables con sus coordenadas para el mapa 
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.kpi.codigo    = :codigoKpi
        AND kh.hato.idHato  != :idHato
        AND kh.valor         IS NOT NULL
        AND (:tropico  IS NULL OR kh.hato.tropico      = :tropico)
        AND (:escala   IS NULL OR kh.hato.escala        = :escala)
        AND (:region   IS NULL OR kh.hato.departamento  = :region)
        AND kh.hato.latitud  IS NOT NULL
        AND kh.hato.longitud IS NOT NULL
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo)
            FROM KpiHato kh2
            WHERE kh2.hato.idHato = kh.hato.idHato
                AND kh2.kpi.codigo  = :codigoKpi
        )
        ORDER BY kh.valor DESC
        """)
    List<KpiHato> findHatosParaMapa(
            @Param("codigoKpi") String codigoKpi,
            @Param("idHato")    UUID   idHato,
            @Param("tropico")   String tropico,
            @Param("escala")    String escala,
            @Param("region")    String region
    );

    // Trae todos los hatos ordenados por valor DESC para un KPI dado.
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.kpi.codigo = :codigoKpi
        AND kh.hato.idHato != :idHato
        AND kh.valor IS NOT NULL
        AND (:region IS NULL OR kh.hato.departamento = :region)
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo)
            FROM KpiHato kh2
            WHERE kh2.kpi.codigo = :codigoKpi
        )
        ORDER BY kh.valor DESC
        """)
    List<KpiHato> findTodosHatosOrdenadosPorKpi(
            @Param("codigoKpi") String codigoKpi,
            @Param("idHato")    UUID   idHato,
            @Param("region")    String region
    );

    // Trae el histórico de un KPI para un hato específico desde hace N meses atrás, ordenado por fecha ASC.
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.hato.idHato  = :idHato
        AND kh.kpi.codigo   = :codigoKpi
        AND kh.fechaCalculo >= :fechaDesde
        ORDER BY kh.fechaCalculo ASC
        """)
    List<KpiHato> findHistoricoKpiHato(
            @Param("idHato")     UUID      idHato,
            @Param("codigoKpi")  String    codigoKpi,
            @Param("fechaDesde") LocalDate fechaDesde
    );

    // Trae el último valor de cada KPI para todos los hatos
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.hato.idHato != :idHato
        AND kh.valor IS NOT NULL
        AND (:region IS NULL OR kh.hato.departamento = :region)
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo)
            FROM KpiHato kh2
            WHERE kh2.hato.idHato = kh.hato.idHato
                AND kh2.kpi.idKpi   = kh.kpi.idKpi
        )
        """)
    List<KpiHato> findUltimosKpisPorTodosLosHatos(
            @Param("idHato")  UUID   idHato,
            @Param("region")  String region
    );

    // Promedio de un KPI específico entre todos los hatos
    @Query("""
        SELECT AVG(kh.valor) FROM KpiHato kh
        WHERE kh.kpi.codigo = :codigoKpi
        AND kh.valor IS NOT NULL
        AND (:departamento IS NULL OR kh.hato.departamento = :departamento)
        AND (:tropico      IS NULL OR kh.hato.tropico      = :tropico)
        AND (:escala       IS NULL OR kh.hato.escala       = :escala)
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo)
            FROM KpiHato kh2
            WHERE kh2.hato.idHato = kh.hato.idHato
            AND kh2.kpi.codigo  = :codigoKpi
        )
        """)
    Double findPromedioKpi(
            @Param("codigoKpi")    String codigoKpi,
            @Param("departamento") String departamento,
            @Param("tropico")      String tropico,
            @Param("escala")       String escala
    );

    // Cuenta hatos con al menos un KPI calculado — hatos activos
    @Query("""
        SELECT COUNT(DISTINCT kh.hato.idHato) FROM KpiHato kh
        WHERE kh.valor IS NOT NULL
        AND (:departamento IS NULL OR kh.hato.departamento = :departamento)
        AND (:tropico      IS NULL OR kh.hato.tropico      = :tropico)
        AND (:escala       IS NULL OR kh.hato.escala       = :escala)
        """)
    long countHatosConKpis(
            @Param("departamento") String departamento,
            @Param("tropico")      String tropico,
            @Param("escala")       String escala
    );

    // Distribución de interpretaciones para un KPI
    @Query("""
        SELECT kh.estado, COUNT(kh) FROM KpiHato kh
        WHERE kh.kpi.codigo = :codigoKpi
        AND kh.valor IS NOT NULL
        AND (:departamento IS NULL OR kh.hato.departamento = :departamento)
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo)
            FROM KpiHato kh2
            WHERE kh2.hato.idHato = kh.hato.idHato
            AND kh2.kpi.codigo  = :codigoKpi
        )
        GROUP BY kh.estado
        """)
    List<Object[]> findDistribucionEstadoKpi(
            @Param("codigoKpi")    String codigoKpi,
            @Param("departamento") String departamento
    );

    // Histórico últimos N meses — para detectar tendencias
    @Query("""
        SELECT kh.kpi.codigo, kh.valor
        FROM KpiHato kh
        WHERE kh.hato.idHato = :idHato
        AND kh.fechaCalculo >= :fechaDesde
        ORDER BY kh.kpi.codigo ASC, kh.fechaCalculo ASC
        """)
    List<Object[]> findHistoricoUltimosMeses(
            @Param("idHato")     UUID      idHato,
            @Param("fechaDesde") LocalDate fechaDesde);

    // Códigos de KPIs en un estado específico
    @Query("""
        SELECT DISTINCT kh.kpi.codigo FROM KpiHato kh
        WHERE kh.hato.idHato = :idHato
        AND kh.estado = :estado
        AND kh.fechaCalculo = (
            SELECT MAX(kh2.fechaCalculo) FROM KpiHato kh2
            WHERE kh2.hato.idHato = kh.hato.idHato
                AND kh2.kpi.codigo  = kh.kpi.codigo
        )
        """)
    List<String> findCodigosEnEstadoByHato(
            @Param("idHato") UUID   idHato,
            @Param("estado") String estado);

    // Último KPI por código
    @Query("""
        SELECT kh FROM KpiHato kh
        WHERE kh.hato.idHato = :idHato
        AND kh.kpi.codigo  = :codigoKpi
        ORDER BY kh.fechaCalculo DESC
        LIMIT 1
        """)
    Optional<KpiHato> findUltimoByHatoAndCodigo(
            @Param("idHato")    UUID   idHato,
            @Param("codigoKpi") String codigoKpi);

    // KPIs que pasaron a OPTIMO en una fecha específica
    @Query("""
        SELECT kh.kpi.codigo FROM KpiHato kh
        WHERE kh.hato.idHato  = :idHato
        AND kh.estado        = 'OPTIMO'
        AND kh.fechaCalculo  = :fecha
        """)
    List<String> findCodigosNuevosOptimosByHato(
            @Param("idHato") UUID      idHato,
            @Param("fecha")  LocalDate fecha);

    // Percentil promedio del mes actual o anterior
    @Query("""
        SELECT AVG(b.percentil) FROM BenchmarkHato b
        WHERE b.hato.idHato    = :idHato
        AND b.fechaCalculo  >= :fechaDesde
        AND b.percentil     IS NOT NULL
        """)
    Float findPercentilPromedioByHato(
            @Param("idHato")     UUID      idHato,
            @Param("fechaDesde") LocalDate fechaDesde);
}