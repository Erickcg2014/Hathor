package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;

@Repository
public interface IRepositoryCategoriaFinanciera extends JpaRepository<CategoriaFinanciera, UUID> {

    CategoriaFinanciera findByIdCategoriaFinanciera(UUID id);

    Optional<CategoriaFinanciera> findByNombreIgnoreCase(String nombre);

    List<CategoriaFinanciera> findByUsuario_IdUsuario(UUID idUsuario);

    // Categorías de primer nivel por tipo
    @Query(value = """
        SELECT cf.* FROM categoria_financiera cf
        INNER JOIN categoria_financiera padre ON cf.id_categoria_padre = padre.id_categoria
        WHERE cf.tipo = :tipo
        AND cf.es_predefinida = true
        AND cf.id_usuario IS NULL
        AND padre.es_predefinida = true
        AND padre.id_usuario IS NULL
        ORDER BY cf.nombre ASC
    """, nativeQuery = true)
    List<CategoriaFinanciera> findPrimerNivelByTipo(@Param("tipo") String tipo);

    @Query("""
        SELECT c FROM CategoriaFinanciera c
        WHERE c.esPredefinida = true
        AND c.usuario IS NULL
        ORDER BY c.nombre ASC
        """)
        List<CategoriaFinanciera> findCategoriasSinUsuario();
}