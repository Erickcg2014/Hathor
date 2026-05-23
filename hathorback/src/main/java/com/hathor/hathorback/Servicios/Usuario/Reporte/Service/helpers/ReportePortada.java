package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteConfigDTO;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

@Component
public class ReportePortada {

    @Autowired
    private ReporteEstilos estilos;

    private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "CO"));

    public void construir(Document doc, Hato hato, ReporteConfigDTO config) throws IOException {

        // ── Fondo superior verde oscuro ───────────────────────────────────
        Table fondoSuperior = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(0f);

        Cell celdaFondo = new Cell()
            .setBackgroundColor(ReporteEstilos.VERDE_OSCURO)
            .setPaddingTop(48f)
            .setPaddingBottom(48f)
            .setPaddingLeft(48f)
            .setPaddingRight(48f)
            .setBorder(Border.NO_BORDER);

        // Etiqueta superior
        celdaFondo.add(new Paragraph("REPORTE DE GESTIÓN GANADERA")
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(9f)
            .setFontColor(new DeviceRgb(134, 239, 172)) 
            .setCharacterSpacing(2f)
            .setMarginBottom(16f));

        // Nombre del hato
        String tituloHato = config.getTituloPersonalizado() != null
            ? config.getTituloPersonalizado()
            : hato.getNombreHato();

        celdaFondo.add(new Paragraph(tituloHato)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(32f)
            .setFontColor(ReporteEstilos.BLANCO)
            .setMarginBottom(8f));

        // Subtítulo verde acento
        celdaFondo.add(new Paragraph("Hathor · Sistema de Gestión Ganadera")
            .setFont(estilos.getFuenteNormal())
            .setFontSize(13f)
            .setFontColor(ReporteEstilos.VERDE_ACENTO)
            .setMarginBottom(32f));

        // Línea divisora 
        celdaFondo.add(new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .addCell(new Cell()
                .setHeight(1f)
                .setBackgroundColor(new DeviceRgb(80, 120, 80))
                .setBorder(Border.NO_BORDER))
            .setMarginBottom(24f));

        // Info del hato en dos columnas
        Table infoGrid = new Table(UnitValue.createPercentArray(new float[]{50f, 50f}))
            .useAllAvailableWidth();

        infoGrid.addCell(celdaInfoPortada("📍 Ubicación",
            hato.getCiudad() + ", " + hato.getDepartamento()));
        infoGrid.addCell(celdaInfoPortada("🌿 Trópico",
            hato.getTropico() != null ? hato.getTropico() : "—"));
        infoGrid.addCell(celdaInfoPortada("📐 Área total",
            hato.getAreaHato() + " ha"));
        infoGrid.addCell(celdaInfoPortada("📊 Escala",
            hato.getEscala() != null ? hato.getEscala() : "—"));
        infoGrid.addCell(celdaInfoPortada("🏔️ Altitud",
            hato.getAltitud() + " m.s.n.m"));
        infoGrid.addCell(celdaInfoPortada("🐄 Tipo de hato",
            hato.getTipoHato() != null ? hato.getTipoHato() : "—"));

        celdaFondo.add(infoGrid);
        fondoSuperior.addCell(celdaFondo);
        doc.add(fondoSuperior);

        Table franjaAccento = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(0f);

        franjaAccento.addCell(new Cell()
            .setHeight(6f)
            .setBackgroundColor(ReporteEstilos.VERDE_ACENTO)
            .setBorder(Border.NO_BORDER));

        doc.add(franjaAccento);

        Table seccionBlanca = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(0f);

        Cell celdaBlanca = new Cell()
            .setBackgroundColor(ReporteEstilos.FONDO_CARD)
            .setPaddingTop(32f)
            .setPaddingBottom(32f)
            .setPaddingLeft(48f)
            .setPaddingRight(48f)
            .setBorder(Border.NO_BORDER);

        // Período del reporte
        String periodoTexto = construirTextoPeriodo(config);
        celdaBlanca.add(new Paragraph("Período del reporte")
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setCharacterSpacing(1f)
            .setMarginBottom(4f));

        celdaBlanca.add(new Paragraph(periodoTexto)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(16f)
            .setFontColor(ReporteEstilos.VERDE_PRIMARIO)
            .setMarginBottom(24f));

        // Secciones incluidas
        celdaBlanca.add(new Paragraph("Contenido del reporte")
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setCharacterSpacing(1f)
            .setMarginBottom(8f));

        Table seccionesGrid = new Table(UnitValue.createPercentArray(
            new float[]{33.3f, 33.3f, 33.3f}))
            .useAllAvailableWidth()
            .setMarginBottom(24f);

        agregarChipSeccion(seccionesGrid, "📋 Resumen ejecutivo",  config.isIncluirResumenEjecutivo());
        agregarChipSeccion(seccionesGrid, "📈 KPIs",               config.isIncluirKpis());
        agregarChipSeccion(seccionesGrid, "🔍 Benchmarking",       config.isIncluirBenchmarking());
        agregarChipSeccion(seccionesGrid, "🏆 Ranking",            config.isIncluirRanking());
        agregarChipSeccion(seccionesGrid, "💰 Finanzas",           config.isIncluirFinanzas());
        agregarChipSeccion(seccionesGrid, "🥛 Producción",         config.isIncluirProduccion());
        agregarChipSeccion(seccionesGrid, "🌱 Prácticas",          config.isIncluirPracticas());

        celdaBlanca.add(seccionesGrid);

        // Pie de portada — fecha de generación
        celdaBlanca.add(estilos.divisor());

        celdaBlanca.add(new Paragraph(
            "Generado el " + LocalDate.now().format(FORMATO_FECHA) +
            " · Hathor — Sistema de Gestión Ganadera Colombia")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(8f)
            .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
            .setTextAlignment(TextAlignment.CENTER));

        seccionBlanca.addCell(celdaBlanca);
        doc.add(seccionBlanca);

        // ── Salto de página ───────────────────────────────────────────────
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
    }

    // ── Helpers privados ──────────────────────────────────────────────────

    private Cell celdaInfoPortada(String label, String valor) throws IOException {
        Cell celda = new Cell()
            .setBorder(Border.NO_BORDER)
            .setPaddingBottom(12f)
            .setPaddingRight(16f);

        celda.add(new Paragraph(label)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(9f)
            .setFontColor(new DeviceRgb(134, 239, 172)) // #86efac
            .setMarginBottom(2f));

        celda.add(new Paragraph(valor)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(11f)
            .setFontColor(ReporteEstilos.BLANCO)
            .setMarginBottom(0f));

        return celda;
    }

    private void agregarChipSeccion(
            Table tabla, String label, boolean incluida) throws IOException {

        DeviceRgb colorFondo = incluida
            ? ReporteEstilos.VERDE_SUAVE
            : ReporteEstilos.ESTADO_SIN_DATOS_BG;

        DeviceRgb colorTexto = incluida
            ? ReporteEstilos.VERDE_PRIMARIO
            : ReporteEstilos.TEXTO_DESHABILITADO;

        DeviceRgb colorBorde = incluida
            ? ReporteEstilos.VERDE_MEDIO
            : new DeviceRgb(229, 231, 235);

        tabla.addCell(new Cell()
            .add(new Paragraph((incluida ? "✓ " : "○ ") + label)
                .setFont(incluida
                    ? estilos.getFuenteNegrita()
                    : estilos.getFuenteNormal())
                .setFontSize(9f)
                .setFontColor(colorTexto))
            .setBackgroundColor(colorFondo)
            .setBorder(new SolidBorder(colorBorde, 1f))
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(4f))
            .setPadding(7f)
            .setPaddingLeft(4f)
            .setPaddingRight(4f));
    }

    private String construirTextoPeriodo(ReporteConfigDTO config) {
        if (config.getPeriodoDesde() == null && config.getPeriodoHasta() == null) {
            return "Histórico completo";
        }
        if (config.getPeriodoDesde() == null) {
            return "Hasta " + formatearFecha(config.getPeriodoHasta());
        }
        if (config.getPeriodoHasta() == null) {
            return "Desde " + formatearFecha(config.getPeriodoDesde());
        }
        return formatearFecha(config.getPeriodoDesde()) +
            " — " +
            formatearFecha(config.getPeriodoHasta());
    }

    private String formatearFecha(String fecha) {
        if (fecha == null) return "—";
        try {
            LocalDate d = LocalDate.parse(fecha);
            return d.format(FORMATO_FECHA);
        } catch (Exception e) {
            return fecha;
        }
    }
}