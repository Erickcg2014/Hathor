-- ============================================================
-- FASE 2.1 — BENCHMARKREFERENCIA NIVEL NACIONAL
-- Hathor — 22 KPIs, sin trópico, sin región, sin escala
-- Fuentes:
--   [F1] FEDEGAN: fedegan.org.co/noticias/como-producir-20-litros-de-leche-por-vaca-en-colombia
--   [F2] Scielo Colombia - Valle del Cauca 2018: scielo.org.co/scielo.php?pid=S0120-29522018000300252
--   [F3] CONtexto Ganadero / USP MADR 2024: contextoganadero.com/economia/los-10-departamentos-donde-mas-bajo-el-precio
--   [F4] Agronegocios.co / FEDEGAN costos 2024: agronegocios.co/agricultura/como-se-compone-el-alto-costo-de-producir-la-leche
--   [F5] CONtexto Ganadero 7 regiones: contextoganadero.com/ganaderia-sostenible/conozca-la-produccion-de-leche-de-vacas-en-7-regiones
--   [F6] Infobae / ASOLECHE 2024: infobae.com/colombia/2024/03/11/ante-bajo-precio-de-la-leche
-- ============================================================

-- ----------------------------------------------------------------
-- 1. KPI_LITROS_VACA_DIA — Litros/Vaca/Día
-- Fuente [F1]: FEDEGAN reporta promedio nacional ~4.2 L/vaca/día
-- Fuente [F5]: sistemas optimizados en zonas altas alcanzan 12-15 L
-- valor_promedio = 4.5 (ajuste 2024 considerando mejora leve vs 4.2 histórico)
-- valor_top      = 15.0 (lechería especializada trópico alto)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 4.5, 15.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';

-- ----------------------------------------------------------------
-- 2. KPI_LITROS_HA_ANIO — Litros/Hectárea/Año
-- Fuente [F2]: sistemas especializados Valle del Cauca = 7.965 L/ha/año promedio,
--             top 15% supera 15.000 L/ha/año
-- Nacional general (incluye doble propósito y extensivo) estimado ~4.500
-- valor_top = 15.000 para lechería especializada de alta eficiencia
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 4500.0, 15000.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

-- ----------------------------------------------------------------
-- 3. KPI_PRODUCCION_HA_DIA — Producción/Hectárea/Día
-- Derivado de KPI_LITROS_HA_ANIO / 365
-- promedio: 4500 / 365 = 12.3 L/ha/día
-- top:     15000 / 365 = 41.1 L/ha/día
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 12.3, 41.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';

-- ----------------------------------------------------------------
-- 4. KPI_CAP_ALMAC_UTILIZADA — Capacidad de Almacenamiento Utilizada (%)
-- No hay fuente directa nacional — se toma como referencia técnica del sector:
-- uso óptimo entre 70-90%, crítico >95% (riesgo de pérdida de leche) o <40% (infraestructura ociosa)
-- valor_promedio = 70 (uso típico de hatos medianos)
-- valor_top      = 90 (uso eficiente sin llegar a saturación)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 70.0, 90.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

-- ----------------------------------------------------------------
-- 5. KPI_CARGA_ANIMAL — Animales/Hectárea
-- Fuente [F2]: promedio ganadero nacional = 0.8 animales/ha (Fedegan 2013)
--             lechería especializada = 3.16 UGG/ha (Scielo Valle del Cauca)
-- valor_promedio = 0.8 (promedio nacional incluyendo carne y doble propósito)
-- valor_top      = 3.16 (lechería especializada — referencia Scielo)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 0.8, 3.16, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';

-- ----------------------------------------------------------------
-- 6. KPI_PCT_VACAS_ORDENIO — % Vacas en Ordeño sobre total hato
-- Referencia técnica sectorial Colombia:
-- sistemas tradicionales: 45-55%, sistemas optimizados: 65-75%
-- valor_promedio = 55.0
-- valor_top      = 70.0
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 55.0, 70.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

-- ----------------------------------------------------------------
-- 7. KPI_HEMBRAS_RECRIA_VACA — Índice hembras de recría / vacas en ordeño
-- Referencia técnica: índice óptimo 0.32-0.45 para reposición sostenible
-- valor_promedio = 0.38
-- valor_top      = 0.45
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 0.38, 0.45, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

