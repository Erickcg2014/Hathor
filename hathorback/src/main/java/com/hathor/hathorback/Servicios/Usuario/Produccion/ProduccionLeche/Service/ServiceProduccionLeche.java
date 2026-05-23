package com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.DTO.RegistroProduccionLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceProduccionLeche implements IServiceProduccionLeche {

    @Autowired
    IRepositoryProduccionLeche repositoryProduccionLeche;

    @Autowired
    IServiceHato hatoService;

    @Override
    public ProduccionLeche crearRegistro(RegistroProduccionLecheDTO dto, String email) {
        Hato hato = hatoService.findHatoById(dto.getIdHato(), email);

        ProduccionLeche registro = ProduccionLeche.builder()
            .hato(hato)
            .fecha(dto.getFecha())
            .litrosProducidos(dto.getLitrosProducidos())
            .vacasOrdenadas(dto.getVacasOrdenadas())
            .build();

        return repositoryProduccionLeche.save(registro);
    }

    @Override
    public List<ProduccionLeche> getByHato(UUID idHato) {
        return repositoryProduccionLeche.findByHato_IdHatoOrderByFechaDesc(idHato);
    }
}