-- ============================================================
-- FASE 4.1 — REGLAS DE PRODUCTIVIDAD (~12 reglas)
-- Hathor — KPIs categoría PRODUCTIVIDAD
--
-- LÓGICA DE UMBRALES:
--   MENOR_PCT_PROMEDIO + PCT_PROMEDIO:
--     condición se cumple cuando valor < benchmark_promedio × umbral_1
--     ej: umbral_1=0.70 → valor < 70% del promedio del trópico
--
--   ENTRE + ABSOLUTO:
--     condición se cumple cuando umbral_1 <= valor <= umbral_2
--
--   MENOR_QUE / MAYOR_QUE + ABSOLUTO:
--     comparación directa contra umbral_1
--
--   NOTA sobre prioridad:
--     1 = ALTA (requiere atención urgente — estado CRÍTICO)
--     2 = MEDIA (mejora importante — estado ACEPTABLE)
--     3 = BAJA (optimización opcional)
--
--   NOTA sobre escala_aplicable = 'TODAS':
--     Los KPIs de productividad aplican a todos los tamaños de hato.
--     El benchmark contextualizado por escala ya diferencia el referente.
-- ============================================================


-- ============================================================
-- KPI_LITROS_VACA_DIA
-- La producción por vaca es el KPI más directo del desempeño
-- productivo. El benchmark varía enormemente por trópico y
-- escala, por lo que las reglas usan PCT_PROMEDIO para que
-- el motor busque el benchmark más específico disponible.
-- ============================================================

-- R01 — CRÍTICO: produce menos del 70% del promedio del trópico+escala
-- Indica un problema serio: nutrición deficiente, manejo del ordeño
-- deficiente, mala genética o combinación de los tres.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',   -- valor < benchmark_promedio × 0.70
    0.70,                   -- umbral: 70% del promedio
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Tu producción por vaca está más del 30% por debajo del promedio '
    'de hatos similares en tu trópico. Revisa nutrición, rutina de '
    'ordeño y manejo reproductivo.',
    1   -- ALTA prioridad
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';


-- R02 — ACEPTABLE: entre 70% y 99% del promedio — zona de mejora
-- El hato produce, pero hay margen real de optimización.
-- Recomendación: suplementación y manejo del período seco.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    -- umbral_1 y umbral_2 en PCT: se expresan como fracción del promedio
    -- El motor evalúa: valor >= benchmark × 0.70 AND valor < benchmark × 1.00
    -- Para ENTRE + PCT_PROMEDIO, el motor multiplica ambos umbrales por el benchmark
    0.70,
    0.99,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'Tu producción por vaca está cerca del promedio de tu trópico '
    'pero aún por debajo. Con ajustes en suplementación y manejo '
    'del período seco puedes alcanzar el referente.',
    2   -- MEDIA prioridad
FROM kpi WHERE codigo = 'KPI_LITROS_VACA_DIA';


-- ============================================================
-- KPI_LITROS_HA_ANIO
-- Mide la eficiencia del uso del suelo. Un valor bajo puede
-- deberse a baja carga animal, potreros degradados o baja
-- producción individual combinada con poca densidad.
-- ============================================================

-- R03 — CRÍTICO: menos del 65% del promedio del trópico
-- La brecha es suficientemente grande para justificar
-- intervención prioritaria en manejo de praderas y carga animal.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.65,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Tu producción por hectárea está muy por debajo del promedio '
    'regional. El suelo disponible no está siendo aprovechado: '
    'revisa la carga animal, el estado de los potreros y el '
    'sistema de pastoreo.',
    1
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';


-- R04 — ACEPTABLE: entre 65% y 89% del promedio
-- Hay productividad por hectárea pero el sistema puede
-- intensificarse con pastoreo rotacional y mejora de praderas.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.65,
    0.89,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'Tu productividad por hectárea está en zona de mejora. '
    'Implementar pastoreo rotacional y optimizar la carga animal '
    'puede acercarte al promedio de tu trópico.',
    2
FROM kpi WHERE codigo = 'KPI_LITROS_HA_ANIO';


-- ============================================================
-- KPI_PRODUCCION_HA_DIA
-- Es la versión diaria de KPI_LITROS_HA_ANIO ÷ 365.
-- Mismo patrón de reglas pero con umbral ligeramente más alto
-- para CRÍTICO porque la métrica diaria amplifica las
-- variaciones estacionales — usamos 70% para ser coherentes
-- con el KPI base de litros/vaca/día.
-- ============================================================

-- R05 — CRÍTICO: menos del 70% del promedio del trópico
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.70,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'La producción diaria por hectárea está significativamente '
    'por debajo del referente de tu trópico. Analiza si el '
    'problema está en la densidad de animales o en la '
    'producción individual por vaca.',
    1
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';


-- R06 — ACEPTABLE: entre 70% y 90% del promedio
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.70,
    0.90,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'La producción diaria por hectárea está cerca del promedio '
    'regional. Hay margen de mejora con rotación de potreros '
    'y optimización de la carga animal.',
    2
FROM kpi WHERE codigo = 'KPI_PRODUCCION_HA_DIA';


-- ============================================================
-- KPI_CAP_ALMAC_UTILIZADA
-- CASO ESPECIAL: este KPI tiene CRÍTICO en AMBAS direcciones.
--
-- CRÍTICO ALTO (>90%): la capacidad de almacenamiento está casi
--   llena — riesgo real de pérdida de leche, incumplimiento con
--   el acopiador, posible deterioro de calidad.
--
-- CRÍTICO BAJO (<40%): la infraestructura está subutilizada —
--   puede indicar caída de producción no detectada, o inversión
--   ociosa en equipos que no se están usando.
--
-- ACEPTABLE: entre 40% y 69% — infraestructura subutilizada
--   pero sin riesgo inmediato. Señal de que hay potencial de
--   aumentar producción sin invertir en nueva infraestructura.
--
-- El rango óptimo (70-90%) NO genera regla — es el estado
-- correcto y no requiere acción.
-- ============================================================

-- R07 — CRÍTICO ALTO: uso de almacenamiento > 90%
-- Valor absoluto — no depende de benchmark
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_QUE',
    90.0,       -- > 90% de la capacidad utilizada
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Estás usando más del 90% de tu capacidad de almacenamiento '
    'de leche. Hay riesgo de pérdidas por desbordamiento o '
    'deterioro. Coordina con tu acopiador para aumentar la '
    'frecuencia de recogida o amplía la capacidad de frío.',
    1
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';


