package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkHato;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteConfigDTO;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.DeviceRgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class ReporteSeccionBenchmarking {

    @Autowired
    private ReporteEstilos estilos;

    private static final Map<String, String> CATEGORIAS = new LinkedHashMap<>() {{
        put("PRODUCTIVIDAD", "🥛  Productividad");
        put("HATO",          "🐄  Hato");
        put("FINANCIERO",    "💰  Financiero");
        put("EFICIENCIA",    "⚙️  Eficiencia");
    }};

    private static final Map<String, String> LABELS_NIVEL = Map.of(
        "NACIONAL",      "🇨🇴 Nacional",
        "TROPICO",       "🌿 Por trópico",
        "TROPICO_ESCALA","📐 Trópico + escala"
    );

    public void construir(
            Document            doc,
            Hato                hato,
            List<BenchmarkHato> benchmarks,
            ReporteConfigDTO    config
    ) throws IOException {

        doc.add(estilos.encabezadoSeccion("🔍", "Benchmarking Sectorial"));

        // Nota introductoria
        String nivel      = config.getNivelBenchmark() != null
            ? config.getNivelBenchmark() : "NACIONAL";
        String labelNivel = LABELS_NIVEL.getOrDefault(nivel, nivel);

        doc.add(new Paragraph(
            "Comparativa del hato contra valores de referencia sectoriales colombianos. " +
            "Nivel de comparación: " + labelNivel + ". " +
            "El percentil indica la posición relativa del hato: " +
            "P50 = promedio del sector, P100 = mejor del sector.")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(12f));

        // Filtrar por nivel
        List<BenchmarkHato> filtrados = benchmarks.stream()
            .filter(b -> nivel.equals(b.getNivelBenchmark()))
            .collect(Collectors.toList());

        if (filtrados.isEmpty()) {
            doc.add(new Paragraph(
                "No hay datos de benchmarking disponibles para el nivel " + labelNivel + ".")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                .setMarginBottom(8f));
            return;
        }

        // Resumen general de percentiles
        doc.add(estilos.subEncabezado("Resumen de posición competitiva"));
        doc.add(construirResumenPercentiles(filtrados));

        // Info del contexto de comparación
        doc.add(construirContextoComparacion(hato, nivel));

        // Tabla por categoría
        Map<String, List<BenchmarkHato>> porCategoria = filtrados.stream()
            .filter(b -> b.getKpi() != null)
            .collect(Collectors.groupingBy(
                b -> b.getKpi().getCategoria() != null
                    ? b.getKpi().getCategoria() : "OTROS",
                LinkedHashMap::new,
                Collectors.toList()
            ));

        for (Map.Entry<String, String> entrada : CATEGORIAS.entrySet()) {
            String categoria = entrada.getKey();
            String label     = entrada.getValue();

            List<BenchmarkHato> items = porCategoria.get(categoria);
            if (items == null || items.isEmpty()) continue;

            // Ordenar por percentil DESC
            items.sort((a, b) ->
                Float.compare(
                    b.getPercentil() != null ? b.getPercentil() : 0f,
                    a.getPercentil() != null ? a.getPercentil() : 0f
                ));

            doc.add(estilos.subEncabezado(label));
            doc.add(construirTablaBenchmark(items));
        }

        doc.add(estilos.divisor());
    }

    // ── Resumen de percentiles ────────────────────────────────────────────

    private Table construirResumenPercentiles(
            List<BenchmarkHato> benchmarks) throws IOException {

        // Agrupar por categoría y calcular percentil promedio
        Map<String, List<BenchmarkHato>> porCat = benchmarks.stream()
            .filter(b -> b.getKpi() != null && b.getPercentil() != null)
            .collect(Collectors.groupingBy(
                b -> b.getKpi().getCategoria() != null
                    ? b.getKpi().getCategoria() : "OTROS"
            ));

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(14f);

        for (Map.Entry<String, String> entrada : CATEGORIAS.entrySet()) {
            String categoria = entrada.getKey();
            String label     = entrada.getValue();

            List<BenchmarkHato> items = porCat.getOrDefault(categoria, List.of());

            double promPercentil = items.stream()
                .mapToDouble(b -> b.getPercentil())
                .average()
                .orElse(0.0);

            long alto  = items.stream().filter(b -> b.getPercentil() >= 70f).count();
            long medio = items.stream().filter(b -> {
                float p = b.getPercentil();
                return p >= 40f && p < 70f;
            }).count();
            long bajo  = items.stream().filter(b -> b.getPercentil() < 40f).count();

            DeviceRgb colorProm  = colorPercentil((float) promPercentil);
            DeviceRgb fondoProm  = fondoPercentil((float) promPercentil);
            DeviceRgb bordeProm  = bordePercentil((float) promPercentil);

            Cell celda = new Cell()
                .setBackgroundColor(fondoProm)
                .setBorder(new SolidBorder(bordeProm, 1f))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(6f))
                .setPadding(10f)
                .setPaddingLeft(4f)
                .setPaddingRight(4f);

            // Categoría
            celda.add(new Paragraph(label)
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(colorProm)
                .setMarginBottom(6f));

            // Percentil promedio grande
            celda.add(new Paragraph(String.format("P%.0f", promPercentil))
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(22f)
                .setFontColor(colorProm)
                .setMarginBottom(4f));

            celda.add(new Paragraph("percentil promedio")
                .setFont(estilos.getFuenteNormal())
                .setFontSize(7f)
                .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
                .setMarginBottom(6f));

            // Mini distribución
            Table mini = new Table(UnitValue.createPercentArray(
                new float[]{33f, 33f, 33f}))
                .useAllAvailableWidth();

            mini.addCell(miniDistribucion(String.valueOf(alto),  "Alto",  ReporteEstilos.ESTADO_OPTIMO));
            mini.addCell(miniDistribucion(String.valueOf(medio), "Medio", ReporteEstilos.ESTADO_ACEPTABLE));
            mini.addCell(miniDistribucion(String.valueOf(bajo),  "Bajo",  ReporteEstilos.ESTADO_CRITICO));

            celda.add(mini);
            tabla.addCell(celda);
        }

        return tabla;
    }

    // ── Contexto de comparación ───────────────────────────────────────────

    private Table construirContextoComparacion(
            Hato hato, String nivel) throws IOException {

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        StringBuilder texto = new StringBuilder("Contexto de comparación: ");

        texto.append("Hato ubicado en ").append(hato.getDepartamento());

        if (hato.getTropico() != null && !nivel.equals("NACIONAL")) {
            texto.append(" · Trópico ").append(hato.getTropico());
        }
        if (hato.getEscala() != null && nivel.equals("TROPICO_ESCALA")) {
            texto.append(" · Escala ").append(hato.getEscala());
        }

        tabla.addCell(new Cell()
            .add(new Paragraph(texto.toString())
                .setFont(estilos.getFuenteNormal())
                .setFontSize(8f)
                .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO))
            .setBackgroundColor(ReporteEstilos.VERDE_SUAVE)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 1f))
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(4f))
            .setPadding(8f));

        return tabla;
    }

    // ── Tabla de benchmarking por categoría ───────────────────────────────

    private Table construirTablaBenchmark(
            List<BenchmarkHato> items) throws IOException {

        float[] anchos = new float[]{28f, 14f, 14f, 14f, 12f, 18f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(14f);

        // Encabezados
        tabla.addHeaderCell(estilos.celdaEncabezado("KPI"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Tu valor"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Prom. sector"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Top sector"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Percentil"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Interpretación"));

        for (int i = 0; i < items.size(); i++) {
            BenchmarkHato b      = items.get(i);
            boolean       alterna = i % 2 != 0;
            String        unidad  = b.getKpi() != null ? b.getKpi().getUnidad() : null;

            // KPI nombre
            tabla.addCell(estilos.celdaDatoNegrita(
                b.getKpi() != null ? b.getKpi().getNombre() : "—", alterna));

            // Tu valor
            String valorHato = estilos.formatearValorKpi(b.getValorHato(), unidad);
            tabla.addCell(estilos.celdaDatoCentrado(valorHato, alterna));

            // Promedio sector
            String promedio = b.getBenchReferencia() != null
                ? estilos.formatearValorKpi(b.getBenchReferencia().getValorPromedio(), unidad)
                : "—";
            tabla.addCell(estilos.celdaDatoCentrado(promedio, alterna));

            // Top sector
            String top = b.getBenchReferencia() != null
                ? estilos.formatearValorKpi(b.getBenchReferencia().getValorTop(), unidad)
                : "—";
            tabla.addCell(estilos.celdaDatoCentrado(top, alterna));

            // Percentil coloreado
            tabla.addCell(estilos.celdaPercentil(b.getPercentil(), alterna));

            // Interpretación coloreada
            tabla.addCell(estilos.celdaEstado(b.getInterpretacion(), alterna));
        }

        return tabla;
    }

    // ── Helpers de color por percentil ────────────────────────────────────

    private DeviceRgb colorPercentil(float p) {
        if (p >= 70f) return ReporteEstilos.ESTADO_OPTIMO;
        if (p >= 40f) return ReporteEstilos.ESTADO_ACEPTABLE;
        return ReporteEstilos.ESTADO_CRITICO;
    }

    private DeviceRgb fondoPercentil(float p) {
        if (p >= 70f) return ReporteEstilos.ESTADO_OPTIMO_BG;
        if (p >= 40f) return ReporteEstilos.ESTADO_ACEPTABLE_BG;
        return ReporteEstilos.ESTADO_CRITICO_BG;
    }

    private DeviceRgb bordePercentil(float p) {
        if (p >= 70f) return ReporteEstilos.VERDE_MEDIO;
        if (p >= 40f) return new DeviceRgb(253, 230, 138);
        return new DeviceRgb(254, 202, 202);
    }

    // ── distribución ─────────────────────────────────────────────────

    private Cell miniDistribucion(
            String texto, String label, DeviceRgb color) throws IOException {

        Cell celda = new Cell()
            .setBorder(Border.NO_BORDER)
            .setPadding(2f);

        celda.add(new Paragraph(texto)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(12f)
            .setFontColor(color)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(1f));

        celda.add(new Paragraph(label)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(7f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(0f));

        return celda;
    }
}