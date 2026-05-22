package com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionGeneral;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Recomendaciones.Repository.IRepositoryRecomendacionGeneral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceRecomendacionGeneral
        implements IServiceRecomendacionGeneral {

    @Autowired
    private IRepositoryRecomendacionGeneral repoRecomendacion;

    @Autowired
    private IRepositoryHato repoHato;

    // ── Usuario ───────────────────────────────────────────────────────────

    @Override
    public RecomendacionesResumenDTO getResumen(UUID idHato) {
        List<RecomendacionGeneral> activas =
            repoRecomendacion.findActivasByHato(idHato);

        long noLeidas = activas.stream()
            .filter(r -> !r.getLeida()).count();

        List<RecomendacionGeneralDTO> altas = activas.stream()
            .filter(r -> "ALTA".equals(r.getPrioridad()))
            .map(this::toDTO).collect(Collectors.toList());

        List<RecomendacionGeneralDTO> medias = activas.stream()
            .filter(r -> "MEDIA".equals(r.getPrioridad()))
            .map(this::toDTO).collect(Collectors.toList());

        List<RecomendacionGeneralDTO> bajas = activas.stream()
            .filter(r -> "BAJA".equals(r.getPrioridad()))
            .map(this::toDTO).collect(Collectors.toList());

        return RecomendacionesResumenDTO.builder()
            .totalNoLeidas(noLeidas)
            .altas(altas)
            .medias(medias)
            .bajas(bajas)
            .build();
    }

    @Override
    @Transactional
    public void marcarLeida(Long idRecomendacion) {
        repoRecomendacion.findById(idRecomendacion)
            .ifPresent(r -> {
                r.setLeida(true);
                repoRecomendacion.save(r);
            });
    }

    @Override
    @Transactional
    public void marcarTodasLeidas(UUID idHato) {
        repoRecomendacion.marcarTodasLeidasByHato(idHato);
    }

    // ── Clima ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<RecomendacionGeneralDTO> crearRecomendacionClima(
            UUID idHato, String subtipo) {

        Hato hato = repoHato.findById(idHato).orElse(null);
        if (hato == null) return Collections.emptyList();

        List<RecomendacionGeneral> plantillas =
            repoRecomendacion.findPlantillasBySubtipo(subtipo);

        List<RecomendacionGeneral> creadas = new ArrayList<>();

        for (RecomendacionGeneral plantilla : plantillas) {
            String subtipoUnico = subtipo + "_"
                + plantilla.getIdRecomendacion();

            if (repoRecomendacion.existeActivaDelTipo(
                    idHato, "CLIMA", subtipoUnico)) {
                // Ya existe — recuperarla para retornarla
                repoRecomendacion.findActivasByHatoAndTipo(
                    idHato, "CLIMA")
                    .stream()
                    .filter(r -> subtipoUnico.equals(r.getSubtipo()))
                    .findFirst()
                    .ifPresent(creadas::add);
                continue;
            }

            RecomendacionGeneral nueva =
                RecomendacionGeneral.builder()
                    .hato(hato)
                    .tipo("CLIMA")
                    .subtipo(subtipoUnico)
                    .titulo(plantilla.getTitulo())
                    .mensaje(plantilla.getMensaje())
                    .prioridad(plantilla.getPrioridad())
                    .estado("ACTIVA")
                    .leida(false)
                    .icono(plantilla.getIcono())
                    .urlAccion(plantilla.getUrlAccion())
                    .labelAccion(plantilla.getLabelAccion())
                    .fechaCreacion(LocalDateTime.now())
                    .fechaExpiracion(
                        LocalDate.now().plusDays(2))
                    .build();

            creadas.add(repoRecomendacion.save(nueva));
        }

        return creadas.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void limpiarRecomendacionesClima(UUID idHato) {
        List<RecomendacionGeneral> climáticas =
            repoRecomendacion.findActivasByHatoAndTipo(
                idHato, "CLIMA");

        climáticas.forEach(r -> {
            r.setEstado("EXPIRADA");
            repoRecomendacion.save(r);
        });
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public RecomendacionGeneralDTO crearRecomendacionAdmin(
            CrearRecomendacionAdminDTO dto) {

        Hato hato = null;
        if (dto.getIdHato() != null) {
            hato = repoHato.findById(dto.getIdHato())
                .orElse(null);
        }

        RecomendacionGeneral rec = RecomendacionGeneral.builder()
            .hato(hato)
            .tipo("ADMIN")
            .subtipo("MANUAL")
            .titulo(dto.getTitulo())
            .mensaje(dto.getMensaje())
            .prioridad(dto.getPrioridad() != null
                ? dto.getPrioridad() : "MEDIA")
            .estado("ACTIVA")
            .leida(false)
            .icono(dto.getIcono() != null
                ? dto.getIcono() : "📢")
            .urlAccion(dto.getUrlAccion())
            .labelAccion(dto.getLabelAccion())
            .fechaCreacion(LocalDateTime.now())
            .fechaExpiracion(dto.getFechaExpiracion())
            .build();

        return toDTO(repoRecomendacion.save(rec));
    }

    @Override
    public List<RecomendacionGeneralDTO> getRecomendacionesAdmin() {
        return repoRecomendacion.findAllAdmin()
            .stream().map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarRecomendacion(Long idRecomendacion) {
        repoRecomendacion.findById(idRecomendacion)
            .ifPresent(r -> {
                r.setEstado("DESCARTADA");
                repoRecomendacion.save(r);
            });
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private RecomendacionGeneralDTO toDTO(RecomendacionGeneral r) {
        return RecomendacionGeneralDTO.builder()
            .idRecomendacion(r.getIdRecomendacion())
            .tipo(r.getTipo())
            .subtipo(r.getSubtipo())
            .titulo(r.getTitulo())
            .mensaje(r.getMensaje())
            .prioridad(r.getPrioridad())
            .estado(r.getEstado())
            .leida(r.getLeida())
            .fechaCreacion(r.getFechaCreacion() != null
                ? r.getFechaCreacion().format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"))
                : null)
            .fechaExpiracion(r.getFechaExpiracion() != null
                ? r.getFechaExpiracion().toString()
                : null)
            .icono(r.getIcono())
            .urlAccion(r.getUrlAccion())
            .labelAccion(r.getLabelAccion())
            .esGlobal(r.getHato() == null)
            .tiempoRelativo(calcularTiempoRelativo(
                r.getFechaCreacion()))
            .build();
    }

    private String calcularTiempoRelativo(LocalDateTime fecha) {
        if (fecha == null) return "—";
        long minutos = ChronoUnit.MINUTES.between(
            fecha, LocalDateTime.now());
        if (minutos < 60)
            return "hace " + minutos + " min";
        long horas = ChronoUnit.HOURS.between(
            fecha, LocalDateTime.now());
        if (horas < 24)
            return "hace " + horas + " h";
        long dias = ChronoUnit.DAYS.between(
            fecha, LocalDateTime.now());
        return "hace " + dias + " día" + (dias > 1 ? "s" : "");
    }
}