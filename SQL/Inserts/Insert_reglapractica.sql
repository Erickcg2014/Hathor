-- ============================================================
-- FASE 5 — VINCULACIÓN REGLAS → PRÁCTICAS
-- tabla: regla_practica (id_regla, id_practica, orden)
--
-- CRITERIOS DE ASIGNACIÓN:
--   Regla CRÍTICO   → 2-3 prácticas: primero la de mayor impacto
--                     directo y menor inversión, luego las de
--                     refuerzo estructural.
--   Regla ACEPTABLE → 1-2 prácticas: optimización incremental.
--
-- REFERENCIAS CRUZADAS:
--   Las prácticas tienen múltiples variantes por escala
--   (mismo nombre, diferente escala). Al vincular se usa el
--   nombre + escala='TODAS' como fallback cuando aplica a
--   todo el hato, o la variante específica cuando la regla
--   tiene escala_aplicable diferente de 'TODAS'.
--
--   Para reglas con escala_aplicable='TODAS' se vinculan las
--   prácticas con escala='TODAS' o las versiones base que
--   el motor después filtra por la escala real del hato.
--   Dado que el catálogo tiene prácticas con escala='TODAS'
--   y variantes por escala, aquí se vincula la versión más
--   representativa — el motor de recomendaciones puede
--   refinar en runtime si se implementa ese filtro adicional.
--
-- IDENTIFICACIÓN DE PRÁCTICAS:
--   Se usan subqueries por nombre + escala + kpi_impactado
--   para ser robustos ante cualquier orden de inserción.
--   Cuando una práctica existe solo en versión 'TODAS',
--   se usa esa. Cuando existe en múltiples escalas,
--   se vincula la versión 'PEQUEÑA' como proxy base
--   (la más básica y universal).
-- ============================================================

-- ============================================================
-- HELPERS: CTEs de referencia para reglas y prácticas
-- Se usan inline en cada INSERT para claridad.
-- ============================================================


-- ============================================================
-- BLOQUE 1: REGLAS DE PRODUCTIVIDAD
-- ============================================================

-- -----------------------------------------------------------
-- R01 — KPI_LITROS_VACA_DIA CRÍTICO (<70% promedio)
-- Problema: producción muy baja por vaca.
-- Causa más frecuente: ordeño deficiente + mala nutrición.
-- P1: Protocolo de ordeño higiénico (impacto directo, bajo costo)
-- P2: Nutrición por etapa productiva (ataca la raíz nutricional)
-- P3: Optimización de frecuencia de ordeño (mejora inmediata)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Protocolo de ordeño higiénico'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Nutrición por etapa productiva'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Optimización de frecuencia de ordeño'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R02 — KPI_LITROS_VACA_DIA ACEPTABLE (70-99% promedio)
-- Problema: producción mejorable pero no crítica.
-- P1: Suplementación estratégica en época seca
-- P2: Manejo del período seco de la vaca
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Suplementación estratégica en época seca'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Manejo del período seco de la vaca'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R03 — KPI_LITROS_HA_ANIO CRÍTICO (<65% promedio)
-- Problema: uso del suelo muy ineficiente.
-- P1: Pastoreo rotacional intensivo (impacto directo en ha)
-- P2: Manejo de carga animal y capacidad de pastoreo
-- P3: Banco de proteína forrajera (aumenta producción/ha en cálido)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Manejo de carga animal y capacidad de pastoreo'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Banco de proteína forrajera'
  AND p.escala = 'TODAS'
  AND p.tropico_aplicable = 'CALIDO';


-- -----------------------------------------------------------
-- R04 — KPI_LITROS_HA_ANIO ACEPTABLE (65-89% promedio)
-- P1: Pastoreo rotacional intensivo (versión base)
-- P2: Programa de mejoramiento genético
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Programa de mejoramiento genético'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R05 — KPI_PRODUCCION_HA_DIA CRÍTICO (<70% promedio)
-- Problema: densidad productiva diaria muy baja.
-- P1: Pastoreo rotacional intensivo
-- P2: Manejo de carga animal
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Manejo de carga animal y capacidad de pastoreo'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R06 — KPI_PRODUCCION_HA_DIA ACEPTABLE
-- P1: Pastoreo rotacional intensivo
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R07 — KPI_CAP_ALMAC_UTILIZADA CRÍTICO ALTO (>90%)
-- Problema: capacidad casi llena — riesgo pérdida de leche.
-- P1: Planificación financiera mensual (coordinar acopio)
-- P2: Trazabilidad animal básica (visibilidad de producción)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.umbral_1 = 90.0
  AND r.operador = 'MAYOR_QUE'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.umbral_1 = 90.0
  AND r.operador = 'MAYOR_QUE'
  AND p.nombre = 'Trazabilidad animal básica'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R08 — KPI_CAP_ALMAC_UTILIZADA CRÍTICO BAJO (<40%)
