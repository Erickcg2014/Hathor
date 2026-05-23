package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.Service;

import com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.DTO.*;
import java.util.List;
import java.util.UUID;

public interface IServiceRecomendacionGeneral {

    // ── Usuario ───────────────────────────────────────────────────────────
    RecomendacionesResumenDTO getResumen(UUID idHato);
    void marcarLeida(Long idRecomendacion);
    void marcarTodasLeidas(UUID idHato);

    // ── Clima ─────────────────────────────────────────────────────────────
    List<RecomendacionGeneralDTO> crearRecomendacionClima(UUID idHato, String subtipo);

    void limpiarRecomendacionesClima(UUID idHato);

    // ── Admin ─────────────────────────────────────────────────────────────
    RecomendacionGeneralDTO crearRecomendacionAdmin(
        CrearRecomendacionAdminDTO dto);
    List<RecomendacionGeneralDTO> getRecomendacionesAdmin();
    void eliminarRecomendacion(Long idRecomendacion);
}