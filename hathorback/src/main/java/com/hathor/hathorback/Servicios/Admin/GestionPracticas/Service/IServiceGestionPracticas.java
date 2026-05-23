package com.hathor.hathorback.Servicios.Admin.GestionPracticas.Service;

import com.hathor.hathorback.Servicios.Admin.GestionPracticas.DTO.*;
import java.util.List;

public interface IServiceGestionPracticas {
    List<PracticaAdminDTO> getPracticas(
        String estado, String categoria,
        String escala, String dificultad);
    PracticaAdminDTO getPracticaById(Integer idPractica);
    PracticaAdminDTO crearPractica(CrearPracticaDTO dto);
    PracticaAdminDTO editarPractica(Integer idPractica, EditarPracticaDTO dto);
    void desactivarPractica(Integer idPractica);
}