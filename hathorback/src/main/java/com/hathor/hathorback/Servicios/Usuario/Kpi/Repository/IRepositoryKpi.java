package com.hathor.hathorback.Servicios.Usuario.Kpi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Entities.Kpi.KpiHato;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IRepositoryKpi extends JpaRepository<Kpi, Integer> {
    Optional<Kpi> findByCodigo(String codigo);
    Kpi findByIdKpi(int idKpi);

    // Todos los KPIs ordenados por categoría y nombre
    @Query("SELECT k FROM Kpi k ORDER BY k.categoria ASC, k.nombre ASC")
    List<Kpi> findAllOrdenados();
}