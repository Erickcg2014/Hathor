package com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.DetalleCalculoItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KpiDetalleService {

    @Autowired private KpiCalculadorHelper helper;

    public Map<String, List<DetalleCalculoItem>> calcularDetalles(Hato hato, DatosHato d) {
        Map<String, List<DetalleCalculoItem>> detalles = new HashMap<>();

        PerfilProductivo pp = d.perfilProductivo;
        List<RegistroFinanciero> regs = d.registros;

        // ---- Variables base ----
        double totalIngresos = regs.stream()
            .filter(r -> "INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double totalGastos = regs.stream()
            .filter(r -> "GASTO".equals(r.getTipoMovimiento()) || "COSTO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double totalEgresos = regs.stream()
            .filter(r -> !"INGRESO".equals(r.getTipoMovimiento()))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double valorInventarioGeneral = d.inventarioGeneral.stream()
            .mapToDouble(i -> (i.getCantidad() != null ? i.getCantidad() : 0)
                * (i.getValorUnitario() != null ? i.getValorUnitario() : 0)).sum();

        double valorInventarioGanado = d.inventarioGanado.stream()
            .mapToDouble(i -> (i.getCantidad() != null ? i.getCantidad() : 0)
                * (i.getValorUnitario() != null ? i.getValorUnitario() : 0)).sum();

        double activosTotales = valorInventarioGeneral + valorInventarioGanado;

        int totalAnimales = d.inventarioGanado.stream()
            .mapToInt(i -> i.getCantidad() != null ? i.getCantidad() : 0).sum();

        int totalEmpleados = hato.getCantEmpleadosPermanentes() + hato.getCantEmpleadosTemporales();

        double totalLitrosProducidos = d.produccionLeche.stream()
            .mapToDouble(p -> p.getLitrosProducidos() != null ? p.getLitrosProducidos() : 0).sum();

        double produccionAnualReal = d.produccionLeche.stream()
            .filter(p -> p.getFecha() != null && p.getFecha().isAfter(LocalDate.now().minusYears(1)))
            .mapToDouble(p -> p.getLitrosProducidos() != null ? p.getLitrosProducidos() : 0).sum();

        double totalLitrosVendidos = d.ventasLeche.stream()
            .mapToDouble(vl -> vl.getLitrosVendidos()).sum();

        double totalIngresoVentas = d.ventasLeche.stream()
            .mapToDouble(vl -> vl.getLitrosVendidos() * vl.getPrecioLitro()).sum();

        double areaPastoreo = hato.getAreaPastoreo() > 0 ? hato.getAreaPastoreo() : 1;
        double areaHato     = hato.getAreaHato()     > 0 ? hato.getAreaHato()     : 1;

        // ==== OBTENER DETALLES DE UN KPI POR CATEGORÍAS 
        // ===== PRODUCTIVIDAD ==========
        
        if (pp != null) {
            double produccionDiaria = pp.getProduccionDiariaLitros();
            Integer vacasEnOrdenio  = pp.getVacasEnOrdenio();

            if (vacasEnOrdenio != null && vacasEnOrdenio > 0) {
                detalles.put("KPI_LITROS_VACA_DIA", List.of(
                    helper.item("Producción diaria total", produccionDiaria, "L", "INPUT"),
                    helper.item("Vacas en ordeño", (double) vacasEnOrdenio, "vacas", "INPUT"),
                    helper.item("Litros/Vaca/Día", produccionDiaria / vacasEnOrdenio, "L/vaca/día", "RESULTADO")
                ));
            }

            double litrosAnio = produccionAnualReal > 0 ? produccionAnualReal : produccionDiaria * 365;
            detalles.put("KPI_LITROS_HA_ANIO", List.of(
                helper.item("Producción anual", litrosAnio, "L", "INPUT"),
                helper.item("Área de pastoreo", areaPastoreo, "ha", "INPUT"),
                helper.item(produccionAnualReal > 0
                    ? "Fuente: registros reales"
                    : "Fuente: estimado (diaria × 365)", "INTERMEDIO"),
                helper.item("Litros/Ha/Año", litrosAnio / areaPastoreo, "L/ha/año", "RESULTADO")
            ));

            detalles.put("KPI_PRODUCCION_HA_DIA", List.of(
                helper.item("Producción diaria", produccionDiaria, "L", "INPUT"),
                helper.item("Área de pastoreo", areaPastoreo, "ha", "INPUT"),
                helper.item("Producción/Ha/Día", produccionDiaria / areaPastoreo, "L/ha/día", "RESULTADO")
            ));

            if (hato.getCapacidadAlmacenarLeche() > 0) {
                detalles.put("KPI_CAP_ALMAC_UTILIZADA", List.of(
                    helper.item("Producción diaria", produccionDiaria, "L", "INPUT"),
                    helper.item("Capacidad almacenamiento", hato.getCapacidadAlmacenarLeche(), "L", "INPUT"),
                    helper.item("Capacidad utilizada",
                        (produccionDiaria / hato.getCapacidadAlmacenarLeche()) * 100, "%", "RESULTADO")
                ));
            }

            if (pp.getPeriodoLactanciaPromedio() != null) {
                detalles.put("KPI_LACTANCIA_VS_ESTANDAR", List.of(
                    helper.item("Período de lactancia del hato",
                        (double) pp.getPeriodoLactanciaPromedio(), "días", "INPUT"),
                    helper.item("Estándar mundial", 305.0, "días", "INPUT"),
                    helper.item("Diferencia vs estándar",
                        (double)(pp.getPeriodoLactanciaPromedio() - 305), "días", "RESULTADO")
                ));
            }

            if (pp.getFrecuenciaOrdenio() != null) {
                detalles.put("KPI_FRECUENCIA_ORDENIO", List.of(
                    helper.item("Frecuencia de ordeño",
                        pp.getFrecuenciaOrdenio().doubleValue(), "ordeños/día", "RESULTADO")
                ));
            }

            if (totalEmpleados > 0) {
                detalles.put("KPI_LITROS_EMPLEADO", List.of(
                    helper.item("Producción diaria", produccionDiaria, "L", "INPUT"),
                    helper.item("Total empleados", (double) totalEmpleados, "empleados", "INPUT"),
                    helper.item("Permanentes",
                        (double) hato.getCantEmpleadosPermanentes(), "empleados", "INTERMEDIO"),
                    helper.item("Temporales",
                        (double) hato.getCantEmpleadosTemporales(), "empleados", "INTERMEDIO"),
                    helper.item("Litros/Empleado/Día",
                        produccionDiaria / totalEmpleados, "L/emp/día", "RESULTADO")
                ));
            }
        }

        // =========== MANEJO DE HATO =============
        if (totalAnimales > 0) {
            detalles.put("KPI_CARGA_ANIMAL", List.of(
                helper.item("Total animales en hato", (double) totalAnimales, "animales", "INPUT"),
                helper.item("Área de pastoreo", areaPastoreo, "ha", "INPUT"),
                helper.item("Carga Animal", totalAnimales / areaPastoreo, "animales/ha", "RESULTADO")
            ));
        }

        if (pp != null && pp.getVacasEnOrdenio() != null && totalAnimales > 0) {
            detalles.put("KPI_PCT_VACAS_ORDENIO", List.of(
                helper.item("Vacas en ordeño", (double) pp.getVacasEnOrdenio(), "vacas", "INPUT"),
                helper.item("Total animales", (double) totalAnimales, "animales", "INPUT"),
                helper.item("% Vacas en ordeño",
                    (pp.getVacasEnOrdenio() / (double) totalAnimales) * 100, "%", "RESULTADO")
            ));
        }

        if (pp != null && pp.getVacasEnOrdenio() != null && pp.getVacasEnOrdenio() > 0) {
            int hembrasRecria = d.inventarioGanado.stream()
                .filter(ig -> {
                    String cat = ig.getCategoriaGanado() != null
                        ? ig.getCategoriaGanado().getNombreCategoria().toLowerCase() : "";
                    return cat.contains("novilla") || cat.contains("ternera") || cat.contains("levante");
                })
                .mapToInt(ig -> ig.getCantidad() != null ? ig.getCantidad() : 0).sum();

            detalles.put("KPI_HEMBRAS_RECRIA_VACA", List.of(
                helper.item("Hembras de recría (novillas + terneras)",
                    (double) hembrasRecria, "animales", "INPUT"),
                helper.item("Vacas en ordeño", (double) pp.getVacasEnOrdenio(), "vacas", "INPUT"),
                helper.item("Índice Hembras Recría/Vaca",
                    hembrasRecria / (double) pp.getVacasEnOrdenio(), "ratio", "RESULTADO")
            ));
        }

        // ================= FINANCIEROS =======================
        if (!regs.isEmpty()) {
            if (totalIngresos > 0) {
                detalles.put("KPI_MARGEN_NETO", List.of(
                    helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                    helper.item("Total egresos", totalEgresos, "COP", "INPUT"),
                    helper.item("Beneficio neto", totalIngresos - totalEgresos, "COP", "INTERMEDIO"),
                    helper.item("Margen Neto",
                        ((totalIngresos - totalEgresos) / totalIngresos) * 100, "%", "RESULTADO")
                ));

                detalles.put("KPI_MARGEN_BRUTO_PCT", List.of(
                    helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                    helper.item("Total gastos y costos (sin inversión)", totalGastos, "COP", "INPUT"),
                    helper.item("Margen bruto", totalIngresos - totalGastos, "COP", "INTERMEDIO"),
                    helper.item("Margen Bruto %",
                        ((totalIngresos - totalGastos) / totalIngresos) * 100, "%", "RESULTADO")
                ));
            }

            if (totalEgresos > 0) {
                detalles.put("KPI_RATIO_INGRESO_EGRESO", List.of(
                    helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                    helper.item("Total egresos", totalEgresos, "COP", "INPUT"),
                    helper.item("Ratio Ingreso/Egreso", totalIngresos / totalEgresos, "x", "RESULTADO")
                ));
            }

            detalles.put("KPI_BALANCE_NETO", List.of(
                helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                helper.item("Total egresos", totalEgresos, "COP", "INPUT"),
                helper.item("Balance Neto", totalIngresos - totalEgresos, "COP", "RESULTADO")
            ));

            if (pp != null && pp.getVacasEnOrdenio() != null && pp.getVacasEnOrdenio() > 0) {
                detalles.put("KPI_INGRESO_VACA", List.of(
                    helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                    helper.item("Vacas en ordeño", (double) pp.getVacasEnOrdenio(), "vacas", "INPUT"),
                    helper.item("Ingreso por Vaca",
                        totalIngresos / pp.getVacasEnOrdenio(), "COP/vaca", "RESULTADO")
                ));
            }

            if (activosTotales > 0) {
                detalles.put("KPI_ROA", List.of(
                    helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                    helper.item("Total egresos", totalEgresos, "COP", "INPUT"),
                    helper.item("Valor inventario ganado", valorInventarioGanado, "COP", "INPUT"),
                    helper.item("Valor inventario general", valorInventarioGeneral, "COP", "INPUT"),
                    helper.item("Beneficio neto", totalIngresos - totalEgresos, "COP", "INTERMEDIO"),
                    helper.item("Activos totales", activosTotales, "COP", "INTERMEDIO"),
                    helper.item("ROA",
                        ((totalIngresos - totalEgresos) / activosTotales) * 100, "%", "RESULTADO")
                ));

                detalles.put("KPI_ROTACION_ACTIVOS", List.of(
                    helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                    helper.item("Activos totales", activosTotales, "COP", "INPUT"),
                    helper.item("Rotación de Activos", totalIngresos / activosTotales, "veces", "RESULTADO")
                ));
            }

            detalles.put("KPI_INGRESO_HA_ANIO", List.of(
                helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                helper.item("Área total del hato", areaHato, "ha", "INPUT"),
                helper.item("Ingreso/Ha/Año", totalIngresos / areaHato, "COP/ha", "RESULTADO")
            ));
        }

        // ============ KPI_INGRESO_LITRO =================
        if (totalLitrosVendidos > 0) {
            detalles.put("KPI_INGRESO_LITRO", List.of(
                helper.item("Ingreso total por ventas de leche", totalIngresoVentas, "COP", "INPUT"),
                helper.item("Total litros vendidos", totalLitrosVendidos, "L", "INPUT"),
                helper.item("Fuente: registros ventaleche", "INTERMEDIO"),
                helper.item("Ingreso por Litro Vendido",
                    totalIngresoVentas / totalLitrosVendidos, "COP/L", "RESULTADO")
            ));
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
            if (ingresoLecheRegistros > 0 && litrosEstimados > 0) {
                detalles.put("KPI_INGRESO_LITRO", List.of(
                    helper.item("Ingreso por ventas de leche (registros financieros)",
                        ingresoLecheRegistros, "COP", "INPUT"),
                    helper.item("Producción diaria estimada",
                        pp.getProduccionDiariaLitros(), "L/día", "INPUT"),
                    helper.item("Meses con registros", (double) mesesConRegistros, "meses", "INPUT"),
                    helper.item("Litros estimados (" + mesesConRegistros + " meses × 30 días)",
                        litrosEstimados, "L", "INTERMEDIO"),
                    helper.item("Fuente: estimado desde perfil productivo", "INTERMEDIO"),
                    helper.item("Ingreso por Litro Vendido",
                        ingresoLecheRegistros / litrosEstimados, "COP/L", "RESULTADO")
                ));
            }
        }

        // =============== KPI_COSTO_LITRO ==================

        if (totalLitrosProducidos > 0 && totalGastos > 0) {
            detalles.put("KPI_COSTO_LITRO", List.of(
                helper.item("Total gastos y costos de producción", totalGastos, "COP", "INPUT"),
                helper.item("Total litros producidos", totalLitrosProducidos, "L", "INPUT"),
                helper.item("Costo por Litro Producido",
                    totalGastos / totalLitrosProducidos, "COP/L", "RESULTADO")
            ));
        }

        // ================== EFICIENCIA ===================

        if (totalEmpleados > 0) {
            detalles.put("KPI_EMPLEADOS_HA", List.of(
                helper.item("Empleados permanentes",
                    (double) hato.getCantEmpleadosPermanentes(), "emp", "INPUT"),
                helper.item("Empleados temporales",
                    (double) hato.getCantEmpleadosTemporales(), "emp", "INPUT"),
                helper.item("Total empleados", (double) totalEmpleados, "emp", "INTERMEDIO"),
                helper.item("Área de pastoreo", areaPastoreo, "ha", "INPUT"),
                helper.item("Empleados/Ha", totalEmpleados / areaPastoreo, "emp/ha", "RESULTADO")
            ));
        }

        // ========= KPI_IOFC ===========

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
        String fuenteLeche = totalIngresoVentaLeche >= totalIngresoLecheRegistros
            ? "ventaleche" : "registros financieros categoría leche";

        double gastoAlimentacionRegistros = regs.stream()
            .filter(r -> helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                "ALIMENTACIÓN", "CONCENTRADO", "SAL MINERALIZADA",
                "SUPLEMENTOS", "ENSILAJE", "PASTO CULTIVADO"))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        double gastoAlimentacionFinal = gastoAlimentacionRegistros > 0
            ? gastoAlimentacionRegistros
            : (hato.getGastoMensualAlimentacion() != null
                && hato.getGastoMensualAlimentacion() > 0
                ? hato.getGastoMensualAlimentacion() : 0);

        String fuenteAlimentacion = gastoAlimentacionRegistros > 0
            ? "registros financieros categoría alimentación"
            : (hato.getGastoMensualAlimentacion() != null
                && hato.getGastoMensualAlimentacion() > 0
                ? "campo fijo del hato (sin registros de alimentación)"
                : "sin datos");

        detalles.put("KPI_IOFC", List.of(
            helper.item("Ingreso por ventas de leche (fuente: " + fuenteLeche + ")",
                totalIngresoLeche, "COP", "INPUT"),
            helper.item("Gasto en alimentación (fuente: " + fuenteAlimentacion + ")",
                gastoAlimentacionFinal, "COP", "INPUT"),
            helper.item("IOFC", totalIngresoLeche - gastoAlimentacionFinal, "COP", "RESULTADO")
        ));

        // =============== KPI_COSTO_LABORAL_PCT =============

        double gastoNominaRegistros = regs.stream()
            .filter(r -> helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                "MANO DE OBRA", "SALARIOS", "PRESTACIONES", "HONORARIOS"))
            .mapToDouble(RegistroFinanciero::getMonto).sum();

        String fuenteNomina = (hato.getGastoMensualNomina() != null
            && hato.getGastoMensualNomina() > 0)
            ? "campo fijo del hato" : "registros financieros categoría nómina";

        if (totalIngresos > 0 && gastoNominaRegistros > 0) {
            detalles.put("KPI_COSTO_LABORAL_PCT", List.of(
                helper.item("Gasto en nómina/mano de obra (fuente: " + fuenteNomina + ")",
                    gastoNominaRegistros, "COP", "INPUT"),
                helper.item("Total ingresos", totalIngresos, "COP", "INPUT"),
                helper.item("Costo Laboral %",
                    (gastoNominaRegistros / totalIngresos) * 100, "%", "RESULTADO")
            ));
        }

        // ================= KPI_BREAKEVEN_LITRO =================

        if (totalLitrosProducidos > 0 && (totalGastos + totalEgresos) > 0) {
            detalles.put("KPI_BREAKEVEN_LITRO", List.of(
                helper.item("Total gastos y costos", totalGastos, "COP", "INPUT"),
                helper.item("Total egresos (incluye inversiones)", totalEgresos, "COP", "INPUT"),
                helper.item("Total litros producidos", totalLitrosProducidos, "L", "INPUT"),
                helper.item("Precio de Equilibrio",
                    (totalGastos + totalEgresos) / totalLitrosProducidos, "COP/L", "RESULTADO")
            ));
        }

        return detalles;
    }
}