-- ----------------------------------------------------------------
-- 8. KPI_LACTANCIA_VS_ESTANDAR — Diferencia vs estándar 305 días (días)
-- Estándar internacional: 305 días de lactancia
-- Colombia promedio: sistemas extensivos 240-270 días → diferencia ~ -50
-- Sistemas especializados: 290-320 días → diferencia ~ -15 a +15
-- valor_promedio = -30 (promedio nacional, incluye todos los sistemas)
-- valor_top      = +15 (lechería especializada con buen manejo reproductivo)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', -30.0, 15.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';

-- ----------------------------------------------------------------
-- 9. KPI_FRECUENCIA_ORDENIO — Ordeños por día
-- Colombia: mayoría hace 1-2 ordeños/día. Sistemas especializados: 2.
-- valor_promedio = 1.5 (entre 1 y 2 ordeños según sistema)
-- valor_top      = 2.0 (dos ordeños diarios — óptimo para producción)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 1.5, 2.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

-- ----------------------------------------------------------------
-- 10. KPI_MARGEN_NETO — Margen Neto (%)
-- Fuente [F4]: en 2024 muchos productores operaban con margen negativo.
-- Sistemas eficientes con precio sobre costo: margen 5-15%
-- valor_promedio = 8.0 (sistemas viables promedio)
-- valor_top      = 20.0 (sistemas altamente eficientes)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 8.0, 20.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';

-- ----------------------------------------------------------------
-- 11. KPI_MARGEN_BRUTO_PCT — Margen Bruto (%)
-- Referencia: margen bruto (ingresos - costos directos) / ingresos
-- Sector lácteo Colombia: 25-40% promedio, top >50%
-- valor_promedio = 28.0
-- valor_top      = 50.0
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 28.0, 50.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

-- ----------------------------------------------------------------
-- 12. KPI_RATIO_INGRESO_EGRESO — Ratio Ingreso/Egreso
-- Fuente [F4]: en 2024 muchos hatos tenían ratio < 1.0 (pérdidas)
-- Hatos viables: ratio 1.05-1.20, top > 1.40
-- valor_promedio = 1.05 (viabilidad mínima en contexto crisis 2024)
-- valor_top      = 1.40
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 1.05, 1.40, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

-- ----------------------------------------------------------------
-- 13. KPI_BALANCE_NETO — Balance Neto (COP)
-- KPI sin benchmark comparativo directo — depende del tamaño del hato
-- Se usa como referencia simbólica: > 0 = viable, umbral mínimo
-- valor_promedio = 0 (punto de equilibrio)
-- valor_top      = NULL (no aplica — depende de escala)
-- Se inserta con valor_top = 1 solo para que el registro exista
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 0.0, 1.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

-- ----------------------------------------------------------------
-- 14. KPI_INGRESO_VACA — Ingreso por Vaca/Año (COP)
-- Derivado: precio litro × litros/año/vaca
-- Promedio: $1.938/L × 4.5 L/día × 305 días lactancia = ~$2.661.000
-- Top:      $2.000/L × 15 L/día × 305 días = ~$9.150.000
-- valor_promedio = 2700000
-- valor_top      = 9000000
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 2700000.0, 9000000.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';

-- ----------------------------------------------------------------
-- 15. KPI_INGRESO_LITRO — Ingreso por Litro Vendido (COP/L)
-- Fuente [F3]: precio promedio nacional 2024 cerró en $1.938/L
-- Fuente [F4]: trópico alto $1.960/L, trópico bajo $1.745/L en julio 2024
-- valor_promedio = 1938 (promedio nacional USP 2024)
-- valor_top      = 2350 (precio con bonificaciones máximas por calidad)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 1938.0, 2350.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

-- ----------------------------------------------------------------
-- 16. KPI_ROA — Retorno sobre Activos (%)
-- Referencia sector agropecuario Colombia: ROA 3-8% viable, >12% eficiente
-- En contexto crisis 2024, muchos hatos con ROA negativo
-- valor_promedio = 5.0
-- valor_top      = 12.0
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 5.0, 12.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROA';

