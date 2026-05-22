package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
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
public class ReporteSeccionFinanzas {

    @Autowired
    private ReporteEstilos estilos;

    private static final DateTimeFormatter FMT_MES =
        DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "CO"));

    private static final DateTimeFormatter FMT_FECHA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void construir(
            Document                 doc,
            List<RegistroFinanciero> registros,
            ReporteConfigDTO         config
    ) throws IOException {

        doc.add(estilos.encabezadoSeccion("💰", "Registros Financieros"));

        // Nota introductoria
        doc.add(new Paragraph(
            "Análisis financiero basado en los registros del hato. " +
            "Incluye ingresos, gastos, costos e inversiones agrupados " +
            "por categoría y período mensual.")
            .setFont(estilos.getFuenteItalica())
            .setFontSize(9f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setMarginBottom(12f));

        // Filtrar por período si aplica
        List<RegistroFinanciero> filtrados = filtrarPorPeriodo(registros, config);

        if (filtrados.isEmpty()) {
            doc.add(new Paragraph("No hay registros financieros en el período seleccionado.")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(9f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO));
            doc.add(estilos.divisor());
            return;
        }

        // ── Resumen general ───────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Resumen general"));
        doc.add(construirResumenGeneral(filtrados));

        // ── Resumen por tipo de movimiento ────────────────────────────────
        doc.add(estilos.subEncabezado("Distribución por tipo"));
        doc.add(construirResumenPorTipo(filtrados));

        // ── Evolución mensual ─────────────────────────────────────────────
        doc.add(estilos.subEncabezado("Evolución mensual"));
        doc.add(construirTablaMensual(filtrados));

        // ── Resumen por categoría ─────────────────────────────────────────
        doc.add(estilos.subEncabezado("Resumen por categoría"));
        doc.add(construirTablaPorCategoria(filtrados));

        // ── Detalle de registros ──────────────────────────────────────────
        doc.add(estilos.subEncabezado("Detalle de registros"));
        doc.add(construirTablaDetalle(filtrados));
        if (filtrados.size() > 50) {
            doc.add(new Paragraph(
                "Se muestran los 50 registros más recientes. " +
                "Total de registros: " + filtrados.size() + ".")
                .setFont(estilos.getFuenteItalica())
                .setFontSize(8f)
                .setFontColor(ReporteEstilos.TEXTO_DESHABILITADO)
                .setMarginTop(4f));
        }
        doc.add(construirTablaDetalle(filtrados));

        doc.add(estilos.divisor());
    }

    // ── Resumen general ───────────────────────────────────────────────────

    private Table construirResumenGeneral(
            List<RegistroFinanciero> registros) throws IOException {

        double ingresos  = sumarPorTipo(registros, "INGRESO");
        double gastos    = sumarPorTipo(registros, "GASTO");
        double costos    = sumarPorTipo(registros, "COSTO");
        double inversiones = sumarPorTipo(registros, "INVERSION");
        double egresos   = gastos + costos + inversiones;
        double balance   = ingresos - egresos;

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaResumen(
            "💚", "Total ingresos",
            estilos.formatearCOP(ingresos),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaResumen(
            "🔴", "Total egresos",
            estilos.formatearCOP(egresos),
            ReporteEstilos.ESTADO_CRITICO,
            ReporteEstilos.ESTADO_CRITICO_BG,
            new DeviceRgb(254, 202, 202)
        ));

        DeviceRgb colorBal  = balance >= 0
            ? ReporteEstilos.ESTADO_OPTIMO   : ReporteEstilos.ESTADO_CRITICO;
        DeviceRgb fondoBal  = balance >= 0
            ? ReporteEstilos.ESTADO_OPTIMO_BG : ReporteEstilos.ESTADO_CRITICO_BG;
        DeviceRgb bordeBal  = balance >= 0
            ? ReporteEstilos.VERDE_MEDIO     : new DeviceRgb(254, 202, 202);

        tabla.addCell(celdaResumen(
            balance >= 0 ? "📈" : "📉",
            "Balance neto",
            estilos.formatearCOP(balance),
            colorBal, fondoBal, bordeBal
        ));

        tabla.addCell(celdaResumen(
            "📋", "Total registros",
            String.valueOf(registros.size()),
            ReporteEstilos.TEXTO_SECUNDARIO,
            ReporteEstilos.ESTADO_SIN_DATOS_BG,
            new DeviceRgb(229, 231, 235)
        ));

        return tabla;
    }

    // ── Resumen por tipo ──────────────────────────────────────────────────

    private Table construirResumenPorTipo(
            List<RegistroFinanciero> registros) throws IOException {

        String[]  tipos   = {"INGRESO", "GASTO", "COSTO", "INVERSION"};
        String[]  labels  = {"Ingresos", "Gastos", "Costos", "Inversiones"};
        String[]  iconos  = {"💚", "🔴", "🟠", "🔵"};

        float[] anchos = new float[]{20f, 20f, 20f, 20f, 20f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        // Encabezados
        tabla.addHeaderCell(estilos.celdaEncabezado("Tipo"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Cantidad"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Total"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Promedio"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("% del total"));

        double totalGeneral = registros.stream()
            .mapToDouble(r -> r.getMonto()).sum();

        for (int i = 0; i < tipos.length; i++) {
            String tipo    = tipos[i];
            String label   = labels[i];
            String icono   = iconos[i];
            boolean alterna = i % 2 != 0;

            List<RegistroFinanciero> delTipo = registros.stream()
                .filter(r -> tipo.equals(r.getTipoMovimiento()))
                .collect(Collectors.toList());

            double total   = delTipo.stream().mapToDouble(r -> r.getMonto()).sum();
            double promedio = delTipo.isEmpty() ? 0 : total / delTipo.size();
            double pct     = totalGeneral > 0 ? (total / totalGeneral) * 100 : 0;

            tabla.addCell(estilos.celdaDatoNegrita(icono + " " + label, alterna));
            tabla.addCell(estilos.celdaDatoCentrado(String.valueOf(delTipo.size()), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(estilos.formatearCOP(total), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(estilos.formatearCOP(promedio), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                String.format("%.1f%%", pct), alterna));
        }

        return tabla;
    }

    // ── Evolución mensual ─────────────────────────────────────────────────

    private Table construirTablaMensual(
            List<RegistroFinanciero> registros) throws IOException {

        // Agrupar por mes
        Map<String, double[]> porMes = new LinkedHashMap<>();

        registros.stream()
            .sorted(Comparator.comparing(RegistroFinanciero::getFecha))
            .forEach(r -> {
                String mes = r.getFecha().format(FMT_MES);
                porMes.computeIfAbsent(mes, k -> new double[3]); // [ingresos, egresos, balance]

                if ("INGRESO".equals(r.getTipoMovimiento())) {
                    porMes.get(mes)[0] += r.getMonto();
                } else {
                    porMes.get(mes)[1] += r.getMonto();
                }
                porMes.get(mes)[2] = porMes.get(mes)[0] - porMes.get(mes)[1];
            });

        float[] anchos = new float[]{25f, 25f, 25f, 25f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        // Encabezados
        tabla.addHeaderCell(estilos.celdaEncabezado("Mes"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Ingresos"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Egresos"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Balance"));

        int i = 0;
        for (Map.Entry<String, double[]> entry : porMes.entrySet()) {
            boolean alterna  = i % 2 != 0;
            double  ingresos = entry.getValue()[0];
            double  egresos  = entry.getValue()[1];
            double  balance  = entry.getValue()[2];

            tabla.addCell(estilos.celdaDatoNegrita(entry.getKey(), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                estilos.formatearCOP(ingresos), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(
                estilos.formatearCOP(egresos), alterna));

            // Balance coloreado
            DeviceRgb colorBal = balance >= 0
                ? ReporteEstilos.ESTADO_OPTIMO : ReporteEstilos.ESTADO_CRITICO;
            DeviceRgb fondoBal = alterna
                ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

            tabla.addCell(new Cell()
                .add(new Paragraph(estilos.formatearCOP(balance))
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(9f)
                    .setFontColor(colorBal))
                .setBackgroundColor(fondoBal)
                .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            i++;
        }

        return tabla;
    }

    // ── Resumen por categoría ─────────────────────────────────────────────

    private Table construirTablaPorCategoria(
            List<RegistroFinanciero> registros) throws IOException {

        // Agrupar por categoría
        Map<String, double[]> porCategoria = new LinkedHashMap<>();

        registros.forEach(r -> {
            String cat = r.getCategoriaFinanciera() != null
                ? r.getCategoriaFinanciera().getNombre()
                : "Sin categoría";

            porCategoria.computeIfAbsent(cat, k -> new double[2]); // [total, cantidad]

            if ("INGRESO".equals(r.getTipoMovimiento())) {
                porCategoria.get(cat)[0] += r.getMonto();
            } else {
                porCategoria.get(cat)[0] -= r.getMonto();
            }
            porCategoria.get(cat)[1]++;
        });

        // Ordenar por total DESC
        List<Map.Entry<String, double[]>> ordenado = porCategoria.entrySet().stream()
            .sorted((a, b) -> Double.compare(
                Math.abs(b.getValue()[0]), Math.abs(a.getValue()[0])))
            .collect(Collectors.toList());

        float[] anchos = new float[]{50f, 25f, 25f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Categoría"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Registros"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Neto"));

        for (int i = 0; i < ordenado.size(); i++) {
            Map.Entry<String, double[]> entry = ordenado.get(i);
            boolean alterna = i % 2 != 0;
            double  neto    = entry.getValue()[0];
            int     cant    = (int) entry.getValue()[1];

            tabla.addCell(estilos.celdaDatoNegrita(entry.getKey(), alterna));
            tabla.addCell(estilos.celdaDatoCentrado(String.valueOf(cant), alterna));

            DeviceRgb colorNeto = neto >= 0
                ? ReporteEstilos.ESTADO_OPTIMO : ReporteEstilos.ESTADO_CRITICO;
            DeviceRgb fondoNeto = alterna
                ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

            tabla.addCell(new Cell()
                .add(new Paragraph(estilos.formatearCOP(neto))
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(9f)
                    .setFontColor(colorNeto))
                .setBackgroundColor(fondoNeto)
                .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        return tabla;
    }

    // ── Detalle de registros ──────────────────────────────────────────────

    private Table construirTablaDetalle(
            List<RegistroFinanciero> registros) throws IOException {

        // Ordenar por fecha DESC — máximo 50 registros
        List<RegistroFinanciero> ordenados = registros.stream()
            .sorted(Comparator.comparing(RegistroFinanciero::getFecha).reversed())
            .limit(50)
            .collect(Collectors.toList());

        float[] anchos = new float[]{15f, 30f, 18f, 18f, 19f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos))
            .useAllAvailableWidth()
            .setMarginBottom(8f);

        tabla.addHeaderCell(estilos.celdaEncabezado("Fecha"));
        tabla.addHeaderCell(estilos.celdaEncabezado("Título"));
        tabla.addHeaderCell(estilos.celdaEncabezado("Categoría"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Tipo"));
        tabla.addHeaderCell(estilos.celdaEncabezadoCentrado("Monto"));

        for (int i = 0; i < ordenados.size(); i++) {
            RegistroFinanciero r    = ordenados.get(i);
            boolean            alt  = i % 2 != 0;

            // Fecha
            String fechaTexto = r.getFecha() != null
                ? r.getFecha().format(FMT_FECHA) : "—";
            tabla.addCell(estilos.celdaDato(fechaTexto, alt));

            // Título
            tabla.addCell(estilos.celdaDatoNegrita(
                r.getTitulo() != null ? r.getTitulo() : "—", alt));

            // Categoría
            String cat = r.getCategoriaFinanciera() != null
                ? r.getCategoriaFinanciera().getNombre() : "—";
            tabla.addCell(estilos.celdaDato(cat, alt));

            // Tipo coloreado
            tabla.addCell(estilos.celdaEstado(
                mapearEstadoTipo(r.getTipoMovimiento()), alt));

            // Monto coloreado
            boolean esIngreso = "INGRESO".equals(r.getTipoMovimiento());
            DeviceRgb colorMonto = esIngreso
                ? ReporteEstilos.ESTADO_OPTIMO : ReporteEstilos.ESTADO_CRITICO;
            DeviceRgb fondoMonto = alt
                ? ReporteEstilos.FONDO_FILA_ALTERNA : ReporteEstilos.FONDO_CARD;

            String montoTexto = (esIngreso ? "+ " : "− ") +
                estilos.formatearCOP((double) r.getMonto());

            tabla.addCell(new Cell()
                .add(new Paragraph(montoTexto)
                    .setFont(estilos.getFuenteNegrita())
                    .setFontSize(9f)
                    .setFontColor(colorMonto))
                .setBackgroundColor(fondoMonto)
                .setBorder(new SolidBorder(ReporteEstilos.VERDE_BORDE, 0.5f))
                .setPadding(5f)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        return tabla;
    }

    // ── Helpers privados ──────────────────────────────────────────────────

    private List<RegistroFinanciero> filtrarPorPeriodo(
            List<RegistroFinanciero> registros,
            ReporteConfigDTO         config) {

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

    private double sumarPorTipo(
            List<RegistroFinanciero> registros, String tipo) {
        return registros.stream()
            .filter(r -> tipo.equals(r.getTipoMovimiento()))
            .mapToDouble(r -> r.getMonto())
            .sum();
    }

    /**
     * Mapea tipo de movimiento a estado para usar celdaEstado()
     * y aprovechar los colores ya definidos en ReporteEstilos.
     */
    private String mapearEstadoTipo(String tipo) {
        if (tipo == null) return "SIN_DATOS";
        return switch (tipo) {
            case "INGRESO"   -> "OPTIMO";
            case "GASTO"     -> "CRITICO";
            case "COSTO"     -> "ACEPTABLE";
            case "INVERSION" -> "BUENO";
            default          -> "SIN_DATOS";
        };
    }

    private Cell celdaResumen(
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