package com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Service;

import com.hathor.hathorback.Entities.Finanzas.PerfilFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.DTO.RegistroPerfilFinancieroDTO;

import java.util.List;
import java.util.UUID;

public interface IServicePerfilFinanciero {
    PerfilFinanciero crearPerfil(RegistroPerfilFinancieroDTO dto, String email);
    List<PerfilFinanciero> getPerfilesByHato(UUID idHato);
    boolean existePerfilParaHato(UUID idHato);
}