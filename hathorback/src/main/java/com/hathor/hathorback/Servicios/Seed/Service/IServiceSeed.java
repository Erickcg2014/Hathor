package com.hathor.hathorback.Servicios.Seed.Service;

import com.hathor.hathorback.Servicios.Seed.DTO.*;
import java.util.UUID;

public interface IServiceSeed {

    // Orquestador completo — crea todo para un hato
    SeedResultadoDTO seedHatoCompleto(
        SeedHatoCompletoDTO dto);

    // Modulares 
    SeedResultadoDTO seedInventario(
        UUID idHato, SeedInventarioDTO dto);

    SeedResultadoDTO seedFinanzas(
        UUID idHato, SeedFinanzasDTO dto);

    SeedResultadoDTO seedProduccion(
        UUID idHato, SeedProduccionDTO dto);

    SeedResultadoDTO seedKpis(UUID idHato);

    SeedResultadoDTO seedPracticas(
        UUID idHato, SeedPracticaDTO dto);

    SeedResultadoDTO eliminarHatoPorUsuario(String idUsuarioAuth);

    SeedResultadoDTO recalcularKpisTodos();

    SeedResultadoDTO recalcularBenchmarkingTodos();
}