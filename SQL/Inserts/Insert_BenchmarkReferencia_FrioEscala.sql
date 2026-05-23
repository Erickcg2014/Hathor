-- ============================================================
-- FASE 2.3 PARTE A — BENCHMARKREFERENCIA TRÓPICO FRÍO + ESCALA
-- Hathor — 22 KPIs × 4 escalas = 88 registros
-- Trópico: FRIO (>2000 msnm aprox.)
-- Regiones representativas: Cundinamarca, Boyacá, Antioquia,
--   Caldas, Nariño, Cauca — lechería especializada Holstein
--
-- Fuentes:
--   [F1] INNOVAR/Scielo — Competitividad lechería norte Antioquia:
--        revistas.unal.edu.co/index.php/innovar/article/download/40487/42324
--        Promedio 16.46 L/vaca/día, economías de escala R²=99.8%
--   [F2] Scielo Valle del Cauca 2018 — unidades <50 vacas y costos fijos:
--        scielo.org.co/scielo.php?pid=S0120-29522018000300252
--   [F3] CONtexto Ganadero 7 regiones — Antioquia/Cundinamarca 15-35 L:
--        contextoganadero.com/ganaderia-sostenible/conozca-la-produccion
--   [F4] FEDEGAN — Alto costo pequeños vs grandes productores:
--        estadisticas.fedegan.org.co/DOC/download.jsp?iIdFiles=1069
--   [F5] Infortambo Andina — comparativa frío vs cálido por escala:
--        infortamboandina.co/es/noticias/rentabilidad-en-las-lecherias
--   [F6] Repositorio El Politécnico — costos 4 hatos norte Antioquia:
--        repositorio.elpoli.edu.co (hatos grande, mediano, pequeño)
--   [F7] Agronegocios.co — costos 2024 trópico frío $1.850-$2.100/L:
--        agronegocios.co/agricultura/como-se-compone-el-alto-costo
--
-- PRINCIPIO BASE (Fuente [F1]):
--   El costo unitario decrece con el nivel de producción (R²=99.8%)
--   Esto significa que cada escala tiene benchmarks DISTINTOS y
--   los valores de referencia deben reflejar esa realidad económica.
--
-- ESCALAS EN HATHOR:
--   PEQUEÑA:      < 25 animales  → ganadero familiar, sin economías de escala
--   MEDIANA:      26-200         → empresa familiar tecnificable
--   GRANDE:       201-500        → empresa ganadera establecida
--   EMPRESARIAL:  > 500          → agroindustria ganadera
-- ============================================================


-- ============================================================
-- ESCALA: PEQUEÑA — Trópico FRIO (<25 animales)
-- Ganadero familiar. Sin economías de escala. Costos fijos
-- altos por litro. Producción entre 5-12 L/vaca/día típico.
-- Fuente [F2]: <50 vacas tienen dificultades con costos fijos.
-- Fuente [F4]: escala insuficiente para generar riqueza.
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7.0, 12.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F3]: minifundios frío producen 5-12 L/día. Top = bien manejado con kikuyo.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5000.0, 10000.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Menor densidad por falta de inversión en praderas. Top = buen manejo de kikuyo.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 13.7, 27.4, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 5000/365=13.7, 10000/365=27.4

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 65.0, 85.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';
-- Pequeño productor tiende a subutilizar por infraestructura básica

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 3.5, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Menor inversión en praderas → menor carga sostenible

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 55.0, 70.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Similar al promedio frío general

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.36, 0.44, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -20.0, 15.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Pequeño productor: menor control reproductivo que mediano/grande

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Trópico frío: 2 ordeños independiente de la escala

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -2.0, 12.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F2][F4]: pequeños operan frecuentemente en margen negativo o casi cero
-- Sin economías de escala: costo fijo por litro muy alto

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 20.0, 40.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.98, 1.25, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';
-- Pequeño productor frío: ratio muy ajustado, frecuentemente <1.0

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3200000.0, 8500000.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.960 × 7 L/día × 305 días = ~$4.179.000 promedio

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1960.0, 2400.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';
-- Precio pagado igual independiente de escala — lo fija el mercado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 8.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_ROA';
-- Activos relativamente altos vs ingresos pequeños → ROA bajo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.25, 0.55, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2100.0, 1700.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F1][F7]: sin economías de escala el costo/litro
-- es el más alto — estimado $2.000-$2.200/L para pequeños en frío

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 9800000.0, 23000000.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 5000 L × $1.960 = $9.800.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 600000.0, 2500000.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_IOFC';
-- Menor ingreso y concentrado caro en frío → IOFC ajustado para pequeños

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 35.0, 22.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Pequeño frío: mano de obra familiar pero pesa más
-- como % del ingreso total por bajo volumen

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2100.0, 1700.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. Igual que costo litro — pequeño tiene breakeven más alto

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.15, 0.08, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Trabajo familiar = 1 persona para todo el hato
-- en pocas hectáreas → ratio alto

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 60.0, 140.0, 2024, 'FRIO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';
-- Menor volumen total → menos litros por empleado


