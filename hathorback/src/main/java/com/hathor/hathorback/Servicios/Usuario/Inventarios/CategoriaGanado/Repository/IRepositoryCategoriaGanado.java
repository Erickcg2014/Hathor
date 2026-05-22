package com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Inventarios.CategoriaGanado;

@Repository
public interface IRepositoryCategoriaGanado extends JpaRepository<CategoriaGanado, Integer> {
}
