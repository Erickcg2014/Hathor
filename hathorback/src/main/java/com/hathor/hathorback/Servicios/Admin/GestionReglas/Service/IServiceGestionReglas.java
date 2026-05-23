package com.hathor.hathorback.Servicios.Admin.GestionReglas.Service;

import com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO.*;
import java.util.List;

public interface IServiceGestionReglas {
    List<ReglaAdminDTO> getReglas(String estado, String escala, Integer idKpi);
    ReglaAdminDTO       getReglaById(Integer idRegla);
    ReglaAdminDTO       crearRegla(CrearReglaDTO dto);
    ReglaAdminDTO       editarRegla(Integer idRegla, EditarReglaDTO dto);
    void                desactivarRegla(Integer idRegla);
    // Gestión de vínculos regla-práctica
    ReglaAdminDTO       vincularPractica(Integer idRegla, VincularPracticaDTO dto);
    ReglaAdminDTO       desvincularPractica(Integer idRegla, Integer idPractica);
    ReglaAdminDTO       reordenarPracticas(Integer idRegla, List<VincularPracticaDTO> practicas);
}