package com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Service;

import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.DTO.RecomendacionDTO;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Repository.IRepositoryRecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceRecomendacion implements IServiceRecomendacion {

    @Autowired private IRepositoryRecomendacionHato repoRecomendacion;
    @Autowired private IRepositoryHatoPractica      repoHatoPractica;

    @Override
    public Page<RecomendacionDTO> getActivasByHato(UUID idHato, Pageable pageable) {
        return repoRecomendacion
            .findActivasByHatoPaginado(idHato, pageable)
            .map(this::mapearRecomendacion);
    }

    @Override
    public long countNoLeidas(UUID idHato) {
        return repoRecomendacion.countNoLeidasByHato(idHato);
    }

    @Override
    @Transactional
    public void marcarComoLeida(Integer idRecomendacion) {
        RecomendacionHato reco = repoRecomendacion.findById(idRecomendacion)
            .orElseThrow(() -> new RuntimeException("RECOMENDACION_NO_ENCONTRADA"));

        reco.setLeida(true);
        repoRecomendacion.save(reco);
    }

    @Override
    @Transactional
    public void descartar(Integer idRecomendacion) {
        RecomendacionHato reco = repoRecomendacion.findById(idRecomendacion)
            .orElseThrow(() -> new RuntimeException("RECOMENDACION_NO_ENCONTRADA"));

        if (!"ACTIVA".equals(reco.getTipoEstado())) {
            throw new RuntimeException("RECOMENDACION_NO_ACTIVA");
        }

        reco.setTipoEstado("DESCARTADA");
        repoRecomendacion.save(reco);
    }

    @Override
    @Transactional
    public void completar(Integer idRecomendacion) {
        RecomendacionHato reco = repoRecomendacion.findById(idRecomendacion)
            .orElseThrow(() -> new RuntimeException("RECOMENDACION_NO_ENCONTRADA"));

        reco.setTipoEstado("COMPLETADA");
        repoRecomendacion.save(reco);
    }

    // ----------------------------------------------------------------
    // Mapper interno
    // ----------------------------------------------------------------

    private RecomendacionDTO mapearRecomendacion(RecomendacionHato reco) {
        List<HatoPractica> practicas =
            repoHatoPractica.findByRecomendacion(reco.getIdRecomendacionHato());

        List<RecomendacionDTO.PracticaResumenDTO> practicasDTO = practicas.stream()
            .map(hp -> RecomendacionDTO.PracticaResumenDTO.builder()
                .idHatoPractica(hp.getIdHatoPractica() != null
                    ? hp.getIdHatoPractica().hashCode() : null) // UUID → int para el DTO
                .nombre(hp.getPractica().getNombre())
                .dificultad(hp.getPractica().getDificultad())
                .estado(hp.getEstado())
                .porcentajeAvance(hp.getPorcentajeAvance())
                .build())
            .collect(Collectors.toList());

        return RecomendacionDTO.builder()
            .idRecomendacion(reco.getIdRecomendacionHato())
            .mensaje(reco.getMensaje())
            .prioridad(reco.getPrioridad())
            .indicador(reco.getIndicador())
            .valorActual(reco.getValorActual())
            .valorReferencia(reco.getValorReferencia())
            .fechaCreacion(reco.getFechaCreacion())
            .tipoEstado(reco.getTipoEstado())
            .leida(reco.getLeida())
            .escalaHato(reco.getEscalaHato())
            .tropicoHato(reco.getTropicoHato())
            .practicas(practicasDTO)
            .build();
    }
}