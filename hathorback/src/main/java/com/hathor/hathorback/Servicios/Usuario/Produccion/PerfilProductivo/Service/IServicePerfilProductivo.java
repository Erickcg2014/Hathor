package com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Service;

import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO.ActualizarPerfilRapidoDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO.RegistroPerfilProductivoDTO;

import java.util.UUID;

public interface IServicePerfilProductivo {
    PerfilProductivo crearPerfil(RegistroPerfilProductivoDTO dto, String email);
    PerfilProductivo actualizarPerfil(UUID idHato, RegistroPerfilProductivoDTO dto, String email);
    PerfilProductivo getPerfilByHato(UUID idHato);
    PerfilProductivo actualizarParcial(UUID idHato, ActualizarPerfilRapidoDTO dto, String email);
}