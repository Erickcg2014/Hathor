package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.DeviceRgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ReporteSeccionRanking {

    @Autowired
    private ReporteEstilos estilos;

    public void construir(
            Document          doc,
            Hato              hato,
            RankingResumenDTO resumen,
            RankingCompuestoDTO compuesto
    ) throws IOException {

        doc.add(estilos.encabezadoSeccion("🏆", "Ranking en el Sistema"));

        // Nota introductoria
        doc.add(new Paragraph(
            "Posición del hato dentro del universo de hatos registrados en Hathor. " +
            "El score compuesto es el promedio normalizado de todos los KPIs del hato " +
            "en escala 0-100, donde 100 representa el mejor desempeño posible.")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(12f));

        if (resumen == null) {
            doc.add(new Paragraph("No hay datos de ranking disponibles.")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO));
            doc.add(estilos.divisor());
            return;
        }

        // ── Posición y score ──────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Posición competitiva"));
        doc.add(construirGridPosicion(resumen));

        // ── Estado de KPIs ────────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Distribución de KPIs"));
        doc.add(construirGridDistribucion(resumen));

        // ── Barra de score ────────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Score compuesto"));
        doc.add(construirBarraScore(resumen));

        // ── Top ranking compuesto ─────────────────────────────────────────
        if (compuesto != null && compuesto.getRanking() != null
        && !compuesto.getRanking().isEmpty()) {
            doc.add(estilos.subEncabezado("Ranking general — Top posiciones"));
            doc.add(construirTablaRanking(compuesto));
            if (compuesto.getRanking().size() > 15) {
                doc.add(new Paragraph(
                    "Se muestran las primeras 15 posiciones. " +
                    "Total en el sistema: " + compuesto.getRanking().size() + " hatos.")
                    .setFont(estilos.getFuenteItalica())
                    .setFontSize(8f)
                    .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                    .setMarginTop(4f));
            }
        }

        doc.add(estilos.divisor());
    }

    // ── Grid posición ─────────────────────────────────────────────────────

    private Table construirGridPosicion(RankingResumenDTO r) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaPosicion(
            medallaIcono(r.getPosicionNacional()),
            "Posición nacional",
            "#" + r.getPosicionNacional(),
            "de " + r.getTotalHatosNacional() + " hatos",
            colorScore(r.getScoreCompuesto()),
            fondoScore(r.getScoreCompuesto()),
            bordeScore(r.getScoreCompuesto())
        ));

        tabla.addCell(celdaPosicion(
            "📍",
            "Posición regional",
            "#" + r.getPosicionRegional(),
            "de " + r.getTotalHatosRegional() + " hatos",
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaPosicion(
            "📊",
            "Score compuesto",
            String.format("%.1f", r.getScoreCompuesto() != null
                ? r.getScoreCompuesto() : 0f),
            "/ 100 puntos",
            colorScore(r.getScoreCompuesto()),
            fondoScore(r.getScoreCompuesto()),
            bordeScore(r.getScoreCompuesto())
        ));

        // Percentil estimado
        float percentil = 0f;
        if (r.getPosicionNacional() != null && r.getTotalHatosNacional() > 1) {
            percentil = ((float)(r.getTotalHatosNacional() - r.getPosicionNacional())
                / (r.getTotalHatosNacional() - 1)) * 100f;
        }

        tabla.addCell(celdaPosicion(
            "📈",
            "Percentil nacional",
            String.format("P%.0f", percentil),
            percentil >= 50f ? "Por encima del promedio" : "Por debajo del promedio",
            colorScore(r.getScoreCompuesto()),
            fondoScore(r.getScoreCompuesto()),
            bordeScore(r.getScoreCompuesto())
        ));

        return tabla;
    }

    // ── Grid distribución KPIs ────────────────────────────────────────────

    private Table construirGridDistribucion(RankingResumenDTO r) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaDistribucion(
            "✅", "Óptimos",
            String.valueOf(r.getKpisOptimos()),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaDistribucion(
            "📈", "Buenos",
            String.valueOf(r.getKpisBuenos()),
            ReporteEstilos.ESTADO_BUENO,
            ReporteEstilos.ESTADO_BUENO_BG,
            new DeviceRgb(191, 219, 254)
        ));

        tabla.addCell(celdaDistribucion(
            "⚠️", "Aceptables",
            String.valueOf(r.getKpisAceptables()),
            ReporteEstilos.ESTADO_ACEPTABLE,
            ReporteEstilos.ESTADO_ACEPTABLE_BG,
            new DeviceRgb(253, 230, 138)
        ));

        tabla.addCell(celdaDistribucion(
            "🔴", "Críticos",
            String.valueOf(r.getKpisCriticos()),
            ReporteEstilos.ESTADO_CRITICO,
            ReporteEstilos.ESTADO_CRITICO_BG,
            new DeviceRgb(254, 202, 202)
        ));

        return tabla;
    }

    // ── Barra visual de score ─────────────────────────────────────────────

    private Table construirBarraScore(RankingResumenDTO r) throws IOException {
        float score = r.getScoreCompuesto() != null ? r.getScoreCompuesto() : 0f;
        float pct   = Math.min(Math.max(score, 0f), 100f);

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(14f);

        Cell celda = new Cell()
            .setBackgroundColor(ReporteEstilos.FONDO_CARD)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 1f))
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8f))
            .setPadding(16f);

        // Label score
        celda.add(new Paragraph(String.format("%.1f / 100", score))
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(24f)
            .setFontColor(colorScore(r.getScoreCompuesto()))
            .setMarginBottom(8f));

        // Barra contenedor (fondo gris)
        Table barraContenedor = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(6f);

        Cell barraFondo = new Cell()
            .setMinHeight(14f)
            .setBackgroundColor(new DeviceRgb(229, 231, 235))
            .setBorder(Border.NO_BORDER)
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(999f))
            .setPadding(0f);

        // Barra relleno — simulada con tabla anidada
        Table barraRelleno = new Table(
            UnitValue.createPercentArray(new float[]{pct, 100f - pct}))
            .useAllAvailableWidth();

        barraRelleno.addCell(new Cell()
            .setMinHeight(14f)
            .setBackgroundColor(colorScore(r.getScoreCompuesto()))
            .setBorder(Border.NO_BORDER)
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(999f)));

        if (pct < 100f) {
            barraRelleno.addCell(new Cell()
                .setMinHeight(14f)
                .setBackgroundColor(new DeviceRgb(229, 231, 235))
                .setBorder(Border.NO_BORDER));
        }

        barraFondo.add(barraRelleno);
        barraContenedor.addCell(barraFondo);
        celda.add(barraContenedor);

        // Etiquetas 0 / 50 / 100
        Table etiquetas = new Table(UnitValue.createPercentArray(
            new float[]{33.3f, 33.3f, 33.3f}))
            .useAllAvailableWidth();

        etiquetas.addCell(labelBarra("0", TextAlignment.LEFT));
        etiquetas.addCell(labelBarra("50 — Promedio sector", TextAlignment.CENTER));
        etiquetas.addCell(labelBarra("100", TextAlignment.RIGHT));

        celda.add(etiquetas);

        // Descripción textual
        celda.add(new Paragraph(descripcionScore(score))
            .setFont(estilos.getFuenteItalica())
            .setFontSize(8f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginTop(8f));

        tabla.addCell(celda);
        return tabla;
    }

    // ── Tabla ranking compuesto ───────────────────────────────────────────

    private Table construirTablaRanking(RankingCompuestoDTO compuesto) throws IOException {
        List<HatoRankingItem> ranking = compuesto.getRanking();

        // Mostrar máximo 15 posiciones
        int limite = Math.min(ranking.size(), 15);

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{10f, 50f, 25f, 15f}))
            .useAllAvailableWidth()
            .setMarginBottom(14f);

        // Encabezados
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Pos."));
        tabla.addHeaderCell(estilos.celdaEncabezado("Hato"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Score"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado(""));

        for (int i = 0; i < limite; i++) {
            HatoRankingItem item   = ranking.get(i);
            boolean         alterna = i % 2 != 0;
            boolean         esMio   = Boolean.TRUE.equals(item.getEsMiHato());

            DeviceRgb colorFondo = esMio
                ? ReporteEstilos.VERDE_SUAVE
                : (alterna ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD);

            DeviceRgb colorBorde = esMio
                ? ReporteEstilos.VERDE_ACENTO
                : ReporteEstilos.VERDE_BORDE;

            // Posición
            String posLabel = item.getPosicion() <= 3
                ? medallaIcono(item.getPosicion())
                : "#" + item.getPosicion();

            tabla.addCell(new Cell()
                .add(new Paragraph(posLabel)
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(item.getPosicion() <= 3 ? 14f : 10f)
                    .setFontColor(ReporteEstilos.TEXTO_PRINCIPAL))
                .setBackgroundColor(colorFondo)
                .setBorder(new SolidBorder(colorBorde, esMio ? 1.5f : 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Alias
            tabla.addCell(new Cell()
                .add(new Paragraph(item.getAlias())
                    .setFont(esMio ? estilos.getFuenteNegrita() : estilos.getFuenteNormal())
                    .setFontSize(9f)
                    .setFontColor(esMio
                        ? ReporteEstilos.VERDE_PRIMARIO
                        : ReporteEstilos.TEXTO_PRINCIPAL))
                .setBackgroundColor(colorFondo)
                .setBorder(new SolidBorder(colorBorde, esMio ? 1.5f : 0.5f))
                .setPadding(5f)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Score
            String scoreTexto = item.getValor() != null
                ? String.format("%.1f", item.getValor())
                : "—";

            tabla.addCell(new Cell()
                .add(new Paragraph(scoreTexto)
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(10f)
                    .setFontColor(esMio
                        ? ReporteEstilos.VERDE_PRIMARIO
                        : ReporteEstilos.TEXTO_PRINCIPAL))
                .setBackgroundColor(colorFondo)
                .setBorder(new SolidBorder(colorBorde, esMio ? 1.5f : 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Tag mi hato
            tabla.addCell(new Cell()
                .add(new Paragraph(esMio ? "← Tu hato" : "")
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(7f)
                    .setFontColor(ReporteEstilos.VERDE_PRIMARIO))
                .setBackgroundColor(colorFondo)
                .setBorder(new SolidBorder(colorBorde, esMio ? 1.5f : 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        // Nota si hay más hatos

        return tabla;
    }

    // ── Helpers privados ──────────────────────────────────────────────────

    private Cell celdaPosicion(
            String    icono,
            String    label,
            String    valorGrande,
            String    valorChico,
            DeviceRgb colorValor,
            DeviceRgb colorFondo,
            DeviceRgb colorBorde
    ) throws IOException {
        Cell celda = new Cell()
            .setBackgroundColor(colorFondo)
            .setBorder(new SolidBorder(colorBorde, 1f))
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(6f))
            .setPadding(12f)
            .setPaddingLeft(4f)
            .setPaddingRight(4f);

        celda.add(new Paragraph(icono)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(20f)
            .setMarginBottom(4f));

        celda.add(new Paragraph(label)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(8f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(4f));

        celda.add(new Paragraph(valorGrande)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(20f)
            .setFontColor(colorValor)
            .setMarginBottom(2f));

        celda.add(new Paragraph(valorChico)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(8f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO));

        return celda;
    }

    private Cell celdaDistribucion(
            String    icono,
            String    label,
            String    valor,
            DeviceRgb colorValor,
            DeviceRgb colorFondo,
            DeviceRgb colorBorde
    ) throws IOException {
        Cell celda = new Cell()
            .setBackgroundColor(colorFondo)
            .setBorder(new SolidBorder(colorBorde, 1f))
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(6f))
            .setPadding(12f)
            .setPaddingLeft(4f)
            .setPaddingRight(4f);

        celda.add(new Paragraph(icono + "  " + label)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(6f));

        celda.add(new Paragraph(valor)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(28f)
            .setFontColor(colorValor)
            .setMarginBottom(0f));

        return celda;
    }

    private Cell labelBarra(String texto, TextAlignment align) throws IOException {
        return new Cell()
            .add(new Paragraph(texto)
                .setFont(estilos.getFuenteNormal())
                .setFontSize(7f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                .setTextAlignment(align))
            .setBorder(Border.NO_BORDER)
            .setPadding(2f);
    }

    private String descripcionScore(float score) {
        if (score >= 75f) return "Excelente desempeño — tu hato está por encima del 75% del sistema.";
        if (score >= 50f) return "Buen desempeño — tu hato supera el promedio del Sistema";
        if (score >= 25f) return "Desempeño aceptable — hay oportunidades de mejora en varios indicadores.";
        return "Desempeño crítico — se recomienda revisar las prácticas y recomendaciones del sistema.";
    }

    private String medallaIcono(Integer pos) {
        if (pos == null) return "🏅";
        return switch (pos) {
            case 1  -> "🥇";
            case 2  -> "🥈";
            case 3  -> "🥉";
            default -> "🏅";
        };
    }

    private DeviceRgb colorScore(Float score) {
        if (score == null)  return ReporteEstilos.TEXTO_DESHABILITADO;
        if (score >= 75f)   return ReporteEstilos.ESTADO_OPTIMO;
        if (score >= 50f)   return ReporteEstilos.ESTADO_BUENO;
        if (score >= 25f)   return ReporteEstilos.ESTADO_ACEPTABLE;
        return ReporteEstilos.ESTADO_CRITICO;
    }

    private DeviceRgb fondoScore(Float score) {
        if (score == null)  return ReporteEstilos.ESTADO_SIN_DATOS_BG;
        if (score >= 75f)   return ReporteEstilos.ESTADO_OPTIMO_BG;
        if (score >= 50f)   return ReporteEstilos.ESTADO_BUENO_BG;
        if (score >= 25f)   return ReporteEstilos.ESTADO_ACEPTABLE_BG;
        return ReporteEstilos.ESTADO_CRITICO_BG;
    }

    private DeviceRgb bordeScore(Float score) {
        if (score == null)  return new DeviceRgb(229, 231, 235);
        if (score >= 75f)   return ReporteEstilos.VERDE_MEDIO;
        if (score >= 50f)   return new DeviceRgb(191, 219, 254);
        if (score >= 25f)   return new DeviceRgb(253, 230, 138);
        return new DeviceRgb(254, 202, 202);
    }
}