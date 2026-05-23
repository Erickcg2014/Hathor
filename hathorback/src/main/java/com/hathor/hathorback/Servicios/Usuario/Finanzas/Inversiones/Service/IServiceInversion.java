package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.Service;

import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO.*;
import java.util.List;
import java.util.UUID;

public interface IServiceInversion {

    List<InversionPlaneadaDTO> getByHato(UUID idHato);

    InversionPlaneadaDTO crear(UUID idHato, CrearInversionDTO dto);

    InversionPlaneadaDTO actualizar(Long idInversion,
        ActualizarInversionDTO dto);

    void cancelar(Long idInversion);

    List<InversionPlaneadaDTO> getByHatoAndRango(
        UUID idHato, String mesDesde, String mesHasta);
}