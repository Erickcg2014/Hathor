package com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.Service;

import com.hathor.hathorback.Servicios.Usuario.Finanzas.Proyecciones.DTO.ProyeccionesResponseDTO;
import java.util.UUID;

public interface IServiceProyecciones {
    ProyeccionesResponseDTO getProyecciones(UUID idHato, int mesesProyectar);
}