-- ----------------------------------------------------------------
-- 17. KPI_ROTACION_ACTIVOS — Rotación de Activos (veces/año)
-- Referencia sector ganadero: 0.3-0.6 típico, >0.8 eficiente
-- valor_promedio = 0.4
-- valor_top      = 0.8
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 0.4, 0.8, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- ----------------------------------------------------------------
-- 18. KPI_COSTO_LITRO — Costo por Litro Producido (COP/L)
-- ATENCIÓN: en este KPI menor es mejor → valor_top es el más BAJO (eficiente)
-- Fuente [F4]: costo 2024 entre $1.850-$2.100/L para alta calidad
-- Sistemas eficientes (trópico alto, escala grande): ~$1.400-$1.600/L
-- valor_promedio = 1900 (promedio nacional 2024 incluyendo todos los sistemas)
-- valor_top      = 1400 (sistemas más eficientes — menor costo)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 1900.0, 1400.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';

-- ----------------------------------------------------------------
-- 19. KPI_INGRESO_HA_ANIO — Ingreso por Hectárea/Año (COP)
-- Derivado: litros/ha/año × precio/litro
-- Promedio: 4.500 L × $1.938 = ~$8.721.000/ha
-- Top:     15.000 L × $2.000 = ~$30.000.000/ha
-- valor_promedio = 8700000
-- valor_top      = 30000000
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 8700000.0, 30000000.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';

-- ----------------------------------------------------------------
-- 20. KPI_IOFC — Income Over Feed Cost (COP)
-- Ingreso leche - Gasto alimentación
-- Referencia: alimentación representa ~33% del costo total de producción (Fedegan)
-- Con ingreso promedio ~$5.5M/mes hato mediano y alimentación ~$2.2M → IOFC ~$3.3M
-- valor_promedio = 800000  (hatos pequeños/doble propósito mes)
-- valor_top      = 3000000 (sistemas especializados eficientes mes)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 800000.0, 3000000.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_IOFC';

-- ----------------------------------------------------------------
-- 21. KPI_COSTO_LABORAL_PCT — Costo Laboral como % del Ingreso
-- ATENCIÓN: menor es mejor → valor_top es el más BAJO (eficiente)
-- Fuente [F4]: mano de obra representa parte significativa del costo
-- En crisis 2024: costo laboral subió 30% mientras precio leche cayó 6%
-- Referencia: <20% eficiente, 20-35% aceptable, >40% crítico
-- valor_promedio = 25.0 (promedio nacional sistemas medianos)
-- valor_top      = 15.0 (sistemas mecanizados o de gran escala)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 25.0, 15.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';

-- ----------------------------------------------------------------
-- 22. KPI_BREAKEVEN_LITRO — Precio de Equilibrio por Litro (COP/L)
-- ATENCIÓN: menor es mejor → valor_top es el más BAJO (más eficiente)
-- Fuente [F4]: costo producción 2024 entre $1.850-$2.100/L
-- Sistemas eficientes logran breakeven ~$1.400-$1.600/L
-- valor_promedio = 1900 (punto de equilibrio promedio nacional 2024)
-- valor_top      = 1400 (sistemas más eficientes)
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 1900.0, 1400.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';

-- ----------------------------------------------------------------
-- 23. KPI_EMPLEADOS_HA — Empleados por Hectárea
-- ATENCIÓN: menor es mejor → valor_top es el más BAJO (más eficiente)
-- Referencia técnica: sistemas extensivos ~0.10-0.15 emp/ha
-- Sistemas tecnificados: 0.05-0.08 emp/ha
-- valor_promedio = 0.12
-- valor_top      = 0.06
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 0.12, 0.06, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';