-- ============================================================
-- ESCALA: MEDIANA — Trópico FRIO (26-200 animales)
-- Empresa familiar. Comienza a aprovechar economías de escala.
-- Fuente [F1]: promedio sector norte Antioquia = 16.46 L/vaca/día.
-- Puede acceder a tecnología básica y mejorar genética.
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 12.0, 20.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F1]: promedio norte Antioquia 16.46 L. Fuente [F3]: 15-18 L Cundinamarca

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8000.0, 16000.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 21.9, 43.8, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 8000/365=21.9, 16000/365=43.8

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 70.0, 90.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.5, 4.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Fuente [F5]: lecherías frío medias: 2.44-3.82 vacas/ha

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 60.0, 75.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -12.0, 20.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Mejor manejo reproductivo que pequeño

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.0, 18.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F1]: margen positivo pero ajustado en contexto crisis 2024

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 26.0, 46.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.04, 1.38, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5500000.0, 12000000.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.960 × 12 L/día × 305 días = ~$7.178.000 como referencia

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1960.0, 2400.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.5, 12.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.75, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1950.0, 1550.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F1]: economías de escala reducen costo vs pequeño

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 15700000.0, 35000000.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 8000 L × $1.960 = $15.680.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1000000.0, 3500000.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_IOFC';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 28.0, 17.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Empieza a distribuir costo laboral entre más vacas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1950.0, 1550.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.10, 0.05, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Mejor relación personal/hectárea que pequeño

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 100.0, 240.0, 2024, 'FRIO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: GRANDE — Trópico FRIO (201-500 animales)
-- Empresa ganadera establecida. Economías de escala
-- significativas. Puede sostener técnicos de planta.
-- Fuente [F3]: Antioquia fincas grandes: 30-35 L/vaca/día
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 16.0, 28.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F3]: Antioquia grandes: 30-35 L/día. [F1]: promedio sector 16.46 L.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 10000.0, 20000.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 27.4, 54.8, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 10000/365=27.4, 20000/365=54.8

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 72.0, 92.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.0, 4.5, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 63.0, 78.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -8.0, 25.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Manejo reproductivo profesional → más cerca del estándar

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6.0, 20.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F1]: mayores economías de escala mejoran el margen vs crisis

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 28.0, 50.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.06, 1.43, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7500000.0, 16000000.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.960 × 16 L/día × 305 días = ~$9.564.800

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1960.0, 2400.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5.5, 14.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.40, 0.82, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1850.0, 1450.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Economías de escala reducen costo vs mediano

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 19600000.0, 42000000.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 10000 L × $1.960 = $19.600.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1400000.0, 4500000.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_IOFC';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 25.0, 15.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Distribuye nómina entre más vacas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1850.0, 1450.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.09, 0.045, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Mayor tecnificación mejora este ratio

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 140.0, 320.0, 2024, 'FRIO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: EMPRESARIAL — Trópico FRIO (>500 animales)
-- Agroindustria ganadera. Máximas economías de escala.
-- Tecnología de punta. Nutricionistas y veterinarios de planta.
-- Fuente [F2]: fincas top Valle del Cauca: 3500-5800 L/día totales
-- Fuente [F3]: mejores Antioquia: 30-35 L/vaca/día
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 20.0, 35.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F3]: mejores Antioquia 30-35 L. Promedio empresarial ~20 L.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 12000.0, 25000.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 32.9, 68.5, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 12000/365=32.9, 25000/365=68.5

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 75.0, 92.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.5, 5.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Intensificación máxima con manejo de praderas tecnificado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 68.0, 82.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Mayor control reproductivo → más vacas productivas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -5.0, 30.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- IATF y manejo reproductivo formal → lactancias más largas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 3.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Top empresarial: algunos hacen 3 ordeños/día en vacas de alta producción

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8.0, 22.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Máximas economías de escala → mejor margen pese a crisis

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 30.0, 55.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.08, 1.50, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 9500000.0, 20000000.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.960 × 20 L/día × 305 días = ~$11.956.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1960.0, 2450.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';
-- Top: accede a todas las bonificaciones por calidad

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6.0, 16.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.45, 0.90, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1700.0, 1300.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F1]: máximas economías de escala
-- reducen costo unitario significativamente

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 23500000.0, 55000000.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 12000 L × $1.960 = $23.520.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2000000.0, 6000000.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_IOFC';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 20.0, 12.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Mecanización y escala reducen este % al mínimo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1700.0, 1300.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. El más bajo de todas las escalas en frío

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.07, 0.03, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Alta mecanización → mínimo personal por hectárea

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 200.0, 450.0, 2024, 'FRIO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';
-- Mecanización → mayor producción por empleado

