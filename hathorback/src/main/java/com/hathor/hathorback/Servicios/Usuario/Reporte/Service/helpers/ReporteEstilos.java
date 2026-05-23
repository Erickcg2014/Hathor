package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.borders.Border;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReporteEstilos {

    // ── Paleta de colores ─────────────────────────────────────────────────

    // Verdes principales
    public static final DeviceRgb VERDE_OSCURO      = new DeviceRgb(5,   46,  15);
    public static final DeviceRgb VERDE_PRIMARIO     = new DeviceRgb(21,  128, 61);
    public static final DeviceRgb VERDE_ACENTO       = new DeviceRgb(25,  230, 77);
    public static final DeviceRgb VERDE_SUAVE        = new DeviceRgb(240, 253, 244);
    public static final DeviceRgb VERDE_BORDE        = new DeviceRgb(233, 245, 235);
    public static final DeviceRgb VERDE_MEDIO        = new DeviceRgb(209, 250, 229);

    // Textos
    public static final DeviceRgb TEXTO_PRINCIPAL    = new DeviceRgb(17,  24,  39);
    public static final DeviceRgb TEXTO_SECUNDARIO   = new DeviceRgb(107, 114, 128);
    public static final DeviceRgb TEXTO_DESHABILITADO = new DeviceRgb(156, 163, 175);
    public static final DeviceRgb BLANCO             = new DeviceRgb(255, 255, 255);

    // Estados KPI
    public static final DeviceRgb ESTADO_OPTIMO      = new DeviceRgb(21,  128, 61);
    public static final DeviceRgb ESTADO_OPTIMO_BG   = new DeviceRgb(240, 253, 244);
    public static final DeviceRgb ESTADO_BUENO       = new DeviceRgb(59,  130, 246);
    public static final DeviceRgb ESTADO_BUENO_BG    = new DeviceRgb(239, 246, 255);
    public static final DeviceRgb ESTADO_ACEPTABLE   = new DeviceRgb(217, 119, 6);
    public static final DeviceRgb ESTADO_ACEPTABLE_BG = new DeviceRgb(255, 251, 235);
    public static final DeviceRgb ESTADO_CRITICO     = new DeviceRgb(239, 68,  68);
    public static final DeviceRgb ESTADO_CRITICO_BG  = new DeviceRgb(254, 242, 242);
    public static final DeviceRgb ESTADO_SIN_DATOS   = new DeviceRgb(156, 163, 175);
    public static final DeviceRgb ESTADO_SIN_DATOS_BG = new DeviceRgb(249, 250, 251);

    // Fondos
    public static final DeviceRgb FONDO_PAGINA       = new DeviceRgb(248, 253, 249);
    public static final DeviceRgb FONDO_CARD         = new DeviceRgb(255, 255, 255);
    public static final DeviceRgb FONDO_FILA_ALTERNA = new DeviceRgb(240, 253, 244);

    // ── Fuentes ───────────────────────────────────────────────────────────

    public PdfFont getFuenteNormal() throws IOException {
        return PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public PdfFont getFuenteNegrita() throws IOException {
        return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public PdfFont getFuenteItalica() throws IOException {
        return PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
    }

    // ── Helpers de párrafo ────────────────────────────────────────────────

    public Paragraph parrafoTitulo(String texto) throws IOException {
        return new Paragraph(texto)
            .setFont(getFuenteNegrita())
            .setFontSize(22f)
            .setFontColor(TEXTO_PRINCIPAL)
            .setMarginBottom(4f);
    }

    public Paragraph parrafoSubtitulo(String texto) throws IOException {
        return new Paragraph(texto)
            .setFont(getFuenteNormal())
            .setFontSize(12f)
            .setFontColor(TEXTO_SECUNDARIO)
            .setMarginBottom(2f);
    }

    public Paragraph parrafoSeccion(String texto) throws IOException {
        return new Paragraph(texto)
            .setFont(getFuenteNegrita())
            .setFontSize(13f)
            .setFontColor(BLANCO)
            .setMarginBottom(0f);
    }

    public Paragraph parrafoNormal(String texto) throws IOException {
        return new Paragraph(texto)
            .setFont(getFuenteNormal())
            .setFontSize(10f)
            .setFontColor(TEXTO_PRINCIPAL)
            .setMarginBottom(2f);
    }

    public Paragraph parrafoChico(String texto) throws IOException {
        return new Paragraph(texto)
            .setFont(getFuenteNormal())
            .setFontSize(8f)
            .setFontColor(TEXTO_SECUNDARIO)
            .setMarginBottom(1f);
    }

    public Paragraph parrafoDestacado(String texto) throws IOException {
        return new Paragraph(texto)
            .setFont(getFuenteNegrita())
            .setFontSize(10f)
            .setFontColor(TEXTO_PRINCIPAL)
            .setMarginBottom(2f);
    }

    // ── Helpers de encabezado de sección ─────────────────────────────────

    /**
     * Barra de encabezado de sección con fondo verde oscuro.
     */
    public Table encabezadoSeccion(String icono, String titulo) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginTop(16f)
            .setMarginBottom(8f);

        Cell celda = new Cell()
            .add(new Paragraph(icono + "  " + titulo)
                .setFont(getFuenteNegrita())
                .setFontSize(13f)
                .setFontColor(BLANCO)
                .setMarginBottom(0f))
            .setBackgroundColor(VERDE_OSCURO)
            .setPadding(10f)
            .setBorder(Border.NO_BORDER)
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(6f));

        tabla.addCell(celda);
        return tabla;
    }

    /**
     * Barra de subencabezado — categoría dentro de una sección.
     */
    public Table subEncabezado(String texto) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginTop(10f)
            .setMarginBottom(6f);

        Cell celda = new Cell()
            .add(new Paragraph(texto)
                .setFont(getFuenteNegrita())
                .setFontSize(11f)
                .setFontColor(VERDE_PRIMARIO)
                .setMarginBottom(0f))
            .setBackgroundColor(VERDE_MEDIO)
            .setPadding(7f)
            .setBorder(new SolidBorder(VERDE_BORDE, 1f))
            .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(4f));

        tabla.addCell(celda);
        return tabla;
    }

    // ── Helpers de tabla ──────────────────────────────────────────────────

    /**
     * Crea una celda de encabezado de tabla — fondo verde primario, texto blanco.
     */
    public Cell celdaEncabezado(String texto) throws IOException {
        return new Cell()
            .add(new Paragraph(texto)
                .setFont(getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(BLANCO))
            .setBackgroundColor(VERDE_PRIMARIO)
            .setPadding(6f)
            .setTextAlignment(TextAlignment.LEFT)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setBorder(Border.NO_BORDER);
    }

    public Cell celdaEncabezadoCentrado(String texto) throws IOException {
        return celdaEncabezado(texto)
            .setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Celda de datos normal — fondo blanco.
     */
    public Cell celdaDato(String texto, boolean filaAlterna) throws IOException {
        return new Cell()
            .add(new Paragraph(texto)
                .setFont(getFuenteNormal())
                .setFontSize(9f)
                .setFontColor(TEXTO_PRINCIPAL))
            .setBackgroundColor(filaAlterna ? FONDO_FILA_ALTERNA : FONDO_CARD)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.LEFT)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(VERDE_BORDE, 0.5f));
    }

    public Cell celdaDatoCentrado(String texto, boolean filaAlterna) throws IOException {
        return celdaDato(texto, filaAlterna)
            .setTextAlignment(TextAlignment.CENTER);
    }

    public Cell celdaDatoNegrita(String texto, boolean filaAlterna) throws IOException {
        return new Cell()
            .add(new Paragraph(texto)
                .setFont(getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(TEXTO_PRINCIPAL))
            .setBackgroundColor(filaAlterna ? FONDO_FILA_ALTERNA : FONDO_CARD)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.LEFT)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(VERDE_BORDE, 0.5f));
    }

    /**
     * Celda de estado — coloreada según OPTIMO/BUENO/ACEPTABLE/CRITICO.
     */
    public Cell celdaEstado(String estado, boolean filaAlterna) throws IOException {
        DeviceRgb colorTexto = colorTextoEstado(estado);
        DeviceRgb colorFondo = filaAlterna ? FONDO_FILA_ALTERNA : FONDO_CARD;

        String label = labelEstado(estado);

        return new Cell()
            .add(new Paragraph(label)
                .setFont(getFuenteNegrita())
                .setFontSize(8f)
                .setFontColor(colorTexto))
            .setBackgroundColor(colorFondo)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(VERDE_BORDE, 0.5f));
    }

    /**
     * Celda de percentil — coloreada según rango.
     */
    public Cell celdaPercentil(Float percentil, boolean filaAlterna) throws IOException {
        String texto = percentil != null
            ? String.format("P%.0f", percentil)
            : "—";

        DeviceRgb colorTexto;
        if (percentil == null)       colorTexto = TEXTO_DESHABILITADO;
        else if (percentil >= 70f)   colorTexto = ESTADO_OPTIMO;
        else if (percentil >= 40f)   colorTexto = ESTADO_ACEPTABLE;
        else                         colorTexto = ESTADO_CRITICO;

        DeviceRgb colorFondo = filaAlterna ? FONDO_FILA_ALTERNA : FONDO_CARD;

        return new Cell()
            .add(new Paragraph(texto)
                .setFont(getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(colorTexto))
            .setBackgroundColor(colorFondo)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(VERDE_BORDE, 0.5f));
    }

    // ── Helpers de formato ────────────────────────────────────────────────

    public String formatearValorKpi(Float valor, String unidad) {
        if (valor == null) return "—";
        if (unidad == null) return String.format("%.2f", valor);
        if (unidad.contains("COP")) {
            return "$ " + String.format("%,.0f", valor.doubleValue());
        }
        if (unidad.equals("%"))    return String.format("%.1f%%", valor);
        if (unidad.equals("ratio") || unidad.equals("veces"))
            return String.format("%.2fx", valor);
        if (valor == Math.floor(valor))
            return String.format("%.0f %s", valor, unidad);
        return String.format("%.1f %s", valor, unidad);
    }

    public String formatearCOP(Double valor) {
        if (valor == null) return "—";
        if (valor >= 1_000_000)
            return String.format("$ %.1fM", valor / 1_000_000);
        if (valor >= 1_000)
            return String.format("$ %.0fK", valor / 1_000);
        return "$ " + String.format("%,.0f", valor);
    }

    public String labelEstado(String estado) {
        if (estado == null) return "—";
        return switch (estado) {
            case "OPTIMO"    -> "Óptimo";
            case "BUENO"     -> "Bueno";
            case "ACEPTABLE" -> "Aceptable";
            case "CRITICO"   -> "Crítico";
            case "SIN_DATOS" -> "Sin datos";
            default          -> estado;
        };
    }

    public DeviceRgb colorTextoEstado(String estado) {
        if (estado == null) return TEXTO_DESHABILITADO;
        return switch (estado) {
            case "OPTIMO"    -> ESTADO_OPTIMO;
            case "BUENO"     -> ESTADO_BUENO;
            case "ACEPTABLE" -> ESTADO_ACEPTABLE;
            case "CRITICO"   -> ESTADO_CRITICO;
            default          -> TEXTO_DESHABILITADO;
        };
    }

    public DeviceRgb colorFondoEstado(String estado) {
        if (estado == null) return ESTADO_SIN_DATOS_BG;
        return switch (estado) {
            case "OPTIMO"    -> ESTADO_OPTIMO_BG;
            case "BUENO"     -> ESTADO_BUENO_BG;
            case "ACEPTABLE" -> ESTADO_ACEPTABLE_BG;
            case "CRITICO"   -> ESTADO_CRITICO_BG;
            default          -> ESTADO_SIN_DATOS_BG;
        };
    }

    public Table divisor() {
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginTop(8f)
            .setMarginBottom(8f);

        tabla.addCell(new Cell()
            .setHeight(1f)
            .setBackgroundColor(VERDE_BORDE)
            .setBorder(Border.NO_BORDER));

        return tabla;
    }
}