-- ----------------------------------------------------------------
-- 24. KPI_LITROS_EMPLEADO — Litros producidos por Empleado/Día
-- Referencia: sistemas manuales ~40-80 L/emp/día
-- Sistemas mecanizados: 150-300 L/emp/día
-- valor_promedio = 80.0
-- valor_top      = 200.0
-- ----------------------------------------------------------------
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, 'NACIONAL', 80.0, 200.0, 2024, NULL, NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-------------- PARTE 2 -----------------
-- ============================================================
-- FASE 2.2 — BENCHMARKREFERENCIA NIVEL POR TRÓPICO
-- Hathor — 22 KPIs × 3 trópicos = 66 registros
-- Trópicos: FRIO, TEMPLADO, CALIDO
-- Fuentes:
--   [F1] FEDEGAN / CONtexto Ganadero 7 regiones:
--        contextoganadero.com/ganaderia-sostenible/conozca-la-produccion-de-leche-de-vacas-en-7-regiones
--   [F2] Scielo Valle del Cauca 2018 (lechería especializada trópico medio):
--        scielo.org.co/scielo.php?pid=S0120-29522018000300252
--   [F3] FEDEGAN / Agronegocios.co costos 2024:
--        agronegocios.co/agricultura/como-se-compone-el-alto-costo-de-producir-la-leche
--   [F4] Infortambo Andina — comparativa frío vs cálido:
--        infortamboandina.co/es/noticias/rentabilidad-en-las-lecherias
--   [F5] CONtexto Ganadero / USP precios 2024:
--        contextoganadero.com/economia/los-10-departamentos-donde-mas-bajo-el-precio
--   [F6] Genética Bovina Colombiana — trópico bajo especializado:
--        revistageneticabovina.com/biotecnologia/tropico-bajo/
--   [F7] ANALAC regiones 1 y 2:
--        analac.org/2022/03/19/el-precio-base-del-litro-de-leche-pagado-al-productor-crecio-8-83/
--   [F8] Agronegocios.co — costos por trópico:
--        agronegocios.co/agricultura/quienes-pagan-el-verdadero-precio-de-la-leche-en-colombia
-- ============================================================
-- MAPEO DE TRÓPICOS EN HATHOR vs FUENTES:
-- FRIO     = trópico alto/región 1 (>2000 msnm): Cundinamarca, Boyacá, Antioquia,
--            Caldas, Nariño, Cauca — lechería especializada Holstein predominante
-- TEMPLADO = trópico medio/valles interandinos (1000-2000 msnm): Eje Cafetero,
--            Valle del Cauca, Santander — sistemas mixtos
-- CALIDO   = trópico bajo/región 2 (<1000 msnm): Costa Caribe, Llanos, Magdalena
--            Medio — doble propósito y Gyr/Girolando
-- ============================================================


-- ============================================================
-- TRÓPICO: FRIO
-- Lechería especializada Holstein. Mayor producción por vaca,
-- mayor costo de insumos, precio de leche más alto al productor.
-- Representa ~45% de la producción nacional (lechería especializada)
-- ============================================================

-- KPI_LITROS_VACA_DIA FRIO
-- Fuente [F1]: zonas altas con manejo eficiente: 12-15 L/vaca/día
-- Fuente [F4]: promedio frío en lecherías comparadas: 17.5-26 L/vaca/día
-- Fuente [F1]: Holstein puede llegar a 20-22 L en trópico frío
-- valor_promedio = 10.0 (incluye todos los tamaños de hato en frío)
-- valor_top      = 22.0 (lechería especializada Holstein tecnificada)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 10.0, 22.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';

-- KPI_LITROS_HA_ANIO FRIO
-- Fuente [F2]: sistemas especializados Valle del Cauca (trópico medio-alto): 7.965 L/ha/año
-- Fuente [F4]: clima frío con kikuyo: 2.44-3.82 vacas/ha × ~3.650 L/vaca/año = ~9.000-14.000 L/ha/año
-- valor_promedio = 9000.0
-- valor_top      = 18000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 9000.0, 18000.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

-- KPI_PRODUCCION_HA_DIA FRIO
-- Derivado: 9000/365 = 24.7, top 18000/365 = 49.3
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 24.7, 49.3, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';

-- KPI_CAP_ALMAC_UTILIZADA FRIO
-- Sin diferencia significativa por trópico — igual al nacional
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 70.0, 90.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

-- KPI_CARGA_ANIMAL FRIO
-- Fuente [F4]: lecherías frío: 2.44 a 3.82 vacas/ha con kikuyo
-- Fuente [F2]: lechería especializada: 3.16 UGG/ha
-- valor_promedio = 2.5 (hatos medianos con kikuyo)
-- valor_top      = 4.0 (intensivos con suplementación)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.5, 4.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';