-- PARTE 2 
-- ============================================================
-- FASE 2.3 PARTE B — BENCHMARKREFERENCIA TRÓPICO TEMPLADO + ESCALA
-- Hathor — 22 KPIs × 4 escalas = 88 registros
-- Trópico: TEMPLADO (1000-2000 msnm aprox.)
-- Regiones representativas: Eje Cafetero (Caldas, Quindío,
--   Risaralda), Valle del Cauca, Santander, sur de Antioquia
--
-- Fuentes:
--   [F1] UPRA — Ficha costos lechería especializada Valle del Cauca 2024:
--        upra.gov.co/sites/default/files/2025-03/01_FCLBovValle.pdf
--        Costo $1.607/L, producción 11.54 L/vaca/día, 32 vacas ordeño,
--        7.607 L/ha/año, carga 2.24 UGG/ha — hatos MEDIANOS
--   [F2] Scielo Valle del Cauca 2018 — lechería especializada:
--        scielo.org.co/scielo.php?pid=S0120-29522018000300252
--        Promedio 133 animales, 7.965 L/ha/año, <50 vacas dificultan costos fijos
--   [F3] CONtexto Ganadero 7 regiones — Eje Cafetero y Valle:
--        contextoganadero.com/ganaderia-sostenible/conozca-la-produccion
--        Minifundio: 4-5 L/día; zonas altas organizadas: 12-15 L/día
--        Valle del Cauca promedio: 13-16 L/día con cruzados bos taurus
--   [F4] FEDEGAN — Alto costo pequeños vs grandes 2024:
--        estadisticas.fedegan.org.co/DOC/download.jsp?iIdFiles=1069
--   [F5] Infortambo Andina — comparativa frío vs cálido (referencia templado):
--        infortamboandina.co/es/noticias/rentabilidad-en-las-lecherias
--   [F6] Agronegocios.co / Analac — costos trópico medio 2024 $1.650-$1.900/L:
--        agronegocios.co/agricultura/quienes-pagan-el-verdadero-precio
--
-- DATO ANCLA (Fuente [F1] — UPRA 2024):
--   Hato mediano Valle del Cauca (32 vacas ordeño):
--   Producción: 11.54 L/vaca/día | Costo: $1.607/L | 7.607 L/ha/año
--   Estos valores son el referente más preciso disponible para
--   escala MEDIANA en trópico TEMPLADO del año 2024.
--
-- ESCALAS EN HATHOR:
--   PEQUEÑA:      < 25 animales
--   MEDIANA:      26-200
--   GRANDE:       201-500
--   EMPRESARIAL:  > 500
-- ============================================================