-- R08 — CRÍTICO BAJO: uso de almacenamiento < 40%
-- Infraestructura ociosa — puede señalar caída de producción
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    40.0,       -- < 40% de la capacidad utilizada
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Solo estás usando menos del 40% de tu capacidad de '
    'almacenamiento de leche. Esto puede indicar una caída '
    'en la producción del hato o una inversión en infraestructura '
    'que no se está aprovechando.',
    2   -- MEDIA: no es urgente como el alto, pero es señal de alerta
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';


-- R09 — ACEPTABLE: entre 40% y 69%
-- Infraestructura subutilizada sin riesgo inmediato
-- Hay potencial para aumentar producción sin nueva inversión
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    40.0,
    69.9,
    'ABSOLUTO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'Estás usando entre el 40% y el 70% de tu capacidad de '
    'almacenamiento. Tienes margen para aumentar la producción '
    'sin necesidad de ampliar la infraestructura de frío.',
    3   -- BAJA: es una oportunidad, no un problema
FROM kpi WHERE codigo = 'KPI_CAP_ALMAC_UTILIZADA';


-- ============================================================
-- KPI_LACTANCIA_VS_ESTANDAR
-- CASO ESPECIAL: este KPI mide la diferencia en días respecto
-- al estándar de 305 días. Puede ser negativo (lactancias
-- cortas) o positivo (lactancias largas).
--
-- NO usa PCT_PROMEDIO porque el estándar de 305 días es fijo
-- internacionalmente — no varía por trópico. Lo que varía por
-- trópico es el promedio observado, pero el objetivo siempre
-- es llegar a 305 días o más.
--
-- CRÍTICO:    < -30 días (lactancia < 275 días — pérdida
--             significativa de producción total por lactancia)
-- ACEPTABLE:  entre -30 y -1 día (ligeramente corta)
-- No hay regla para valores > 0 (es el estado deseado)
-- ============================================================

-- R10 — CRÍTICO: lactancia más de 30 días por debajo del estándar
-- Equivale a que las vacas producen menos de 275 días en promedio.
-- Causas típicas: mal manejo del período seco, problemas
-- reproductivos que acortan el ciclo, o destete muy temprano.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    -30.0,      -- valor < -30 días (lactancia < 275 días)
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El período de lactancia promedio del hato está más de '
    '30 días por debajo del estándar de 305 días. Cada día '
    'perdido representa leche no producida. Revisa el manejo '
    'del período seco y el programa reproductivo.',
    1
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';


-- R11 — ACEPTABLE: lactancia entre -30 y -1 días del estándar
-- Las vacas producen entre 275 y 304 días. Es mejorable
-- pero no es una situación crítica.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    -30.0,
    -1.0,
    'ABSOLUTO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El período de lactancia promedio está ligeramente por '
    'debajo del estándar de 305 días. Ajustar el manejo del '
    'período seco y la nutrición pre-parto puede mejorar '
    'la persistencia de la lactancia.',
    2
FROM kpi WHERE codigo = 'KPI_LACTANCIA_VS_ESTANDAR';


-- ============================================================
-- KPI_FRECUENCIA_ORDENIO
-- Mide los ordeños diarios promedio del hato.
-- Valor absoluto — no tiene benchmark comparativo por trópico
-- porque el estándar técnico es el mismo para todos: 2 ordeños
-- diarios es la práctica recomendada en lechería especializada.
--
-- CRÍTICO: < 2 ordeños (la mayoría del hato hace 1 ordeño/día)
--   → pérdida estimada del 15-25% de producción posible.
--
-- No hay estado ACEPTABLE porque la situación es binaria:
-- o se hace el segundo ordeño o no. No hay "casi 2 ordeños".
-- El estado óptimo (≥ 2) no genera regla.
-- ============================================================

-- R12 — CRÍTICO: menos de 2 ordeños diarios promedio
-- Solo aplica para escalas donde la mano de obra
-- permite el segundo ordeño. En PEQUEÑA puede ser difícil,
-- pero la regla aplica igual — la práctica recomendada
-- evalúa la viabilidad económica.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    2.0,        -- < 2 ordeños/día
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El hato está siendo ordeñado menos de 2 veces al día. '
    'Pasar a doble ordeño puede aumentar la producción entre '
    '15% y 25% sin añadir animales. Evalúa si el costo '
    'adicional de mano de obra es justificado con el '
    'ingreso adicional.',
    1
FROM kpi WHERE codigo = 'KPI_FRECUENCIA_ORDENIO';

-- ============================================================
-- FASE 4.2 — REGLAS DE HATO (~7 reglas)
-- Hathor — KPIs categoría HATO
--
-- KPIs cubiertos:
--   KPI_CARGA_ANIMAL         → CRÍTICO por exceso + CRÍTICO por defecto
--   KPI_PCT_VACAS_ORDENIO    → CRÍTICO + ACEPTABLE
--   KPI_HEMBRAS_RECRIA_VACA  → CRÍTICO por defecto + CRÍTICO por exceso
--
-- NOTA sobre CARGA_ANIMAL:
--   Este KPI tiene lógica opuesta según dirección:
--   - Exceso (MAYOR_PCT_TOP): riesgo de degradación de praderas,
--     sobrecarga que reduce calidad del pasto y la producción.
--     Se compara con PCT_TOP porque superar el máximo eficiente
--     del sector es el umbral de riesgo real.
--   - Defecto (MENOR_PCT_PROMEDIO): subutilización del predio,
--     el hato tiene capacidad para más animales pero no la usa.
--     Se compara con PCT_PROMEDIO porque estar muy por debajo
--     del promedio indica ineficiencia productiva.
--
-- NOTA sobre HEMBRAS_RECRIA_VACA:
--   También tiene problema en ambas direcciones:
--   - Muy bajo (<0.25): pocas hembras de reposición — riesgo de
--     descapitalización del hato a mediano plazo.
--   - Muy alto (>0.55): exceso de hembras improductivas que
--     consumen recursos sin generar ingresos corrientes.
--   Estos umbrales son valores absolutos del sector colombiano,
--   no porcentaje del benchmark, porque representan rangos
--   técnicos estables independientes del trópico.
-- ============================================================


-- ============================================================
-- KPI_CARGA_ANIMAL
-- UGG/ha (Unidades Gran Ganado por hectárea).
-- El benchmark varía significativamente por trópico:
--   FRÍO:      óptimo 2.5-4.0 UGG/ha, top ~4.5
--   TEMPLADO:  óptimo 2.0-3.0 UGG/ha, top ~3.8 (UPRA 2024: 2.24)
--   CÁLIDO:    óptimo 3.0-5.0 UGG/ha, top ~5.7 (Infortambo Andina)
-- Por eso las reglas usan PCT del benchmark contextualizado
-- en lugar de valores absolutos — el motor buscará el benchmark
-- más específico disponible (trópico + escala si existe).
-- ============================================================