-- KPI_PCT_VACAS_ORDENIO FRIO
-- Lechería especializada tiene mayor % de vacas productivas
-- valor_promedio = 60.0, valor_top = 75.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 60.0, 75.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

-- KPI_HEMBRAS_RECRIA_VACA FRIO
-- Sin diferencia significativa por trópico
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

-- KPI_LACTANCIA_VS_ESTANDAR FRIO
-- Lechería especializada frío: mejor manejo reproductivo
-- Promedio más cercano al estándar que el nacional
-- valor_promedio = -15, valor_top = +20
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -15.0, 20.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';

-- KPI_FRECUENCIA_ORDENIO FRIO
-- Trópico frío: mayoría hace 2 ordeños/día
-- valor_promedio = 2.0, valor_top = 2.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

-- KPI_MARGEN_NETO FRIO
-- Fuente [F3]: en trópico frío el costo es más alto ($1.850-$2.100/L)
-- y el precio pagado fue $1.960/L en julio 2024 → margen muy estrecho
-- En 2024 muchos hatos fríos operaron con margen negativo o casi cero
-- valor_promedio = 3.0 (margen ajustado por crisis 2024)
-- valor_top      = 18.0 (sistemas muy eficientes)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.0, 18.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';

-- KPI_MARGEN_BRUTO_PCT FRIO
-- Mayor insumo en frío implica menor margen bruto pese a mayor producción
-- valor_promedio = 25.0, valor_top = 45.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 25.0, 45.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

-- KPI_RATIO_INGRESO_EGRESO FRIO
-- Contexto crisis 2024: ratio muy ajustado en trópico frío
-- valor_promedio = 1.03, valor_top = 1.35
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.03, 1.35, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

-- KPI_BALANCE_NETO FRIO
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

-- KPI_INGRESO_VACA FRIO
-- Precio $1.960 × 10 L/día × 305 días = ~$5.978.000
-- valor_promedio = 5900000, valor_top = 14000000
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5900000.0, 14000000.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';

-- KPI_INGRESO_LITRO FRIO
-- Fuente [F5]: precio promedio región 1 (trópico alto/frío) 2024 = $2.125 (enero)
-- Fuente [F3]: $1.960 en julio 2024 — promedio año $1.938 región 1 ligeramente más alto
-- valor_promedio = 1960.0 (precio promedio trópico frío julio 2024)
-- valor_top      = 2400.0 (con todas las bonificaciones de calidad)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1960.0, 2400.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

-- KPI_ROA FRIO
-- Mayor valor de activos en frío (tierra más cara, más infraestructura) deprime el ROA
-- valor_promedio = 4.0, valor_top = 10.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.0, 10.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROA';

-- KPI_ROTACION_ACTIVOS FRIO
-- Activos más altos → menor rotación
-- valor_promedio = 0.35, valor_top = 0.70
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.35, 0.70, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- KPI_COSTO_LITRO FRIO
-- MENOR ES MEJOR
-- Fuente [F3]: costo entre $1.850-$2.100/L en trópico alto 2024
-- Fuente [F4]: en lecherías frío estudiadas: $1.036-$1.133 (dato anterior, actualizado a 2024)
-- valor_promedio = 1950.0 (promedio trópico frío 2024)
-- valor_top      = 1500.0 (sistemas muy eficientes de gran escala en frío)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1950.0, 1500.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';

-- KPI_INGRESO_HA_ANIO FRIO
-- 9000 L/ha/año × $1.960 = ~$17.640.000
-- valor_promedio = 17600000.0, valor_top = 42000000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 17600000.0, 42000000.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';

-- KPI_IOFC FRIO
-- Mayor producción pero también mayor costo de concentrado
-- Fuente [F4]: en frío consumen 7 kg concentrado/vaca/día de alta calidad
-- valor_promedio = 1200000.0, valor_top = 4000000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1200000.0, 4000000.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_IOFC';

-- KPI_COSTO_LABORAL_PCT FRIO
-- MENOR ES MEJOR
-- Mano de obra representa ~1/3 del costo total
-- valor_promedio = 28.0, valor_top = 18.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 28.0, 18.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';

