package com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KpiCalculadorService {

    @Autowired private KpiCalculadorHelper helper;

    public Map<String, Float> calcularTodos(Hato hato, DatosHato d) {
        Map<String, Float> v = new HashMap<>();

        PerfilProductivo pp = d.perfilProductivo;
        List<RegistroFinanciero> regs = d.registros;

        // ==== Totales financieros ====
        double totalIngresos = regs.stream()
            .filter(r -> "INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double totalGastos = regs.stream()
            .filter(r -> "GASTO".equals(r.getTipoMovimiento()) || "COSTO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double totalEgresos = regs.stream()
            .filter(r -> !"INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        // ==== Activos totales ====
        double valorInventarioGeneral = d.inventarioGeneral.stream()
            .mapToDouble(i -> (i.getCantidad() != null ? i.getCantidad() : 0)
                * (i.getValorUnitario() != null ? i.getValorUnitario() : 0)).sum();

        double valorInventarioGanado = d.inventarioGanado.stream()
            .mapToDouble(i -> (i.getCantidad() != null ? i.getCantidad() : 0)
                * (i.getValorUnitario() != null ? i.getValorUnitario() : 0)).sum();

        double activosTotales = valorInventarioGeneral + valorInventarioGanado;

        // ==== Total animales ====
        int totalAnimales = d.inventarioGanado.stream()
            .mapToInt(i -> i.getCantidad() != null ? i.getCantidad() : 0).sum();

        // ==== Total empleados ====
        int totalEmpleados = hato.getCantEmpleadosPermanentes() + hato.getCantEmpleadosTemporales();

        // ==== Producción ====
        double produccionAnualReal = d.produccionLeche.stream()
            .filter(p -> p.getFecha() != null
                && p.getFecha().isAfter(LocalDate.now().minusYears(1)))
            .mapToDouble(p -> p.getLitrosProducidos() != null ? p.getLitrosProducidos() : 0).sum();

        double totalLitrosProducidos = d.produccionLeche.stream()
            .mapToDouble(p -> p.getLitrosProducidos() != null ? p.getLitrosProducidos() : 0).sum();
        
        double produccionParaKpis;
        if (pp != null && pp.getProduccionDiariaLitros() > 0) {
            double estimadaAnual = pp.getProduccionDiariaLitros() * 365;
            if (produccionAnualReal >= estimadaAnual * 0.10) {
                produccionParaKpis = produccionAnualReal;
            } else {
                produccionParaKpis = estimadaAnual;
            }
        } else {
            produccionParaKpis = produccionAnualReal > 0 ? produccionAnualReal : totalLitrosProducidos;
        }
        // ==== Ventas ====
        double totalLitrosVendidos = d.ventasLeche.stream()
            .mapToDouble(vl -> vl.getLitrosVendidos()).sum();

        double totalIngresoVentas = d.ventasLeche.stream()
            .mapToDouble(vl -> vl.getLitrosVendidos() * vl.getPrecioLitro()).sum();

        // ==== Área ====
        double areaPastoreo = hato.getAreaPastoreo() > 0 ? hato.getAreaPastoreo() : 1;
        double areaHato     = hato.getAreaHato()     > 0 ? hato.getAreaHato()     : 1;

        // === INICIO CALCULO KPIs
        
        // ==== KPI_INGRESO_LITRO — ventaleche primero ====

        if (totalLitrosVendidos > 0) {
            v.put("KPI_INGRESO_LITRO", (float)(totalIngresoVentas / totalLitrosVendidos));
        } else if (pp != null && pp.getProduccionDiariaLitros() > 0) {
            double ingresoLecheRegistros = regs.stream()
                .filter(r -> "INGRESO".equals(r.getTipoMovimiento())
                    && r.getCategoriaFinanciera() != null
                    && helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                        "VENTA DE LECHE", "LECHE", "LÁCTEOS", "LACTEOS"))
                .mapToDouble(RegistroFinanciero::getMonto).sum();

            long mesesConRegistros = regs.stream()
                .filter(r -> "INGRESO".equals(r.getTipoMovimiento())
                    && r.getCategoriaFinanciera() != null
                    && helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                        "VENTA DE LECHE", "LECHE", "LÁCTEOS", "LACTEOS"))
                .map(r -> r.getFecha().getYear() + "-" + r.getFecha().getMonthValue())
                .distinct().count();

            if (mesesConRegistros == 0) {
                mesesConRegistros = regs.stream()
                    .filter(r -> "INGRESO".equals(r.getTipoMovimiento()))
                    .map(r -> r.getFecha().getYear() + "-" + r.getFecha().getMonthValue())
                    .distinct().count();
            }

            double litrosEstimados = pp.getProduccionDiariaLitros() * 30 * mesesConRegistros;
            if (ingresoLecheRegistros > 0 && litrosEstimados > 0)
                v.put("KPI_INGRESO_LITRO", (float)(ingresoLecheRegistros / litrosEstimados));
        }

        // ==== KPI_IOFC ====

        double totalIngresoVentaLeche = d.ventasLeche.stream()
            .mapToDouble(vl -> vl.getLitrosVendidos() * vl.getPrecioLitro()).sum();

        double totalIngresoLecheRegistros = regs.stream()
            .filter(r -> "INGRESO".equals(r.getTipoMovimiento())
                && r.getCategoriaFinanciera() != null
                && (r.getCategoriaFinanciera().getNombre().toLowerCase().contains("leche")
                    || r.getCategoriaFinanciera().getNombre().toLowerCase().contains("lácteo")
                    || r.getCategoriaFinanciera().getNombre().toLowerCase().contains("lacteo")))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double totalIngresoLeche = Math.max(totalIngresoVentaLeche, totalIngresoLecheRegistros);

        double gastoAlimentacionRegistros = regs.stream()
            .filter(r -> helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                "ALIMENTACIÓN", "CONCENTRADO", "SAL MINERALIZADA",
                "SUPLEMENTOS", "ENSILAJE", "PASTO CULTIVADO"))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double gastoAlimentacion = gastoAlimentacionRegistros > 0
            ? gastoAlimentacionRegistros
            : (hato.getGastoMensualAlimentacion() != null
                && hato.getGastoMensualAlimentacion() > 0
                ? hato.getGastoMensualAlimentacion() : 0);

        if (totalIngresoLeche > 0 || gastoAlimentacion > 0)
            v.put("KPI_IOFC", (float)(totalIngresoLeche - gastoAlimentacion));

        // ==== KPI_COSTO_LABORAL_PCT ====

        double gastoNomina = hato.getGastoMensualNomina() != null ? hato.getGastoMensualNomina() : 0;
        if (gastoNomina == 0) {
            gastoNomina = regs.stream()
                .filter(r -> helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                    "MANO DE OBRA", "SALARIOS", "PRESTACIONES", "HONORARIOS"))
                .mapToDouble(RegistroFinanciero::getMonto).sum();
        }
        if (totalIngresos > 0 && gastoNomina > 0)
            v.put("KPI_COSTO_LABORAL_PCT", (float)((gastoNomina / totalIngresos) * 100));

        // ==== KPI_BREAKEVEN_LITRO ====

        if (produccionParaKpis > 0 && totalEgresos > 0)
            v.put("KPI_BREAKEVEN_LITRO", (float)(totalEgresos / produccionParaKpis));

        // ==== KPIs DE PRODUCTIVIDAD ====

        if (pp != null) {
            double produccionDiaria = pp.getProduccionDiariaLitros();
            Integer vacasEnOrdenio  = pp.getVacasEnOrdenio();

            if (vacasEnOrdenio != null && vacasEnOrdenio > 0)
                v.put("KPI_LITROS_VACA_DIA", (float)(produccionDiaria / vacasEnOrdenio));

            double litrosAnioCalc = produccionParaKpis;
            v.put("KPI_LITROS_HA_ANIO",    (float)(litrosAnioCalc  / areaPastoreo));
            v.put("KPI_PRODUCCION_HA_DIA", (float)(produccionDiaria / areaPastoreo));

            if (hato.getCapacidadAlmacenarLeche() > 0)
                v.put("KPI_CAP_ALMAC_UTILIZADA",
                    (float)((produccionDiaria / hato.getCapacidadAlmacenarLeche()) * 100));

            if (pp.getPeriodoLactanciaPromedio() != null)
                v.put("KPI_LACTANCIA_VS_ESTANDAR",
                    (float)(pp.getPeriodoLactanciaPromedio() - 305));

            if (pp.getFrecuenciaOrdenio() != null)
                v.put("KPI_FRECUENCIA_ORDENIO", pp.getFrecuenciaOrdenio().floatValue());

            if (totalEmpleados > 0)
                v.put("KPI_LITROS_EMPLEADO", (float)(produccionDiaria / totalEmpleados));
        }

        // ==== KPIs DE MANEJO DE HATO ====

        if (totalAnimales > 0)
            v.put("KPI_CARGA_ANIMAL", (float)(totalAnimales / areaPastoreo));

        if (pp != null && pp.getVacasEnOrdenio() != null && totalAnimales > 0)
            v.put("KPI_PCT_VACAS_ORDENIO",
                (float)((pp.getVacasEnOrdenio() / (double) totalAnimales) * 100));

        if (pp != null && pp.getVacasEnOrdenio() != null && pp.getVacasEnOrdenio() > 0) {
            int hembrasRecria = d.inventarioGanado.stream()
                .filter(ig -> {
                    String cat = ig.getCategoriaGanado() != null
                        ? ig.getCategoriaGanado().getNombreCategoria().toLowerCase() : "";
                    return cat.contains("novilla") || cat.contains("ternera") || cat.contains("levante");
                })
                .mapToInt(ig -> ig.getCantidad() != null ? ig.getCantidad() : 0).sum();
            v.put("KPI_HEMBRAS_RECRIA_VACA",
                (float)(hembrasRecria / (double) pp.getVacasEnOrdenio()));
        }

        // ==== KPIs FINANCIEROS ====

        if (!regs.isEmpty()) {
            if (totalIngresos > 0)
                v.put("KPI_MARGEN_NETO",
                    (float)(((totalIngresos - totalEgresos) / totalIngresos) * 100));

            if (totalIngresos > 0)
                v.put("KPI_MARGEN_BRUTO_PCT",
                    (float)(((totalIngresos - totalGastos) / totalIngresos) * 100));

            if (totalEgresos > 0)
                v.put("KPI_RATIO_INGRESO_EGRESO", (float)(totalIngresos / totalEgresos));

            v.put("KPI_BALANCE_NETO", (float)(totalIngresos - totalEgresos));

            if (pp != null && pp.getVacasEnOrdenio() != null && pp.getVacasEnOrdenio() > 0)
                v.put("KPI_INGRESO_VACA", (float)(totalIngresos / pp.getVacasEnOrdenio()));

            if (activosTotales > 0) {
                v.put("KPI_ROA",
                    (float)(((totalIngresos - totalEgresos) / activosTotales) * 100));
                v.put("KPI_ROTACION_ACTIVOS", (float)(totalIngresos / activosTotales));
            }

            v.put("KPI_INGRESO_HA_ANIO", (float)(totalIngresos / areaHato));
        }

        if (totalLitrosVendidos > 0)
            v.put("KPI_INGRESO_LITRO", (float)(totalIngresoVentas / totalLitrosVendidos));

        if (produccionParaKpis > 0 && totalGastos > 0)
            v.put("KPI_COSTO_LITRO", (float)(totalGastos / produccionParaKpis));

        // ==== KPIs DE EFICIENCIA ====
        
        if (totalEmpleados > 0)
            v.put("KPI_EMPLEADOS_HA", (float)(totalEmpleados / areaPastoreo));

        return v;
    }
}