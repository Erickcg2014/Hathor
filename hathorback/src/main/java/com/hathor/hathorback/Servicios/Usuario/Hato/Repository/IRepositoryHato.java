package com.hathor.hathorback.Servicios.Usuario.Hato.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Hato.Hato;

@Repository
public interface IRepositoryHato extends JpaRepository<Hato, UUID>{

    public List<Hato> findByUsuario_IdUsuario(UUID idUsuario);
    @Query("""
        SELECT h FROM Hato h
        WHERE h.latitud  IS NOT NULL
        AND h.longitud IS NOT NULL
        """)
    List<Hato> findAllConCoordenadas();

    // ADMINISTRADOR 
    // Todos los hatos con filtros opcionales
    @Query("""
        SELECT h FROM Hato h
        WHERE (:departamento IS NULL OR h.departamento = :departamento)
        AND (:region       IS NULL OR h.ciudad       = :region)
        AND (:tropico      IS NULL OR h.tropico       = :tropico)
        AND (:escala       IS NULL OR h.escala        = :escala)
        AND (:tipoHato     IS NULL OR h.tipoHato      = :tipoHato)
        ORDER BY h.departamento ASC, h.nombreHato ASC
        """)
    List<Hato> findAllFiltrado(
            @Param("departamento") String departamento,
            @Param("region")       String region,
            @Param("tropico")      String tropico,
            @Param("escala")       String escala,
            @Param("tipoHato")     String tipoHato
    );

    // Lista de departamentos únicos
    @Query("SELECT DISTINCT h.departamento FROM Hato h WHERE h.departamento IS NOT NULL ORDER BY h.departamento ASC")
    List<String> findDepartamentosUnicos();

    // Lista de ciudades por departamento
    @Query("SELECT DISTINCT h.ciudad FROM Hato h WHERE h.departamento = :departamento AND h.ciudad IS NOT NULL ORDER BY h.ciudad ASC")
    List<String> findCiudadesPorDepartamento(@Param("departamento") String departamento);

    // Lista de trópicos únicos
    @Query("SELECT DISTINCT h.tropico FROM Hato h WHERE h.tropico IS NOT NULL ORDER BY h.tropico ASC")
    List<String> findTropicosUnicos();

    // Lista de tipos de hato únicos
    @Query("SELECT DISTINCT h.tipoHato FROM Hato h WHERE h.tipoHato IS NOT NULL AND h.tipoHato <> '' ORDER BY h.tipoHato ASC")
    List<String> findTiposHatoUnicos();

    // Lista de escalas únicas
    @Query("SELECT DISTINCT h.escala FROM Hato h WHERE h.escala IS NOT NULL ORDER BY h.escala ASC")
    List<String> findEscalasUnicas();

    // Conteo de hatos por departamento
    @Query("""
        SELECT h.departamento, COUNT(h)
        FROM Hato h
        WHERE h.departamento IS NOT NULL
        GROUP BY h.departamento
        ORDER BY COUNT(h) DESC
        """)
    List<Object[]> countHatosPorDepartamento();

    // Conteo de hatos por escala
    @Query("""
        SELECT h.escala, COUNT(h)
        FROM Hato h
        WHERE h.escala IS NOT NULL
        GROUP BY h.escala
        ORDER BY COUNT(h) DESC
        """)
    List<Object[]> countHatosPorEscala();

    // Conteo de hatos por trópico
    @Query("""
        SELECT h.tropico, COUNT(h)
        FROM Hato h
        WHERE h.tropico IS NOT NULL
        GROUP BY h.tropico
        ORDER BY COUNT(h) DESC
        """)
    List<Object[]> countHatosPorTropico();

    // Promedio de completitud general
    @Query("SELECT AVG(h.porcentajeCompletitud) FROM Hato h")
    Double findPromedioCompletitud();

}