-- R13 — CRÍTICO ALTO: carga animal > 120% del top del trópico
-- Superar el benchmark top en un 20% es sobrecarga severa.
-- En Colombia, la sobrecarga es una causa frecuente de
-- degradación de praderas y reducción de producción a largo plazo.
-- Fuente: FEDEGAN Carta No.149 — rotación de potreros y carga.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_PCT_TOP',    -- valor > benchmark_top × 1.20
    1.20,               -- 120% del top del trópico
    NULL,
    'PCT_TOP',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'La carga animal supera significativamente el máximo '
    'recomendado para tu trópico. El sobrepastoreo degrada '
    'las praderas y reduce la producción a mediano plazo. '
    'Considera vender excedente de animales o implementar '
    'pastoreo rotacional intensivo.',
    1   -- ALTA prioridad — riesgo estructural del hato
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';


-- R14 — CRÍTICO BAJO: carga animal < 50% del promedio del trópico
-- El hato tiene mucha tierra disponible pero pocos animales.
-- Indica subutilización severa del predio — puede deberse a
-- hato reducido por ventas, mortalidad o falta de reposición.
-- Umbral en 50% (no 70%) porque carga animal baja no es
-- siempre un problema urgente — depende de la estrategia.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',   -- valor < benchmark_promedio × 0.50
    0.50,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'La carga animal está muy por debajo del promedio de hatos '
    'similares en tu trópico. El predio tiene capacidad '
    'subutilizada. Evalúa aumentar el inventario o diversificar '
    'el uso del área disponible.',
    2   -- MEDIA: no es urgente, pero representa ineficiencia
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';


-- R15 — ACEPTABLE: entre 105% y 120% del top (zona de alerta temprana)
-- La carga está por encima del tope referencial pero sin llegar
-- al nivel crítico. Es una señal de advertencia para actuar
-- antes de que la pradera se degrade.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    1.05,   -- > 105% del top: empieza a ser preocupante
    1.20,   -- < 120% del top: aún no es crítico
    'PCT_TOP',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'La carga animal está ligeramente por encima del máximo '
    'recomendado para tu trópico. Es buen momento para '
    'optimizar la rotación de potreros antes de que el '
    'sobrepastoreo afecte la calidad del forraje.',
    2
FROM kpi WHERE codigo = 'KPI_CARGA_ANIMAL';


-- ============================================================
-- KPI_PCT_VACAS_ORDENIO
-- Porcentaje de vacas en ordeño sobre el total del hato.
-- En lechería especializada el óptimo es 60-75%.
-- Valores bajos indican exceso de vacas secas, vacías,
-- novillas no paridas o animales improductivos.
--
-- Referencia sectorial Colombia:
--   Promedio nacional: ~55% (FEDEGAN — incluye doble propósito)
--   Lechería especializada: 62-77%
--   UPRA 2024 Valle del Cauca: 32 ordeño / 50 total = 64%
--
-- Las reglas usan PCT_PROMEDIO para adaptarse al benchmark
-- del trópico — en cálido el promedio es más bajo por el
-- manejo con ternero en sistemas de doble propósito.
-- ============================================================

-- R16 — CRÍTICO: menos del 70% del promedio del trópico
-- Si el benchmark promedio del trópico es 55% y el hato
-- está al 70% de eso → el hato tiene ~38% de vacas en ordeño.
-- Señal fuerte de problema reproductivo o exceso de
-- animales improductivos.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.70,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El porcentaje de vacas en ordeño está significativamente '
    'por debajo del promedio de tu trópico. Hay demasiados '
    'animales improductivos en el hato. Revisa el programa '
    'reproductivo y evalúa el descarte de vacas vacías '
    'o de baja producción.',
    1
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';


-- R17 — ACEPTABLE: entre 70% y 90% del promedio del trópico
-- El hato tiene una proporción mejorable de vacas productivas.
-- No es crítico pero hay margen claro de optimización.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.70,
    0.90,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'La proporción de vacas en ordeño está cerca del promedio '
    'de tu trópico pero hay margen de mejora. Un programa '
    'reproductivo consistente y el descarte oportuno de '
    'animales improductivos puede aumentar este indicador.',
    2
FROM kpi WHERE codigo = 'KPI_PCT_VACAS_ORDENIO';


-- ============================================================
-- KPI_HEMBRAS_RECRIA_VACA
-- Índice de hembras en recría (novillas + terneras hembra)
-- por cada vaca en ordeño.
-- Mide la capacidad de reposición interna del hato.
--
-- Referencia técnica sector ganadero colombiano:
--   < 0.25: pocas hembras de reposición — riesgo de
--     descapitalización y dependencia de compra externa
--   0.25 - 0.45: rango óptimo — reposición sostenible
--   > 0.55: exceso de hembras improductivas — presión
--     económica sin retorno inmediato
--
-- Fuente: FEDEGAN Asistegán — "novillas = 15% del total
-- de vacas (paridas + secas) suponiendo 15% de descarte anual".
-- 15% de descarte → índice óptimo ≈ 0.30-0.40 hembras/vaca.
--
-- Estas reglas usan valores ABSOLUTOS porque los umbrales
-- técnicos (0.25 y 0.55) no varían significativamente por
-- trópico — son parámetros zootécnicos del negocio.
-- ============================================================

-- R18 — CRÍTICO BAJO: índice < 0.25 hembras de recría por vaca
-- El hato no está generando suficientes reemplazos internos.
-- A mediano plazo (2-3 años), esto obliga a comprar novillas
-- externas a precio de mercado para mantener el inventario —
-- con el riesgo sanitario y económico que eso implica.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    0.25,       -- < 0.25 hembras de recría por vaca en ordeño
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Hay pocas hembras de reposición en el hato. Con menos '
    'de 0.25 hembras de recría por vaca en ordeño, en 2-3 '
    'años no habrá reemplazos propios suficientes. Considera '
    'retener más terneras y revisar el programa de mejoramiento '
    'genético.',
    1
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

-- R19 — CRÍTICO ALTO: índice > 0.55 hembras de recría por vaca
-- Exceso de animales improductivos que consumen forraje y
-- recursos sin generar ingresos corrientes. Puede indicar
-- que se están reteniendo demasiadas terneras, que hay
-- problemas para preñar novillas o que el ritmo de incorporación
-- al hato productivo es muy lento.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_QUE',
    0.55,       -- > 0.55 hembras de recría por vaca en ordeño
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Hay demasiadas hembras de recría en relación al hato '
    'productivo. Están consumiendo recursos sin generar '
    'ingresos. Evalúa vender el excedente de novillas o '
    'acelerar su incorporación al hato de ordeño.',
    2   -- MEDIA: es un problema económico, no de producción inmediata
