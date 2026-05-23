package com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository;

import com.hathor.hathorback.Entities.Practicas.Regla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IRepositoryRegla extends JpaRepository<Regla, Integer> {

    // Busca reglas activas para un KPI específico que apliquen
    // a la escala del hato O a todas las escalas.
    // El motor llama esto una vez por KPI calculado.
    @Query("""
        SELECT r FROM Regla r
        WHERE r.estado = 'ACTIVA'
          AND r.kpi.codigo = :codigoKpi
          AND (r.escalaAplicable = :escala OR r.escalaAplicable = 'TODAS')
        ORDER BY r.prioridad ASC
        """)
    List<Regla> findActivasByKpiYEscala(
            @Param("codigoKpi") String codigoKpi,
            @Param("escala")    String escala
    );

    // Carga todas las reglas activas de una vez con sus prácticas
    // en JOIN FETCH para evitar N+1 al iterar los KPIs calculados.
    // El motor puede usar esto si prefiere cargar todo al inicio.
    @Query("""
        SELECT DISTINCT r FROM Regla r
        LEFT JOIN FETCH r.practicas rp
        LEFT JOIN FETCH rp.practica
        WHERE r.estado = 'ACTIVA'
          AND (r.escalaAplicable = :escala OR r.escalaAplicable = 'TODAS')
        ORDER BY r.prioridad ASC
        """)
    List<Regla> findActivasConPracticasByEscala(
            @Param("escala") String escala
    );
}