package com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Practicas.Regla;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;
import com.hathor.hathorback.Servicios.Admin.GestionRecomendaciones.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Repository.IRepositoryRecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository.IRepositoryRegla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceGestionRecomendaciones
        implements IServiceGestionRecomendaciones {

    @Autowired
    private IRepositoryRecomendacionHato repoRecomendacion;

    @Autowired
    private IRepositoryHato              repoHato;

    @Autowired
    private IRepositoryRegla             repoRegla;

    // ── Por hato ──────────────────────────────────────────────────────────

    @Override
    public List<RecomendacionAdminDTO> getRecomendacionesPorHato(UUID idHato) {
        return repoRecomendacion.findAllByHato(idHato)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ── Filtradas ─────────────────────────────────────────────────────────

    @Override
    public List<RecomendacionAdminDTO> getRecomendacionesFiltradas(
            UUID idHato, String tipoEstado, String prioridad) {
        return repoRecomendacion
            .findAllFiltrado(idHato, tipoEstado, prioridad)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ── Crear manual ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public RecomendacionAdminDTO crearRecomendacion(CrearRecomendacionDTO dto) {
        Hato hato = repoHato.findById(dto.getIdHato())
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));

        Regla regla = null;
        if (dto.getIdRegla() != null) {
            regla = repoRegla.findById(dto.getIdRegla())
                .orElseThrow(() -> new RuntimeException("REGLA_NO_ENCONTRADA"));
        }

        RecomendacionHato rec = RecomendacionHato.builder()
            .hato(hato)
            .tipo(dto.getTipo())
            .mensaje(dto.getMensaje())
            .indicador(dto.getIndicador())
            .valorActual(dto.getValorActual())
            .valorReferencia(dto.getValorReferencia())
            .prioridad(dto.getPrioridad() != null
                ? dto.getPrioridad() : "MEDIA")
            .tipoEstado("ACTIVA")
            .leida(false)
            .fechaCreacion(LocalDate.now())
            .escalaHato(hato.getEscala())
            .tropicoHato(hato.getTropico())
            .regionHato(hato.getDepartamento())
            .regla(regla)
            .build();

        rec = repoRecomendacion.save(rec);
        return toDTO(rec);
    }

    // ── Cambiar estado ────────────────────────────────────────────────────

    @Override
    @Transactional
    public RecomendacionAdminDTO cambiarEstado(
            Integer idRecomendacion,
            CambiarEstadoRecomendacionDTO dto) {

        RecomendacionHato rec = repoRecomendacion.findById(idRecomendacion)
            .orElseThrow(() ->
                new RuntimeException("RECOMENDACION_NO_ENCONTRADA"));

        rec.setTipoEstado(dto.getTipoEstado());
        rec = repoRecomendacion.save(rec);
        return toDTO(rec);
    }

    // ── Eliminar ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void eliminarRecomendacion(Integer idRecomendacion) {
        if (!repoRecomendacion.existsById(idRecomendacion)) {
            throw new RuntimeException("RECOMENDACION_NO_ENCONTRADA");
        }
        repoRecomendacion.deleteById(idRecomendacion);
    }

    // ── Mapper ────────────────────────────────────────────────────────────

    private RecomendacionAdminDTO toDTO(RecomendacionHato r) {
        return RecomendacionAdminDTO.builder()
            .idRecomendacion(r.getIdRecomendacionHato())
            .idHato(r.getHato() != null
                ? r.getHato().getIdHato().toString() : null)
            .nombreHato(r.getHato() != null
                ? r.getHato().getNombreHato() : null)
            .tipo(r.getTipo())
            .mensaje(r.getMensaje())
            .indicador(r.getIndicador())
            .valorActual(r.getValorActual())
            .valorReferencia(r.getValorReferencia())
            .leida(r.getLeida())
            .prioridad(r.getPrioridad())
            .tipoEstado(r.getTipoEstado())
            .fechaCreacion(r.getFechaCreacion() != null
                ? r.getFechaCreacion().toString() : null)
            .escalaHato(r.getEscalaHato())
            .tropicoHato(r.getTropicoHato())
            .regionHato(r.getRegionHato())
            .idRegla(r.getRegla() != null
                ? r.getRegla().getIdRegla() : null)
            .codigoKpiRegla(r.getRegla() != null
                && r.getRegla().getKpi() != null
                ? r.getRegla().getKpi().getCodigo() : null)
            .nombreKpiRegla(r.getRegla() != null
                && r.getRegla().getKpi() != null
                ? r.getRegla().getKpi().getNombre() : null)
            .build();
    }
}