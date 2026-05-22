package com.hathor.hathorback.Servicios.Usuario.Reporte.Service;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;
import com.hathor.hathorback.Entities.Reportes.ReporteHistorial;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Repository.IRepositoryBenchmarking;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.RankingCompuestoDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.RankingResumenDTO;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service.IServiceRanking;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;
import com.hathor.hathorback.Servicios.Usuario.Practicas.RecomendacionHato.Repository.IRepositoryRecomendacionHato;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;
import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteConfigDTO;
import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteHistorialDTO;
import com.hathor.hathorback.Servicios.Usuario.Reporte.Repository.IRepositoryReporteHistorial;
import com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.properties.AreaBreakType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceReporte implements IServiceReporte {

    // ── Helpers de sección ────────────────────────────────────────────────
    @Autowired private ReportePortada            portada;
    @Autowired private ReporteResumenEjecutivo   resumenEjecutivo;
    @Autowired private ReporteSeccionKpis        seccionKpis;
    @Autowired private ReporteSeccionBenchmarking seccionBenchmarking;
    @Autowired private ReporteSeccionRanking     seccionRanking;
    @Autowired private ReporteSeccionFinanzas    seccionFinanzas;
    @Autowired private ReporteSeccionProduccion  seccionProduccion;
    @Autowired private ReporteSeccionPracticas   seccionPracticas;

    // ── Repositorios y servicios ──────────────────────────────────────────
    @Autowired private IServiceHato                    serviceHato;
    @Autowired private IServiceKpi                     serviceKpi;
    @Autowired private IServiceRanking                 serviceRanking;
    @Autowired private IRepositoryBenchmarking         repoBenchmarking;
    @Autowired private IRepositoryRegistroFinanciero   repoFinanciero;
    @Autowired private IRepositoryPerfilProductivo     repoPerfilProductivo;
    @Autowired private IRepositoryProduccionLeche      repoProduccionLeche;
    @Autowired private IRepositoryRecomendacionHato    repoRecomendacion;
    @Autowired private IRepositoryHatoPractica         repoHatoPractica;
    @Autowired private IRepositoryReporteHistorial     repoHistorial;
    @Autowired private IRepositoryHato                 repoHato;

    // Para serializar/deserializar ReporteConfigDTO a JSON
    @Autowired private com.fasterxml.jackson.databind.ObjectMapper objectMapper;


    @Override
    public byte[] generarReporte(UUID idHato, ReporteConfigDTO config) {
        try {
            // ── Setup iText ───────────────────────────────────────────────
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter             writer = new PdfWriter(baos);
            PdfDocument           pdf    = new PdfDocument(writer);
            Document              doc    = new Document(pdf, PageSize.A4);

            doc.setMargins(40f, 40f, 40f, 40f);

            // ── Cargar datos ──────────────────────────────────────────────
            Hato hato = serviceHato.findHatoById(idHato);

            // KPIs
            List<KpiResultadoDTO> kpis = null;
            if (config.isIncluirKpis()
                    || config.isIncluirResumenEjecutivo()
                    || config.isIncluirBenchmarking()) {
                kpis = serviceKpi.getKpisDelHato(idHato);
            }

            // Benchmarks
            List<BenchmarkHato> benchmarks = null;
            if (config.isIncluirBenchmarking() || config.isIncluirResumenEjecutivo()) {
                String nivel = config.getNivelBenchmark() != null
                    ? config.getNivelBenchmark() : "NACIONAL";
                benchmarks = repoBenchmarking
                    .findAllConReferenciaByHato(idHato)
                    .stream()
                    .filter(b -> nivel.equals(b.getNivelBenchmark()))
                    .collect(java.util.stream.Collectors.toList());
            }

            // Ranking
            RankingResumenDTO   rankingResumen   = null;
            RankingCompuestoDTO rankingCompuesto = null;
            if (config.isIncluirRanking() || config.isIncluirResumenEjecutivo()) {
                try {
                    rankingResumen   = serviceRanking.getResumenRanking(idHato);
                    rankingCompuesto = serviceRanking.getRankingCompuesto(idHato, null);
                } catch (Exception e) {
                    // Ranking puede no estar disponible — continuar sin él
                }
            }

            // Finanzas
            List<RegistroFinanciero> registrosFinancieros = null;
            if (config.isIncluirFinanzas() || config.isIncluirResumenEjecutivo()) {
                registrosFinancieros = repoFinanciero
                    .findByHato_IdHato(idHato);
            }

            // Producción
            PerfilProductivo      perfilProductivo  = null;
            List<ProduccionLeche> registrosProduccion = null;
            if (config.isIncluirProduccion() || config.isIncluirResumenEjecutivo()) {
                perfilProductivo = repoPerfilProductivo
                    .findByHato_IdHato(idHato)
                    .orElse(null);
                registrosProduccion = repoProduccionLeche
                    .findByHato_IdHatoOrderByFechaDesc(idHato);
            }

            // Prácticas
            List<RecomendacionHato> recomendaciones = null;
            List<HatoPractica>      practicas       = null;
            if (config.isIncluirPracticas()) {
                recomendaciones = repoRecomendacion
                    .findActivasByHatoPaginado(idHato, PageRequest.of(0, 100))
                    .getContent();
                practicas = repoHatoPractica
                    .findByHatoOrdenadas(idHato);
            }

            // ── Portada ───────────────────────────────────────────────────
            portada.construir(doc, hato, config);

            // ── Resumen ejecutivo ─────────────────────────────────────────
            if (config.isIncluirResumenEjecutivo()) {
                resumenEjecutivo.construir(doc, hato, kpis, rankingResumen);
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // ── KPIs ──────────────────────────────────────────────────────
            if (config.isIncluirKpis() && kpis != null && !kpis.isEmpty()) {
                seccionKpis.construir(doc, kpis);
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // ── Benchmarking ──────────────────────────────────────────────
            if (config.isIncluirBenchmarking()
                    && benchmarks != null && !benchmarks.isEmpty()) {
                seccionBenchmarking.construir(doc, hato, benchmarks, config);
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // ── Ranking ───────────────────────────────────────────────────
            if (config.isIncluirRanking()) {
                seccionRanking.construir(doc, hato, rankingResumen, rankingCompuesto);
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // ── Finanzas ──────────────────────────────────────────────────
            if (config.isIncluirFinanzas()
                    && registrosFinancieros != null
                    && !registrosFinancieros.isEmpty()) {
                seccionFinanzas.construir(doc, registrosFinancieros, config);
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // ── Producción ────────────────────────────────────────────────
            if (config.isIncluirProduccion()) {
                seccionProduccion.construir(
                    doc,
                    perfilProductivo,
                    registrosProduccion != null ? registrosProduccion : List.of(),
                    config
                );
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // ── Prácticas ─────────────────────────────────────────────────
            if (config.isIncluirPracticas()) {
                seccionPracticas.construir(
                    doc,
                    recomendaciones != null ? recomendaciones : List.of(),
                    practicas       != null ? practicas       : List.of()
                );
            }

            // ── Pie de página en todas las páginas ────────────────────────
            try {
                agregarPieDePagina(pdf, hato);
            } catch (Exception e) {
                // El pie de página falló pero el reporte puede estar completo
                System.err.println("Advertencia pie de página: " + e.getMessage());
            }

            try {
                doc.close();
            } catch (Exception e) {
                System.err.println("Advertencia al cerrar documento: " + e.getMessage());
            }

            // Guardar historial
            try {
                String configJson = objectMapper.writeValueAsString(config);
                String nombre = config.getTituloPersonalizado() != null
                    ? config.getTituloPersonalizado()
                    : "Reporte " + hato.getNombreHato() + " — " +
                    LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                ReporteHistorial historial = ReporteHistorial.builder()
                    .hato(hato)
                    .tipo(config.getTipo() != null ? config.getTipo() : "MANUAL")
                    .nombre(nombre)
                    .fechaGeneracion(java.time.LocalDateTime.now())
                    .configuracionJson(configJson)
                    .periodoDesde(config.getPeriodoDesde())
                    .periodoHasta(config.getPeriodoHasta())
                    .tamanioBytes((long) baos.size())
                    .estado("GENERADO")
                    .build();

                repoHistorial.save(historial);
            } catch (Exception e) {
                // No fallar el reporte por error en historial
                System.err.println("Error guardando historial: " + e.getMessage());
            }
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("ERROR_GENERANDO_REPORTE: " + e.getMessage(), e);
        }
    }

    // ── Pie de página ─────────────────────────────────────────────────────

    private void agregarPieDePagina(PdfDocument pdf, Hato hato) {
        int totalPaginas = pdf.getNumberOfPages();

        for (int i = 1; i <= totalPaginas; i++) {
            // Saltar portada (página 1)
            if (i == 1) continue;

            com.itextpdf.kernel.pdf.canvas.PdfCanvas canvas =
                new com.itextpdf.kernel.pdf.canvas.PdfCanvas(
                    pdf.getPage(i));

            com.itextpdf.kernel.font.PdfFont font;
            try {
                font = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
            } catch (Exception e) {
                continue;
            }

            PageSize pageSize = PageSize.A4;
            float    ancho    = pageSize.getWidth();
            float    yPos     = 25f;

            // Línea separadora
            canvas.setStrokeColor(new com.itextpdf.kernel.colors.DeviceRgb(233, 245, 235))
                  .setLineWidth(0.5f)
                  .moveTo(40f, yPos + 10f)
                  .lineTo(ancho - 40f, yPos + 10f)
                  .stroke();

            // Texto izquierda — nombre del hato
            canvas.beginText()
                  .setFontAndSize(font, 7f)
                  .setColor(new com.itextpdf.kernel.colors.DeviceRgb(156, 163, 175), true)
                  .moveText(40f, yPos)
                  .showText(hato.getNombreHato() + " · Hathor — Gestión Ganadera Colombia")
                  .endText();

            // Texto derecha — paginación
            String pagTexto = "Página " + i + " de " + totalPaginas;
            canvas.beginText()
                  .setFontAndSize(font, 7f)
                  .setColor(new com.itextpdf.kernel.colors.DeviceRgb(156, 163, 175), true)
                  .moveText(ancho - 40f - (pagTexto.length() * 4f), yPos)
                  .showText(pagTexto)
                  .endText();

            canvas.release();
        }
    }

    @Override
    public List<ReporteHistorialDTO> getHistorial(UUID idHato) {
        return repoHistorial.findByHatoOrderByFechaDesc(idHato)
            .stream()
            .map(this::toHistorialDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public ReporteHistorialDTO getReporteById(Integer idReporte) {
        ReporteHistorial r = repoHistorial.findById(idReporte)
            .orElseThrow(() -> new RuntimeException("REPORTE_NO_ENCONTRADO"));
        return toHistorialDTO(r);
    }

    @Override
    public byte[] regenerarReporte(Integer idReporte) {
        ReporteHistorial historial = repoHistorial.findById(idReporte)
            .orElseThrow(() -> new RuntimeException("REPORTE_NO_ENCONTRADO"));
        try {
            ReporteConfigDTO config = objectMapper.readValue(
                historial.getConfiguracionJson(), ReporteConfigDTO.class);
            return generarReporte(historial.getHato().getIdHato(), config);
        } catch (Exception e) {
            throw new RuntimeException("ERROR_REGENERANDO_REPORTE: "
                + e.getMessage(), e);
        }
    }

    @Override
    public void generarReporteMensual(UUID idHato) {
        LocalDate primerDiaMes = LocalDate.now()
            .with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        String periodoDesde = primerDiaMes.toString();

        if (repoHistorial.existeReporteMensual(idHato, periodoDesde)) return;

        ReporteConfigDTO config = new ReporteConfigDTO();
        config.setIncluirResumenEjecutivo(true);
        config.setIncluirKpis(true);
        config.setIncluirFinanzas(true);
        config.setIncluirProduccion(true);
        config.setIncluirBenchmarking(false);
        config.setIncluirRanking(false);
        config.setIncluirPracticas(false);
        config.setNivelBenchmark("NACIONAL");
        config.setPeriodoDesde(periodoDesde);
        config.setPeriodoHasta(LocalDate.now().toString());
        config.setTipo("MENSUAL");

        generarReporte(idHato, config);
    }

    private ReporteHistorialDTO toHistorialDTO(ReporteHistorial r) {
        ReporteConfigDTO config = null;
        try {
            if (r.getConfiguracionJson() != null) {
                config = objectMapper.readValue(
                    r.getConfiguracionJson(), ReporteConfigDTO.class);
            }
        } catch (Exception ignored) {}

        return ReporteHistorialDTO.builder()
            .idReporte(r.getIdReporte())
            .tipo(r.getTipo())
            .nombre(r.getNombre())
            .fechaGeneracion(r.getFechaGeneracion() != null
                ? r.getFechaGeneracion().toString() : null)
            .periodoDesde(r.getPeriodoDesde())
            .periodoHasta(r.getPeriodoHasta())
            .urlArchivo(r.getUrlArchivo())
            .tamanioBytes(r.getTamanioBytes())
            .estado(r.getEstado())
            .tamanioFormateado(formatearTamanio(r.getTamanioBytes()))
            .configuracion(config)
            .build();
    }

    private String formatearTamanio(Long bytes) {
        if (bytes == null) return "—";
        if (bytes >= 1_048_576)
            return String.format("%.1f MB", bytes / 1_048_576.0);
        if (bytes >= 1_024)
            return String.format("%.0f KB", bytes / 1_024.0);
        return bytes + " B";
    }
}