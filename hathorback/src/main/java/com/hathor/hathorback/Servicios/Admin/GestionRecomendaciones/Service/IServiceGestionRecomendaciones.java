package com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.Service;

import com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.DTO.*;
import java.util.List;
import java.util.UUID;

public interface IServiceGestionRecomendaciones {
    List<RecomendacionAdminDTO> getRecomendacionesPorHato(UUID idHato);
    List<RecomendacionAdminDTO> getRecomendacionesFiltradas(UUID idHato, String tipoEstado, String prioridad);
    RecomendacionAdminDTO crearRecomendacion(CrearRecomendacionDTO dto);
    RecomendacionAdminDTO cambiarEstado(Integer idRecomendacion, CambiarEstadoRecomendacionDTO dto);
    void eliminarRecomendacion(Integer idRecomendacion);
}