-- ============================================================
-- ESCALA: PEQUEÑA — Trópico TEMPLADO (<25 animales)
-- Ganadero minifundista. Eje Cafetero y valles templados.
-- Fuente [F3]: minifundios Eje Cafetero 4-5 L/vaca/día.
-- Valle del Cauca pequeño: cruzados con producción modesta.
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5.0, 11.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F3]: minifundio Eje Cafetero 4-5 L. Top = bien manejado en zona alta.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4500.0, 9000.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Menor inversión en praderas y gestión → menor producción por ha

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 12.3, 24.7, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 4500/365=12.3, 9000/365=24.7

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 62.0, 83.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.8, 3.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Fuente [F1]: mediano Valle 2.24 UGG/ha. Pequeño < 2 UGG/ha típico.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 53.0, 68.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.36, 0.43, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -25.0, 10.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Fuente [F1]: intervalo entre partos 386 días → lactancia ~261 días (-44 vs estándar)
-- Pequeños sin control reproductivo → peor que mediano UPRA

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.5, 2.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Templado pequeño: mezcla de 1 y 2 ordeños

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 14.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F2][F4]: pequeños templado con dificultades para absorber costos fijos

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 22.0, 42.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.00, 1.28, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';
-- Punto de equilibrio crítico para pequeño templado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2700000.0, 8000000.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.870 × 5 L/día × 305 días = ~$2.851.750

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1870.0, 2300.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';
-- Precio templado promedio 2024

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 8.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.28, 0.58, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1950.0, 1550.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Sin economías de escala → costo más alto
-- Fuente [F6]: trópico medio costo $1.650-$1.900/L. Pequeño en el techo.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8400000.0, 18000000.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 4500 L × $1.870 = $8.415.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 500000.0, 2200000.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_IOFC';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 32.0, 20.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Fuente [F1]: mano de obra pesa 28.86% del costo/litro
-- en mediano. Pequeño con trabajo familiar sube como % del ingreso total.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1950.0, 1550.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. Mismo valor que costo/litro para pequeño

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.14, 0.07, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Trabajo familiar en pocas hectáreas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 55.0, 130.0, 2024, 'TEMPLADO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: MEDIANA — Trópico TEMPLADO (26-200 animales)
-- Empresa familiar tecnificada. DATO ANCLA UPRA 2024:
-- 32 vacas ordeño, 11.54 L/vaca/día, $1.607/L, 7.607 L/ha/año
-- Valle del Cauca promedio: 13-16 L/vaca/día (bos taurus cruzados)
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 11.5, 17.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F1]: UPRA 2024 → 11.54 L/vaca/día hatos medianos Valle del Cauca
-- Fuente [F3]: Valle promedio 13-16 L con cruzados. Promedio ajustado.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7607.0, 14000.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Fuente [F1]: UPRA 2024 → 7.607,55 L/ha/año exacto para mediano Valle del Cauca

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 20.8, 38.4, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 7607/365=20.8, 14000/365=38.4

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 70.0, 90.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.24, 4.5, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Fuente [F1]: UPRA 2024 → 2.24 UGG/ha exacto para mediano Valle del Cauca

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 58.0, 74.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Fuente [F1]: 32 vacas ordeño / 50 total (32+18 secas) = 64%

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -21.0, 12.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Fuente [F1]: UPRA 2024 → intervalo partos 386 días. Lactancia ~265 días → -40 días
-- vs estándar 305. Valor promedio ajustado considerando mejores hatos = -21 días.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Fuente [F1]: ordeño mecánico 2 veces/día en lechería especializada

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7.0, 20.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F1]: costo $1.607/L vs precio ~$1.870 → margen ~14% bruto
-- Incluyendo costos indirectos completos: margen neto ~7-8%

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 28.0, 50.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';
-- Fuente [F1]: (1870-1607)/1870 = 14% margen directo. Incluyendo todos = ~28%

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.07, 1.40, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4100000.0, 10000000.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.870 × 11.5 L/día × 305 días = ~$6.559.000 promedio → ajustado con todos costos

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1870.0, 2300.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5.0, 13.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.40, 0.80, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1608.0, 1250.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F1]: UPRA 2024 → $1.607,85/L exacto para mediano
-- Top = hatos muy eficientes del mismo trópico con mejor genética y manejo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 14200000.0, 30000000.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- Fuente [F1]: 7607 L/ha/año × $1.870 = $14.225.090

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 900000.0, 3000000.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_IOFC';
-- Fuente [F1]: alimentación = $470/L + $117 praderas = $587/L de los $1.607 totales

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 27.0, 16.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Fuente [F1]: mano de obra $464/L de $1.607 total = 28.86%
-- Como % del ingreso: $464/$1.870 = 24.8% → redondeado a 27% incluyendo indirectos

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1608.0, 1250.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. Igual al costo/litro UPRA 2024

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.11, 0.055, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 88.0, 200.0, 2024, 'TEMPLADO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: GRANDE — Trópico TEMPLADO (201-500 animales)
-- Empresa ganadera. Supera el promedio UPRA (32 vacas).
-- Fuente [F2]: fincas Valle del Cauca grandes: hasta 502 vacas adultas.
-- Fuente [F3]: Valle del Cauca organizado: 13-16 L/vaca/día.
-- Con escala → economías que reducen costo vs $1.607/L del mediano.
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 14.0, 22.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F3]: Valle bien manejado 13-16 L. Grande con mejor genética alcanza más.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 9500.0, 18000.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Fuente [F2]: top 15% Valle del Cauca > 15.000 L/ha/año

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 26.0, 49.3, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 9500/365=26.0, 18000/365=49.3

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 72.0, 91.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.0, 5.2, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Mayor inversión en praderas → mayor carga sostenible vs mediano UPRA (2.24)

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 62.0, 76.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -15.0, 15.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Mejor manejo reproductivo que mediano UPRA (que tiene -21 días promedio)

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 9.0, 22.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Economías de escala mejoran margen vs mediano

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 30.0, 52.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.09, 1.44, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6100000.0, 12500000.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.870 × 14 L/día × 305 días = ~$7.988.300

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1870.0, 2320.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5.5, 14.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.42, 0.85, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1480.0, 1150.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Mejor que mediano UPRA ($1.607) por economías de escala

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 17800000.0, 38000000.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 9500 L × $1.870 = $17.765.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1300000.0, 4200000.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_IOFC';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 24.0, 14.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Economías de escala distribuyen costo laboral

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1480.0, 1150.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.09, 0.045, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 120.0, 280.0, 2024, 'TEMPLADO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: EMPRESARIAL — Trópico TEMPLADO (>500 animales)
-- Agroindustria. Fuente [F2]: fincas top Valle del Cauca
-- producen 3.500-5.800 L/día totales por finca.
-- Con >500 animales y 77% en ordeño = ~385 vacas productivas.
-- Producción estimada: 385 × ~15 L = ~5.775 L/día por finca.
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 16.0, 28.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F2]: fincas grandes Valle del Cauca: producción total 3.500-5.800 L/día
-- Con 250-400 vacas en ordeño → ~14-18 L/vaca/día

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 12000.0, 22000.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Fuente [F2]: top 15% Valle > 15.000 L/ha/año en lechería especializada

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 32.9, 60.3, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 12000/365=32.9, 22000/365=60.3

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 75.0, 92.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.8, 5.8, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Intensificación máxima con pasto estrella/guinea tecnificado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 66.0, 80.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.38, 0.45, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -10.0, 20.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- IATF y veterinario de planta → mejor manejo reproductivo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 3.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Top empresarial puede hacer 3 ordeños en vacas de pico

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 11.0, 25.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 32.0, 56.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.11, 1.52, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 7200000.0, 16000000.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.870 × 16 L/día × 305 días = ~$9.124.800

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1870.0, 2350.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';
-- Acceso a todas las bonificaciones por volumen y calidad

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6.5, 16.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_ROA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.48, 0.92, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1280.0, 980.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Máximas economías de escala — muy por debajo del mediano ($1.607)

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 22440000.0, 46000000.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 12000 L × $1.870 = $22.440.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800000.0, 5500000.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_IOFC';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 20.0, 11.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Mecanización y escala reducen al mínimo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1280.0, 980.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.07, 0.03, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Salas de ordeño automatizadas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 170.0, 400.0, 2024, 'TEMPLADO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';

