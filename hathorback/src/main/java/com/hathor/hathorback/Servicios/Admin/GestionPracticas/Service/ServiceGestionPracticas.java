package com.hathor.hathorback.Servicios.Admin.GestionPracticas.Service;

import com.hathor.hathorback.Entities.Practicas.*;
import com.hathor.hathorback.Servicios.Admin.GestionPracticas.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository.IRepositoryReglaPractica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceGestionPracticas implements IServiceGestionPracticas {

    @Autowired
    private IRepositoryPractica      repositoryPractica;

    @Autowired
    private IRepositoryReglaPractica repositoryReglaPractica;

    // ── Obtener todas con filtros ─────────────────────────────────────────

    @Override
    public List<PracticaAdminDTO> getPracticas(
            String estado, String categoria,
            String escala, String dificultad) {

        return repositoryPractica
            .findAllFiltrado(estado, categoria, escala, dificultad)
            .stream()
            .map(this::toPracticaAdminDTO)
            .collect(Collectors.toList());
    }

    // ── Obtener por ID ────────────────────────────────────────────────────

    @Override
    public PracticaAdminDTO getPracticaById(Integer idPractica) {
        Practica practica = repositoryPractica.findById(idPractica)
            .orElseThrow(() -> new RuntimeException("PRACTICA_NO_ENCONTRADA"));
        return toPracticaAdminDTO(practica);
    }

    // ── Crear práctica ────────────────────────────────────────────────────

    @Override
    @Transactional
    public PracticaAdminDTO crearPractica(CrearPracticaDTO dto) {
        Practica practica = Practica.builder()
            .nombre(dto.getNombre())
            .descripcion(dto.getDescripcion())
            .objetivo(dto.getObjetivo())
            .categoria(dto.getCategoria())
            .impactoEsperado(dto.getImpactoEsperado())
            .pasos(dto.getPasos())
            .kpiImpactado(dto.getKpiImpactado())
            .dificultad(dto.getDificultad() != null
                ? dto.getDificultad() : "MEDIA")
            .duracionDias(dto.getDuracionDias())
            .escala(dto.getEscala() != null
                ? dto.getEscala() : "TODAS")
            .tropicaAplicable(dto.getTropicaAplicable() != null
                ? dto.getTropicaAplicable() : "TODOS")
            .estado("ACTIVA")
            .build();

        practica = repositoryPractica.save(practica);
        return toPracticaAdminDTO(practica);
    }

    // ── Editar práctica ───────────────────────────────────────────────────

    @Override
    @Transactional
    public PracticaAdminDTO editarPractica(
            Integer idPractica, EditarPracticaDTO dto) {

        Practica practica = repositoryPractica.findById(idPractica)
            .orElseThrow(() -> new RuntimeException("PRACTICA_NO_ENCONTRADA"));

        if (dto.getNombre()          != null) practica.setNombre(dto.getNombre());
        if (dto.getDescripcion()     != null) practica.setDescripcion(dto.getDescripcion());
        if (dto.getObjetivo()        != null) practica.setObjetivo(dto.getObjetivo());
        if (dto.getCategoria()       != null) practica.setCategoria(dto.getCategoria());
        if (dto.getImpactoEsperado() != null) practica.setImpactoEsperado(dto.getImpactoEsperado());
        if (dto.getPasos()           != null) practica.setPasos(dto.getPasos());
        if (dto.getKpiImpactado()    != null) practica.setKpiImpactado(dto.getKpiImpactado());
        if (dto.getDificultad()      != null) practica.setDificultad(dto.getDificultad());
        if (dto.getDuracionDias()    != null) practica.setDuracionDias(dto.getDuracionDias());
        if (dto.getEscala()          != null) practica.setEscala(dto.getEscala());
        if (dto.getTropicaAplicable() != null) practica.setTropicaAplicable(dto.getTropicaAplicable());
        if (dto.getEstado()          != null) practica.setEstado(dto.getEstado());

        repositoryPractica.save(practica);
        return toPracticaAdminDTO(practica);
    }

    // ── Desactivar práctica ───────────────────────────────────────────────

    @Override
    @Transactional
    public void desactivarPractica(Integer idPractica) {
        Practica practica = repositoryPractica.findById(idPractica)
            .orElseThrow(() -> new RuntimeException("PRACTICA_NO_ENCONTRADA"));
        practica.setEstado("INACTIVA");
        repositoryPractica.save(practica);
    }

    // ── Mapper ────────────────────────────────────────────────────────────

    private PracticaAdminDTO toPracticaAdminDTO(Practica p) {
        // Reglas vinculadas a esta práctica
        List<ReglaResumenDTO> reglas = repositoryReglaPractica
            .findByPractica_IdPractica(p.getIdPractica())
            .stream()
            .map(rp -> ReglaResumenDTO.builder()
                .idRegla(rp.getRegla().getIdRegla())
                .codigoKpi(rp.getRegla().getKpi() != null
                    ? rp.getRegla().getKpi().getCodigo() : null)
                .nombreKpi(rp.getRegla().getKpi() != null
                    ? rp.getRegla().getKpi().getNombre() : null)
                .operador(rp.getRegla().getOperador())
                .escalaAplicable(rp.getRegla().getEscalaAplicable())
                .estado(rp.getRegla().getEstado())
                .orden(rp.getOrden())
                .build())
            .collect(Collectors.toList());

        return PracticaAdminDTO.builder()
            .idPractica(p.getIdPractica())
            .nombre(p.getNombre())
            .descripcion(p.getDescripcion())
            .objetivo(p.getObjetivo())
            .categoria(p.getCategoria())
            .impactoEsperado(p.getImpactoEsperado())
            .estado(p.getEstado())
            .pasos(p.getPasos())
            .kpiImpactado(p.getKpiImpactado())
            .dificultad(p.getDificultad())
            .duracionDias(p.getDuracionDias())
            .escala(p.getEscala())
            .tropicaAplicable(p.getTropicaAplicable())
            .reglasVinculadas(reglas)
            .build();
    }
}