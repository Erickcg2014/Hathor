package com.hathor.hathorback.Servicios.Admin.Hatos.Service;

import com.hathor.hathorback.Servicios.Admin.Hatos.DTO.*;
import java.util.List;
import java.util.UUID;

public interface IServiceAdminHatos {
    List<HatoAdminDTO> getHatosFiltrados(FiltroHatoDTO filtro);
    HatoAdminDetalleDTO getDetalleHato(UUID idHato);
    List<String> getDepartamentos();
    List<String> getRegionesPorDepartamento(String departamento);
    List<String> getTropicos();
    List<String> getEscalas();
}