-- PARTE 3:
-- ============================================================
-- FASE 2.3 PARTE C — BENCHMARKREFERENCIA TRÓPICO CALIDO + ESCALA
-- Hathor — 22 KPIs × 4 escalas = 88 registros
-- Trópico: CALIDO (<1000 msnm aprox.)
-- Regiones: Costa Caribe, Llanos Orientales, Magdalena Medio,
--   Bajo Cauca, Caquetá, Meta, Cesar, Córdoba, Tolima, Huila
--
-- Fuentes:
--   [F1] Genética Bovina Colombiana — lechería trópico bajo:
--        revistageneticabovina.com/biotecnologia/tropico-bajo/
--        Proyectos empresariales: >20 L/vaca/día, 300-1.000 vacas ordeño
--   [F2] EDairyNews / Portal Lechero — Girolando trópico bajo:
--        edairynews.com/girolando-las-vacas-mas-apetecidas-para-producir-leche
--        3.500 kg/305 días = ~11.5 L/día promedio, picos 18 L
--   [F3] Infortambo Andina — comparativa frío vs cálido:
--        infortamboandina.co/es/noticias/rentabilidad-en-las-lecherias
--        Cálido: 12.5-17.5 L/día (mejores lecherías), 4.75-5.7 vacas/ha
--        Rentabilidad cálido SUPERIOR a frío incluyendo venta terneros
--   [F4] CONtexto Ganadero 7 regiones — cebú cálido:
--        contextoganadero.com/ganaderia-sostenible/conozca-la-produccion
--        Cebú puro: 2.5-3.5 L/día. Con manejo eficiente: hasta 5 L.
--   [F5] ANALAC / Asocebú — ventajas trópico bajo:
--        analac.org/2022/05/17/lecheria-colombiana-en-el-tropico-bajo
--        Tierra barata, mano de obra disponible, subproductos cosechas
--   [F6] CONtexto Ganadero — estructura costos trópico bajo:
--        contextoganadero.com/reportaje/cual-es-la-estructura-de-costos
--        Concentrados 30-40%, mano de obra 20%, praderas 10-15%
--   [F7] FEDEGAN — costos 2024: precio pagado $1.745/L trópico bajo julio 2024
--        agronegocios.co/agricultura/como-se-compone-el-alto-costo
--   [F8] Scielo Magangué — doble propósito cálido punto de equilibrio:
--        scielo.org.co/scielo.php?pid=S0122-02682006000200005
--        60 L/día como punto de equilibrio (hato pequeño/mediano cálido)
--
-- PARTICULARIDAD DEL TRÓPICO CALIDO:
--   A diferencia del frío y templado, en cálido la escala EMPRESARIAL
--   es donde están los proyectos más innovadores y rentables del país.
--   El PEQUEÑO cálido (doble propósito extensivo) es el sistema menos
--   productivo por vaca pero puede ser rentable por bajo costo de tierra
--   e insumos locales. El GRANDE/EMPRESARIAL cálido con Girolando y
--   salas automatizadas está superando en rentabilidad al frío.
-- ============================================================