FROM kpi WHERE codigo = 'KPI_HEMBRAS_RECRIA_VACA';

-- ============================================================
-- FASE 4.3 — REGLAS FINANCIERAS (~14 reglas)
-- Hathor — KPIs categoría FINANCIERO
--
-- KPIs cubiertos:
--   KPI_MARGEN_NETO          → CRÍTICO absoluto negativo + ACEPTABLE
--   KPI_MARGEN_BRUTO_PCT     → CRÍTICO + ACEPTABLE
--   KPI_RATIO_INGRESO_EGRESO → CRÍTICO absoluto + ACEPTABLE
--   KPI_BALANCE_NETO         → CRÍTICO absoluto negativo (solo)
--   KPI_COSTO_LITRO          → CRÍTICO por exceso + ACEPTABLE
--   KPI_INGRESO_LITRO        → CRÍTICO por defecto
--   KPI_IOFC                 → CRÍTICO absoluto negativo + ACEPTABLE
--   KPI_BREAKEVEN_LITRO      → CRÍTICO especial: compara contra
--                              KPI_INGRESO_LITRO del mismo hato
--   KPI_ROA                  → CRÍTICO (solo MEDIANA/GRANDE/EMPRESARIAL)
--   KPI_INGRESO_VACA         → CRÍTICO + ACEPTABLE
--   KPI_INGRESO_HA_ANIO      → omitido: cubierto indirectamente por
--                              LITROS_HA_ANIO e INGRESO_LITRO
--   KPI_ROTACION_ACTIVOS     → CRÍTICO (solo MEDIANA/GRANDE/EMPRESARIAL)
--
-- NOTAS DE DISEÑO:
--
-- 1. KPI_MARGEN_NETO CRÍTICO negativo usa MENOR_QUE 0.0 ABSOLUTO —
--    es la única regla con prioridad 1 garantizada sin importar el
--    contexto. Un hato que pierde dinero necesita acción inmediata.
--
-- 2. KPI_BREAKEVEN_LITRO tiene lógica especial: el punto de
--    equilibrio supera el ingreso real cuando el hato pierde dinero.
--    No hay benchmark externo útil — la comparación relevante es
--    interna: BREAKEVEN_LITRO > INGRESO_LITRO. Se modela con
--    operador MAYOR_PCT_PROMEDIO usando un factor > 1.0 sobre el
--    benchmark de INGRESO_LITRO. Esto es una aproximación válida
--    porque si breakeven > promedio del precio pagado en el trópico,
--    es altamente probable que supere también el ingreso real del hato.
--    El motor evalúa: valor_breakeven > benchmark_ingreso × umbral_1.
--    Con umbral_1 = 1.00 capturamos cuando el breakeven supera
--    el precio promedio del trópico — señal de que el hato opera
--    con pérdidas estructurales.
--
-- 3. KPI_ROA y KPI_ROTACION_ACTIVOS: para escala PEQUEÑA estos KPIs
--    son poco relevantes operativamente (el ganadero familiar no
--    gestiona por ROA). Las reglas se limitan a MEDIANA, GRANDE
--    y EMPRESARIAL — se insertan tres reglas separadas por escala
--    para evitar un campo de lista en escala_aplicable.
--
-- 4. KPI_COSTO_LITRO y KPI_BREAKEVEN_LITRO: MENOR ES MEJOR.
--    La regla CRÍTICO usa MAYOR_PCT_PROMEDIO — el hato tiene costo
--    más alto que el promedio del sector, lo cual es el problema.
-- ============================================================


-- ============================================================
-- KPI_MARGEN_NETO
-- El indicador financiero de mayor peso del sistema.
-- Un margen negativo significa que por cada litro vendido
-- el hato pierde dinero — situación insostenible a cualquier
-- escala. Prioridad 1 (ALTA) absoluta sin excepción.
-- ============================================================

-- R20 — CRÍTICO ABSOLUTO: margen neto negativo
-- La regla más importante del sistema financiero.
-- No depende de benchmark — cualquier valor < 0 es crítico.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    0.0,        -- margen neto < 0% → pérdidas
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El hato está operando con pérdidas: los egresos superan '
    'los ingresos. Cada litro producido cuesta más de lo que '
    'se recibe. Se requiere intervención urgente en costos '
    'de alimentación, estructura de personal o precio de '
    'venta de la leche.',
    1   -- ALTA — máxima prioridad sin excepción
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';


-- R21 — ACEPTABLE: margen neto positivo pero bajo
-- El hato genera algún margen pero por debajo del 70%
-- del promedio del sector — zona de riesgo ante cualquier
-- variación de costos o precio.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.0,    -- positivo pero...
    0.70,   -- ...menos del 70% del promedio sectorial
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El margen neto es positivo pero está por debajo del '
    'promedio del sector. El hato es rentable pero vulnerable '
    'a fluctuaciones de precio o costos. Trabaja en reducir '
    'el costo por litro y mejorar el precio recibido.',
    2
FROM kpi WHERE codigo = 'KPI_MARGEN_NETO';


-- ============================================================
-- KPI_MARGEN_BRUTO_PCT
-- Diferencia porcentual entre ingresos y costos directos.
-- Un margen bruto bajo indica que los costos variables
-- (alimentación, sanidad, mano de obra directa) están
-- consumiendo casi todo el ingreso, sin dejar espacio
-- para cubrir costos fijos ni generar utilidad.
-- ============================================================

-- R22 — CRÍTICO: margen bruto < 65% del promedio del trópico
-- Si el promedio del trópico es 28% de margen bruto,
-- el 65% de eso es ~18% — nivel donde los costos fijos
-- ya no se pueden cubrir.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.65,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El margen bruto está muy por debajo del promedio del '
    'sector. Los costos directos de producción consumen '
    'casi todo el ingreso. Revisa con urgencia los costos '
    'de alimentación y el precio de venta de la leche.',
    1
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';


-- R23 — ACEPTABLE: entre 65% y 90% del promedio
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.65,
    0.90,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El margen bruto está por debajo del promedio del sector '
    'pero en zona recuperable. Optimizar la alimentación y '
    'acceder a bonificaciones por calidad puede mejorar '
    'este indicador.',
    2
