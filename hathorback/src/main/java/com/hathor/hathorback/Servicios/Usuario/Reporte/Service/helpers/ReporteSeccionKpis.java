package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.itextpdf.kernel.colors.DeviceRgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
public class ReporteSeccionKpis {

    @Autowired
    private ReporteEstilos estilos;

    // Orden y labels de categorías
    private static final Map<String, String> CATEGORIAS = new LinkedHashMap<>() {{
        put("PRODUCTIVIDAD", "🥛  Productividad");
        put("HATO",          "🐄  Hato");
        put("FINANCIERO",    "💰  Financiero");
        put("EFICIENCIA",    "⚙️  Eficiencia");
    }};

    public void construir(Document doc, List<KpiResultadoDTO> kpis) throws IOException {

        doc.add(estilos.encabezadoSeccion("📈", "Indicadores de Desempeño (KPIs)"));

        // Nota introductoria
        doc.add(new Paragraph(
            "Los indicadores se calculan con base en los datos registrados del hato. " +
            "El estado se determina comparando el valor con los promedios sectoriales nacionales.")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(12f));

        // Agrupar por categoría
        Map<String, List<KpiResultadoDTO>> porCategoria = kpis.stream()
            .collect(Collectors.groupingBy(
                k -> k.getCategoria() != null ? k.getCategoria() : "OTROS",
                LinkedHashMap::new,
                Collectors.toList()
            ));

        // Resumen semáforo por categoría
        doc.add(construirResumenCategorias(porCategoria));

        // Tabla por cada categoría
        for (Map.Entry<String, String> entrada : CATEGORIAS.entrySet()) {
            String categoria = entrada.getKey();
            String label     = entrada.getValue();

            List<KpiResultadoDTO> items = porCategoria.get(categoria);
            if (items == null || items.isEmpty()) continue;

            doc.add(estilos.subEncabezado(label));
            doc.add(construirTablaKpis(items));
        }

        doc.add(estilos.divisor());
    }

    // ── Resumen semáforo ──────────────────────────────────────────────────

    private Table construirResumenCategorias(
            Map<String, List<KpiResultadoDTO>> porCategoria) throws IOException {

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(16f);

        for (Map.Entry<String, String> entrada : CATEGORIAS.entrySet()) {
            String categoria = entrada.getKey();
            String label     = entrada.getValue();

            List<KpiResultadoDTO> items = porCategoria.getOrDefault(
                categoria, List.of());

            long optimos    = items.stream().filter(k -> "OPTIMO".equals(k.getEstado())).count();
            long aceptables = items.stream().filter(k -> "ACEPTABLE".equals(k.getEstado())).count();
            long criticos   = items.stream().filter(k -> "CRITICO".equals(k.getEstado())).count();
            long total      = items.size();

            // Color dominante de la categoría
            DeviceRgb colorDom  = colorDominante(optimos, aceptables, criticos, total);
            DeviceRgb fondoDom  = fondoDominante(optimos, aceptables, criticos, total);
            DeviceRgb bordeDom  = bordeDominante(optimos, aceptables, criticos, total);

            Cell celda = new Cell()
                .setBackgroundColor(fondoDom)
                .setBorder(new SolidBorder(bordeDom, 1f))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(6f))
                .setPadding(10f)
                .setPaddingLeft(4f)
                .setPaddingRight(4f);

            celda.add(new Paragraph(label)
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(colorDom)
                .setMarginBottom(6f));

            // Mini semáforo
            Table mini = new Table(UnitValue.createPercentArray(
                new float[]{33f, 33f, 33f}))
                .useAllAvailableWidth();

            mini.addCell(miniChip(String.valueOf(optimos),    "Óptimos",    ReporteEstilos.ESTADO_OPTIMO));
            mini.addCell(miniChip(String.valueOf(aceptables), "Accept.",    ReporteEstilos.ESTADO_ACEPTABLE));
            mini.addCell(miniChip(String.valueOf(criticos),   "Críticos",   ReporteEstilos.ESTADO_CRITICO));

            celda.add(mini);
            tabla.addCell(celda);
        }

