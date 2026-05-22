package com.hathor.hathorback.Servicios.Usuario.Alertas.Service;

import com.hathor.hathorback.Servicios.Usuario.Alertas.DTO.AlertaHatoDTO;
import com.hathor.hathorback.Servicios.Usuario.Alertas.DTO.AlertasResumenDTO;
import com.hathor.hathorback.Servicios.Usuario.Alertas.DTO.AlertasAdminResumenDTO;

import java.util.UUID;

public interface IServiceAlertas {

    // ── Usuario ───────────────────────────────────────────────────────────

    AlertasResumenDTO getResumen(UUID idHato);

    void marcarLeida(Long idAlerta);

    void marcarTodasLeidas(UUID idHato);

    void evaluarAlertas(UUID idHato);

    // ── Admin ─────────────────────────────────────────────────────────────

    AlertasAdminResumenDTO getResumenAdmin();
}