-- ============================================================
-- ESCALA: PEQUEÑA — Trópico CALIDO (<25 animales)
-- Ganadero familiar/tradicional. Doble propósito extensivo.
-- Cebú y cruces básicos. 1 ordeño/día con ternero al pie.
-- Fuente [F4]: cebú puro 2.5-3.5 L/día, con manejo hasta 5 L
-- Fuente [F8]: punto de equilibrio ~60 L/día total (1-2 vacas!)
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.0, 6.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F4]: cebú 2.5-3.5 L/día. Con cruzamiento básico hasta 6 L.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3500.0, 7000.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Extensivo cálido: baja densidad, pastos tropicales sin manejo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 9.6, 19.2, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 3500/365=9.6, 7000/365=19.2

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 55.0, 78.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';
-- Pequeño cálido: infraestructura básica, frecuentemente subutiliza

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.5, 3.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Extensivo sin rotación → carga baja. Fuente [F3]: cálido intensivo llega a 5.7/ha

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 40.0, 58.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Doble propósito con ternero: menor % en ordeño que especializados

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.32, 0.40, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';
-- Doble propósito: retiene machos también → menor ratio hembras recría

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -60.0, -15.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Doble propósito extensivo: lactancias más cortas por destete temprano
-- y manejo con ternero. Pequeño sin control reproductivo formal.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.0, 1.5, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Doble propósito pequeño: mayoritariamente 1 ordeño/día

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5.0, 16.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F3]: rentabilidad cálido puede ser superior a frío
-- Pequeño cálido: costo tierra bajo, sin concentrado caro → margen positivo
-- aunque bajo. Mejor que pequeño frío por menores costos fijos.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 25.0, 48.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';
-- Fuente [F6]: estructura costos cálido: concentrados 30-40%, mano obra 20%
-- Sin concentrado caro → margen bruto mejor que pequeño frío

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.05, 1.35, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1600000.0, 5500000.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.800 × 3 L/día × 305 días = ~$1.647.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 2100.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';
-- Fuente [F7]: $1.745/L trópico bajo julio 2024. Promedio anual ~$1.800.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.0, 12.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_ROA';
-- Tierra barata → activos menores → mejor ROA potencial vs pequeño frío

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.35, 0.70, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1700.0, 1200.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F6]: sin concentrado caro, tierra barata
-- Pequeño cálido: costo ~$1.500-$1.800/L. Mejor que pequeño frío ($2.100).

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6300000.0, 14500000.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 3500 L × $1.800 = $6.300.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 250000.0, 1000000.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_IOFC';
-- Bajo ingreso por leche pero también bajo costo alimentación
-- Fuente [F5]: subproductos cosechas como suplemento → reduce costo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 20.0, 12.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Fuente [F6]: mano de obra ~20% costos en cálido
-- Trabajo familiar → menor carga laboral formal

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1700.0, 1200.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. Igual al costo/litro pequeño cálido

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.14, 0.07, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Sistema extensivo → pocos empleados por ha

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 40.0, 100.0, 2024, 'CALIDO', NULL, 'PEQUEÑA'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';
-- Menor producción/vaca → menos litros por empleado