-- Problema: infraestructura ociosa → posible caída de producción.
-- P1: Implementación de registros digitales (detectar la causa)
-- P2: Plan reproductivo con detección de celos (recuperar vacas)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.umbral_1 = 40.0
  AND r.operador = 'MENOR_QUE'
  AND p.nombre = 'Implementación de registros digitales'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.umbral_1 = 40.0
  AND r.operador = 'MENOR_QUE'
  AND p.nombre = 'Plan reproductivo con detección de celos'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R09 — KPI_CAP_ALMAC_UTILIZADA ACEPTABLE (40-69%)
-- P1: Nutrición por etapa productiva (aumentar producción)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Nutrición por etapa productiva'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R10 — KPI_LACTANCIA_VS_ESTANDAR CRÍTICO (<-30 días)
-- Problema: lactancias muy cortas — pérdida de producción.
-- P1: Manejo del período seco (causa directa)
-- P2: Plan reproductivo con detección de celos (IEP largo)
-- P3: Nutrición por etapa productiva (condición corporal)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Manejo del período seco de la vaca'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Plan reproductivo con detección de celos'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Nutrición por etapa productiva'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R11 — KPI_LACTANCIA_VS_ESTANDAR ACEPTABLE (-30 a -1 días)
-- P1: Manejo del período seco
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Manejo del período seco de la vaca'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R12 — KPI_FRECUENCIA_ORDENIO CRÍTICO (<2 ordeños/día)
-- Problema: solo 1 ordeño diario — pérdida 15-25% producción.
-- P1: Optimización de frecuencia de ordeño (acción directa)
-- P2: Evaluación de mecanización del ordeño (facilita 2do ordeño)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Optimización de frecuencia de ordeño'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Evaluación de mecanización del ordeño'
  AND p.escala = 'PEQUEÑA';


-- ============================================================
-- BLOQUE 2: REGLAS DE HATO
-- ============================================================

-- -----------------------------------------------------------
-- R13 — KPI_CARGA_ANIMAL CRÍTICO ALTO (>120% top)
-- Problema: sobrepastoreo — riesgo de degradación de praderas.
-- P1: Pastoreo rotacional intensivo (distribución de carga)
-- P2: Manejo de carga animal (ajuste técnico directo)
-- P3: Selección y descarte de animales improductivos (reducir carga)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MAYOR_PCT_TOP'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MAYOR_PCT_TOP'
  AND p.nombre = 'Manejo de carga animal y capacidad de pastoreo'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MAYOR_PCT_TOP'
  AND p.nombre = 'Selección y descarte de animales improductivos'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R14 — KPI_CARGA_ANIMAL CRÍTICO BAJO (<50% promedio)
-- Problema: subutilización del predio.
-- P1: Plan reproductivo con detección de celos (aumentar hato)
-- P2: Programa de mejoramiento genético (mejorar reposición)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MENOR_PCT_PROMEDIO'
  AND p.nombre = 'Plan reproductivo con detección de celos'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MENOR_PCT_PROMEDIO'
  AND p.nombre = 'Programa de mejoramiento genético'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R15 — KPI_CARGA_ANIMAL ACEPTABLE (105-120% top)
-- P1: Pastoreo rotacional intensivo (gestionar la carga)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R16 — KPI_PCT_VACAS_ORDENIO CRÍTICO (<70% promedio)
-- Problema: demasiados animales improductivos.
-- P1: Selección y descarte de animales improductivos
-- P2: Plan reproductivo con detección de celos
-- P3: Plan sanitario preventivo y control de mastitis
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Selección y descarte de animales improductivos'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Plan reproductivo con detección de celos'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Plan sanitario preventivo y control de mastitis'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R17 — KPI_PCT_VACAS_ORDENIO ACEPTABLE (70-90% promedio)
-- P1: Plan reproductivo con detección de celos
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Plan reproductivo con detección de celos'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R18 — KPI_HEMBRAS_RECRIA_VACA CRÍTICO BAJO (<0.25)
-- Problema: pocas hembras de reposición — descapitalización.
-- P1: Programa de mejoramiento genético (retener mejores terneras)
-- P2: Trazabilidad animal básica (identificar y registrar hembras)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MENOR_QUE'
  AND p.nombre = 'Programa de mejoramiento genético'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MENOR_QUE'
  AND p.nombre = 'Trazabilidad animal básica'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R19 — KPI_HEMBRAS_RECRIA_VACA CRÍTICO ALTO (>0.55)
