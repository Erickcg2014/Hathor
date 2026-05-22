package com.hathor.hathorback.Servicios.Usuario.Inventarios.ValorReferenciaGanado.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Inventarios.ValorReferenciaGanado;

@Repository
public interface IRepositoryValorReferenciaGanado extends JpaRepository<ValorReferenciaGanado, Integer> {
    Optional<ValorReferenciaGanado> findByRaza_IdRazaAndCategoriaGanado_IdCategoria(
    Integer idRaza, Integer idCategoria);
}