FROM kpi WHERE codigo = 'KPI_MARGEN_BRUTO_PCT';


-- ============================================================
-- KPI_RATIO_INGRESO_EGRESO
-- Razón entre ingresos totales y egresos totales.
-- < 1.0 = el hato gasta más de lo que recibe (pérdidas).
-- = 1.0 = punto de equilibrio exacto.
-- > 1.0 = el hato genera excedente.
-- La regla CRÍTICO usa valor absoluto 1.0 — no hay
-- benchmark que sea más relevante que el punto de equilibrio.
-- ============================================================

-- R24 — CRÍTICO ABSOLUTO: ratio < 1.0 → más egresos que ingresos
-- Complementa R20 (margen neto negativo) pero desde otra
-- perspectiva: la razón total de flujo financiero.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    1.0,        -- ratio < 1.0 → egresos > ingresos
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Los egresos del hato superan los ingresos. Por cada '
    'peso que entra, sale más de un peso. El negocio no '
    'es sostenible en esta condición. Identifica el rubro '
    'de mayor gasto y actúa de inmediato.',
    1
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';


-- R25 — ACEPTABLE: ratio entre 1.0 y 1.15
-- El hato genera ingresos levemente superiores a los egresos
-- pero con margen de seguridad muy estrecho. Una variación
-- de precio o un mes de costos extra puede llevarlo a
-- pérdidas. Sector colombiano promedio en 2024: ~1.05.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    1.0,
    1.15,
    'ABSOLUTO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El ratio ingreso/egreso es positivo pero con margen '
    'muy estrecho. Cualquier alza en costos o baja en el '
    'precio de la leche puede poner el hato en pérdidas. '
    'Trabaja en ampliar este margen reduciendo costos o '
    'aumentando ingresos.',
    2
FROM kpi WHERE codigo = 'KPI_RATIO_INGRESO_EGRESO';


-- ============================================================
-- KPI_BALANCE_NETO
-- Diferencia absoluta en COP entre ingresos y egresos del mes.
-- Una sola regla: balance negativo es señal de alerta.
-- No tiene regla ACEPTABLE porque el balance negativo es
-- binario — o hay déficit o no. El nivel positivo varía
-- tanto por escala que un benchmark no es útil aquí.
-- ============================================================

-- R26 — CRÍTICO ABSOLUTO: balance neto negativo (< 0 COP)
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    0.0,        -- balance negativo en COP
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El balance del hato este período es negativo: los '
    'egresos superaron los ingresos en términos absolutos. '
    'Revisa los registros financieros para identificar '
    'gastos inusuales o caídas en el ingreso por leche.',
    1
FROM kpi WHERE codigo = 'KPI_BALANCE_NETO';


-- ============================================================
-- KPI_COSTO_LITRO
-- MENOR ES MEJOR. La regla CRÍTICO se dispara cuando el
-- costo es excesivamente alto comparado con el sector.
-- Usar MAYOR_PCT_PROMEDIO: el problema es tener un costo
-- MÁS ALTO que el promedio del trópico+escala.
-- Umbral 1.20: costo 20% por encima del promedio regional.
-- Fuente: FEDEGAN 2024 — costo $1.850-$2.100/L, muchos
-- productores perdiendo dinero con precios de $1.745-$1.960/L.
-- ============================================================

-- R27 — CRÍTICO: costo/litro > 120% del promedio del trópico
-- Si el promedio del trópico frío es $1.950/L, el 120%
-- son ~$2.340/L — claramente insostenible con precio ~$1.960/L.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_PCT_PROMEDIO',   -- costo > benchmark_promedio × 1.20
    1.20,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El costo de producción por litro supera el 120% del '
    'promedio del sector en tu trópico. Estás produciendo '
    'significativamente más caro que tus pares. Revisa '
    'los costos de alimentación, que representan el mayor '
    'rubro, y evalúa fuentes de forraje más económicas.',
    1
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';


-- R28 — ACEPTABLE: costo entre 100% y 120% del promedio
-- Costo por encima del promedio pero sin llegar al nivel
-- crítico — zona donde hay oportunidad de mejora concreta.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    1.00,   -- igual al promedio del trópico
    1.20,   -- hasta 20% por encima
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El costo de producción por litro está ligeramente '
    'por encima del promedio del sector. Con mejoras en '
    'eficiencia alimentaria y escala de compra de insumos '
    'puedes acercarte al promedio o superarlo.',
    2
FROM kpi WHERE codigo = 'KPI_COSTO_LITRO';


-- ============================================================
-- KPI_INGRESO_LITRO
-- Precio efectivo recibido por litro (con bonificaciones).
-- Un precio bajo puede deberse a: venta informal sin acceso
-- a bonificaciones, mala calidad higiénica o composicional,
-- falta de negociación con el acopiador.
-- ============================================================

-- R29 — CRÍTICO: ingreso/litro < 80% del promedio del trópico
-- Si el trópico frío promedia $1.960/L, el 80% son $1.568/L —
-- claramente por debajo del precio regulado mínimo Res 017/2012.
-- Fuente: USP-MADR — precio promedio 2024 por trópico.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.80,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El precio recibido por litro está más del 20% por debajo '
    'del promedio de tu trópico. Esto puede indicar venta '
    'informal, baja calidad higiénica o ausencia de '
    'bonificaciones. Formaliza el acopio y mejora la calidad '
    'de la leche para acceder a los precios regulados.',
    1
FROM kpi WHERE codigo = 'KPI_INGRESO_LITRO';


-- ============================================================
-- KPI_IOFC — Income Over Feed Cost
-- Ingreso por leche menos costo de alimentación.
-- Negativo = la alimentación cuesta más de lo que produce
-- la leche → el hato destruye valor con cada litro que produce.
-- Este es el KPI de eficiencia alimentaria más directo.
-- ============================================================

-- R30 — CRÍTICO ABSOLUTO: IOFC negativo
-- El costo de alimentación supera el ingreso por leche.
-- Situación insostenible que requiere reformular la dieta
-- o cambiar las fuentes de alimentación urgentemente.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_QUE',
    0.0,        -- IOFC negativo en COP
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El costo de alimentación supera el ingreso generado '
    'por la leche. Cada litro producido destruye valor. '
    'Revisa urgentemente la dieta: cotiza fuentes de '
    'forraje más económicas, reduce concentrado caro '
    'e implementa banco de proteína forrajera.',
    1
FROM kpi WHERE codigo = 'KPI_IOFC';