-- ============================================================
-- ESCALA: MEDIANA — Trópico CALIDO (26-200 animales)
-- Sistema mixto o especializado incipiente. Girolando F1.
-- Fuente [F2]: Girolando mediano: ~11.5 L/vaca/día promedio.
-- Fuente [F8]: 60 L/día total = punto equilibrio (doble propósito)
-- Fuente [F3]: lecherías cálidas medianas: 12.5-17.5 L/vaca/día
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8.0, 16.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F2]: Girolando 3.500 kg/305 días = 11.5 L/día.
-- Fuente [F3]: lecherías cálidas medianas: 12.5-17.5 L. Promedio = 8 incluyendo todos.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6000.0, 12000.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Fuente [F3]: cálido intensivo 4.75 vacas/ha × 8 L/día × 365 = ~13.870 L/ha/año (top)
-- Promedio incluyendo medianos no especializados = ~6.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 16.4, 32.9, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 6000/365=16.4, 12000/365=32.9

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 65.0, 88.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3.0, 5.5, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Fuente [F3]: cálido intensivo 4.75-5.7 vacas/ha. Mediano = 3.0 promedio.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 48.0, 65.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Sistemas mixtos: menor que especializado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.34, 0.42, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -45.0, -10.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Doble propósito: lactancias más cortas. Fuente [F3]: vacas cruzadas longevas
-- pero lactancias ≠ 305 días estándar. Mediano con algo de manejo: mejora.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.5, 2.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Mediano: transición entre 1 y 2 ordeños. Top = 2 ordeños sin ternero.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8.0, 20.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F3]: rentabilidad cálido superior a frío incluyendo venta terneros
-- Menor costo insumos compensando menor precio por litro

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 30.0, 52.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.08, 1.42, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 3000000.0, 9000000.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.800 × 8 L/día × 305 días = ~$4.392.000. + ingreso ternero macho.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 2100.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 6.0, 14.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_ROA';
-- Fuente [F5]: tierra barata → activos menores → mejor ROA

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.45, 0.88, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1500.0, 1050.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F6]: sin concentrado caro = costo más bajo que frío/templado
-- Mediano cálido: ~$1.300-$1.600/L estimado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 10800000.0, 24000000.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 6000 L × $1.800 = $10.800.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 500000.0, 1800000.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_IOFC';
-- Fuente [F5]: subproductos cosechas (palmiste, maíz, semilla algodón)
-- reducen costo alimentación → mejor IOFC que frío con concentrado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 22.0, 13.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Fuente [F6]: mano de obra 20% costos cálido

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1500.0, 1050.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. Igual costo/litro mediano cálido

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.12, 0.06, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Extensivo-semintensivo cálido

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 65.0, 160.0, 2024, 'CALIDO', NULL, 'MEDIANA'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: GRANDE — Trópico CALIDO (201-500 animales)
-- Empresa especializada. Girolando sin ternero, 2 ordeños.
-- Fuente [F3]: lecherías cálidas grandes: 12.5-17.5 L/vaca/día
-- Fuente [F1]: hatos 300+ vacas en trópico bajo Colombia
-- Mayor densidad: 4.75-5.7 vacas/ha con pasto estrella
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 13.0, 20.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F3]: mejores lecherías cálidas 12.5-17.5 L. Grande = más genética.
-- Fuente [F2]: Girolando picos 18 L. Promedio grande: 13-15 L.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8500.0, 16000.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Fuente [F3]: 5.0 vacas/ha × 13 L/día × 365 = ~23.725 L/ha/año (top intensivo)
-- Promedio grande con gestión: ~8.500 L/ha/año

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 23.3, 43.8, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 8500/365=23.3, 16000/365=43.8

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 68.0, 90.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.0, 5.7, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Fuente [F3]: cálido intensivo 4.75-5.7 vacas/ha con estrella africana

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 56.0, 70.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Sistema especializado sin ternero → mayor % en ordeño que mediano cálido

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.36, 0.43, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -30.0, -5.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Con control reproductivo: mejora vs mediano/pequeño cálido
-- Fuente [F3]: vacas cruzadas longevas (>9 partos) → mejora acumulada

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 2.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Sistema especializado sin ternero → 2 ordeños

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 12.0, 24.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F3]: rentabilidad cálido supera frío incluyendo venta terneros machos
-- Grande cálido con gestión profesional: margen 12-24%

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 35.0, 58.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.12, 1.48, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 5600000.0, 12000000.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.800 × 13 L/día × 305 días = ~$7.137.000 (solo leche)
-- + ingreso ternero macho Girolando para ceba

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 2100.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8.0, 18.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_ROA';
-- Fuente [F5]: tierra barata + alta productividad = mejor ROA que frío grande

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.52, 0.95, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1350.0, 950.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F3]: producen más barato que frío incluso con más insumos
-- Grande cálido con escala: economías + pastos baratos = ~$1.200-$1.500/L

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 15300000.0, 32000000.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 8500 L × $1.800 = $15.300.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1200000.0, 3500000.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_IOFC';
-- Fuente [F5]: subproductos locales baratos → IOFC competitivo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 18.0, 11.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Fuente [F6]: mano de obra 20% costos cálido
-- Grande distribuye entre más animales

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1350.0, 950.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.10, 0.05, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Mayor área disponible en cálido → mejor ratio

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 95.0, 220.0, 2024, 'CALIDO', NULL, 'GRANDE'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- ESCALA: EMPRESARIAL — Trópico CALIDO (>500 animales)
-- Agroindustria. Los proyectos más tecnificados de Colombia
-- están en trópico bajo. Fuente [F1]: salas automatizadas
-- 300-1.000+ vacas ordeño, >20 L/vaca/día. Meta, Caquetá,
-- Cesar, Magdalena Medio. Referente mundial en costo/eficiencia.
-- ============================================================

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 18.0, 28.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';
-- Fuente [F1]: promedios >20 L en hatos empresariales trópico bajo
-- Fuente [F2]: Girolando casos excepcionales hasta 40 L. Promedio empresarial ~18-22 L.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 12000.0, 24000.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';
-- Fuente [F3]: cálido intensivo top: 5.7 vacas/ha × 18 L/día × 365 = ~37.503 L/ha/año
-- Promedio empresarial con toda el área: ~12.000-15.000 L/ha/año

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 32.9, 65.8, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';
-- Derivado: 12000/365=32.9, 24000/365=65.8

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 75.0, 92.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 4.5, 6.5, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';
-- Fuente [F3]: techo observado cálido intensivo = 5.7 vacas/ha
-- Empresarial con manejo agronómico intensivo supera ese techo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 65.0, 80.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';
-- Sistema especializado sin ternero, control reproductivo IATF

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.36, 0.44, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, -20.0, 0.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';
-- Fuente [F3]: vacas Girolando longevas con buen manejo reproductivo
-- Empresarial con IATF: intervalo partos más controlado

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2.0, 3.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';
-- Fuente [F1]: salas automatizadas — algunos hacen 3 ordeños en vacas pico

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 15.0, 28.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';
-- Fuente [F3]: rentabilidad cálido puede superar frío.
-- Empresarial cálido: mayor volumen, menor costo tierra/insumos = mejor margen

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 38.0, 62.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1.15, 1.58, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.0, 1.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 8000000.0, 16500000.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';
-- $1.800 × 18 L/día × 305 días = ~$9.882.000 (solo leche)
-- + ingreso ternero Girolando macho para ceba = valor adicional significativo

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1800.0, 2150.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';
-- Acceso a bonificaciones por volumen y calidad composicional

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 10.0, 22.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_ROA';
-- Fuente [F5]: tierra barata + alta escala = mejor ROA del sector en Colombia

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.58, 1.10, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';
-- Mayor ingreso por activo barato = mayor rotación. Puede superar 1.0 en los mejores.

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1100.0, 750.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';
-- MENOR ES MEJOR. Fuente [F1][F3]: los proyectos más eficientes de Colombia
-- están en trópico bajo. Pastos baratos + economías escala = costo mínimo.
-- Estimado empresarial cálido: $900-$1.200/L

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 21600000.0, 48000000.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_INGRESO_HA_ANIO';
-- 12000 L × $1.800 = $21.600.000

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 2500000.0, 7000000.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_IOFC';
-- Fuente [F5]: subproductos locales baratos + alta producción = IOFC máximo del sector

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 15.0, 9.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';
-- MENOR ES MEJOR. Fuente [F1]: salas automatizadas 80 unidades
-- Alta mecanización + gran volumen = costo laboral % mínimo del sector

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 1100.0, 750.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';
-- MENOR ES MEJOR. El más bajo de todos los trópicos y escalas

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 0.07, 0.025, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';
-- MENOR ES MEJOR. Fuente [F1]: salas automatizadas + gran área disponible
-- = ratio empleados/ha más bajo del sector ganadero colombiano

INSERT INTO benchmarkreferencia (id_kpi, region, valor_promedio, valor_top, anio, tropico, sistema_ordenio, escala)
SELECT id_kpi, NULL, 220.0, 550.0, 2024, 'CALIDO', NULL, 'EMPRESARIAL'
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';
-- Fuente [F1]: alta producción + mecanización = máximo litros/empleado del sector