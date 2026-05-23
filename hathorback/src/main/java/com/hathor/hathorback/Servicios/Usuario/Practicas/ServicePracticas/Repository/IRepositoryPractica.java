package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository;

import com.hathor.hathorback.Entities.Practicas.Practica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRepositoryPractica extends JpaRepository<Practica, Integer> {

    // Catálogo completo activo filtrado por escala del hato —
    // incluye prácticas de esa escala específica y las de TODAS.
    @Query("""
        SELECT p FROM Practica p
        WHERE p.estado = 'ACTIVA'
          AND (p.escala = :escala OR p.escala = 'TODAS')
        ORDER BY p.categoria ASC, p.nombre ASC
        """)
    List<Practica> findActivasByEscala(@Param("escala") String escala);

    // Busca una práctica específica por nombre y escala —
    // usado internamente para verificar duplicados o en tests.
    Optional<Practica> findByNombreAndEscala(String nombre, String escala);

    // Catálogo completo por categoría — para el frontend
    // cuando quiere listar prácticas de un tipo específico.
    @Query("""
        SELECT p FROM Practica p
        WHERE p.estado = 'ACTIVA'
          AND p.categoria = :categoria
          AND (p.escala = :escala OR p.escala = 'TODAS')
        ORDER BY p.nombre ASC
        """)
    List<Practica> findActivasByCategoriaYEscala(
            @Param("categoria") String categoria,
            @Param("escala")    String escala
    );

    // ADMIN 
    // Todas las prácticas con filtros opcionales
    @Query("""
        SELECT p FROM Practica p
        WHERE (:estado    IS NULL OR p.estado    = :estado)
          AND (:categoria IS NULL OR p.categoria = :categoria)
          AND (:escala    IS NULL OR p.escala    = :escala
              OR p.escala = 'TODAS')
          AND (:dificultad IS NULL OR p.dificultad = :dificultad)
        ORDER BY p.categoria ASC, p.nombre ASC
        """)
    List<Practica> findAllFiltrado(
            @Param("estado")     String estado,
            @Param("categoria")  String categoria,
            @Param("escala")     String escala,
            @Param("dificultad") String dificultad
    );

    // Prácticas vinculadas a una regla específica
    @Query("""
        SELECT rp.practica FROM ReglaPractica rp
        WHERE rp.regla.idRegla = :idRegla
        ORDER BY rp.orden ASC
        """)
    List<Practica> findByRegla(@Param("idRegla") Integer idRegla);
}