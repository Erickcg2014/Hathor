package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;
import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteConfigDTO;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.DeviceRgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class ReporteSeccionProduccion {

    @Autowired
    private ReporteEstilos estilos;

    private static final DateTimeFormatter FMT_MES =
        DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "CO"));

    private static final DateTimeFormatter FMT_FECHA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void construir(
            Document              doc,
            PerfilProductivo      perfil,
            List<ProduccionLeche> registros,
            ReporteConfigDTO      config
    ) throws IOException {

        doc.add(estilos.encabezadoSeccion("🥛", "Producción Lechera"));

        // Nota introductoria
        doc.add(new Paragraph(
            "Análisis de la producción lechera del hato basado en el perfil " +
            "productivo y los registros diarios de producción.")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(12f));

        // ── Perfil productivo ─────────────────────────────────────────────
        if (perfil != null) {
            doc.add(estilos.subEncabezado("Perfil productivo"));
            doc.add(construirGridPerfil(perfil));
            doc.add(construirTablaPerfil(perfil));
        } else {
            doc.add(new Paragraph("No hay perfil productivo registrado.")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                .setMarginBottom(8f));
        }

        // Filtrar registros por período
        List<ProduccionLeche> filtrados = filtrarPorPeriodo(registros, config);

        if (filtrados.isEmpty()) {
            doc.add(new Paragraph(
                "No hay registros de producción diaria en el período seleccionado.")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO));
            doc.add(estilos.divisor());
            return;
        }

        // ── Resumen estadístico ───────────────────────────────────────────
        doc.add(estilos.subEncabezado("Estadísticas del período"));
        doc.add(construirResumenEstadistico(filtrados));

        // ── Evolución mensual ─────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Evolución mensual"));
        doc.add(construirTablaMensual(filtrados));

        // ── Detalle diario ────────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Detalle de registros diarios"));
        doc.add(construirTablaDetalle(filtrados));

        if (filtrados.size() > 60) {
            doc.add(new Paragraph(
                "Se muestran los 60 registros más recientes. " +
                "Total de registros: " + filtrados.size() + ".")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(8f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                .setMarginTop(4f));
        }

        doc.add(estilos.divisor());
    }

    // ── Grid perfil ───────────────────────────────────────────────────────

    private Table construirGridPerfil(PerfilProductivo p) throws IOException {
        double ingresoMensual = p.getProduccionDiariaLitros()
            * 30 * p.getPrecioLitroPromedio();

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            "🥛", "Producción diaria",
            String.format("%.0f L/día", p.getProduccionDiariaLitros()),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "💲", "Precio por litro",
            estilos.formatearCOP(p.getPrecioLitroPromedio()),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "📅", "Ingreso mensual est.",
            estilos.formatearCOP(ingresoMensual),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaMetrica(
            "🐄", "Vacas en ordeño",
            p.getVacasEnOrdenio() != null
                ? p.getVacasEnOrdenio() + " vacas" : "—",
            ReporteEstilos.TEXTO_SECUNDARIO,
            ReporteEstilos.ESTADO_SIN_DATOS_BG,
            new DeviceRgb(229, 231, 235)
        ));

        return tabla;
    }

    // ── Tabla detalle perfil ──────────────────────────────────────────────

    private Table construirTablaPerfil(PerfilProductivo p) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{50f, 50f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        // Encabezados
        tabla.addHeaderCell(estilos.celdaEncabezado("Característica"));
        tabla.addHeaderCell(estilos.celdaEncabezado("Valor"));

        String[][] filas = {
            {"Raza predominante",       p.getRazaPredominante() != null
                ? p.getRazaPredominante() : "—"},
            {"Sistema de ordeño",       p.getSistemaOrdenio() != null
                ? p.getSistemaOrdenio() : "—"},
            {"Frecuencia de ordeño",    p.getFrecuenciaOrdenio() != null
                ? p.getFrecuenciaOrdenio() + " veces/día" : "—"},
            {"Destino de la leche",     p.getDestinoLeche() != null
                ? p.getDestinoLeche() : "—"},
            {"Período de lactancia",    p.getPeriodoLactanciaPromedio() != null
                ? p.getPeriodoLactanciaPromedio() + " días" : "—"},
            {"Fecha de registro",       p.getFechaRegistro() != null
                ? p.getFechaRegistro().format(FMT_FECHA) : "—"},
            {"Última actualización",    p.getFechaActualizacion() != null
                ? p.getFechaActualizacion().format(FMT_FECHA) : "—"},
        };

        for (int i = 0; i < filas.length; i++) {
            boolean alterna = i % 2 != 0;
            tabla.addCell(estilos.celdaDatoNegrita(filas[i][0], alterna));
            tabla.addCell(estilos.celdaDato(filas[i][1], alterna));
        }

        return tabla;
    }

    // ── Resumen estadístico ───────────────────────────────────────────────

    private Table construirResumenEstadistico(
            List<ProduccionLeche> registros) throws IOException {

        DoubleSummaryStatistics stats = registros.stream()
            .filter(r -> r.getLitrosProducidos() != null)
            .mapToDouble(r -> r.getLitrosProducidos())
            .summaryStatistics();

        double totalLitros  = stats.getSum();
        double promedioDia  = stats.getAverage();
        double maxDia       = stats.getMax();
        double minDia       = stats.getMin() == Double.MAX_VALUE ? 0 : stats.getMin();
        int    diasRegistro = (int) stats.getCount();

        // Promedio vacas en ordeño
        OptionalDouble promedioVacas = registros.stream()
            .filter(r -> r.getVacasOrdenadas() != null)
            .mapToInt(r -> r.getVacasOrdenadas())
            .average();

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{20f, 20f, 20f, 20f, 20f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            "📊", "Total litros",
            String.format("%.0f L", totalLitros),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "📈", "Promedio diario",
            String.format("%.1f L/día", promedioDia),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "🏆", "Mejor día",
            String.format("%.1f L", maxDia),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaMetrica(
            "📉", "Menor día",
            String.format("%.1f L", minDia),
            ReporteEstilos.ESTADO_ACEPTABLE,
            ReporteEstilos.ESTADO_ACEPTABLE_BG,
            new DeviceRgb(253, 230, 138)
        ));

        tabla.addCell(celdaMetrica(
            "📅", "Días registrados",
            diasRegistro + " días",
            ReporteEstilos.TEXTO_SECUNDARIO,
            ReporteEstilos.ESTADO_SIN_DATOS_BG,
            new DeviceRgb(229, 231, 235)
        ));

        // Segunda fila si hay datos de vacas
        if (promedioVacas.isPresent()) {
            Table tablaVacas = new Table(UnitValue.createPercentArray(
                new float[]{25f, 25f, 25f, 25f}))
                .useAllAvailableWidth()
                .setMarginBottom(12f);

            // Litros por vaca por día
            double litrosPorVaca = promedioVacas.getAsDouble() > 0
                ? promedioDia / promedioVacas.getAsDouble() : 0;

            tablaVacas.addCell(celdaMetrica(
                "🐄", "Prom. vacas ordeñadas",
                String.format("%.0f vacas", promedioVacas.getAsDouble()),
                ReporteEstilos.VERDE_PRIMARIO,
                ReporteEstilos.VERDE_SUAVE,
                ReporteEstilos.VERDE_BORDE
            ));

            tablaVacas.addCell(celdaMetrica(
                "🥛", "L/vaca/día estimado",
                String.format("%.1f L/vaca/día", litrosPorVaca),
                ReporteEstilos.VERDE_PRIMARIO,
                ReporteEstilos.VERDE_SUAVE,
                ReporteEstilos.VERDE_BORDE
            ));

            // Celdas vacías para completar grid
            tablaVacas.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(ReporteEstilos.FONDO_PAGINA));
            tablaVacas.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(ReporteEstilos.FONDO_PAGINA));

            return combinarTablas(tabla, tablaVacas);
        }

        return tabla;
    }

    // ── Evolución mensual ─────────────────────────────────────────────────

    private Table construirTablaMensual(
            List<ProduccionLeche> registros) throws IOException {

        // Agrupar por mes
        Map<String, DoubleSummaryStatistics> porMes = registros.stream()
            .filter(r -> r.getLitrosProducidos() != null && r.getFecha() != null)
            .sorted(Comparator.comparing(ProduccionLeche::getFecha))
            .collect(Collectors.groupingBy(
                r -> r.getFecha().format(FMT_MES),
                LinkedHashMap::new,
                Collectors.summarizingDouble(r -> r.getLitrosProducidos())
            ));

        float[] anchos = new float[]{25f, 20f, 20f, 20f, 15f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Mes"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Total litros"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Promedio/día"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Máximo día"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Días reg."));

        int i = 0;
        for (Map.Entry<String, DoubleSummaryStatistics> entry : porMes.entrySet()) {
            boolean            alterna = i % 2 != 0;
            DoubleSummaryStatistics s = entry.getValue();

            tabla.addCell(estilos.celdaDatoNegrita(entry.getKey(), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                String.format("%.0f L", s.getSum()), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                String.format("%.1f L", s.getAverage()), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                String.format("%.1f L", s.getMax()), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                String.valueOf(s.getCount()), alterna));
            i++;
        }

        return tabla;
    }

    // ── Detalle diario ────────────────────────────────────────────────────

    private Table construirTablaDetalle(
            List<ProduccionLeche> registros) throws IOException {

        List<ProduccionLeche> ordenados = registros.stream()
            .filter(r -> r.getFecha() != null)
            .sorted(Comparator.comparing(ProduccionLeche::getFecha).reversed())
            .limit(60)
            .collect(Collectors.toList());

        float[] anchos = new float[]{30f, 35f, 35f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(8f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Fecha"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Litros producidos"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Vacas ordeñadas"));

        // Calcular promedio para colorear
        double promedio = ordenados.stream()
            .filter(r -> r.getLitrosProducidos() != null)
            .mapToDouble(r -> r.getLitrosProducidos())
            .average()
            .orElse(0);

        for (int i = 0; i < ordenados.size(); i++) {
            ProduccionLeche r      = ordenados.get(i);
            boolean         alterna = i % 2 != 0;

            tabla.addCell(estilos.celdaDato(
                r.getFecha().format(FMT_FECHA), alterna));

            // Litros coloreados vs promedio
            Float litros = r.getLitrosProducidos();
            DeviceRgb colorLitros = litros == null
                ? ReporteEstilos.TEXTO_DESHABILITADO
                : litros >= promedio
                    ? ReporteEstilos.ESTADO_OPTIMO
                    : ReporteEstilos.ESTADO_ACEPTABLE;
            DeviceRgb fondoLitros = alterna
                ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

            tabla.addCell(new Cell()
                .add(new Paragraph(litros != null
                    ? String.format("%.1f L", litros) : "—")
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(9f)
                    .setFontColor(colorLitros))
                .setBackgroundColor(fondoLitros)
                .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            tabla.addCell(estilos.celdaDatoCentrado(
                r.getVacasOrdenadas() != null
                    ? r.getVacasOrdenadas() + " vacas" : "—",
                alterna));
        }

        return tabla;
    }

    // ── Helpers privados ──────────────────────────────────────────────────

    private List<ProduccionLeche> filtrarPorPeriodo(
            List<ProduccionLeche> registros,
            ReporteConfigDTO      config) {

        if (config.getPeriodoDesde() == null && config.getPeriodoHasta() == null) {
            return registros;
        }

        return registros.stream().filter(r -> {
            if (r.getFecha() == null) return false;
            if (config.getPeriodoDesde() != null) {
                LocalDate desde = LocalDate.parse(config.getPeriodoDesde());
                if (r.getFecha().isBefore(desde)) return false;
            }
            if (config.getPeriodoHasta() != null) {
                LocalDate hasta = LocalDate.parse(config.getPeriodoHasta());
                if (r.getFecha().isAfter(hasta)) return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Combina dos tablas en un contenedor vertical.
     * Usado para agregar la fila de vacas debajo del resumen estadístico.
     */
    private Table combinarTablas(Table t1, Table t2) throws IOException {
        Table contenedor = new Table(UnitValue.createPercentArray(new float[]{100f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        contenedor.addCell(new Cell()
            .add(t1)
            .add(t2)
            .setBorder(Border.NO_BORDER)
            .setPadding(0f));

        return contenedor;
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
}