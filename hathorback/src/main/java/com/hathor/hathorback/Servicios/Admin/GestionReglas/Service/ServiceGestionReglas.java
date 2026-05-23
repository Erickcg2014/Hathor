package com.hathor.hathorback.Servicios.Admin.GestionReglas.Service;

import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Entities.Practicas.*;
import com.hathor.hathorback.Servicios.Admin.GestionReglas.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpi;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Repository.*;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceGestionReglas implements IServiceGestionReglas {

    @Autowired
    private IRepositoryRegla        repositoryRegla;

    @Autowired
    private IRepositoryReglaPractica repositoryReglaPractica;

    @Autowired
    private IRepositoryPractica     repositoryPractica;

    @Autowired
    private IRepositoryKpi          repositoryKpi;

    // ── Obtener todas con filtros ─────────────────────────────────────────

    @Override
    public List<ReglaAdminDTO> getReglas(
            String estado, String escala, Integer idKpi) {

        List<Regla> reglas = repositoryRegla.findAll();

        return reglas.stream()
            .filter(r -> estado == null || estado.equals(r.getEstado()))
            .filter(r -> escala == null
                || escala.equals(r.getEscalaAplicable())
                || "TODAS".equals(r.getEscalaAplicable()))
            .filter(r -> idKpi == null
                || (r.getKpi() != null
                    && idKpi.equals(r.getKpi().getIdKpi())))
            .map(this::toReglaAdminDTO)
            .collect(Collectors.toList());
    }

    // ── Obtener por ID ────────────────────────────────────────────────────

    @Override
    public ReglaAdminDTO getReglaById(Integer idRegla) {
        Regla regla = repositoryRegla.findById(idRegla)
            .orElseThrow(() -> new RuntimeException("REGLA_NO_ENCONTRADA"));
        return toReglaAdminDTO(regla);
    }

    // ── Crear regla ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public ReglaAdminDTO crearRegla(CrearReglaDTO dto) {
        Kpi kpi = repositoryKpi.findById(dto.getIdKpi())
            .orElseThrow(() -> new RuntimeException("KPI_NO_ENCONTRADO"));

        Regla regla = Regla.builder()
            .kpi(kpi)
            .operador(dto.getOperador())
            .umbral1(dto.getUmbral1())
            .umbral2(dto.getUmbral2())
            .umbralTipo(dto.getUmbralTipo())
            .estadoKpiObjetivo(dto.getEstadoKpiObjetivo())
            .escalaAplicable(dto.getEscalaAplicable())
            .mensaje(dto.getMensaje())
            .prioridad(dto.getPrioridad())
            .estado("ACTIVA")
            .build();

        regla = repositoryRegla.save(regla);

        // Vincular prácticas si se proporcionaron
        if (dto.getPracticas() != null && !dto.getPracticas().isEmpty()) {
            vincularPracticas(regla, dto.getPracticas());
        }

        return toReglaAdminDTO(
            repositoryRegla.findById(regla.getIdRegla()).orElseThrow()
        );
    }

    // ── Editar regla ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public ReglaAdminDTO editarRegla(Integer idRegla, EditarReglaDTO dto) {
        Regla regla = repositoryRegla.findById(idRegla)
            .orElseThrow(() -> new RuntimeException("REGLA_NO_ENCONTRADA"));

        if (dto.getOperador()          != null) regla.setOperador(dto.getOperador());
        if (dto.getUmbral1()           != null) regla.setUmbral1(dto.getUmbral1());
        if (dto.getUmbral2()           != null) regla.setUmbral2(dto.getUmbral2());
        if (dto.getUmbralTipo()        != null) regla.setUmbralTipo(dto.getUmbralTipo());
        if (dto.getEstadoKpiObjetivo() != null) regla.setEstadoKpiObjetivo(dto.getEstadoKpiObjetivo());
        if (dto.getEscalaAplicable()   != null) regla.setEscalaAplicable(dto.getEscalaAplicable());
        if (dto.getMensaje()           != null) regla.setMensaje(dto.getMensaje());
        if (dto.getPrioridad()         != null) regla.setPrioridad(dto.getPrioridad());
        if (dto.getEstado()            != null) regla.setEstado(dto.getEstado());

        repositoryRegla.save(regla);

        // Si se envía nueva lista de prácticas — reemplazar completa
        if (dto.getPracticas() != null) {
            repositoryReglaPractica.deleteByRegla_IdRegla(idRegla);
            if (!dto.getPracticas().isEmpty()) {
                vincularPracticas(regla, dto.getPracticas());
            }
        }

        return toReglaAdminDTO(
            repositoryRegla.findById(idRegla).orElseThrow()
        );
    }

    // ── Desactivar regla ──────────────────────────────────────────────────

    @Override
    @Transactional
    public void desactivarRegla(Integer idRegla) {
        Regla regla = repositoryRegla.findById(idRegla)
            .orElseThrow(() -> new RuntimeException("REGLA_NO_ENCONTRADA"));
        regla.setEstado("INACTIVA");
        repositoryRegla.save(regla);
    }

    // ── Vincular práctica ─────────────────────────────────────────────────

    @Override
    @Transactional
    public ReglaAdminDTO vincularPractica(
            Integer idRegla, VincularPracticaDTO dto) {

        Regla    regla    = repositoryRegla.findById(idRegla)
            .orElseThrow(() -> new RuntimeException("REGLA_NO_ENCONTRADA"));
        Practica practica = repositoryPractica.findById(dto.getIdPractica())
            .orElseThrow(() -> new RuntimeException("PRACTICA_NO_ENCONTRADA"));

        // Verificar que no esté ya vinculada
        boolean yaVinculada = repositoryReglaPractica
            .existsByRegla_IdReglaAndPractica_IdPractica(
                idRegla, dto.getIdPractica());

        if (yaVinculada) {
            throw new RuntimeException("PRACTICA_YA_VINCULADA");
        }

        ReglaPractica rp = ReglaPractica.builder()
            .regla(regla)
            .practica(practica)
            .orden(dto.getOrden())
            .build();

        repositoryReglaPractica.save(rp);

        return toReglaAdminDTO(
            repositoryRegla.findById(idRegla).orElseThrow()
        );
    }

    // ── Desvincular práctica ──────────────────────────────────────────────

    @Override
    @Transactional
    public ReglaAdminDTO desvincularPractica(
            Integer idRegla, Integer idPractica) {

        repositoryReglaPractica
            .deleteByRegla_IdReglaAndPractica_IdPractica(idRegla, idPractica);

        return toReglaAdminDTO(
            repositoryRegla.findById(idRegla).orElseThrow()
        );
    }

    // ── Reordenar prácticas ───────────────────────────────────────────────

    @Override
    @Transactional
    public ReglaAdminDTO reordenarPracticas(
            Integer idRegla, List<VincularPracticaDTO> practicas) {

        Regla regla = repositoryRegla.findById(idRegla)
            .orElseThrow(() -> new RuntimeException("REGLA_NO_ENCONTRADA"));

        // Eliminar vínculos actuales y recrear con nuevo orden
        repositoryReglaPractica.deleteByRegla_IdRegla(idRegla);
        vincularPracticas(regla, practicas);

        return toReglaAdminDTO(
            repositoryRegla.findById(idRegla).orElseThrow()
        );
    }

    // ── Helpers privados ──────────────────────────────────────────────────

    private void vincularPracticas(
            Regla regla, List<VincularPracticaDTO> practicasDTO) {

        for (VincularPracticaDTO dto : practicasDTO) {
            Practica practica = repositoryPractica
                .findById(dto.getIdPractica())
                .orElseThrow(() -> new RuntimeException(
                    "PRACTICA_NO_ENCONTRADA: " + dto.getIdPractica()));

            ReglaPractica rp = ReglaPractica.builder()
                .regla(regla)
                .practica(practica)
                .orden(dto.getOrden() != null ? dto.getOrden() : 1)
                .build();

            repositoryReglaPractica.save(rp);
        }
    }

    private ReglaAdminDTO toReglaAdminDTO(Regla r) {
        List<ReglaPracticaDTO> practicasDTO =
            repositoryReglaPractica.findByReglaOrdenadas(r.getIdRegla())
                .stream()
                .map(rp -> ReglaPracticaDTO.builder()
                    .id(rp.getId())
                    .idPractica(rp.getPractica().getIdPractica())
                    .nombrePractica(rp.getPractica().getNombre())
                    .categoriaPractica(rp.getPractica().getCategoria())
                    .dificultadPractica(rp.getPractica().getDificultad())
                    .orden(rp.getOrden())
                    .build())
                .collect(Collectors.toList());

        return ReglaAdminDTO.builder()
            .idRegla(r.getIdRegla())
            .idKpi(r.getKpi() != null ? r.getKpi().getIdKpi() : null)
            .codigoKpi(r.getKpi() != null ? r.getKpi().getCodigo() : null)
            .nombreKpi(r.getKpi() != null ? r.getKpi().getNombre() : null)
            .operador(r.getOperador())
            .umbral1(r.getUmbral1())
            .umbral2(r.getUmbral2())
            .umbralTipo(r.getUmbralTipo())
            .estadoKpiObjetivo(r.getEstadoKpiObjetivo())
            .escalaAplicable(r.getEscalaAplicable())
            .estado(r.getEstado())
            .mensaje(r.getMensaje())
            .prioridad(r.getPrioridad())
            .practicas(practicasDTO)
            .build();
    }
}