-- Problema: exceso de hembras improductivas — presión económica.
-- P1: Selección y descarte de animales improductivos
-- P2: Planificación financiera mensual (evaluar flujo de caja)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MAYOR_QUE'
  AND p.nombre = 'Selección y descarte de animales improductivos'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.operador = 'MAYOR_QUE'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';


-- ============================================================
-- BLOQUE 3: REGLAS FINANCIERAS
-- ============================================================

-- -----------------------------------------------------------
-- R20 — KPI_MARGEN_NETO CRÍTICO (<0)
-- Problema: el hato pierde dinero — máxima urgencia.
-- P1: Análisis y reducción de costos de alimentación
-- P2: Planificación financiera mensual (visibilidad urgente)
-- P3: Negociación de precio de leche (mejorar ingreso)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_NETO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_NETO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_NETO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Negociación de precio de leche'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R21 — KPI_MARGEN_NETO ACEPTABLE (0-70% promedio)
-- P1: Análisis de punto de equilibrio
-- P2: Diversificación de ingresos del hato
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_NETO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Análisis de punto de equilibrio'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_NETO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Diversificación de ingresos del hato'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R22 — KPI_MARGEN_BRUTO_PCT CRÍTICO (<65% promedio)
-- P1: Análisis y reducción de costos de alimentación
-- P2: Análisis de punto de equilibrio
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis de punto de equilibrio'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R23 — KPI_MARGEN_BRUTO_PCT ACEPTABLE (65-90% promedio)
-- P1: Negociación de precio de leche
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Negociación de precio de leche'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R24 — KPI_RATIO_INGRESO_EGRESO CRÍTICO (<1.0)
-- Problema: egresos > ingresos.
-- P1: Planificación financiera mensual
-- P2: Análisis y reducción de costos de alimentación
-- P3: Análisis de punto de equilibrio
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis de punto de equilibrio'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R25 — KPI_RATIO_INGRESO_EGRESO ACEPTABLE (1.0-1.15)
-- P1: Planificación financiera mensual
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R26 — KPI_BALANCE_NETO CRÍTICO (<0 COP)
-- P1: Planificación financiera mensual
-- P2: Gestión de acceso a crédito y subsidios FINAGRO
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_BALANCE_NETO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_BALANCE_NETO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Gestión de acceso a crédito y subsidios FINAGRO'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R27 — KPI_COSTO_LITRO CRÍTICO (>120% promedio)
-- Problema: producir mucho más caro que el sector.
-- P1: Análisis y reducción de costos de alimentación
-- P2: Banco de proteína forrajera (cálido — sustituye concentrado)
-- P3: Análisis de punto de equilibrio
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Banco de proteína forrajera'
  AND p.escala = 'TODAS'
  AND p.tropico_aplicable = 'CALIDO';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis de punto de equilibrio'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R28 — KPI_COSTO_LITRO ACEPTABLE (100-120% promedio)
-- P1: Análisis y reducción de costos de alimentación
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LITRO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R29 — KPI_INGRESO_LITRO CRÍTICO (<80% promedio)
-- Problema: precio recibido muy bajo.
-- P1: Negociación de precio de leche (acción directa)
-- P2: Plan sanitario preventivo y control de mastitis (calidad)
-- P3: Protocolo de ordeño higiénico (mejorar RCS para bonificaciones)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Negociación de precio de leche'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Plan sanitario preventivo y control de mastitis'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Protocolo de ordeño higiénico'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R30 — KPI_IOFC CRÍTICO (<0 COP)
-- Problema: alimentación cuesta más que el ingreso por leche.
-- P1: Análisis y reducción de costos de alimentación
-- P2: Banco de proteína forrajera (sustituye concentrado caro)
-- P3: Suplementación estratégica en época seca (optimizar dosis)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_IOFC')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_IOFC')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Banco de proteína forrajera'
  AND p.escala = 'TODAS'
  AND p.tropico_aplicable = 'CALIDO';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_IOFC')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Suplementación estratégica en época seca'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R31 — KPI_IOFC ACEPTABLE (0-70% promedio)
