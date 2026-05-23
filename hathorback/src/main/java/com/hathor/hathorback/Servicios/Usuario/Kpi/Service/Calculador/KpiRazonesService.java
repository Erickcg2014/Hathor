package com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KpiRazonesService {

    @Autowired private KpiCalculadorHelper helper;

    public Map<String, String> getRazonesSinDatos(Hato hato, DatosHato datos) {
        Map<String, String> razones = new HashMap<>();
        PerfilProductivo pp = datos.perfilProductivo;

        // ---- Perfil productivo ----
        if (pp == null) {
            razones.put("KPI_LITROS_VACA_DIA",      "Falta registrar el perfil productivo");
            razones.put("KPI_LITROS_HA_ANIO",        "Falta registrar el perfil productivo");
            razones.put("KPI_PRODUCCION_HA_DIA",     "Falta registrar el perfil productivo");
            razones.put("KPI_CAP_ALMAC_UTILIZADA",   "Falta registrar el perfil productivo");
            razones.put("KPI_LITROS_EMPLEADO",       "Falta registrar el perfil productivo");
            razones.put("KPI_FRECUENCIA_ORDENIO",    "Falta registrar el perfil productivo");
            razones.put("KPI_LACTANCIA_VS_ESTANDAR", "Falta registrar el perfil productivo");
            razones.put("KPI_PCT_VACAS_ORDENIO",     "Falta registrar el perfil productivo");
            razones.put("KPI_INGRESO_VACA",          "Falta registrar el perfil productivo");
        } else {
            if (pp.getVacasEnOrdenio() == null || pp.getVacasEnOrdenio() == 0) {
                razones.put("KPI_LITROS_VACA_DIA",
                    "Falta registrar vacas en ordeño en el perfil productivo");
                razones.put("KPI_PCT_VACAS_ORDENIO",
                    "Falta registrar vacas en ordeño en el perfil productivo");
            }
            if (pp.getFrecuenciaOrdenio() == null)
                razones.put("KPI_FRECUENCIA_ORDENIO",
                    "Falta registrar frecuencia de ordeño en el perfil productivo");

            if (pp.getPeriodoLactanciaPromedio() == null)
                razones.put("KPI_LACTANCIA_VS_ESTANDAR",
                    "Falta registrar período de lactancia en el perfil productivo");
        }

        // ---- Inventario ganado ----
        if (datos.inventarioGanado.isEmpty()) {
            razones.put("KPI_CARGA_ANIMAL",
                "Falta registrar el inventario de ganado");
            razones.put("KPI_HEMBRAS_RECRIA_VACA",
                "Falta registrar categorías de ganado (novillas/terneras)");
        }

        // ---- Registros financieros ----
        if (datos.registros.isEmpty()) {
            razones.put("KPI_MARGEN_NETO",          "Falta registrar movimientos financieros");
            razones.put("KPI_MARGEN_BRUTO_PCT",      "Falta registrar movimientos financieros");
            razones.put("KPI_RATIO_INGRESO_EGRESO",  "Falta registrar movimientos financieros");
            razones.put("KPI_BALANCE_NETO",          "Falta registrar movimientos financieros");
            razones.put("KPI_INGRESO_VACA",          "Falta registrar movimientos financieros");
            razones.put("KPI_ROA",                   "Falta registrar movimientos financieros");
            razones.put("KPI_ROTACION_ACTIVOS",      "Falta registrar movimientos financieros");
            razones.put("KPI_INGRESO_HA_ANIO",       "Falta registrar movimientos financieros");
            razones.put("KPI_COSTO_LABORAL_PCT",     "Falta registrar movimientos financieros");
            razones.put("KPI_BREAKEVEN_LITRO",       "Falta registrar costos y gastos en el módulo de finanzas");
        }

        // ---- KPI_INGRESO_LITRO ----
        boolean tieneVentasLeche = !datos.ventasLeche.isEmpty();
        boolean tieneIngresoLecheRegistros = datos.registros.stream()
            .anyMatch(r -> "INGRESO".equals(r.getTipoMovimiento())
                && r.getCategoriaFinanciera() != null
                && helper.perteneceAGrupo(r.getCategoriaFinanciera(),
                    "VENTA DE LECHE", "LECHE", "LÁCTEOS", "LACTEOS"));

        if (!tieneVentasLeche && !tieneIngresoLecheRegistros)
            razones.put("KPI_INGRESO_LITRO",
                "Falta registrar ventas de leche — ya sea desde el módulo de finanzas "
                + "con litros y precio, o como categoría 'Venta de Leche' en los registros financieros");

        // ---- KPI_COSTO_LITRO ----
        double totalLitros = datos.produccionLeche.stream()
            .mapToDouble(p -> p.getLitrosProducidos()).sum();
        if (totalLitros == 0)
            razones.put("KPI_COSTO_LITRO",
                "Falta registrar litros producidos en el módulo de producción");

        // ---- Inventario para activos ----
        if (datos.inventarioGeneral.isEmpty() && datos.inventarioGanado.isEmpty()) {
            razones.put("KPI_ROA",
                "Falta registrar inventario para calcular activos totales");
            razones.put("KPI_ROTACION_ACTIVOS",
                "Falta registrar inventario para calcular activos totales");
        }

        // ---- Área de pastoreo ----
        if (hato.getAreaPastoreo() == 0) {
            razones.put("KPI_LITROS_HA_ANIO",    "El área de pastoreo del hato es cero");
            razones.put("KPI_PRODUCCION_HA_DIA", "El área de pastoreo del hato es cero");
            razones.put("KPI_CARGA_ANIMAL",      "El área de pastoreo del hato es cero");
            razones.put("KPI_EMPLEADOS_HA",      "El área de pastoreo del hato es cero");
        }

        // ---- KPI_IOFC ----
        boolean tieneIngresoLeche = datos.ventasLeche.stream()
            .anyMatch(vl -> vl.getLitrosVendidos() > 0 && vl.getPrecioLitro() > 0);
        boolean tieneGastoAlimentacion = hato.getGastoMensualAlimentacion() != null
            && hato.getGastoMensualAlimentacion() > 0;

        if (!tieneIngresoLeche && !tieneGastoAlimentacion)
            razones.put("KPI_IOFC",
                "Falta registrar ventas de leche y el gasto mensual en alimentación del ganado");
        else if (!tieneIngresoLeche)
            razones.put("KPI_IOFC",
                "Falta registrar ventas de leche en el módulo de finanzas");
        else if (!tieneGastoAlimentacion)
            razones.put("KPI_IOFC",
                "Falta registrar el gasto mensual en alimentación en el paso financiero del formulario");

        // ---- KPI_COSTO_LABORAL_PCT ----
        boolean tieneGastoNomina = hato.getGastoMensualNomina() != null
            && hato.getGastoMensualNomina() > 0;
        if (!datos.registros.isEmpty() && !tieneGastoNomina)
            razones.put("KPI_COSTO_LABORAL_PCT",
                "Falta registrar el gasto mensual en nómina en el paso financiero del formulario");

        // ---- KPI_LACTANCIA_VS_ESTANDAR (refuerzo) ----
        if (pp == null || pp.getPeriodoLactanciaPromedio() == null)
            razones.put("KPI_LACTANCIA_VS_ESTANDAR",
                "Falta registrar el período de lactancia promedio en el perfil productivo");

        // ---- KPI_BREAKEVEN_LITRO ----
        if (datos.produccionLeche.isEmpty()) {
            razones.put("KPI_BREAKEVEN_LITRO",
                "Falta registrar producción de leche en el módulo de producción");
        } else {
            double litros = datos.produccionLeche.stream()
                .mapToDouble(p -> p.getLitrosProducidos()).sum();
            if (litros == 0)
                razones.put("KPI_BREAKEVEN_LITRO",
                    "Los registros de producción tienen 0 litros — verifica los datos ingresados");
        }

        return razones;
    }
}