package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.hathorback.Entities.Practicas.Practica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.PracticaDetalleDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicePractica implements IServicePractica {

    @Autowired
    private IRepositoryPractica repositoryPractica;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Practica> getCatalogoByEscala(String escala) {
        return repositoryPractica.findActivasByEscala(escala);
    }

    @Override
    public List<Practica> getCatalogoByCategoriaYEscala(String categoria, String escala) {
        return repositoryPractica.findActivasByCategoriaYEscala(categoria, escala);
    }

    @Override
    public PracticaDetalleDTO getDetalle(Integer idPractica) {
        Practica practica = repositoryPractica.findById(idPractica)
                .orElseThrow(() -> new RuntimeException("PRACTICA_NO_ENCONTRADA"));

        List<String> pasos;
        try {
            pasos = objectMapper.readValue(practica.getPasos(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            pasos = List.of();
        }

        return PracticaDetalleDTO.builder()
                .idPractica(practica.getIdPractica())
                .nombre(practica.getNombre())
                .descripcion(practica.getDescripcion())
                .objetivo(practica.getObjetivo())
                .categoria(practica.getCategoria())
                .impactoEsperado(practica.getImpactoEsperado())
                .pasos(pasos)
                .kpiImpactado(practica.getKpiImpactado())
                .dificultad(practica.getDificultad())
                .duracionDias(practica.getDuracionDias())
                .escala(practica.getEscala())
                .tropicaAplicable(practica.getTropicaAplicable())
                .build();
    }
}
