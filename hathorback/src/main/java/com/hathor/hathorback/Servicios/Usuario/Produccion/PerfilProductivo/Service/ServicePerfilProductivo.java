package com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO.ActualizarPerfilRapidoDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.DTO.RegistroPerfilProductivoDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ServicePerfilProductivo implements IServicePerfilProductivo {

    @Autowired
    IRepositoryPerfilProductivo repositoryPerfilProductivo;

    @Autowired
    IServiceHato hatoService;

    @Autowired
    IRepositoryHato repositoryHato;

    private String calcularEscala(Integer vacasEnOrdenio) {
        if (vacasEnOrdenio == null) return "PEQUEÑA";
        if (vacasEnOrdenio < 25)   return "PEQUEÑA";
        if (vacasEnOrdenio <= 200) return "MEDIANA";
        if (vacasEnOrdenio <= 500) return "GRANDE";
        return "EMPRESARIAL";
    }

    @Override
    public PerfilProductivo crearPerfil(RegistroPerfilProductivoDTO dto, String email) {
        Hato hato = hatoService.findHatoById(dto.getIdHato(), email);

        repositoryPerfilProductivo.findByHato_IdHato(dto.getIdHato())
            .ifPresent(p -> { throw new RuntimeException("PERFIL_YA_EXISTE"); });

        hato.setEscala(calcularEscala(dto.getVacasEnOrdenio()));
        repositoryHato.save(hato);

        PerfilProductivo perfil = PerfilProductivo.builder()
            .hato(hato)
            .razaPredominante(dto.getRazaPredominante())
            .produccionDiariaLitros(dto.getProduccionDiariaLitros())
            .precioLitroPromedio(dto.getPrecioLitroPromedio())
            .vacasEnOrdenio(dto.getVacasEnOrdenio())
            .frecuenciaOrdenio(dto.getFrecuenciaOrdenio())
            .sistemaOrdenio(dto.getSistemaOrdenio())
            .destinoLeche(dto.getDestinoLeche())
            .periodoLactanciaPromedio(dto.getPeriodoLactanciaPromedio())
            .build();

        return repositoryPerfilProductivo.save(perfil);
    }

    @Override
    public PerfilProductivo actualizarPerfil(UUID idHato, RegistroPerfilProductivoDTO dto, String email) {
        Hato hato = hatoService.findHatoById(idHato, email);

        PerfilProductivo perfil = repositoryPerfilProductivo.findByHato_IdHato(idHato)
            .orElseThrow(() -> new RuntimeException("PERFIL_NO_ENCONTRADO"));

        hato.setEscala(calcularEscala(dto.getVacasEnOrdenio()));
        repositoryHato.save(hato);

        perfil.setRazaPredominante(dto.getRazaPredominante());
        perfil.setProduccionDiariaLitros(dto.getProduccionDiariaLitros());
        perfil.setPrecioLitroPromedio(dto.getPrecioLitroPromedio());
        perfil.setVacasEnOrdenio(dto.getVacasEnOrdenio());
        perfil.setFrecuenciaOrdenio(dto.getFrecuenciaOrdenio());
        perfil.setSistemaOrdenio(dto.getSistemaOrdenio());
        perfil.setDestinoLeche(dto.getDestinoLeche());
        perfil.setPeriodoLactanciaPromedio(dto.getPeriodoLactanciaPromedio());

        return repositoryPerfilProductivo.save(perfil);
    }

    @Override
    public PerfilProductivo getPerfilByHato(UUID idHato) {
        return repositoryPerfilProductivo.findByHato_IdHato(idHato)
            .orElseThrow(() -> new RuntimeException("PERFIL_NO_ENCONTRADO"));
    }

    @Override
    public PerfilProductivo actualizarParcial(UUID idHato,
            ActualizarPerfilRapidoDTO dto, String email) {

        Hato hato = hatoService.findHatoById(idHato, email);

        PerfilProductivo perfil = repositoryPerfilProductivo
            .findByHato_IdHato(idHato)
            .orElseThrow(() -> new RuntimeException("PERFIL_NO_ENCONTRADO"));

        if (dto.getVacasEnOrdenio() != null) {
            perfil.setVacasEnOrdenio(dto.getVacasEnOrdenio());
            hato.setEscala(calcularEscala(dto.getVacasEnOrdenio()));
            repositoryHato.save(hato);
        }
        if (dto.getProduccionDiariaLitros() != null)
            perfil.setProduccionDiariaLitros(dto.getProduccionDiariaLitros());
        if (dto.getPrecioLitroPromedio() != null)
            perfil.setPrecioLitroPromedio(dto.getPrecioLitroPromedio());
        if (dto.getPeriodoLactanciaPromedio() != null)
            perfil.setPeriodoLactanciaPromedio(dto.getPeriodoLactanciaPromedio());
        if (dto.getFrecuenciaOrdenio() != null)
            perfil.setFrecuenciaOrdenio(dto.getFrecuenciaOrdenio());

        return repositoryPerfilProductivo.save(perfil);
    }
}