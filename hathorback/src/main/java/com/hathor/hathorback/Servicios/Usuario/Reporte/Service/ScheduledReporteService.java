package com.hathor.hathorback.Servicios.Usuario.Reporte.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ScheduledReporteService {

    @Autowired
    private IServiceReporte  serviceReporte;

    @Autowired
    private IRepositoryHato  repositoryHato;

    // Corre todos los días a las 11:00 PM
    // Solo ejecuta lógica real el último día del mes
    @Scheduled(cron = "0 0 23 * * ?")
    public void generarReportesMensuales() {
        LocalDate hoy = LocalDate.now();

        // Verificar que sea el último día del mes
        if (!hoy.equals(hoy.with(TemporalAdjusters.lastDayOfMonth()))) {
            return;
        }

        System.out.println("📄 Iniciando generación de reportes mensuales: " + hoy);

        // Obtener todos los hatos con coordenadas o con datos
        List<Hato> hatos = repositoryHato.findAll()
            .stream()
            .filter(h -> h.getPorcentajeCompletitud() > 25)
            .toList();

        int exitosos = 0;
        int errores  = 0;

        for (Hato hato : hatos) {
            try {
                serviceReporte.generarReporteMensual(hato.getIdHato());
                exitosos++;
                // Pequeña pausa para no saturar el sistema
                Thread.sleep(500);
            } catch (Exception e) {
                errores++;
                System.err.println("❌ Error generando reporte mensual para hato "
                    + hato.getIdHato() + ": " + e.getMessage());
            }
        }

        System.out.println("✅ Reportes mensuales completados — "
            + "Exitosos: " + exitosos + " | Errores: " + errores);
    }

    // Reporte trimestral — corre el último día de marzo, junio,
    // septiembre y diciembre a las 11:30 PM
    @Scheduled(cron = "0 30 23 * * ?")
    public void generarReportesTrimestrales() {
        LocalDate hoy = LocalDate.now();

        // Solo en meses de cierre trimestral
        int mes = hoy.getMonthValue();
        if (mes != 3 && mes != 6 && mes != 9 && mes != 12) return;

        // Solo el último día del mes
        if (!hoy.equals(hoy.with(TemporalAdjusters.lastDayOfMonth()))) return;

        System.out.println("📄 Iniciando generación de reportes trimestrales: " + hoy);

        List<Hato> hatos = repositoryHato.findAll()
            .stream()
            .filter(h -> h.getPorcentajeCompletitud() > 50)
            .toList();

        for (Hato hato : hatos) {
            try {
                generarReporteTrimestral(hato);
                Thread.sleep(500);
            } catch (Exception e) {
                System.err.println("❌ Error reporte trimestral hato "
                    + hato.getIdHato() + ": " + e.getMessage());
            }
        }
    }

    private void generarReporteTrimestral(Hato hato) {
        LocalDate hoy        = LocalDate.now();
        LocalDate inicioTrim = hoy.minusMonths(2)
            .with(TemporalAdjusters.firstDayOfMonth());

        com.hathor.hathorback.Servicios.Usuario.Reporte.DTO
            .ReporteConfigDTO config =
            new com.hathor.hathorback.Servicios.Usuario.Reporte.DTO
                .ReporteConfigDTO();

        config.setIncluirResumenEjecutivo(true);
        config.setIncluirKpis(true);
        config.setIncluirFinanzas(true);
        config.setIncluirProduccion(true);
        config.setIncluirBenchmarking(true);
        config.setIncluirRanking(true);
        config.setIncluirPracticas(true);
        config.setNivelBenchmark("NACIONAL");
        config.setPeriodoDesde(inicioTrim.toString());
        config.setPeriodoHasta(hoy.toString());
        config.setTipo("TRIMESTRAL");

        serviceReporte.generarReporte(hato.getIdHato(), config);
    }
}