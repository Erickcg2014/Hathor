package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Inventarios.CategoriaInventario;

import java.util.List;

@Repository
public interface IRepositoryCategoriaInventario extends JpaRepository<CategoriaInventario, Integer> {
    // Trae solo las categorías PADRE activas
    @Query("SELECT c FROM CategoriaInventario c WHERE c.tipo = 'PADRE' AND c.activa = true ORDER BY c.orden")
    List<CategoriaInventario> findCategoriasParent();

    List<CategoriaInventario> findByCategoriaPadre_IdCategoriaInventarioAndActivaTrue(Integer idPadre);

}