-- P1: Suplementación estratégica en época seca
-- P2: Pastoreo rotacional intensivo (reducir dependencia concentrado)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_IOFC')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Suplementación estratégica en época seca'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_IOFC')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Pastoreo rotacional intensivo'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R32 — KPI_BREAKEVEN_LITRO CRÍTICO (≥ precio promedio trópico)
-- Problema: punto de equilibrio supera el precio de mercado.
-- P1: Análisis de punto de equilibrio (entender la brecha)
-- P2: Análisis y reducción de costos de alimentación
-- P3: Planificación financiera mensual (mapa de costos)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis de punto de equilibrio'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Análisis y reducción de costos de alimentación'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R33a/b/c — KPI_ROA CRÍTICO (MEDIANA, GRANDE, EMPRESARIAL)
-- Problema: activos no generan retorno suficiente.
-- P1: Análisis de punto de equilibrio
-- P2: Diversificación de ingresos del hato
-- -----------------------------------------------------------
-- Aplica para las tres reglas (una por escala) — mismas prácticas
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_ROA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('MEDIANA', 'GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Análisis de punto de equilibrio'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_ROA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('MEDIANA', 'GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Diversificación de ingresos del hato'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R34 — KPI_INGRESO_VACA CRÍTICO (<65% promedio)
-- Problema: ingreso por animal muy bajo.
-- P1: Protocolo de ordeño higiénico (aumentar producción + calidad)
-- P2: Negociación de precio de leche
-- P3: Selección y descarte de animales improductivos
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Protocolo de ordeño higiénico'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Negociación de precio de leche'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_VACA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Selección y descarte de animales improductivos'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R35 — KPI_INGRESO_VACA ACEPTABLE (65-85% promedio)
-- P1: Suplementación estratégica en época seca
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_INGRESO_VACA')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Suplementación estratégica en época seca'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R36a/b/c — KPI_ROTACION_ACTIVOS CRÍTICO
-- P1: Implementación de registros digitales (visibilidad activos)
-- P2: Diversificación de ingresos del hato
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('MEDIANA', 'GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Implementación de registros digitales'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('MEDIANA', 'GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Diversificación de ingresos del hato'
  AND p.escala = 'PEQUEÑA';


-- ============================================================
-- BLOQUE 4: REGLAS DE EFICIENCIA
-- ============================================================

-- -----------------------------------------------------------
-- R37a/b/c — KPI_EMPLEADOS_HA CRÍTICO (MEDIANA, GRANDE, EMPRESARIAL)
-- Problema: exceso de personal por hectárea.
-- P1: Optimización de estructura de personal
-- P2: Evaluación de mecanización del ordeño
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('MEDIANA', 'GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Optimización de estructura de personal'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('MEDIANA', 'GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Evaluación de mecanización del ordeño'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R38 — KPI_LITROS_EMPLEADO CRÍTICO (<65% promedio)
-- Problema: baja productividad laboral.
-- P1: Capacitación del personal en buenas prácticas
-- P2: Evaluación de mecanización del ordeño
-- P3: Optimización de estructura de personal
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Capacitación del personal en buenas prácticas'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Evaluación de mecanización del ordeño'
  AND p.escala = 'PEQUEÑA';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND p.nombre = 'Optimización de estructura de personal'
  AND p.escala = 'TODAS';


-- -----------------------------------------------------------
-- R39 — KPI_LITROS_EMPLEADO ACEPTABLE (65-85% promedio)
-- P1: Capacitación del personal en buenas prácticas
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO')
  AND r.estado_kpi_objetivo = 'ACEPTABLE'
  AND p.nombre = 'Capacitación del personal en buenas prácticas'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R40 — KPI_COSTO_LABORAL_PCT CRÍTICO PEQUEÑA (>40%)
-- P1: Optimización de estructura de personal
-- P2: Planificación financiera mensual
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable = 'PEQUEÑA'
  AND p.nombre = 'Optimización de estructura de personal'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable = 'PEQUEÑA'
  AND p.nombre = 'Planificación financiera mensual'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R41 — KPI_COSTO_LABORAL_PCT CRÍTICO MEDIANA (>32%)
-- P1: Optimización de estructura de personal
-- P2: Evaluación de mecanización del ordeño
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable = 'MEDIANA'
  AND p.nombre = 'Optimización de estructura de personal'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable = 'MEDIANA'
  AND p.nombre = 'Evaluación de mecanización del ordeño'
  AND p.escala = 'PEQUEÑA';


-- -----------------------------------------------------------
-- R42 — KPI_COSTO_LABORAL_PCT CRÍTICO GRANDE y EMPRESARIAL (>25%)
-- P1: Evaluación de mecanización del ordeño (mayor impacto a gran escala)
-- P2: Optimización de estructura de personal
-- P3: Implementación de registros digitales (automatizar tareas)
-- -----------------------------------------------------------
INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 1
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Evaluación de mecanización del ordeño'
  AND p.escala = 'GRANDE';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 2
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Optimización de estructura de personal'
  AND p.escala = 'TODAS';

INSERT INTO regla_practica (id_regla, id_practica, orden)
SELECT r.id_regla, p.id_practica, 3
FROM regla r, practica p
WHERE r.id_kpi = (SELECT id_kpi FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT')
  AND r.estado_kpi_objetivo = 'CRITICO'
  AND r.escala_aplicable IN ('GRANDE', 'EMPRESARIAL')
  AND p.nombre = 'Implementación de registros digitales'
  AND p.escala = 'GRANDE';