-- R31 — ACEPTABLE: IOFC positivo pero < 70% del promedio del trópico
-- El hato genera algo sobre el costo de alimentación pero
-- el margen es ajustado comparado con hatos similares.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.0,    -- positivo pero...
    0.70,   -- ...menos del 70% del promedio del trópico
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El margen sobre el costo de alimentación es positivo '
    'pero por debajo del promedio del sector. Hay oportunidad '
    'de mejorar la eficiencia alimentaria con pastoreo '
    'rotacional, banco de proteína o reducción de concentrado.',
    2
FROM kpi WHERE codigo = 'KPI_IOFC';


-- ============================================================
-- KPI_BREAKEVEN_LITRO
-- CASO ESPECIAL — LÓGICA DE COMPARACIÓN CRUZADA.
--
-- El punto de equilibrio es el precio mínimo al que se debe
-- vender cada litro para cubrir todos los costos. Cuando
-- BREAKEVEN > INGRESO_LITRO, el hato pierde dinero por cada
-- litro vendido.
--
-- No hay benchmark externo directo útil aquí — la comparación
-- relevante es interna: ¿el breakeven supera lo que pagan?
--
-- SOLUCIÓN DE MODELADO:
-- Se usa MAYOR_PCT_PROMEDIO con umbral_1 = 1.00 sobre el
-- benchmark del KPI_INGRESO_LITRO. El motor buscará el
-- benchmark de KPI_INGRESO_LITRO para el trópico del hato
-- (ej: $1.960/L para frío, $1.745/L para cálido).
-- Si el breakeven del hato supera ese precio promedio,
-- es muy probable que también supere su ingreso real.
--
-- NOTA TÉCNICA: el motor evalúa esta regla contra el KPI
-- KPI_BREAKEVEN_LITRO del hato usando el benchmark de
-- KPI_INGRESO_LITRO como referente. Para esto, la regla
-- se inserta con id_kpi de BREAKEVEN pero el mensaje
-- explica la comparación. El motor ya maneja esto porque
-- busca el benchmark por código KPI del campo id_kpi
-- de la regla — con esto funciona correctamente al
-- buscar el benchmark de KPI_BREAKEVEN_LITRO (que tiene
-- los mismos valores que KPI_INGRESO_LITRO en el benchmark).
-- ============================================================

-- R32 — CRÍTICO ESPECIAL: breakeven ≥ precio promedio del trópico
-- Si el punto de equilibrio supera el precio que paga
-- el sector, el hato opera con pérdidas estructurales.
-- Umbral = 1.00 (100% del promedio) — cualquier breakeven
-- que iguale o supere el precio promedio del trópico es crítico.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_PCT_PROMEDIO',   -- breakeven > benchmark_promedio × 1.00
    1.00,                   -- iguala o supera el precio promedio del trópico
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'Tu precio de equilibrio por litro supera el precio '
    'promedio que se paga en tu trópico. El hato necesita '
    'vender más caro de lo que el mercado paga para '
    'cubrir sus costos. Reduce costos fijos y variables '
    'con urgencia para bajar el punto de equilibrio.',
    1
FROM kpi WHERE codigo = 'KPI_BREAKEVEN_LITRO';


-- ============================================================
-- KPI_ROA — Retorno sobre Activos (%)
-- Solo aplica para MEDIANA, GRANDE y EMPRESARIAL.
-- Para escala PEQUEÑA el ROA no es un indicador relevante
-- operativamente — el ganadero familiar gestiona por flujo
-- de caja, no por retorno sobre activos.
-- Se insertan tres reglas idénticas en contenido, una por
-- escala, para no usar listas en escala_aplicable (que el
-- motor no maneja).
--
-- Umbral CRÍTICO: < 60% del promedio del trópico
-- Si el promedio es 5% de ROA, el 60% son 3% — nivel donde
-- la inversión en el hato no genera retorno razonable
-- comparado con alternativas de inversión del sector rural.
-- ============================================================

-- R33a — CRÍTICO ROA — escala MEDIANA
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.60,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'MEDIANA',
    'ACTIVA',
    'El retorno sobre los activos del hato está muy por debajo '
    'del promedio del sector para tu escala y trópico. Los '
    'activos invertidos (ganado, tierra, equipos) no están '
    'generando el retorno esperado. Evalúa la eficiencia '
    'productiva y la estructura de costos.',
    2   -- MEDIA: es señal de alerta estratégica, no urgencia operativa
FROM kpi WHERE codigo = 'KPI_ROA';

-- R33b — CRÍTICO ROA — escala GRANDE
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.60,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'GRANDE',
    'ACTIVA',
    'El retorno sobre los activos del hato está muy por debajo '
    'del promedio del sector para tu escala y trópico. Los '
    'activos invertidos (ganado, tierra, equipos) no están '
    'generando el retorno esperado. Evalúa la eficiencia '
    'productiva y la estructura de costos.',
    2
FROM kpi WHERE codigo = 'KPI_ROA';

-- R33c — CRÍTICO ROA — escala EMPRESARIAL
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.60,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'EMPRESARIAL',
    'ACTIVA',
    'El retorno sobre los activos del hato está muy por debajo '
    'del promedio del sector para tu escala y trópico. Los '
    'activos invertidos (ganado, tierra, equipos) no están '
    'generando el retorno esperado. Evalúa la eficiencia '
    'productiva y la estructura de costos.',
    2
FROM kpi WHERE codigo = 'KPI_ROA';


-- ============================================================
-- KPI_INGRESO_VACA
-- Ingreso anual generado por cada vaca en ordeño (COP).
-- Combina el efecto del precio recibido y la producción
-- individual — si alguno de los dos falla, este KPI cae.
-- Útil como diagnóstico integrado del desempeño económico
-- por animal.
-- ============================================================

-- R34 — CRÍTICO: ingreso/vaca < 65% del promedio del trópico+escala
-- Si el promedio del trópico frío mediano es $4.1M/vaca/año,
-- el 65% son ~$2.7M — nivel donde el animal no cubre su
-- costo de mantenimiento.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.65,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'El ingreso generado por vaca está muy por debajo '
    'del promedio del sector para tu trópico y escala. '
    'Cada animal produce significativamente menos valor '
    'económico del esperado. Revisa tanto la producción '
    'por vaca como el precio recibido por litro.',
    1
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';


-- R35 — ACEPTABLE: entre 65% y 85% del promedio
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.65,
    0.85,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'El ingreso por vaca está por debajo del promedio del '
    'sector pero en zona recuperable. Mejorar la producción '
    'individual o el precio recibido puede acercarte al '
    'referente de tu trópico.',
    2
FROM kpi WHERE codigo = 'KPI_INGRESO_VACA';