-- KPI_BREAKEVEN_LITRO FRIO
-- MENOR ES MEJOR
-- valor_promedio = 1950.0 (mismo que costo litro frío)
-- valor_top      = 1500.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1950.0, 1500.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';

-- KPI_EMPLEADOS_HA FRIO
-- MENOR ES MEJOR
-- Lechería especializada frío: más tecnificada, menos empleados/ha
-- valor_promedio = 0.10, valor_top = 0.05
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.10, 0.05, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';

-- KPI_LITROS_EMPLEADO FRIO
-- Mayor producción por vaca → más litros por empleado
-- valor_promedio = 100.0, valor_top = 280.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 100.0, 280.0, 2024, 'FRIO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- TRÓPICO: TEMPLADO
-- Valles interandinos, Eje Cafetero, Valle del Cauca
-- Sistemas mixtos: lechería especializada en zonas altas,
-- doble propósito en zonas medias. Temperatura 17-24°C.
-- ============================================================

-- KPI_LITROS_VACA_DIA TEMPLADO
-- Fuente [F1]: Eje Cafetero minifundios: 4-5 L/día, zonas altas: 12-15 L/día
-- Fuente [F2]: Valle del Cauca especializado: 3.75-18.1 L/vaca/día
-- valor_promedio = 7.0 (promedio entre minifundio y sistema tecnificado)
-- valor_top      = 18.0 (lechería especializada trópico medio-alto)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7.0, 18.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';

-- KPI_LITROS_HA_ANIO TEMPLADO
-- Fuente [F2]: Valle del Cauca lechería especializada = 7.965 L/ha/año promedio
-- valor_promedio = 7965.0
-- valor_top      = 15000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7965.0, 15000.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

-- KPI_PRODUCCION_HA_DIA TEMPLADO
-- Derivado: 7965/365 = 21.8, top 15000/365 = 41.1
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 21.8, 41.1, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';

-- KPI_CAP_ALMAC_UTILIZADA TEMPLADO
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 70.0, 90.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

-- KPI_CARGA_ANIMAL TEMPLADO
-- Fuente [F4]: cálido con pasto estrella: 4.75-5.7 vacas/ha
-- Templado es intermedio entre frío y cálido
-- valor_promedio = 3.0, valor_top = 5.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.0, 5.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';

-- KPI_PCT_VACAS_ORDENIO TEMPLADO
-- Sistemas mixtos: valor intermedio
-- valor_promedio = 58.0, valor_top = 72.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 58.0, 72.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

-- KPI_HEMBRAS_RECRIA_VACA TEMPLADO
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

-- KPI_LACTANCIA_VS_ESTANDAR TEMPLADO
-- Sistemas mixtos: intermedio entre frío y cálido
-- valor_promedio = -20.0, valor_top = +15.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -20.0, 15.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';

-- KPI_FRECUENCIA_ORDENIO TEMPLADO
-- Mayoritariamente 2 ordeños en sistemas especializados
-- valor_promedio = 1.8, valor_top = 2.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.8, 2.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

-- KPI_MARGEN_NETO TEMPLADO
-- Fuente [F8]: costo entre $1.650-$1.900/L en trópico medio
-- Precio ligeramente inferior al frío pero costo también menor
-- valor_promedio = 6.0, valor_top = 20.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6.0, 20.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';

-- KPI_MARGEN_BRUTO_PCT TEMPLADO
-- valor_promedio = 27.0, valor_top = 48.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 27.0, 48.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

-- KPI_RATIO_INGRESO_EGRESO TEMPLADO
-- valor_promedio = 1.05, valor_top = 1.38
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.05, 1.38, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

-- KPI_BALANCE_NETO TEMPLADO
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

-- KPI_INGRESO_VACA TEMPLADO
-- Precio ~$1.850 × 7 L/día × 305 días = ~$3.948.000
-- valor_promedio = 3900000.0, valor_top = 11000000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3900000.0, 11000000.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';

-- KPI_INGRESO_LITRO TEMPLADO
-- Precio intermedio entre frío y cálido
-- valor_promedio = 1870.0, valor_top = 2300.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1870.0, 2300.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

