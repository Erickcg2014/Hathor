package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Service.HatoPractica;

import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.ActualizarPasosDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.HatoPracticaDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.HatoPracticaPasoResponseDTO;

public interface IServiceHatoPractica {

    // Devuelve todas las prácticas asignadas al hato con su estado
    // actual (PENDIENTE, EN_CURSO, COMPLETADA) y porcentaje de avance,
    // ordenadas con EN_CURSO primero y luego PENDIENTE.
    // Lo consume el controller del módulo de prácticas del ganadero.
    List<HatoPracticaDTO> getByHato(UUID idHato);

    // Cambia el estado de una práctica asignada al siguiente paso:
    // PENDIENTE → EN_CURSO → COMPLETADA.
    // Cuando pasa a EN_CURSO registra la fecha de inicio automáticamente.
    // Cuando pasa a COMPLETADA registra la fecha de fin y evalúa si
    // todas las prácticas de la recomendación asociada están completas
    // para marcar la recomendación como COMPLETADA también.
    HatoPracticaDTO actualizarEstado(UUID idHatoPractica, String nuevoEstado);

    // Actualiza el porcentaje de avance de una práctica EN_CURSO.
    // El ganadero puede indicar qué tan avanzado está (0-100).
    // Si el porcentaje llega a 100 cambia automáticamente el estado
    // a COMPLETADA llamando internamente a actualizarEstado().
    HatoPracticaDTO actualizarAvance(UUID idHatoPractica, Float porcentaje);
    HatoPracticaPasoResponseDTO getPasos(UUID idHatoPractica);
    HatoPracticaPasoResponseDTO actualizarPasos(
        UUID idHatoPractica, ActualizarPasosDTO dto);
}