        return tabla;
    }

    // ── Tabla de KPIs ─────────────────────────────────────────────────────

    private Table construirTablaKpis(List<KpiResultadoDTO> kpis) throws IOException {
        // Columnas: Nombre | Valor | Unidad | Estado | Prom.sector | Top sector | vs Promedio
        float[] anchos = new float[]{28f, 14f, 10f, 12f, 14f, 14f, 8f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(14f);

        // Encabezados
        tabla.addHeaderCell(estilos.celdaEncabezado("Indicador"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Valor"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Unidad"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Estado"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Prom. sector"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Top sector"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("vs Prom."));

        // Filas
        for (int i = 0; i < kpis.size(); i++) {
            KpiResultadoDTO kpi   = kpis.get(i);
            boolean         alterna = i % 2 != 0;

            // Nombre + descripción corta
            Cell celdaNombre = new Cell()
                .setBackgroundColor(alterna
                    ? ReporteEstilos.FONDO_FILA_ALTERNA
                    : ReporteEstilos.FONDO_CARD)
                .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
                .setPadding(5f);

            celdaNombre.add(new Paragraph(kpi.getNombre())
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_PRINCIPAL)
                .setMarginBottom(1f));

            if (kpi.getRazonSinDatos() != null) {
                celdaNombre.add(new Paragraph(kpi.getRazonSinDatos())
                    .setFont(estilos.getFuenteItalica())
                    .setFontSize(7f)
                    .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                    .setMarginBottom(0f));
            }

            tabla.addCell(celdaNombre);

            // Valor
            String valorTexto = kpi.getValor() != null
                ? estilos.formatearValorKpi(kpi.getValor(), kpi.getUnidad())
                : "—";
            tabla.addCell(estilos.celdaDatoCentrado(valorTexto, alterna));

            // Unidad
            tabla.addCell(estilos.celdaDatoCentrado(
                kpi.getUnidad() != null ? kpi.getUnidad() : "—", alterna));

            // Estado coloreado
            tabla.addCell(estilos.celdaEstado(kpi.getEstado(), alterna));

            // Promedio sector
            String promTexto = kpi.getBenchmarkPromedio() != null
                ? estilos.formatearValorKpi(kpi.getBenchmarkPromedio(), kpi.getUnidad())
                : "—";
            tabla.addCell(estilos.celdaDatoCentrado(promTexto, alterna));

            // Top sector
            String topTexto = kpi.getBenchmarkTop() != null
                ? estilos.formatearValorKpi(kpi.getBenchmarkTop(), kpi.getUnidad())
                : "—";
            tabla.addCell(estilos.celdaDatoCentrado(topTexto, alterna));

            // vs Promedio — coloreado
            tabla.addCell(celdaDiferencia(kpi.getDiferenciaPct(), alterna));
        }

        return tabla;
    }

    // ── Celda diferencia vs promedio ──────────────────────────────────────

    private Cell celdaDiferencia(Float pct, boolean alterna) throws IOException {
        if (pct == null) {
            return estilos.celdaDatoCentrado("—", alterna);
        }

        String texto = (pct >= 0 ? "+" : "") + String.format("%.1f%%", pct);

        DeviceRgb colorTexto = pct >= 0
            ? ReporteEstilos.ESTADO_OPTIMO
            : ReporteEstilos.ESTADO_CRITICO;

        DeviceRgb colorFondo = alterna
            ? ReporteEstilos.FONDO_FILA_ALTERNA
            : ReporteEstilos.FONDO_CARD;

        return new Cell()
            .add(new Paragraph(texto)
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(8f)
                .setFontColor(colorTexto))
            .setBackgroundColor(colorFondo)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f));
    }

    // ── Mini chip para resumen ────────────────────────────────────────────

    private Cell miniChip(
            String texto, String label, DeviceRgb color) throws IOException {

        Cell celda = new Cell()
            .setBorder(Border.NO_BORDER)
            .setPadding(2f);

        celda.add(new Paragraph(texto)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(13f)
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

    // ── Helpers de color dominante ────────────────────────────────────────

    private DeviceRgb colorDominante(
            long optimos, long aceptables, long criticos, long total) {
        if (total == 0)             return ReporteEstilos.TEXTO_DESHABILITADO;
        if (criticos > total / 2)   return ReporteEstilos.ESTADO_CRITICO;
        if (optimos  > total / 2)   return ReporteEstilos.ESTADO_OPTIMO;
        return ReporteEstilos.ESTADO_ACEPTABLE;
    }

    private DeviceRgb fondoDominante(
            long optimos, long aceptables, long criticos, long total) {
        if (total == 0)             return ReporteEstilos.ESTADO_SIN_DATOS_BG;
        if (criticos > total / 2)   return ReporteEstilos.ESTADO_CRITICO_BG;
        if (optimos  > total / 2)   return ReporteEstilos.ESTADO_OPTIMO_BG;
        return ReporteEstilos.ESTADO_ACEPTABLE_BG;
    }

    private DeviceRgb bordeDominante(
            long optimos, long aceptables, long criticos, long total) {
        if (total == 0)             return new DeviceRgb(229, 231, 235);
        if (criticos > total / 2)   return new DeviceRgb(254, 202, 202);
        if (optimos  > total / 2)   return ReporteEstilos.VERDE_MEDIO;
        return new DeviceRgb(253, 230, 138);
    }
}