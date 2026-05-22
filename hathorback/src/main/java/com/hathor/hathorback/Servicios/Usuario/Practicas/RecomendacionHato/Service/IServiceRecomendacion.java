package com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Service;

import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.DTO.RecomendacionDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IServiceRecomendacion {

    // Devuelve las recomendaciones ACTIVAS del hato paginadas,
    // ordenadas por prioridad (ALTA primero) y fecha de creación.
    // Lo consume el controller para mostrar las alertas del dashboard.
    Page<RecomendacionDTO> getActivasByHato(UUID idHato, Pageable pageable);
    // Devuelve cuántas recomendaciones activas NO ha leído el ganadero.
    // Lo consume el frontend para mostrar el badge de notificaciones
    // en el sidebar.
    long countNoLeidas(UUID idHato);

    // Marca una recomendación como leída (leida = true).
    // El ganadero la vio pero aún está activa — no la descarta.
    void marcarComoLeida(Integer idRecomendacion);

    // Cambia el estado de la recomendación a DESCARTADA.
    // El ganadero decidió ignorarla conscientemente.
    // No se elimina físicamente — queda en historial.
    void descartar(Integer idRecomendacion);

    // Cambia el estado de la recomendación a COMPLETADA.
    // Se llama automáticamente cuando todas las HatoPractica
    // vinculadas a esa recomendación pasan a estado COMPLETADA.
    void completar(Integer idRecomendacion);
}