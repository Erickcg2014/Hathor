package com.hathor.hathorback.Servicios.Usuario.Reporte.Service.helpers;

import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.RankingResumenDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.DeviceRgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class ReporteResumenEjecutivo {

    @Autowired
    private ReporteEstilos estilos;

    @Autowired
    private IRepositoryRegistroFinanciero repoFinanciero;

    @Autowired
    private IRepositoryPerfilProductivo repoPerfilProductivo;

    public void construir(
            Document         doc,
            Hato             hato,
            List<KpiResultadoDTO> kpis,
            RankingResumenDTO     ranking
    ) throws IOException {

        doc.add(estilos.encabezadoSeccion("📋", "Resumen Ejecutivo"));

        // ── Fila 1: Posición y score ──────────────────────────────────────
        if (ranking != null) {
            doc.add(estilos.subEncabezado("Posición competitiva"));
            doc.add(construirGridRanking(ranking));
        }

        // ── Fila 2: Estado de KPIs ────────────────────────────────────────
        if (kpis != null && !kpis.isEmpty()) {
            doc.add(estilos.subEncabezado("Estado de indicadores"));
            doc.add(construirGridKpis(kpis));
        }

        // ── Fila 3: Finanzas rápidas ──────────────────────────────────────
        List<RegistroFinanciero> registros = repoFinanciero
            .findByHato_IdHato(hato.getIdHato());

        if (!registros.isEmpty()) {
            doc.add(estilos.subEncabezado("Resumen financiero"));
            doc.add(construirGridFinanzas(registros));
        }

        // ── Fila 4: Producción rápida ─────────────────────────────────────
        repoPerfilProductivo.findByHato_IdHato(hato.getIdHato()).ifPresent(perfil -> {
            try {
                doc.add(estilos.subEncabezado("Perfil productivo"));
                doc.add(construirGridProduccion(perfil));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        doc.add(estilos.divisor());
    }

    // ── Grid para ranking ──────────────────────────────────────────────────────

    private Table construirGridRanking(RankingResumenDTO ranking) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            medallaIcono(ranking.getPosicionNacional()),
            "Posición nacional",
            "#" + ranking.getPosicionNacional() + " de " + ranking.getTotalHatosNacional(),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "📍",
            "Posición regional",
            "#" + ranking.getPosicionRegional() + " de " + ranking.getTotalHatosRegional(),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "📊",
            "Score compuesto",
            String.format("%.1f / 100",
                ranking.getScoreCompuesto() != null ? ranking.getScoreCompuesto() : 0f),
            colorScore(ranking.getScoreCompuesto()),
            fondoScore(ranking.getScoreCompuesto()),
            bordeScore(ranking.getScoreCompuesto())
        ));

        tabla.addCell(celdaMetrica(
            "🐄",
            "Hatos en el sistema",
            String.valueOf(ranking.getTotalHatosNacional()),
            ReporteEstilos.TEXTO_SECUNDARIO,
            ReporteEstilos.ESTADO_SIN_DATOS_BG,
            new DeviceRgb(229, 231, 235)
        ));

        return tabla;
    }

    // ── Grid KPIs ─────────────────────────────────────────────────────────

    private Table construirGridKpis(List<KpiResultadoDTO> kpis) throws IOException {
        long optimos    = kpis.stream().filter(k -> "OPTIMO".equals(k.getEstado())).count();
        long aceptables = kpis.stream().filter(k -> "ACEPTABLE".equals(k.getEstado())).count();
        long criticos   = kpis.stream().filter(k -> "CRITICO".equals(k.getEstado())).count();
        long sinDatos   = kpis.stream().filter(k -> "SIN_DATOS".equals(k.getEstado())).count();

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{25f, 25f, 25f, 25f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            "✅", "KPIs óptimos",
            String.valueOf(optimos),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaMetrica(
            "📈", "KPIs aceptables",
            String.valueOf(aceptables),
            ReporteEstilos.ESTADO_ACEPTABLE,
            ReporteEstilos.ESTADO_ACEPTABLE_BG,
            new DeviceRgb(253, 230, 138)
        ));

        tabla.addCell(celdaMetrica(
            "⚠️", "KPIs críticos",
            String.valueOf(criticos),
            ReporteEstilos.ESTADO_CRITICO,
            ReporteEstilos.ESTADO_CRITICO_BG,
            new DeviceRgb(254, 202, 202)
        ));

        tabla.addCell(celdaMetrica(
            "❓", "Sin datos",
            String.valueOf(sinDatos),
            ReporteEstilos.TEXTO_DESHABILITADO,
            ReporteEstilos.ESTADO_SIN_DATOS_BG,
            new DeviceRgb(229, 231, 235)
        ));

        return tabla;
    }

    // ── Grid finanzas ─────────────────────────────────────────────────────

    private Table construirGridFinanzas(
            List<RegistroFinanciero> registros) throws IOException {

        double totalIngresos = registros.stream()
            .filter(r -> "INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(r -> r.getMonto())
            .sum();

        double totalEgresos = registros.stream()
            .filter(r -> !"INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(r -> r.getMonto())
            .sum();

        double balance = totalIngresos - totalEgresos;

        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{33.3f, 33.3f, 33.3f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            "💚", "Total ingresos",
            estilos.formatearCOP(totalIngresos),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        tabla.addCell(celdaMetrica(
            "🔴", "Total egresos",
            estilos.formatearCOP(totalEgresos),
            ReporteEstilos.ESTADO_CRITICO,
            ReporteEstilos.ESTADO_CRITICO_BG,
            new DeviceRgb(254, 202, 202)
        ));

        DeviceRgb colorBalance = balance >= 0
            ? ReporteEstilos.ESTADO_OPTIMO
            : ReporteEstilos.ESTADO_CRITICO;
        DeviceRgb fondoBalance = balance >= 0
            ? ReporteEstilos.ESTADO_OPTIMO_BG
            : ReporteEstilos.ESTADO_CRITICO_BG;
        DeviceRgb bordeBalance = balance >= 0
            ? ReporteEstilos.VERDE_MEDIO
            : new DeviceRgb(254, 202, 202);

        tabla.addCell(celdaMetrica(
            balance >= 0 ? "📈" : "📉",
            "Balance neto",
            estilos.formatearCOP(balance),
            colorBalance,
            fondoBalance,
            bordeBalance
        ));

        return tabla;
    }

    // ── Grid producción ───────────────────────────────────────────────────

    private Table construirGridProduccion(PerfilProductivo perfil) throws IOException {
        Table tabla = new Table(UnitValue.createPercentArray(
            new float[]{33.3f, 33.3f, 33.3f}))
            .useAllAvailableWidth()
            .setMarginBottom(12f);

        tabla.addCell(celdaMetrica(
            "🥛", "Producción diaria",
            String.format("%.0f L/día", perfil.getProduccionDiariaLitros()),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        tabla.addCell(celdaMetrica(
            "💲", "Precio por litro",
            estilos.formatearCOP(perfil.getPrecioLitroPromedio()),
            ReporteEstilos.VERDE_PRIMARIO,
            ReporteEstilos.VERDE_SUAVE,
            ReporteEstilos.VERDE_BORDE
        ));

        double ingresoMensual = perfil.getProduccionDiariaLitros()
            * 30
            * perfil.getPrecioLitroPromedio();

        tabla.addCell(celdaMetrica(
            "📅", "Ingreso mensual est.",
            estilos.formatearCOP(ingresoMensual),
            ReporteEstilos.ESTADO_OPTIMO,
            ReporteEstilos.ESTADO_OPTIMO_BG,
            ReporteEstilos.VERDE_MEDIO
        ));

        return tabla;
    }

    // ── Helpers privados ──────────────────────────────────────────────────

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
            .setMargin(4f);

        celda.add(new Paragraph(icono)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(18f)
            .setMarginBottom(4f)
            .setTextAlignment(TextAlignment.LEFT));

        celda.add(new Paragraph(label)
            .setFont(estilos.getFuenteNormal())
            .setFontSize(8f)
            .setFontColor(ReporteEstilos.TEXTO_SECUNDARIO)
            .setCharacterSpacing(0.5f)
            .setMarginBottom(4f));

        celda.add(new Paragraph(valor)
            .setFont(estilos.getFuenteNegrita())
            .setFontSize(14f)
            .setFontColor(colorValor)
            .setMarginBottom(0f));

        return celda;
    }

    private String medallaIcono(Integer posicion) {
        if (posicion == null) return "🏅";
        return switch (posicion) {
            case 1  -> "🥇";
            case 2  -> "🥈";
            case 3  -> "🥉";
            default -> "🏅";
        };
    }

    private DeviceRgb colorScore(Float score) {
        if (score == null)    return ReporteEstilos.TEXTO_DESHABILITADO;
        if (score >= 75f)     return ReporteEstilos.ESTADO_OPTIMO;
        if (score >= 50f)     return ReporteEstilos.ESTADO_BUENO;
        if (score >= 25f)     return ReporteEstilos.ESTADO_ACEPTABLE;
        return ReporteEstilos.ESTADO_CRITICO;
    }

    private DeviceRgb fondoScore(Float score) {
        if (score == null)    return ReporteEstilos.ESTADO_SIN_DATOS_BG;
        if (score >= 75f)     return ReporteEstilos.ESTADO_OPTIMO_BG;
        if (score >= 50f)     return ReporteEstilos.ESTADO_BUENO_BG;
        if (score >= 25f)     return ReporteEstilos.ESTADO_ACEPTABLE_BG;
        return ReporteEstilos.ESTADO_CRITICO_BG;
    }

    private DeviceRgb bordeScore(Float score) {
        if (score == null)    return new DeviceRgb(229, 231, 235);
        if (score >= 75f)     return ReporteEstilos.VERDE_MEDIO;
        if (score >= 50f)     return new DeviceRgb(191, 219, 254);
        if (score >= 25f)     return new DeviceRgb(253, 230, 138);
        return new DeviceRgb(254, 202, 202);
    }
}