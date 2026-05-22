package com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Inventarios.Raza;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface IRepositoryRaza extends JpaRepository<Raza, Integer> {
    @Query("SELECT DISTINCT r.tipoRaza FROM Raza r ORDER BY r.tipoRaza")
    List<String> findDistinctTipoRaza();
    List<Raza> findByTipoRaza(String tipoRaza);
}
