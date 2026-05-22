package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Recomendaciones.RecomendacionHato;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.DeviceRgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReporteSeccionPracticas {

    @Autowired
    private ReporteEstilos estilos;

    private static final DateTimeFormatter FMT_FECHA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void construir(
            Document                  doc,
            List<RecomendacionHato>   recomendaciones,
            List<HatoPractica>        practicas
    ) throws IOException {

        doc.add(estilos.encabezadoSeccion("🌱", "Prácticas y Recomendaciones"));

        // Nota introductoria
        doc.add(new Paragraph(
            "Recomendaciones generadas por el sistema de reglas de Hathor " +
            "basadas en los KPIs críticos del hato, y el estado de las " +
            "prácticas de mejora asignadas.")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(12f));

        // ── Resumen general ───────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Resumen general"));
        doc.add(construirResumenGeneral(recomendaciones, practicas));

        // ── Recomendaciones ───────────────────────────────────────────────
        if (recomendaciones != null && !recomendaciones.isEmpty()) {
            doc.add(estilos.subEncabezado("Recomendaciones activas"));
            doc.add(construirTablaRecomendaciones(recomendaciones));
        } else {
            doc.add(new Paragraph("No hay recomendaciones activas.")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                .setMarginBottom(8f));
        }

        // ── Prácticas en curso ────────────────────────────────────────────
        List<HatoPractica> enCurso = practicas != null
            ? practicas.stream()
                .filter(p -> "EN_CURSO".equals(p.getEstado()))
                .collect(Collectors.toList())
            : List.of();

        if (!enCurso.isEmpty()) {
            doc.add(estilos.subEncabezado("Prácticas en progreso"));
            doc.add(construirTablaPracticasEnCurso(enCurso));
        }

        // ── Prácticas completadas ─────────────────────────────────────────
        List<HatoPractica> completadas = practicas != null
            ? practicas.stream()
                .filter(p -> "COMPLETADA".equals(p.getEstado()))
                .collect(Collectors.toList())
            : List.of();

        if (!completadas.isEmpty()) {
            doc.add(estilos.subEncabezado("Prácticas completadas"));
            doc.add(construirTablaPracticasCompletadas(completadas));
        }

        // ── Prácticas pendientes ──────────────────────────────────────────
        List<HatoPractica> pendientes = practicas != null
            ? practicas.stream()
                .filter(p -> "PENDIENTE".equals(p.getEstado()))
                .collect(Collectors.toList())
            : List.of();

        if (!pendientes.isEmpty()) {
            doc.add(estilos.subEncabezado("Prácticas pendientes"));
            doc.add(construirTablaPracticasPendientes(pendientes));
        }

        doc.add(estilos.divisor());
    }

    // ── Resumen general ───────────────────────────────────────────────────

    private Table construirResumenGeneral(
        List<RecomendacionHato> recomendaciones,
        List<HatoPractica>      practicas) throws IOException {

        long recActivas = recomendaciones != null
            ? recomendaciones.stream()
                .filter(r -> "ACTIVA".equals(r.getTipoEstado())).count()
            : 0;

        long recAlta = recomendaciones != null
            ? recomendaciones.stream()
                .filter(r -> "ALTA".equals(r.getPrioridad())).count()
            : 0;

        long pracEnCurso = practicas != null
            ? practicas.stream()
                .filter(p -> "EN_CURSO".equals(p.getEstado())).count()
            : 0;

        long pracCompletadas = practicas != null
            ? practicas.stream()
                .filter(p -> "COMPLETADA".equals(p.getEstado())).count()
            : 0;

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            "⚠️", "Recomendaciones activas",
            String.valueOf(recActivas),
            recActivas > 0
                ? ReporteEstilos.ESTADO_ACEPTABLE : ReporteEstilos.ESTADO_OPTIMO,
            recActivas > 0
                ? ReporteEstilos.ESTADO_ACEPTABLE_BG : ReporteEstilos.ESTADO_OPTIMO_BG,
            recActivas > 0
                ? new DeviceRgb(253, 230, 138) : ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaMetrica(
            "🔴", "Prioridad alta",
            String.valueOf(recAlta),
            recAlta > 0
                ? ReporteEstilos.ESTADO_CRITICO : ReporteEstilos.ESTADO_OPTIMO,
            recAlta > 0
                ? ReporteEstilos.ESTADO_CRITICO_BG : ReporteEstilos.ESTADO_OPTIMO_BG,
            recAlta > 0
                ? new DeviceRgb(254, 202, 202) : ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaMetrica(
            "🔄", "Prácticas en curso",
            String.valueOf(pracEnCurso),
            ReporteEstilos.ESTADO_BUENO,
            ReporteEstilos.ESTADO_BUENO_BG,
            new DeviceRgb(191, 219, 254)
        ));

        tabla.addCell(celdaMetrica(
            "✅", "Prácticas completadas",
            String.valueOf(pracCompletadas),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        // Completar celdas vacías si el número de ítems no es múltiplo de 4
        int totalColumnas = 4;
        int celdasAgregadas = 4; 
        while (celdasAgregadas % totalColumnas != 0) {
            tabla.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(ReporteEstilos.FONDO_PAGINA));
            celdasAgregadas++;
        }

        return tabla;
    }

    // ── Tabla recomendaciones ─────────────────────────────────────────────

    private Table construirTablaRecomendaciones(
            List<RecomendacionHato> recomendaciones) throws IOException {

        // Ordenar: ALTA primero, luego MEDIA, luego BAJA
        List<RecomendacionHato> ordenadas = recomendaciones.stream()
            .sorted(Comparator.comparingInt(r -> prioridadOrden(r.getPrioridad())))
            .collect(Collectors.toList());

        float[] anchos = new float[]{30f, 15f, 15f, 15f, 25f};


        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Prioridad"));
        tabla.addHeaderCell(estilos.celdaEncabezado("Mensaje"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("KPI"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Estado"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Valor actual"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Referencia"));

        for (int i = 0; i < ordenadas.size(); i++) {
            RecomendacionHato r   = ordenadas.get(i);
            boolean           alt = i % 2 != 0;

            // Prioridad coloreada
            tabla.addCell(celdaPrioridad(r.getPrioridad(), alt));

            // Mensaje
            tabla.addCell(new Cell()
                .add(new Paragraph(
                    r.getMensaje() != null ? r.getMensaje() : "—")
                    .setFont(estilos.getFuenteNormal())
                    .setFontSize(8f)
                    .setFontColor(ReporteEstilos.TEXTO_PRINCIPAL))
                .setBackgroundColor(alt
                    ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD)
                .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
                .setPadding(5f)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // KPI indicador
            tabla.addCell(estilos.celdaDatoCentrado(
                r.getIndicador() != null ? r.getIndicador() : "—", alt));

            // Estado (tipoEstado)
            tabla.addCell(celdaTipoEstado(r.getTipoEstado(), alt));

            // Valor actual
            tabla.addCell(estilos.celdaDatoCentrado(
                r.getValorActual() != null
                    ? String.format("%.2f", r.getValorActual()) : "—", alt));

            // Valor referencia
            tabla.addCell(estilos.celdaDatoCentrado(
                r.getValorReferencia() != null
                    ? String.format("%.2f", r.getValorReferencia()) : "—", alt));
        }

        return tabla;
    }

    // ── Tabla prácticas en curso ──────────────────────────────────────────

    private Table construirTablaPracticasEnCurso(
            List<HatoPractica> practicas) throws IOException {

        float[] anchos = new float[]{30f, 15f, 15f, 15f, 25f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Práctica"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Avance"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Dificultad"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Inicio"));
        tabla.addHeaderCell(estilos.celdaEncabezado("KPI impactado"));

        for (int i = 0; i < practicas.size(); i++) {
            HatoPractica p   = practicas.get(i);
            boolean      alt = i % 2 != 0;

            String nombre = p.getPractica() != null
                ? p.getPractica().getNombre() : "—";
            tabla.addCell(estilos.celdaDatoNegrita(nombre, alt));

            // Avance con barra visual textual
            Float avance = p.getPorcentajeAvance();
            tabla.addCell(celdaAvance(avance, alt));

            // Dificultad
            String dificultad = p.getPractica() != null
                ? p.getPractica().getDificultad() : "—";
            tabla.addCell(celdaDificultad(dificultad, alt));

            // Fecha inicio
            tabla.addCell(estilos.celdaDatoCentrado(
                p.getFechaInicio() != null
                    ? p.getFechaInicio().format(FMT_FECHA) : "—", alt));

            // KPI impactado
            String kpi = p.getPractica() != null
                ? p.getPractica().getKpiImpactado() : "—";
            tabla.addCell(estilos.celdaDato(
                kpi != null ? kpi : "—", alt));
        }

        return tabla;
    }

    // ── Tabla prácticas completadas ───────────────────────────────────────

    private Table construirTablaPracticasCompletadas(
            List<HatoPractica> practicas) throws IOException {

        float[] anchos = new float[]{35f, 15f, 20f, 15f, 15f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Práctica"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Categoría"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("KPI impactado"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Inicio"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Fin"));

        for (int i = 0; i < practicas.size(); i++) {
            HatoPractica p   = practicas.get(i);
            boolean      alt = i % 2 != 0;

            String nombre = p.getPractica() != null
                ? p.getPractica().getNombre() : "—";
            tabla.addCell(estilos.celdaDatoNegrita(nombre, alt));

            String categoria = p.getPractica() != null
                ? p.getPractica().getCategoria() : "—";
            tabla.addCell(estilos.celdaDatoCentrado(
                categoria != null ? categoria : "—", alt));

            String kpi = p.getPractica() != null
                ? p.getPractica().getKpiImpactado() : "—";
            tabla.addCell(estilos.celdaDato(
                kpi != null ? kpi : "—", alt));

            tabla.addCell(estilos.celdaDatoCentrado(
                p.getFechaInicio() != null
                    ? p.getFechaInicio().format(FMT_FECHA) : "—", alt));

            tabla.addCell(estilos.celdaDatoCentrado(
                p.getFechaFin() != null
                    ? p.getFechaFin().format(FMT_FECHA) : "—", alt));
        }

        return tabla;
    }

    // ── Tabla prácticas pendientes ────────────────────────────────────────

    private Table construirTablaPracticasPendientes(
            List<HatoPractica> practicas) throws IOException {

        float[] anchos = new float[]{40f, 20f, 20f, 20f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Práctica"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Dificultad"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Duración est."));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("KPI impactado"));

        for (int i = 0; i < practicas.size(); i++) {
            HatoPractica p   = practicas.get(i);
            boolean      alt = i % 2 != 0;

            String nombre = p.getPractica() != null
                ? p.getPractica().getNombre() : "—";
            tabla.addCell(estilos.celdaDatoNegrita(nombre, alt));

            String dificultad = p.getPractica() != null
                ? p.getPractica().getDificultad() : "—";
            tabla.addCell(celdaDificultad(dificultad, alt));

            String duracion = p.getPractica() != null
                && p.getPractica().getDuracionDias() != null
                ? p.getPractica().getDuracionDias() + " días" : "—";
            tabla.addCell(estilos.celdaDatoCentrado(duracion, alt));

            String kpi = p.getPractica() != null
                ? p.getPractica().getKpiImpactado() : "—";
            tabla.addCell(estilos.celdaDato(
                kpi != null ? kpi : "—", alt));
        }

        return tabla;
    }

    // ── Helpers de celdas especiales ──────────────────────────────────────

    private Cell celdaPrioridad(String prioridad, boolean alterna) throws IOException {
        DeviceRgb color = switch (prioridad != null ? prioridad : "") {
            case "ALTA"  -> ReporteEstilos.ESTADO_CRITICO;
            case "MEDIA" -> ReporteEstilos.ESTADO_ACEPTABLE;
            case "BAJA"  -> ReporteEstilos.ESTADO_OPTIMO;
            default      -> ReporteEstilos.TEXTO_DESHABILITADO;
        };

        String label = switch (prioridad != null ? prioridad : "") {
            case "ALTA"  -> "🔴 Alta";
            case "MEDIA" -> "🟡 Media";
            case "BAJA"  -> "🟢 Baja";
            default      -> "—";
        };

        DeviceRgb fondo = alterna
            ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

        return new Cell()
            .add(new Paragraph(label)
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(8f)
                .setFontColor(color))
            .setBackgroundColor(fondo)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell celdaTipoEstado(String estado, boolean alterna) throws IOException {
        DeviceRgb color = switch (estado != null ? estado : "") {
            case "ACTIVA"     -> ReporteEstilos.ESTADO_ACEPTABLE;
            case "COMPLETADA" -> ReporteEstilos.ESTADO_OPTIMO;
            case "DESCARTADA" -> ReporteEstilos.TEXTO_DESHABILITADO;
            default           -> ReporteEstilos.TEXTO_DESHABILITADO;
        };

        DeviceRgb fondo = alterna
            ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

        return new Cell()
            .add(new Paragraph(estado != null ? estado : "—")
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(8f)
                .setFontColor(color))
            .setBackgroundColor(fondo)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell celdaAvance(Float avance, boolean alterna) throws IOException {
        String texto = avance != null
            ? String.format("%.0f%%", avance) : "—";

        DeviceRgb color = avance == null
            ? ReporteEstilos.TEXTO_DESHABILITADO
            : avance >= 75f ? ReporteEstilos.ESTADO_OPTIMO
            : avance >= 40f ? ReporteEstilos.ESTADO_ACEPTABLE
            : ReporteEstilos.ESTADO_CRITICO;

        DeviceRgb fondo = alterna
            ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

        return new Cell()
            .add(new Paragraph(texto)
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(9f)
                .setFontColor(color))
            .setBackgroundColor(fondo)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell celdaDificultad(String dificultad, boolean alterna) throws IOException {
        DeviceRgb color = switch (dificultad != null ? dificultad : "") {
            case "ALTA"  -> ReporteEstilos.ESTADO_CRITICO;
            case "MEDIA" -> ReporteEstilos.ESTADO_ACEPTABLE;
            case "BAJA"  -> ReporteEstilos.ESTADO_OPTIMO;
            default      -> ReporteEstilos.TEXTO_DESHABILITADO;
        };

        DeviceRgb fondo = alterna
            ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

        return new Cell()
            .add(new Paragraph(dificultad != null ? dificultad : "—")
                .setFont(estilos.getFuenteNegrita())
                .setFontSize(8f)
                .setFontColor(color))
            .setBackgroundColor(fondo)
            .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
            .setPadding(5f)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell celdaMetrica(
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

        celda.add(new Paragraph(icono)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(18f)
            .setMarginBottom(4f));

        celda.add(new Paragraph(label)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(8f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(4f));

        celda.add(new Paragraph(valor)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(14f)
            .setFontColor(colorValor)
            .setMarginBottom(0f));

        return celda;
    }

    private int prioridadOrden(String prioridad) {
        return switch (prioridad != null ? prioridad : "") {
            case "ALTA"  -> 0;
            case "MEDIA" -> 1;
            case "BAJA"  -> 2;
            default      -> 3;
        };
    }
}