-- KPI_ROA TEMPLADO
-- valor_promedio = 5.0, valor_top = 12.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5.0, 12.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROA';

-- KPI_ROTACION_ACTIVOS TEMPLADO
-- valor_promedio = 0.40, valor_top = 0.80
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.40, 0.80, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- KPI_COSTO_LITRO TEMPLADO
-- MENOR ES MEJOR
-- Fuente [F8]: costo entre $1.650-$1.900/L en trópico medio
-- valor_promedio = 1800.0, valor_top = 1350.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 1350.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';

-- KPI_INGRESO_HA_ANIO TEMPLADO
-- 7965 L/ha/año × $1.870 = ~$14.894.000
-- valor_promedio = 14900000.0, valor_top = 34000000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 14900000.0, 34000000.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';

-- KPI_IOFC TEMPLADO
-- Intermedio: mayor producción que cálido, menor costo de concentrado que frío
-- valor_promedio = 900000.0, valor_top = 3200000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 900000.0, 3200000.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_IOFC';

-- KPI_COSTO_LABORAL_PCT TEMPLADO
-- MENOR ES MEJOR
-- valor_promedio = 26.0, valor_top = 16.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 26.0, 16.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';

-- KPI_BREAKEVEN_LITRO TEMPLADO
-- MENOR ES MEJOR
-- valor_promedio = 1800.0, valor_top = 1350.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 1350.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';

-- KPI_EMPLEADOS_HA TEMPLADO
-- MENOR ES MEJOR
-- valor_promedio = 0.11, valor_top = 0.055
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.11, 0.055, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';

-- KPI_LITROS_EMPLEADO TEMPLADO
-- valor_promedio = 90.0, valor_top = 220.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 90.0, 220.0, 2024, 'TEMPLADO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- TRÓPICO: CALIDO
-- <1000 msnm. Costa Caribe, Llanos, Magdalena Medio, Caquetá.
-- Predomina doble propósito. Razas Cebú, Gyr, Girolando.
-- Menor producción por vaca, menor costo de insumos,
-- mayor carga animal posible con pastos tropicales,
-- precio de leche más bajo al productor.
-- Representa ~55% de la producción nacional (doble propósito)
-- ============================================================

-- KPI_LITROS_VACA_DIA CALIDO
-- Fuente [F1]: cebú doble propósito: 2.5-3.5 L/día, con manejo eficiente hasta 5 L
-- Fuente [F4]: lecherías cálidas estudiadas: 12.5-17.5 L/día (proyectos especializados)
-- Fuente [F6]: proyectos especializados cálido con Girolando: >20 L/día en hatos grandes
-- valor_promedio = 4.0 (promedio general doble propósito cálido)
-- valor_top      = 17.0 (proyectos especializados con Girolando tecnificado)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.0, 17.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';

-- KPI_LITROS_HA_ANIO CALIDO
-- Fuente [F4]: cálido con estrella africana: 4.75-5.7 vacas/ha × ~1.460 L/vaca/año = ~6.935-8.322 L/ha/año
-- A pesar de menor producción por vaca, mayor densidad compensa
-- valor_promedio = 6000.0, valor_top = 12000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6000.0, 12000.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

-- KPI_PRODUCCION_HA_DIA CALIDO
-- Derivado: 6000/365 = 16.4, top 12000/365 = 32.9
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 16.4, 32.9, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';

-- KPI_CAP_ALMAC_UTILIZADA CALIDO
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 65.0, 88.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

-- KPI_CARGA_ANIMAL CALIDO
-- Fuente [F4]: cálido con pasto estrella: 4.75-5.7 vacas/ha
-- valor_promedio = 3.5, valor_top = 5.5
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.5, 5.5, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';

-- KPI_PCT_VACAS_ORDENIO CALIDO
-- Doble propósito: menor % de vacas en ordeño por manejo con ternero
-- valor_promedio = 48.0, valor_top = 65.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 48.0, 65.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

-- KPI_HEMBRAS_RECRIA_VACA CALIDO
-- Doble propósito: mayor retención de machos y hembras para carne
-- valor_promedio = 0.35, valor_top = 0.42
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.35, 0.42, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

