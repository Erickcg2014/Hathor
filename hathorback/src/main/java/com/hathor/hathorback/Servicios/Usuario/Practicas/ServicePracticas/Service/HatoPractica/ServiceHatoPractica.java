package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Service.HatoPractica;

import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Practicas.HatoPracticaPaso;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Service.IServiceRecomendacion;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.ActualizarPasosDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.HatoPracticaDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.HatoPracticaPasoResponseDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.PasoDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPracticaPaso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceHatoPractica implements IServiceHatoPractica {

    @Autowired
    private IRepositoryHatoPractica repositoryHatoPractica;

    @Autowired
    private IServiceRecomendacion serviceRecomendacion;

    @Autowired
    private IRepositoryHatoPracticaPaso repoPasos;
    
    @Override
    public List<HatoPracticaDTO> getByHato(UUID idHato) {
        return repositoryHatoPractica.findByHatoOrdenadas(idHato)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HatoPracticaDTO actualizarEstado(UUID idHatoPractica, String nuevoEstado) {
        HatoPractica hp = repositoryHatoPractica.findById(idHatoPractica)
                .orElseThrow(() -> new RuntimeException("HATO_PRACTICA_NO_ENCONTRADA"));

        validarTransicion(hp.getEstado(), nuevoEstado);

        hp.setEstado(nuevoEstado);

        if ("EN_CURSO".equals(nuevoEstado) && hp.getFechaInicio() == null) {
            hp.setFechaInicio(LocalDate.now());
        }

        if ("COMPLETADA".equals(nuevoEstado)) {
            hp.setFechaFin(LocalDate.now());
        }

        HatoPractica saved = repositoryHatoPractica.save(hp);

        if ("COMPLETADA".equals(nuevoEstado) && saved.getRecomendacion() != null) {
            Integer idRecomendacion = saved.getRecomendacion().getIdRecomendacionHato();
            List<HatoPractica> practicasRecomendacion =
                    repositoryHatoPractica.findByRecomendacion(idRecomendacion);
            boolean todasCompletadas = practicasRecomendacion.stream()
                    .allMatch(p -> "COMPLETADA".equals(p.getEstado()));
            if (todasCompletadas) {
                serviceRecomendacion.completar(idRecomendacion);
            }
        }

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public HatoPracticaDTO actualizarAvance(UUID idHatoPractica, Float porcentaje) {
        HatoPractica hp = repositoryHatoPractica.findById(idHatoPractica)
                .orElseThrow(() -> new RuntimeException("HATO_PRACTICA_NO_ENCONTRADA"));

        if (!"EN_CURSO".equals(hp.getEstado())) {
            throw new RuntimeException("HATO_PRACTICA_NO_EN_CURSO");
        }

        hp.setPorcentajeAvance(porcentaje);
        repositoryHatoPractica.save(hp);

        if (porcentaje >= 100f) {
            return actualizarEstado(idHatoPractica, "COMPLETADA");
        }

        return mapToDTO(hp);
    }

    // ----------------------------------------------------------------
    // Validación de transiciones de estado
    // ----------------------------------------------------------------

    private void validarTransicion(String estadoActual, String nuevoEstado) {
        if ("COMPLETADA".equals(estadoActual)) {
            throw new RuntimeException("TRANSICION_INVALIDA");
        }
        if ("EN_CURSO".equals(estadoActual) && "PENDIENTE".equals(nuevoEstado)) {
            throw new RuntimeException("TRANSICION_INVALIDA");
        }
        if ("PENDIENTE".equals(estadoActual) && "COMPLETADA".equals(nuevoEstado)) {
            throw new RuntimeException("TRANSICION_INVALIDA");
        }
    }

    // ----------------------------------------------------------------
    // Mapper interno
    // ----------------------------------------------------------------

    private HatoPracticaDTO mapToDTO(HatoPractica hp) {
        return HatoPracticaDTO.builder()
                .idHatoPractica(hp.getIdHatoPractica())
                .estado(hp.getEstado())
                .porcentajeAvance(hp.getPorcentajeAvance())
                .fechaInicio(hp.getFechaInicio())
                .fechaFin(hp.getFechaFin())
                .idPractica(hp.getPractica().getIdPractica())
                .nombrePractica(hp.getPractica().getNombre())
                .categoria(hp.getPractica().getCategoria())
                .dificultad(hp.getPractica().getDificultad())
                .kpiImpactado(hp.getPractica().getKpiImpactado())
                .duracionDias(hp.getPractica().getDuracionDias())
                .idRecomendacion(hp.getRecomendacion() != null
                        ? hp.getRecomendacion().getIdRecomendacionHato()
                        : null)
                .build();
    }


    @Override
    public HatoPracticaPasoResponseDTO getPasos(UUID idHatoPractica) {
        HatoPractica hp = repositoryHatoPractica.findById(idHatoPractica)
            .orElseThrow(() -> new RuntimeException(
                "HATO_PRACTICA_NO_ENCONTRADA"));

        List<HatoPracticaPaso> pasos =
            repoPasos.findByHatoPractica_IdHatoPractica(idHatoPractica);

        List<PasoDTO> pasosDTO = pasos.stream().map(p -> {
            PasoDTO dto = new PasoDTO();
            dto.setIndicePaso(p.getIndicePaso());
            dto.setCompletado(p.getCompletado());
            return dto;
        }).collect(java.util.stream.Collectors.toList());

        return HatoPracticaPasoResponseDTO.builder()
            .pasos(pasosDTO)
            .porcentajeAvance(hp.getPorcentajeAvance())
            .estado(hp.getEstado())
            .build();
    }

    @Override
    @Transactional
    public HatoPracticaPasoResponseDTO actualizarPasos(
            UUID idHatoPractica, ActualizarPasosDTO dto) {

        HatoPractica hp = repositoryHatoPractica.findById(idHatoPractica)
            .orElseThrow(() -> new RuntimeException(
                "HATO_PRACTICA_NO_ENCONTRADA"));

        repoPasos.deleteByHatoPractica(idHatoPractica);

        List<HatoPracticaPaso> nuevos = dto.getPasos().stream()
            .map(p -> HatoPracticaPaso.builder()
                .hatoPractica(hp)
                .indicePaso(p.getIndicePaso())
                .completado(p.getCompletado())
                .fechaCompletado(p.getCompletado()
                    ? java.time.LocalDate.now() : null)
                .build())
            .collect(java.util.stream.Collectors.toList());

        repoPasos.saveAll(nuevos);

        long totalPasos     = dto.getPasos().size();
        long completados    = dto.getPasos().stream()
            .filter(PasoDTO::getCompletado).count();

        float porcentaje = totalPasos > 0
            ? Math.round((completados / (float) totalPasos) * 100)
            : 0f;

        hp.setPorcentajeAvance(porcentaje);

        if (completados > 0 && "PENDIENTE".equals(hp.getEstado())) {
            hp.setEstado("EN_CURSO");
            hp.setFechaInicio(java.time.LocalDate.now());
        }

        if (completados == totalPasos && totalPasos > 0) {
            hp.setEstado("COMPLETADA");
            hp.setFechaFin(java.time.LocalDate.now());
        }

        repositoryHatoPractica.save(hp);

        List<PasoDTO> pasosDTO = nuevos.stream().map(p -> {
            PasoDTO d = new PasoDTO();
            d.setIndicePaso(p.getIndicePaso());
            d.setCompletado(p.getCompletado());
            return d;
        }).collect(java.util.stream.Collectors.toList());

        return HatoPracticaPasoResponseDTO.builder()
            .pasos(pasosDTO)
            .porcentajeAvance(porcentaje)
            .estado(hp.getEstado())
            .build();
    }
}