-- ============================================================
-- KPI_ROTACION_ACTIVOS
-- Veces que el ingreso total representa los activos totales.
-- Solo aplica MEDIANA, GRANDE y EMPRESARIAL — mismo
-- razonamiento que KPI_ROA.
-- Umbral CRÍTICO: < 60% del promedio del trópico+escala.
-- Valor bajo indica que los activos no están siendo
-- suficientemente productivos en relación a su valor.
-- ============================================================

-- R36a — CRÍTICO ROTACION ACTIVOS — escala MEDIANA
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.60,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'MEDIANA',
    'ACTIVA',
    'La rotación de activos está por debajo del promedio '
    'del sector. Los activos del hato generan menos ingresos '
    'de lo esperado para su valor. Evalúa si hay activos '
    'subutilizados o si la producción puede intensificarse '
    'con la infraestructura actual.',
    2
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- R36b — CRÍTICO ROTACION ACTIVOS — escala GRANDE
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.60,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'GRANDE',
    'ACTIVA',
    'La rotación de activos está por debajo del promedio '
    'del sector. Los activos del hato generan menos ingresos '
    'de lo esperado para su valor. Evalúa si hay activos '
    'subutilizados o si la producción puede intensificarse '
    'con la infraestructura actual.',
    2
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- R36c — CRÍTICO ROTACION ACTIVOS — escala EMPRESARIAL
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.60,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'EMPRESARIAL',
    'ACTIVA',
    'La rotación de activos está por debajo del promedio '
    'del sector. Los activos del hato generan menos ingresos '
    'de lo esperado para su valor. Evalúa si hay activos '
    'subutilizados o si la producción puede intensificarse '
    'con la infraestructura actual.',
    2
FROM kpi WHERE codigo = 'KPI_ROTACION_ACTIVOS';

-- ============================================================
-- FASE 4.4 — REGLAS DE EFICIENCIA (~7 reglas)
-- Hathor — KPIs categoría EFICIENCIA
--
-- KPIs cubiertos:
--   KPI_EMPLEADOS_HA       → CRÍTICO y ACEPTABLE
--                            excluye PEQUEÑA explícitamente
--   KPI_LITROS_EMPLEADO    → CRÍTICO + ACEPTABLE (dos niveles)
--   KPI_COSTO_LABORAL_PCT  → CRÍTICO con umbrales distintos
--                            por escala (PEQUEÑA vs resto)
--
-- NOTAS DE DISEÑO:
--
-- 1. KPI_EMPLEADOS_HA — MENOR ES MEJOR.
--    Excluir PEQUEÑA explícitamente es correcto porque en
--    hatos familiares el indicador casi siempre es "alto"
--    por trabajo familiar no asalariado. Disparar esta regla
--    para un ganadero de 15 vacas generaría una recomendación
--    de "reducir personal" que no tiene sentido — su mano
--    de obra es familiar y no monetizable fácilmente.
--    Se insertan tres reglas (MEDIANA, GRANDE, EMPRESARIAL)
--    en lugar de usar una sola con escala TODAS.
--
-- 2. KPI_LITROS_EMPLEADO — MAYOR ES MEJOR.
--    Dos niveles de alerta:
--    - CRÍTICO: < 65% del promedio → productividad laboral
--      muy baja, posible exceso de personal o capacitación
--      deficiente.
--    - ACEPTABLE: entre 65% y 85% → productividad mejorable
--      con capacitación o reorganización de tareas.
--    Esta regla aplica a TODAS las escalas porque la
--    productividad por empleado es relevante en cualquier
--    tamaño de hato — lo que cambia es la práctica
--    recomendada (capacitación para PEQUEÑA,
--    mecanización para GRANDE).
--
-- 3. KPI_COSTO_LABORAL_PCT — MENOR ES MEJOR.
--    Los umbrales difieren por escala porque la estructura
--    de costos laborales es fundamentalmente distinta:
--    - PEQUEÑA: mano de obra familiar → umbral más alto
--      (40%) antes de ser crítico, porque parte del costo
--      laboral es el propio propietario.
--    - MEDIANA/GRANDE/EMPRESARIAL: personal contratado →
--      umbral más bajo (30%) porque la mano de obra es un
--      costo real y controlable que en sistemas eficientes
--      debería estar por debajo del 20-25%.
--    Fuente: FEDEGAN — estructura costos lechería:
--    nacional promedio 40-42%, empresas sobresalientes 15.3%.
--    UPRA 2024 Valle del Cauca: 28.86% ($464 de $1.607/L).
-- ============================================================


-- ============================================================
-- KPI_EMPLEADOS_HA
-- Empleados por hectárea de pastoreo.
-- MENOR ES MEJOR — regla CRÍTICO usa MAYOR_PCT_PROMEDIO.
-- Excluye escala PEQUEÑA: trabajo familiar no aplica la
-- misma lógica de eficiencia laboral que el personal
-- contratado en hatos medianos y grandes.
-- ============================================================

-- R37a — CRÍTICO EMPLEADOS_HA — escala MEDIANA
-- > 130% del promedio del trópico+escala: exceso claro
-- de personal para el área manejada.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_PCT_PROMEDIO',   -- empleados/ha > benchmark_promedio × 1.30
    1.30,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'MEDIANA',
    'ACTIVA',
    'El número de empleados por hectárea supera el 130% '
    'del promedio del sector para tu escala. La carga '
    'laboral es elevada en relación al área productiva. '
    'Revisa si hay tareas duplicadas o si la mecanización '
    'del ordeño puede reducir la dependencia de mano '
    'de obra.',
    2   -- MEDIA: es ineficiencia costosa, no emergencia operativa
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';

-- R37b — CRÍTICO EMPLEADOS_HA — escala GRANDE
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_PCT_PROMEDIO',
    1.30,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'GRANDE',
    'ACTIVA',
    'El número de empleados por hectárea supera el 130% '
    'del promedio del sector para tu escala. La carga '
    'laboral es elevada en relación al área productiva. '
    'Revisa si hay tareas duplicadas o si la mecanización '
    'del ordeño puede reducir la dependencia de mano '
    'de obra.',
    2
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';

-- R37c — CRÍTICO EMPLEADOS_HA — escala EMPRESARIAL
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_PCT_PROMEDIO',
    1.30,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'EMPRESARIAL',
    'ACTIVA',
    'El número de empleados por hectárea supera el 130% '
    'del promedio del sector para tu escala. La carga '
    'laboral es elevada en relación al área productiva. '
    'A esta escala, la mecanización del ordeño y la '
    'automatización de registros son las palancas de '
    'mayor impacto para reducir este indicador.',
    2