-- KPI_LACTANCIA_VS_ESTANDAR CALIDO
-- Doble propósito: lactancias más cortas por manejo con ternero
-- valor_promedio = -50.0, valor_top = -10.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -50.0, -10.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';

-- KPI_FRECUENCIA_ORDENIO CALIDO
-- Doble propósito: mayoritariamente 1 ordeño/día con ternero
-- valor_promedio = 1.2, valor_top = 2.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.2, 2.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

-- KPI_MARGEN_NETO CALIDO
-- Fuente [F4]: rentabilidad superior en cálido pese a menor producción por vaca
-- Menor costo de insumos compensa la menor producción
-- valor_promedio = 10.0, valor_top = 22.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 10.0, 22.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';

-- KPI_MARGEN_BRUTO_PCT CALIDO
-- Menor costo de insumos → mayor margen bruto en cálido
-- valor_promedio = 32.0, valor_top = 55.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 32.0, 55.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

-- KPI_RATIO_INGRESO_EGRESO CALIDO
-- Mejor ratio por menores costos de insumos
-- valor_promedio = 1.10, valor_top = 1.45
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.10, 1.45, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

-- KPI_BALANCE_NETO CALIDO
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

-- KPI_INGRESO_VACA CALIDO
-- Precio $1.745 × 4 L/día × 305 días = ~$2.130.940
-- valor_promedio = 2100000.0, valor_top = 7600000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2100000.0, 7600000.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';

-- KPI_INGRESO_LITRO CALIDO
-- Fuente [F3]: precio trópico bajo julio 2024 = $1.745/L
-- Fuente [F5]: región 2 promedio enero 2024 = $1.878/L
-- valor_promedio = 1800.0 (promedio anual trópico cálido 2024)
-- valor_top      = 2100.0 (con bonificaciones por calidad)
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 2100.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

-- KPI_ROA CALIDO
-- Tierra más barata → activos menores → mejor ROA potencial
-- valor_promedio = 6.0, valor_top = 14.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6.0, 14.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROA';

-- KPI_ROTACION_ACTIVOS CALIDO
-- Activos más bajos → mayor rotación potencial
-- valor_promedio = 0.45, valor_top = 0.90
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.45, 0.90, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- KPI_COSTO_LITRO CALIDO
-- MENOR ES MEJOR
-- Fuente [F4]: en lecherías cálidas estudiadas: $909-$1.043 COP/L (dato anterior)
-- Actualizado a 2024 considerando inflación: ~$1.400-$1.700/L
-- Fuente [F8]: trópico bajo: costo más bajo que alto y medio
-- valor_promedio = 1600.0, valor_top = 1100.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1600.0, 1100.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';

-- KPI_INGRESO_HA_ANIO CALIDO
-- 6000 L/ha/año × $1.800 = $10.800.000
-- valor_promedio = 10800000.0, valor_top = 25000000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 10800000.0, 25000000.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';

-- KPI_IOFC CALIDO
-- Menor costo de alimentación (pastos tropicales, poco concentrado)
-- pero también menor ingreso por leche
-- valor_promedio = 400000.0, valor_top = 1500000.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 400000.0, 1500000.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_IOFC';

-- KPI_COSTO_LABORAL_PCT CALIDO
-- MENOR ES MEJOR
-- Menor tecnificación pero también menor salario promedio en zonas cálidas
-- valor_promedio = 22.0, valor_top = 13.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 22.0, 13.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';

-- KPI_BREAKEVEN_LITRO CALIDO
-- MENOR ES MEJOR
-- Menor costo de producción → menor precio de equilibrio
-- valor_promedio = 1600.0, valor_top = 1100.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1600.0, 1100.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';

-- KPI_EMPLEADOS_HA CALIDO
-- MENOR ES MEJOR
-- Doble propósito extensivo: menos empleados por hectárea
-- valor_promedio = 0.13, valor_top = 0.07
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.13, 0.07, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';

-- KPI_LITROS_EMPLEADO CALIDO
-- Menor producción por vaca → menos litros por empleado
-- valor_promedio = 60.0, valor_top = 150.0
INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 60.0, 150.0, 2024, 'CALIDO', NULL, NULL
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';