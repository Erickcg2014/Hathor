package com.hathor.hathorback.Servicios.Admin.Estadisticas.Service;

import com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpiHato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceEstadisticas implements IServiceEstadisticas {

    @Autowired
    private IRepositoryHato    repositoryHato;

    @Autowired
    private IRepositoryKpiHato repositoryKpiHato;

    // KPIs clave para estadísticas
    private static final String KPI_LITROS_VACA    = "KPI_LITROS_VACA_DIA";
    private static final String KPI_MARGEN_NETO    = "KPI_MARGEN_NETO";
    private static final String KPI_CARGA_ANIMAL   = "KPI_CARGA_ANIMAL";

    // ── Estadísticas globales ─────────────────────────────────────────────

    @Override
    public EstadisticasGlobalesDTO getEstadisticasGlobales() {
        long   totalHatos     = repositoryHato.count();
        long   hatosConKpis   = repositoryKpiHato.countHatosConKpis(null, null, null);
        Double promComp       = repositoryHato.findPromedioCompletitud();

        List<Object[]> porEscala      = repositoryHato.countHatosPorEscala();
        List<Object[]> porTropico     = repositoryHato.countHatosPorTropico();
        List<Object[]> porDepartamento = repositoryHato.countHatosPorDepartamento();

        return EstadisticasGlobalesDTO.builder()
            .totalHatos(totalHatos)
            .hatosConKpis(hatosConKpis)
            .promedioCompletitud(promComp != null ? promComp : 0.0)
            .totalDepartamentos(porDepartamento.size())
            .hatosPorEscala(toEntradas(porEscala, totalHatos))
            .hatosPorTropico(toEntradas(porTropico, totalHatos))
            .hatosPorDepartamento(toEntradas(porDepartamento, totalHatos))
            .build();
    }

    // ── Estadísticas por departamento ─────────────────────────────────────

    @Override
    public List<EstadisticasDepartamentoDTO> getEstadisticasPorDepartamento() {
        List<Object[]> porDepto = repositoryHato.countHatosPorDepartamento();

        return porDepto.stream().map(row -> {
            String depto = (String) row[0];
            long   total = (Long)   row[1];

            long   conKpis   = repositoryKpiHato
                .countHatosConKpis(depto, null, null);
            Double promLitros = repositoryKpiHato
                .findPromedioKpi(KPI_LITROS_VACA, depto, null, null);

            return EstadisticasDepartamentoDTO.builder()
                .departamento(depto)
                .totalHatos(total)
                .promedioCompletitud(0.0) 
                .hatosConKpis(conKpis)
                .kpiDestacado("Litros/Vaca/Día")
                .promedioKpiDestacado(promLitros)
                .build();
        }).collect(Collectors.toList());
    }

    // ── Estadísticas por escala ───────────────────────────────────────────

    @Override
    public List<EstadisticasEscalaDTO> getEstadisticasPorEscala() {
        long           totalHatos = repositoryHato.count();
        List<Object[]> porEscala  = repositoryHato.countHatosPorEscala();

        return porEscala.stream().map(row -> {
            String escala = (String) row[0];
            long   total  = (Long)   row[1];
            double pct    = totalHatos > 0
                ? ((double) total / totalHatos) * 100 : 0;

            Double promLitros = repositoryKpiHato
                .findPromedioKpi(KPI_LITROS_VACA, null, null, escala);
            Double promMargen = repositoryKpiHato
                .findPromedioKpi(KPI_MARGEN_NETO, null, null, escala);

            return EstadisticasEscalaDTO.builder()
                .escala(escala)
                .totalHatos(total)
                .porcentajeDelTotal(pct)
                .promedioLitrosVacaDia(promLitros)
                .promedioMargenNeto(promMargen)
                .build();
        }).collect(Collectors.toList());
    }

    // ── Estadísticas por trópico ──────────────────────────────────────────

    @Override
    public List<EstadisticasTropicoDTO> getEstadisticasPorTropico() {
        long           totalHatos = repositoryHato.count();
        List<Object[]> porTropico = repositoryHato.countHatosPorTropico();

        return porTropico.stream().map(row -> {
            String tropico = (String) row[0];
            long   total   = (Long)   row[1];
            double pct     = totalHatos > 0
                ? ((double) total / totalHatos) * 100 : 0;

            Double promLitros = repositoryKpiHato
                .findPromedioKpi(KPI_LITROS_VACA, null, tropico, null);
            Double promCarga  = repositoryKpiHato
                .findPromedioKpi(KPI_CARGA_ANIMAL, null, tropico, null);

            return EstadisticasTropicoDTO.builder()
                .tropico(tropico)
                .totalHatos(total)
                .porcentajeDelTotal(pct)
                .promedioLitrosVacaDia(promLitros)
                .promedioCargarAnimal(promCarga)
                .build();
        }).collect(Collectors.toList());
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private List<EntradaEstadisticaDTO> toEntradas(
            List<Object[]> rows, long total) {
        return rows.stream().map(row -> {
            String clave    = (String) row[0];
            Long   cantidad = (Long)   row[1];
            double pct      = total > 0
                ? ((double) cantidad / total) * 100 : 0;
            return EntradaEstadisticaDTO.builder()
                .clave(clave)
                .cantidad(cantidad)
                .porcentaje(pct)
                .build();
        }).collect(Collectors.toList());
    }
}