FROM kpi WHERE codigo = 'KPI_EMPLEADOS_HA';


-- ============================================================
-- KPI_LITROS_EMPLEADO
-- Litros producidos por empleado por día.
-- MAYOR ES MEJOR.
-- Dos niveles de alerta — aplica a TODAS las escalas
-- porque la productividad laboral importa en cualquier
-- tamaño, aunque la causa y la solución varíen.
-- ============================================================

-- R38 — CRÍTICO: litros/empleado < 65% del promedio del trópico+escala
-- Productividad laboral muy baja. Las causas más comunes:
-- exceso de personal, mala organización de tareas, capacitación
-- deficiente o ausencia de mecanización básica.
-- Fuente: Teuken Bidikay Antioquia 2017 — diferencia entre
-- ordeño manual (13.83 L/vaca/día) vs mecánico (15.62 L/vaca/día)
-- impacta directamente los litros por empleado.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MENOR_PCT_PROMEDIO',
    0.65,
    NULL,
    'PCT_PROMEDIO',
    'CRITICO',
    'TODAS',
    'ACTIVA',
    'La productividad por empleado está muy por debajo '
    'del promedio del sector para tu escala y trópico. '
    'Cada trabajador produce significativamente menos '
    'litros de los esperados. Revisa la organización '
    'de tareas, la capacitación del personal y evalúa '
    'si el ordeño mecánico puede mejorar este indicador.',
    1   -- ALTA: impacta directamente el costo laboral por litro
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- R39 — ACEPTABLE: entre 65% y 85% del promedio
-- Productividad laboral mejorable pero sin ser crítica.
-- En este rango la capacitación y reorganización de tareas
-- suelen ser suficientes — no se requiere inversión en
-- mecanización necesariamente.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'ENTRE',
    0.65,
    0.85,
    'PCT_PROMEDIO',
    'ACEPTABLE',
    'TODAS',
    'ACTIVA',
    'La productividad por empleado está por debajo del '
    'promedio pero en zona recuperable. Capacitación en '
    'buenas prácticas y mejor organización del tiempo '
    'de ordeño pueden mejorar este indicador sin '
    'inversiones mayores.',
    2
FROM kpi WHERE codigo = 'KPI_LITROS_EMPLEADO';


-- ============================================================
-- KPI_COSTO_LABORAL_PCT
-- Porcentaje del ingreso total que consume la nómina.
-- MENOR ES MEJOR — regla CRÍTICO usa MAYOR_QUE ABSOLUTO.
--
-- UMBRALES DIFERENCIADOS POR ESCALA:
--
-- PEQUEÑA (umbral 40%):
--   El trabajo familiar difumina la línea entre costo laboral
--   real y retorno al propietario. Un umbral de 40% reconoce
--   que parte de ese porcentaje es ingreso del propio ganadero,
--   no un costo eliminable. Solo es crítico si supera 40%
--   porque ahí ya hay personal contratado que claramente
--   excede la capacidad de ingresos.
--   Fuente: FEDEGAN — promedio nacional pequeños ~35-40%.
--
-- MEDIANA (umbral 32%):
--   Con personal contratado, el costo laboral es un gasto
--   real. El promedio nacional en lechería especializada
--   es 40-42% (FEDEGAN) pero las empresas sobresalientes
--   logran 15.3%. Para medianos, 32% es el umbral donde
--   la nómina empieza a ser una carga estructural.
--   Fuente: UPRA 2024 Valle del Cauca mediano: 28.86%.
--
-- GRANDE/EMPRESARIAL (umbral 25%):
--   A esta escala, la mecanización y la economía de escala
--   deberían mantener el costo laboral bajo. Si supera 25%
--   en un hato grande, hay ineficiencias estructurales
--   claras — exceso de personal o baja productividad.
--   Fuente: FEDEGAN empresas sobresalientes: 15.3%.
-- ============================================================

-- R40 — CRÍTICO COSTO_LABORAL_PCT — escala PEQUEÑA (umbral 40%)
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_QUE',
    40.0,       -- > 40% del ingreso va a nómina
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'PEQUEÑA',
    'ACTIVA',
    'El costo laboral supera el 40% del ingreso del hato. '
    'La nómina está consumiendo demasiado del flujo de caja. '
    'Evalúa si todas las tareas contratadas son necesarias '
    'o si pueden ser asumidas con mejor organización '
    'del trabajo familiar.',
    2
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';


-- R41 — CRÍTICO COSTO_LABORAL_PCT — escala MEDIANA (umbral 32%)
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_QUE',
    32.0,       -- > 32% del ingreso va a nómina
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'MEDIANA',
    'ACTIVA',
    'El costo laboral supera el 32% del ingreso, por encima '
    'del referente UPRA 2024 para hatos medianos (28.86%). '
    'Revisa la estructura de personal: identifica tareas '
    'que pueden tecnificarse o reagruparse para reducir '
    'la dependencia de mano de obra.',
    2
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';


-- R42 — CRÍTICO COSTO_LABORAL_PCT — escala GRANDE y EMPRESARIAL
-- Un umbral de 25% para hatos grandes es coherente con las
-- economías de escala esperadas. Superar ese nivel indica
-- que la mecanización no está siendo aprovechada o que hay
-- exceso de personal en alguna área del hato.
-- Mismo mensaje para GRANDE y EMPRESARIAL — se insertan dos.
INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_QUE',
    25.0,       -- > 25% del ingreso va a nómina
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'GRANDE',
    'ACTIVA',
    'El costo laboral supera el 25% del ingreso para un hato '
    'de tu escala. Las economías de escala y la mecanización '
    'disponible deberían mantener este indicador por debajo '
    'de ese umbral. Evalúa la mecanización del ordeño y '
    'la automatización de registros para reducirlo.',
    2
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';

INSERT INTO regla (
    id_kpi, operador, umbral_1, umbral_2, umbral_tipo,
    estado_kpi_objetivo, escala_aplicable, estado, mensaje, prioridad
)
SELECT
    id_kpi,
    'MAYOR_QUE',
    25.0,
    NULL,
    'ABSOLUTO',
    'CRITICO',
    'EMPRESARIAL',
    'ACTIVA',
    'El costo laboral supera el 25% del ingreso para un hato '
    'de tu escala. Las economías de escala y la mecanización '
    'disponible deberían mantener este indicador por debajo '
    'de ese umbral. Evalúa la mecanización del ordeño y '
    'la automatización de registros para reducirlo.',
    2
FROM kpi WHERE codigo = 'KPI_COSTO_LABORAL_PCT';