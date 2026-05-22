package com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Service;

import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.DTO.RegistroProduccionLecheDTO;

import java.util.List;
import java.util.UUID;

public interface IServiceProduccionLeche {
    ProduccionLeche crearRegistro(RegistroProduccionLecheDTO dto, String email);
    List<ProduccionLeche> getByHato(UUID idHato);
}