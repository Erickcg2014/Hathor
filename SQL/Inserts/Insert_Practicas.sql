-- ============================================================
-- FASE 3.1 — CATÁLOGO DE PRÁCTICAS DE PRODUCTIVIDAD
-- Hathor — 8 prácticas con pasos por escala y trópico
-- ============================================================
-- NOTA TÉCNICA:
-- El campo 'pasos' almacena un JSON array de strings.
-- El campo 'escala' = TODAS indica que aplica a todas las escalas.
-- Cuando una práctica varía por escala, se insertan múltiples
-- registros con diferente escala y pasos adaptados.
-- kpi_impactado = código del KPI principal que mejora.
-- ============================================================

-- ============================================================
-- P01 — PASTOREO ROTACIONAL INTENSIVO
-- Fuentes:
--   FEDEGAN Carta No.149 — Rotación de potreros:
--     static.fedegan.org.co/Revistas_Carta_Fedegan/149/...
--   CONtexto Ganadero — Pastoreo rotacional clave:
--     contextoganadero.com/reportaje/pastoreo-rotacional-clave
--   CONtexto Ganadero — Claves producción leche pastoreo:
--     contextoganadero.com/ganaderia-sostenible/conozca-algunas-claves
--   Voisin — Leyes universales pastoreo rotacional:
--     infopastosyforrajes.com/sistemas-de-pastoreo/leyes-universales
--   Scielo Valle del Cauca 2018 — 62% fincas con pastoreo rotacional:
--     scielo.org.co/scielo.php?pid=S0120-29522018000300252
-- ============================================================

-- P01 versión PEQUEÑA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Pastoreo rotacional intensivo',
  'División del área de pastoreo en potreros para alternar el uso y el descanso del pasto, mejorando su calidad y la producción por hectárea.',
  'Aumentar la disponibilidad y calidad del forraje, incrementar la carga animal sostenible y mejorar la producción de leche por hectárea.',
  'PRODUCTIVIDAD',
  'Mejora KPI_LITROS_HA_ANIO entre 15-30% y KPI_CARGA_ANIMAL hasta 40% en 90 días con manejo adecuado.',
  'ACTIVA',
  '["1. Dividir el área de pastoreo en mínimo 4 potreros usando cerca eléctrica o postes baratos.", "2. Calcular el tiempo de ocupación: no más de 2-3 días por potrero.", "3. Respetar el descanso según trópico: frío 35-40 días, templado 25-30 días, cálido 20-27 días.", "4. Observar el pasto antes de rotar: debe estar a la altura del codo, no pastoreado al ras.", "5. Llevar registro simple del día de entrada y salida de cada potrero.", "6. Medir producción de leche antes y después de 60 días para evaluar el impacto."]',
  'KPI_LITROS_HA_ANIO',
  'BAJA',
  90,
  'PEQUEÑA',
  'TODOS'
);

-- P01 versión MEDIANA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Pastoreo rotacional intensivo',
  'Sistema intensivo de manejo de praderas con división en múltiples franjas para maximizar el aprovechamiento del forraje y la productividad animal.',
  'Maximizar la producción de forraje de calidad, aumentar la carga animal y reducir costos de suplementación.',
  'PRODUCTIVIDAD',
  'Mejora KPI_LITROS_HA_ANIO 20-40%, KPI_CARGA_ANIMAL hasta 50%, reducción costos concentrado 15-25% en 90 días.',
  'ACTIVA',
  '["1. Dividir el área en mínimo 8-15 potreros con cerca eléctrica según la investigación FEDEGAN.", "2. Calcular días de descanso óptimo según especie forrajera: kikuyo 30-35 días, estrella africana 22-27 días, brachiaria 25-30 días.", "3. Implementar franjas diarias o cada 2 días para maximizar calidad del forraje.", "4. Aforar cada potrero: lanzar marco 1x1m, pesar el forraje para calcular disponibilidad.", "5. Ajustar la carga animal según disponibilidad real de forraje.", "6. Fertilizar potreros después del pastoreo, no antes, para evitar pérdidas.", "7. Registrar producción individual de vacas por potrero para identificar mejores praderas.", "8. Evaluar a los 60 días: si los potreros están degradados, ampliar el descanso."]',
  'KPI_LITROS_HA_ANIO',
  'MEDIA',
  90,
  'MEDIANA',
  'TODOS'
);

-- P01 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Pastoreo rotacional intensivo',
  'Sistema de pastoreo de alta densidad con gestión técnica de praderas, monitoreo de calidad forrajera y optimización basada en datos productivos.',
  'Alcanzar la máxima productividad por hectárea mediante manejo científico de praderas con soporte técnico permanente.',
  'PRODUCTIVIDAD',
  'Mejora KPI_LITROS_HA_ANIO 30-50%, incremento carga animal sostenible >60%, reducción costos forraje comprado >30% en 6 meses.',
  'ACTIVA',
  '["1. Diseñar el sistema con agrónomo: dividir en 20-30+ potreros para rotación diaria.", "2. Implementar monitoreo de altura y condición del pasto con regla aforadora en cada lote.", "3. Establecer días de descanso diferenciados por época: lluvias vs sequía con ajuste dinámico.", "4. Instalar sistema de riego complementario si el trópico lo permite.", "5. Implementar análisis bromatológico semestral de las praderas para ajustar fertilización.", "6. Llevar registro digital de producción por potrero y correlacionar con producción individual.", "7. Renovar potreros degradados con especies mejoradas según el trópico.", "8. Evaluar introducción de sistemas silvopastoriles en zonas donde aplique.", "9. Medir indicadores productivos mensualmente: litros/ha/día, carga animal real, condición corporal."]',
  'KPI_LITROS_HA_ANIO',
  'ALTA',
  180,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P02 — SUPLEMENTACIÓN ESTRATÉGICA EN ÉPOCA SECA
-- Fuentes:
--   FEDEGAN Manual Práctico Ganadero — suplementación:
--     contextoganadero.com/ganaderia-sostenible/suplementacion-estrategica
--   ICA — Utilización de henos y ensilajes en vacas:
--     repository.agrosavia.co/handle/20.500.12324/15718
--     "época seca reduce producción hasta 50% y forraje hasta 60%"
--   FINAGRO — balance forraje-concentrado bovinos:
--     finagro.com.co/noticias/ganadero-vigile-balance-forraje-concentrado
--   Scielo Cundinamarca — suplementación estratégica lechería:
--     scielo.org.pe/scielo.php?pid=S1609-91172019000300014
--     "costos alimentación representan 40% costos producción trópico alto"
-- ============================================================

-- P02 versión PEQUEÑA/MEDIANA (todos los trópicos, diferenciada)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Suplementación estratégica en época seca',
  'Suministro planificado de alimentos adicionales durante los períodos de menor disponibilidad de forraje para mantener la producción de leche y la condición corporal de las vacas.',
  'Prevenir la caída de producción de leche en época seca, que puede llegar hasta el 50% según el ICA, manteniendo el balance energético de las vacas lactantes.',
  'PRODUCTIVIDAD',
  'Previene caída de producción >25% en época seca, mejora KPI_LITROS_VACA_DIA y KPI_IOFC en períodos críticos.',
  'ACTIVA',
  '["1. Identificar los meses de menor lluvia en tu región consultando el IDEAM o el historial propio.", "2. Estimar el déficit forrajero: comparar el aforo de potreros con la necesidad de las vacas (2,5% del peso vivo en materia seca).", "3. Conseguir fuentes de suplemento según disponibilidad local: ensilaje de maíz, heno, subproductos de cosecha, concentrado.", "4. En trópico frío: suplementar con ensilaje de maíz + concentrado 1-2 kg/vaca/día según producción.", "5. En trópico templado/cálido: aprovechar subproductos locales (melaza, palmiste, semilla de algodón) más económicos.", "6. Suministrar el suplemento 2 horas antes del ordeño para maximizar respuesta en leche.", "7. Ajustar la dosis: una vaca de 450 kg produciendo 10 L/día necesita 11 kg de materia seca total.", "8. Monitorear la condición corporal semanalmente — debe mantenerse en 2,5-3,5 en escala de 1 a 5."]',
  'KPI_LITROS_VACA_DIA',
  'MEDIA',
  60,
  'PEQUEÑA',
  'TODOS'
);

-- P02 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Suplementación estratégica en época seca',
  'Programa formal de alimentación suplementaria con análisis bromatológico, formulación de dietas y conservación de forrajes propios para estabilizar la producción en épocas críticas.',
  'Eliminar la estacionalidad de la producción, reducir dependencia de concentrados comerciales y optimizar el costo por litro de leche en época seca.',
  'PRODUCTIVIDAD',
  'Reduce variación estacional de producción a menos del 10%, mejora KPI_COSTO_LITRO 10-20% vs compra de concentrado, mejora KPI_IOFC.',
  'ACTIVA',
  '["1. Contratar nutricionista para formular dieta balanceada según etapa de lactancia y producción.", "2. Implementar producción propia de ensilaje en época de lluvias para usar en época seca.", "3. Instalar silos de trinchera o bolsa según el volumen requerido.", "4. Realizar análisis bromatológico del forraje ensilado para ajustar la ración.", "5. Formular concentrado a mínimo costo con materias primas locales: según estudios CORPOICA reduce costo suplementación 16-31%.", "6. Establecer contratos de suministro anticipado de insumos para asegurar precio.", "7. Monitorear producción individual y condición corporal semanalmente.", "8. Ajustar dietas cada 3-4 semanas según respuesta productiva del hato."]',
  'KPI_LITROS_VACA_DIA',
  'ALTA',
  90,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P03 — PROTOCOLO DE ORDEÑO HIGIÉNICO
-- Fuentes:
--   Scielo U.D.C.A — BPO Ubaté y Chiquinquirá:
--     revistas.udca.edu.co/index.php/ruadc/article/view/611
--     "75% ordenan correctamente, solo 10% hace peluqueado de ubres"
--   ICA — Buenas Prácticas Ganaderas:
--     ica.gov.co/getattachment/Areas/Pecuaria/.../GENERALIDADES-DE-BPG
--   Scielo — Mastitis Cundiboyacense (CMT):
--     scielo.org.co/scielo.php?pid=S0120-06902008000400006
--   Scielo Montería — factores protección mastitis:
--     scielo.org.co/scielo.php?pid=S0123-42262009000200007
--     "sellado pezones: 0,40 veces menos probabilidad de mastitis"
--   Ganadería California — buenas prácticas ordeño:
--     gan.ciacalifornia.com.co/2021/02/19/buenas-practicas-de-ordeno
-- ============================================================

-- P03 versión TODOS (con diferenciación interna manual vs mecánico)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Protocolo de ordeño higiénico',
  'Implementación de la rutina estandarizada de ordeño con énfasis en higiene, prevención de mastitis y obtención de leche de calidad, cumpliendo las Buenas Prácticas Ganaderas del ICA.',
  'Reducir la incidencia de mastitis, mejorar la calidad higiénica de la leche para acceder a bonificaciones, y aumentar la producción por vaca mediante un estímulo adecuado.',
  'PRODUCTIVIDAD',
  'Reduce mastitis subclínica hasta 40% (Scielo Montería 2009), mejora calidad higiénica para bonificaciones ICA hasta $100/L adicionales. Mejora KPI_LITROS_VACA_DIA e KPI_INGRESO_LITRO.',
  'ACTIVA',
  '["1. ANTES DEL ORDEÑO: lavar manos y brazos del ordeñador, preparar utensilios limpios.", "2. Traer las vacas con calma — el estrés reduce la eyección de leche hasta 20%.", "3. DESPUNTE: extraer los primeros 2-3 chorros de leche en recipiente oscuro para detectar mastitis clínica.", "4. PRE-SELLADO: aplicar yodo al 0,5% en los pezones y dejar actuar 30 segundos.", "5. SECADO: secar cada pezón con toalla individual desechable o paño exclusivo por vaca.", "6. ORDEÑO: completar en 5-7 minutos máximo — el ordeño prolongado aumenta riesgo de mastitis.", "7. Para ordeño mecánico: verificar vacío entre 42-45 kPa, remover unidades sin dejarlas caer.", "8. POST-SELLADO: sellar pezones con yodo al 0,5% inmediatamente al terminar (reduce mastitis 40%).", "9. Mantener las vacas de pie 30 minutos post-ordeño para que el esfínter se cierre.", "10. CONTROL MENSUAL: aplicar CMT (California Mastitis Test) a todo el hato — meta: menos del 10% de cuartos positivos.", "11. LIMPIEZA: lavar utensilios con detergente, enjuague con agua caliente, desinfectar con yodo o cloro."]',
  'KPI_LITROS_VACA_DIA',
  'BAJA',
  30,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P04 — BANCO DE PROTEÍNA FORRAJERA
-- Fuentes:
--   Asocebú/CONtexto Ganadero — arbustos forrajeros trópico:
--     ecologiasocebu.blogspot.com/2013/12/arboles-y-arbustos-forrajeros
--     "leucaena 22.8% PC, matarratón 24% PC, morera 22.7% PC"
--     "incrementos hasta 40% producción leche con leucaena"
--   CORPOICA / Redalyc — BMF Leucaena, Tithonia, Gliricidia:
--     researchgate.net/publication/279443014
--     "25.46% PC, digestibilidad DIVMS 61.7%"
--   Redalyc SSPi Leucaena Colombia 2017:
--     redalyc.org/journal/2691/269158175001/html/
--     "leucaena: animales producen 2-3 veces más leche y carne/ha/año"
--   CONtexto Ganadero — morera silvopastoril:
--     contextoganadero.com/ganaderia-sostenible/la-morera-como-alimento
-- ============================================================

-- P04 versión CALIDO/TEMPLADO (donde aplica principalmente)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Banco de proteína forrajera',
  'Establecimiento de áreas compactas con arbustos y árboles de alto valor proteico (leucaena, matarratón, Tithonia, morera) para suplementar el hato con forraje de calidad, especialmente en época seca.',
  'Reducir el costo de concentrados hasta 30%, mejorar la dieta proteica del hato con fuentes propias y aumentar la producción de leche con recursos forrajeros locales.',
  'PRODUCTIVIDAD',
  'Según CORPOICA/Redalyc 2017: animales con leucaena producen 2-3 veces más leche/ha/año. Mejora KPI_LITROS_VACA_DIA, KPI_IOFC y reduce KPI_COSTO_LITRO.',
  'ACTIVA',
  '["1. Reservar el 10-15% del área de pastoreo para el banco de proteína.", "2. Seleccionar especie según trópico: trópico cálido y templado bajo → leucaena (Leucaena leucocephala) o matarratón (Gliricidia sepium); trópico templado y frío → Tithonia diversifolia (botón de oro) o morera (Morus alba).", "3. Preparar el suelo y sembrar al inicio de la temporada de lluvias.", "4. En leucaena: sembrar en alta densidad (5.000-10.000 plantas/ha) en surcos para silvopastoril.", "5. Cercar el banco los primeros 6 meses para que las plantas se establezcan sin pastoreo.", "6. Primer corte: leucaena a los 90 días, Tithonia a los 60 días, morera a los 90 días.", "7. Suministrar 2-3 kg de materia fresca por vaca al día inicialmente, aumentar gradualmente.", "8. Combinar con pastos en la ración — no suministrar solo el banco de proteína.", "9. Evaluar reducción en costo de concentrado a los 6 meses — meta: reducción del 20-30%."]',
  'KPI_IOFC',
  'ALTA',
  180,
  'TODAS',
  'CALIDO'
);

-- P04 versión TEMPLADO/FRIO con Tithonia y morera
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Banco de proteína forrajera',
  'Establecimiento de sistemas silvopastoriles con Tithonia diversifolia (botón de oro) y morera para suplementar el hato lechero en zonas de trópico templado y frío.',
  'Reducir costos de concentrado y mejorar la dieta proteica con fuentes forrajeras propias adaptadas a climas de altura.',
  'PRODUCTIVIDAD',
  'Mejora KPI_LITROS_VACA_DIA, reduce KPI_COSTO_LITRO e KPI_IOFC con forraje de 25% PC según CORPOICA.',
  'ACTIVA',
  '["1. Seleccionar especie principal: Tithonia diversifolia (botón de oro) para trópico medio, morera para trópico alto (adapta hasta 2500 msnm).", "2. Para morera: seleccionar terreno fértil bien drenado, requiere fertilización regular — sembrar asociada con matarratón.", "3. Para Tithonia: sembrar por estacas de 20-30 cm, muy fácil de establecer, crece rápido.", "4. Cercar el banco los primeros 3-4 meses.", "5. Cosechar Tithonia cada 30-35 días — primer corte a 80-100 cm de altura.", "6. Picar el material y ofrecer en comedero — no pastoreo directo de Tithonia (tallos frágiles).", "7. Suministrar 3-5 kg de materia fresca picada por vaca/día.", "8. Evaluar impacto en producción y en costo de concentrado a los 90 días."]',
  'KPI_IOFC',
  'MEDIA',
  120,
  'TODAS',
  'FRIO'
);


-- ============================================================
-- P05 — PROGRAMA DE MEJORAMIENTO GENÉTICO
-- Fuentes:
--   Revista Investigación Agraria UNAD — IA e IATF bovinos:
--     hemeroteca.unad.edu.co/index.php/riaa/article/view/2050
--   Agrocolanta — programa IA dirigida:
--     agrocolanta.com/genetica/
--     "genética representa aproximadamente el 30% del desempeño"
--   Infortambo Andina — inseminación artificial bovina:
--     infortamboandina.co/es/noticias/inseminacion-artificial-bovina
--   FEDEGAN — mejoramiento genético programa Colombia:
--     thefoodtech.com/tecnologia-de-los-alimentos/mejoramiento-genetico
-- ============================================================

-- P05 versión PEQUEÑA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Programa de mejoramiento genético',
  'Mejora progresiva de la genética del hato mediante selección de reproductores y uso básico de inseminación artificial para aumentar la producción de leche por vaca.',
  'Aumentar gradualmente la producción por vaca mediante selección de los mejores animales del hato y acceso a semen de toros probados.',
  'PRODUCTIVIDAD',
  'La genética representa el 30% del desempeño animal (Agrocolanta). Mejora KPI_LITROS_VACA_DIA entre 15-30% a mediano plazo (2-3 lactancias).',
  'ACTIVA',
  '["1. Identificar las 3 vacas de menor producción del hato — son candidatas a descarte o cruzamiento.", "2. Seleccionar el mejor macho disponible en la zona para servicio natural, o iniciar con inseminación artificial.", "3. Para inseminación: contactar técnico del SENA, ICA o proveedor de semen con experiencia en la región.", "4. Seleccionar semen según el objetivo: mayor producción de leche (Holstein, Jersey para frío; Girolando para cálido).", "5. Guardar las mejores terneras hembras para reposición del hato.", "6. Registrar la genealogía de cada ternero nacido: madre, padre, fecha de nacimiento.", "7. Evaluar la producción de las hijas al primer parto para validar el mejoramiento."]',
  'KPI_LITROS_VACA_DIA',
  'MEDIA',
  365,
  'PEQUEÑA',
  'TODOS'
);

-- P05 versión MEDIANA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Programa de mejoramiento genético',
  'Programa sistemático de inseminación artificial con semen de toros probados, evaluación de descendencia y selección de reemplazos para acelerar el progreso genético del hato.',
  'Incrementar el mérito genético del hato en producción de leche y características de ubre, con resultados visibles en 2-3 generaciones.',
  'PRODUCTIVIDAD',
  'Programas similares en Colombia reportan incrementos del 20% en producción en 3 años. Mejora KPI_LITROS_VACA_DIA y KPI_INGRESO_VACA.',
  'ACTIVA',
  '["1. Implementar inseminación artificial con semen de toros con alto valor genético estimado (VGE) en producción.", "2. Establecer detección de celos 2 veces al día (mañana y tarde) — el 41% de los celos se manifiestan en la noche.", "3. Inseminar en el momento óptimo: 12-18 horas después de detectado el celo.", "4. Confirmar preñez a los 45 días por palpación rectal o ecosonografía.", "5. Vacas con más de 3 servicios sin preñez: evaluar causa e incluir en lista de descarte.", "6. Llevar registro de evaluación lineal: ubre, patas y conformación para corrección dirigida.", "7. Seleccionar toros diferentes para corregir debilidades de cada vaca (apareamiento dirigido).", "8. Evaluar producción de las hijas al primer parto y comparar con la madre."]',
  'KPI_LITROS_VACA_DIA',
  'MEDIA',
  365,
  'MEDIANA',
  'TODOS'
);

-- P05 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Programa de mejoramiento genético',
  'Programa formal con IATF (inseminación artificial a tiempo fijo), control lechero individual, evaluación genómica y selección intensiva de reemplazos para maximizar el progreso genético.',
  'Alcanzar los más altos valores genéticos disponibles para el trópico colombiano, con mérito genético medible y mejoramiento generacional documentado.',
  'PRODUCTIVIDAD',
  'IATF permite inseminar todo el hato en días definidos, mejorando uniformidad y progreso genético. Mejora KPI_LITROS_VACA_DIA >25% en 3 años.',
  'ACTIVA',
  '["1. Implementar IATF con protocolos de progesterona + estradiol para sincronizar todo el hato.", "2. Mínimo 45 días de involución post-parto antes del primer servicio.", "3. Usar semen de los mejores toros disponibles con prueba de descendientes en Colombia.", "4. Para trópico cálido: usar semen sexado de Girolando ¾ o ⅝ Holstein para priorizar hembras lecheras.", "5. Implementar control lechero individual mensual con Colanta o laboratorio certificado.", "6. Evaluar cada vaca: RCS (recuento células somáticas), proteína, grasa, urea en leche.", "7. Usar análisis de urea en leche para ajustar la dieta de proteína del hato.", "8. Seleccionar el 20% superior de novillas por índice genético estimado para reposición.", "9. Descartar el 10% inferior de vacas por producción y reproducción anualmente.", "10. Registrar toda la información en sistema digital para cálculo de índices genéticos."]',
  'KPI_LITROS_VACA_DIA',
  'ALTA',
  365,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P06 — OPTIMIZACIÓN DE FRECUENCIA DE ORDEÑO
-- Fuentes:
--   CONtexto Ganadero — producción leche pastoreo:
--     contextoganadero.com/ganaderia-sostenible/conozca-algunas-claves
--   Voisin — vaca lechera no debe permanecer más de 3 días en potrero:
--     infopastosyforrajes.com/sistemas-de-pastoreo/leyes-universales
--   Infortambo Andina — comparativa frío vs cálido (frecuencia ordeño):
--     infortamboandina.co/es/noticias/rentabilidad-en-las-lecherias
--   Genética Bovina — trópico bajo sin ternero 2 ordeños:
--     revistageneticabovina.com/biotecnologia/tropico-bajo/
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Optimización de frecuencia de ordeño',
  'Transición de 1 a 2 ordeños diarios o ajuste de horarios para maximizar la extracción de leche y aprovechar la curva de producción de las vacas.',
  'Aumentar la producción total diaria de leche entre 15-25% al pasar de 1 a 2 ordeños, mejorando la eficiencia productiva sin aumentar el número de animales.',
  'PRODUCTIVIDAD',
  'Paso de 1 a 2 ordeños incrementa producción 15-25%. Mejora KPI_LITROS_VACA_DIA y KPI_FRECUENCIA_ORDENIO. Requiere calcular si el ingreso adicional justifica el costo laboral.',
  'ACTIVA',
  '["1. Evaluar si el hato actualmente hace 1 ordeño diario — si es así, hay potencial directo de mejora.", "2. Calcular el costo adicional: 1 empleado extra × horas × salario vs ingreso adicional esperado.", "3. Si el análisis económico es favorable: establecer 2 ordeños diarios con máximo 14 horas entre ellos.", "4. Horarios recomendados: 5 AM y 5 PM, o 6 AM y 6 PM — consistencia es clave.", "5. Las vacas responden mejor cuando los horarios son fijos — el estrés del cambio dura solo 3-5 días.", "6. Para trópico cálido con doble propósito: separar el ternero durante la noche para ordeñar en la mañana sin apoyo.", "7. Medir producción diaria los 15 días antes y 30 días después del cambio para cuantificar el impacto.", "8. Si el aumento de producción no cubre el costo laboral adicional: evaluar mecanización parcial del ordeño."]',
  'KPI_LITROS_VACA_DIA',
  'BAJA',
  30,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P07 — MANEJO DEL PERÍODO SECO DE LA VACA
-- Fuentes:
--   Scielo Colombia — balance energético negativo y mérito genético:
--     scielo.org.co/scielo.php?pid=S0120-06902005000300004
--     "vacas con déficit energético presentan elevación de lípidos en sangre"
--   ICA — período seco y terapia vaca seca:
--     revistas.udca.edu.co/index.php/ruadc/article/view/611
--     "terapia vaca seca: antimastítico intramamario al final de lactancia"
--   FEDEGAN / Infortambo Andina:
--     "condición corporal al parto debe ser 3-3.5 en escala de 1 a 5"
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Manejo del período seco de la vaca',
  'Protocolo para el secado adecuado de las vacas 60 días antes del parto, con manejo nutricional y sanitario para garantizar una lactancia siguiente óptima.',
  'Garantizar que las vacas lleguen al parto en la condición corporal ideal (3-3.5/5), reducir la incidencia de mastitis y maximizar la producción en la siguiente lactancia.',
  'PRODUCTIVIDAD',
  'El período seco bien manejado mejora KPI_LACTANCIA_VS_ESTANDAR y KPI_LITROS_VACA_DIA en la siguiente lactancia 10-20%. Reduce mastitis post-parto.',
  'ACTIVA',
  '["1. Calcular la fecha de secado: 60 días antes de la fecha esperada de parto (usar fecha de servicio + 283 días).", "2. Reducir gradualmente la producción los 5 días previos al secado: disminuir la frecuencia de ordeño.", "3. Al último ordeño: aplicar sellador intramamario (terapia vaca seca) en cada cuarto — hacerlo con el veterinario.", "4. Sellar los pezones con yodo y no volver a ordeñar hasta el próximo parto.", "5. NUTRICIÓN en período seco: reducir energía los primeros 45 días — pastos de menor calidad.", "6. STEAMING UP: las últimas 3 semanas antes del parto aumentar gradualmente el concentrado (1 kg/día/semana).", "7. Al parto: la condición corporal debe estar en 3.0-3.5 en escala de 1 a 5.", "8. Si la vaca llega al parto con CC < 2.5: presentará balance energético negativo severo, mayor riesgo de cetosis.", "9. Registrar fecha de secado, fecha esperada de parto y condición corporal al secar."]',
  'KPI_LACTANCIA_VS_ESTANDAR',
  'MEDIA',
  60,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P08 — NUTRICIÓN POR ETAPA PRODUCTIVA
-- Fuentes:
--   Scielo Colombia — balance energético negativo:
--     scielo.org.co/scielo.php?pid=S0120-06902005000300004
--     "80% de la glucosa requerida va a la glándula mamaria"
--   FINAGRO — balance forraje-concentrado:
--     finagro.com.co/noticias/ganadero-vigile-balance-forraje-concentrado
--     "vaca 450 kg, 20 L/día: 11 kg MS total (7.7 pasto + 3.3 concentrado)"
--   Scielo Cundinamarca — suplementación estratégica:
--     scielo.org.pe/scielo.php?pid=S1609-91172019000300014
--   Infortambo Andina — IA bovina y condición corporal:
--     infortamboandina.co/es/noticias/inseminacion-artificial-bovina
-- ============================================================

-- P08 versión PEQUEÑA/MEDIANA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Nutrición por etapa productiva',
  'Ajuste de la alimentación de las vacas según su etapa de lactancia (temprana, media, tardía y seca) para cubrir adecuadamente sus requerimientos en cada fase y evitar el balance energético negativo.',
  'Reducir el balance energético negativo post-parto, maximizar el pico de lactancia y mejorar la eficiencia alimenticia durante toda la lactancia.',
  'PRODUCTIVIDAD',
  'La lactancia temprana bien alimentada define el pico — una vaca que alcanza mayor pico produce más durante toda la lactancia. Mejora KPI_LITROS_VACA_DIA y KPI_IOFC.',
  'ACTIVA',
  '["1. Clasificar las vacas del hato en 4 grupos: pre-parto (última semana), lactancia temprana (0-100 días), lactancia media (101-200 días), lactancia tardía y seca.", "2. LACTANCIA TEMPRANA (0-100 días): es el período más crítico. La vaca produce más leche de lo que puede comer. Aumentar concentrado gradualmente hasta 1 kg por cada 2-2.5 L de leche.", "3. Una vaca de 450 kg que produce 20 L/día necesita ~11 kg de materia seca: 7.7 kg de pasto + 3.3 kg de concentrado.", "4. LACTANCIA MEDIA (101-200 días): la producción empieza a bajar. Mantener buena condición corporal. Reducir concentrado gradualmente.", "5. LACTANCIA TARDÍA (>200 días): reducir energía, dejar que la vaca acumule reservas para el siguiente parto.", "6. Monitorear condición corporal mensualmente — si baja de 2.5 en lactancia temprana: aumentar concentrado urgente.", "7. Suministrar sal mineralizada libre todo el tiempo — las vacas en producción necesitan calcio, fósforo y magnesio.", "8. Registrar producción diaria por vaca y comparar con la semana anterior para detectar caídas tempranas."]',
  'KPI_LITROS_VACA_DIA',
  'MEDIA',
  90,
  'PEQUEÑA',
  'TODOS'
);

-- P08 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Nutrición por etapa productiva',
  'Programa formal de alimentación con formulación de raciones por grupo productivo, análisis de leche para evaluar estado nutricional y ajuste continuo basado en datos individuales.',
  'Maximizar la eficiencia alimenticia, reducir el costo por litro de leche y prevenir enfermedades metabólicas mediante alimentación de precisión.',
  'PRODUCTIVIDAD',
  'Nutrición de precisión reduce KPI_COSTO_LITRO 10-20% vs alimentación homogénea. Mejora KPI_LITROS_VACA_DIA, KPI_IOFC y reduce enfermedades metabólicas post-parto.',
  'ACTIVA',
  '["1. Contratar nutricionista para formular raciones por grupo (mínimo 4 grupos: pre-parto, alta producción, media y seca).", "2. Separar físicamente las vacas por grupo productivo — la alimentación diferenciada requiere grupos separados.", "3. Analizar mensualmente el nivel de urea en leche (NUL) — valores >16 mg/dL indican exceso de proteína.", "4. Monitorear cetosis en posparto temprano: tiras de cuerpos cetónicos en leche o orina.", "5. Implementar steaming up 3 semanas preparto: incrementar 0.5 kg concentrado/día para preparar el rumen.", "6. Usar análisis bromatológico de forrajes cada 6 meses para ajustar la formulación.", "7. Implementar índice de condición corporal (ICC) 1-5 para todas las vacas al parto, secado y reproducción.", "8. Registrar consumo de alimento, producción y condición corporal en sistema digital para análisis.", "9. Evaluar costo de alimentación por litro producido mensualmente y ajustar cuando supere el benchmark."]',
  'KPI_LITROS_VACA_DIA',
  'ALTA',
  90,
  'GRANDE',
  'TODOS'
);

-- ============================================================
-- FASE 3.2 — CATÁLOGO DE PRÁCTICAS DE MANEJO DE HATO
-- Hathor — 5 prácticas con variantes por escala
-- ============================================================

-- ============================================================
-- P09 — PLAN REPRODUCTIVO CON DETECCIÓN DE CELOS
-- Fuentes:
--   Intagri — parámetros reproductivos bovino:
--     intagri.com/articulos/ganaderia/parametros-reproductivos-del-ganado-bovino
--     "Meta: intervalo entre partos ≤385 días, días abiertos 50-110"
--   CONtexto Ganadero / FEDEGAN FNG — programación partos:
--     contextoganadero.com/ganaderia-sostenible/la-formula-para-que-los-intervalos
--     "Colombia: hatos DP con IEP de 680-700 días, lechería especia. desde 664"
--     "ganaderos pierden dinero por cada día abierto — período improductivo"
--   Genética Bovina Colombia — índices reproductivos lechería:
--     revistageneticabovina.com/reproduccion/indices-reproductivos/
--     "causa más frecuente del atraso: falla en detección del celo"
--     "solo 25-35% de vacas tienen período abierto conforme a meta"
--   Club Ganadero — días abiertos bovinos:
--     clubganadero.com/dias-abiertos-en-vacas/
--   Infortambo Andina — IA bovina detección celo:
--     infortamboandina.co/es/noticias/inseminacion-artificial-bovina
--     "63.8% de celos ocurren sin supervisión humana, 41% en la noche"
-- ============================================================

-- P09 versión PEQUEÑA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Plan reproductivo con detección de celos',
  'Implementación de un calendario reproductivo básico para asegurar que cada vaca quede preñada en el menor tiempo posible después del parto, mejorando el intervalo entre partos.',
  'Reducir el intervalo entre partos a menos de 400 días, disminuyendo los días improductivos y aumentando los ingresos por más terneros y más días en lactancia.',
  'HATO',
  'Reducción de IEP de más de 450 días a menos de 400 días mejora KPI_LACTANCIA_VS_ESTANDAR y KPI_PCT_VACAS_ORDENIO directamente. Cada día abierto por encima de 110 días representa pérdida de ingreso.',
  'ACTIVA',
  '["1. Registrar la fecha de parto de cada vaca en cuaderno o tabla visible en el establo.", "2. Marcar en el calendario el día 50 post-parto: desde ese día comenzar a observar el celo.", "3. Signos de celo: la vaca permite que otras la monten, se muestra inquieta, produce menos leche, la vulva está inflamada.", "4. Observar el hato 3 veces al día: en la mañana, al medio día y al final del día — el 41% de los celos ocurren en la noche.", "5. Al detectar el celo: servir la vaca entre 12-18 horas después de la detección con monta natural o inseminación.", "6. Si a los 21 días no repite el celo: probablemente quedó preñada — confirmar a los 45-60 días por palpación.", "7. Si repite el celo más de 3 veces sin quedar preñada: llamar al veterinario para revisión.", "8. Meta: que el 70% de las vacas queden preñadas en los primeros 3 meses post-parto."]',
  'KPI_LACTANCIA_VS_ESTANDAR',
  'BAJA',
  90,
  'PEQUEÑA',
  'TODOS'
);

-- P09 versión MEDIANA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Plan reproductivo con detección de celos',
  'Programa reproductivo estructurado con registros individuales, detección sistemática de celos e inseminación artificial para lograr metas reproductivas medibles.',
  'Alcanzar un intervalo entre partos de 385-400 días y días abiertos menores a 110, con tasa de preñez superior al 15% por ciclo de 21 días.',
  'HATO',
  'Reducción de días abiertos mejora KPI_LACTANCIA_VS_ESTANDAR, KPI_PCT_VACAS_ORDENIO y KPI_HEMBRAS_RECRIA_VACA. Vacas con más partos generan más ingresos totales.',
  'ACTIVA',
  '["1. Establecer período de espera voluntaria (PEV): no inseminar antes del día 50 post-parto.", "2. Llevar registro individual por vaca: fecha de parto, primer celo, servicios, diagnóstico de gestación.", "3. Observar el hato 3 veces al día: mañana (6 AM), mediodía (12 PM) y tarde (6 PM) durante 20-30 minutos.", "4. Usar detección auxiliar: pintura en la grupa (Scratchcard) o parche detector de monta para confirmar celo nocturno.", "5. Inseminar en la ventana de 12-18 horas post-detección — el momento correcto define la tasa de concepción.", "6. Diagnóstico de gestación a los 45 días por ecosonografía o a los 60 días por palpación rectal.", "7. Vacas con 3 o más servicios sin preñez: revisión reproductiva por veterinario (quiste, endometritis, etc.).", "8. Calcular mensualmente: tasa de detección de celos (TDC), tasa de concepción (TC) y tasa de preñez (TP = TDC × TC / 100).", "9. Meta según Genética Bovina Colombia: TDC >50%, TC >50%, TP >22%."]',
  'KPI_LACTANCIA_VS_ESTANDAR',
  'MEDIA',
  90,
  'MEDIANA',
  'TODOS'
);

-- P09 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Plan reproductivo con detección de celos',
  'Programa reproductivo formal con IATF, monitoreo digital, chequeo reproductivo periódico y análisis de indicadores para optimizar la eficiencia del hato al máximo nivel.',
  'Lograr tasa de preñez >25% por ciclo de 21 días e intervalo entre partos <390 días mediante protocolos hormonales y monitoreo sistemático.',
  'HATO',
  'IATF elimina la dependencia de detección de celos. Mejora KPI_LACTANCIA_VS_ESTANDAR >20 días vs sistema sin programa. Incrementa KPI_PCT_VACAS_ORDENIO y KPI_HEMBRAS_RECRIA_VACA.',
  'ACTIVA',
  '["1. Implementar IATF con protocolo de progesterona + estradiol a los 50-60 días post-parto.", "2. Agrupar partos para hacer IATF en lotes — eficiencia de mano de obra y hormonal.", "3. Implementar sistema de monitoreo de actividad (collares o podómetros) para detección automatizada de celo.", "4. Chequeo reproductivo mensual por veterinario: detección de anestro, quistes, endometritis.", "5. Ecografía de diagnóstico a los 28-35 días post-servicio para confirmación temprana.", "6. Re-sincronización inmediata de vacas no preñadas: no esperar el siguiente celo.", "7. Registrar todos los eventos reproductivos en sistema digital — calcular TP, TDC, TC mensualmente.", "8. Monitorear condición corporal al parto, al servicio y al diagnóstico — es el predictor reproductivo más importante.", "9. Meta empresarial: IEP <390 días, TP >25%, SPC <1.8, mortalidad perinatal <3%."]',
  'KPI_LACTANCIA_VS_ESTANDAR',
  'ALTA',
  90,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P10 — SELECCIÓN Y DESCARTE DE ANIMALES IMPRODUCTIVOS
-- Fuentes:
--   CONtexto Ganadero / Sinergia Gestión Agropecuaria — descarte:
--     contextoganadero.com/ganaderia-sostenible/por-que-el-criterio-para-descartar
--     "criterio NO debe ser el número de partos — evaluar desempeño reproductivo"
--   CONtexto Ganadero — qué saber para descartar vacas:
--     contextoganadero.com/ganaderia-sostenible/que-necesita-saber-para-descartar-vacas
--   CONtexto Ganadero / CORPOICA — vacas improductivas:
--     contextoganadero.com/ganaderia-sostenible/los-ganaderos-pueden-prevenir
--     "improductiva: no genera suficiente leche ni se preña — Luis Carlos Arreaza CORPOICA"
--     "salida voluntaria: patologías reproductivas, mastitis, cojera"
--     "salida involuntaria: baja producción, deficiencia de preñez"
--   Zoovetesmipasion — criterios descarte lechería:
--     zoovetesmipasion.com/ganaderia/descarte-de-vacas
--     "máx. 3 servicios IA + 2-3 toro = 6 meses posparto → decisión descarte"
--   INIA La Estanzuela — clasificación descartes tambo:
--     ainfo.inia.uy/digital/bitstream/item/14745/1/SAD795-p.1-6-Doncel.pdf
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Selección y descarte de animales improductivos',
  'Proceso sistemático de evaluación del hato para identificar vacas que no pagan su estadía y tomar decisiones de retiro oportunas, reemplazándolas con animales de mejor genética.',
  'Mejorar la eficiencia productiva y reproductiva del hato eliminando los animales que generan costos sin retorno, y elevar el promedio genético a través de la selección de reemplazos.',
  'HATO',
  'El descarte oportuno libera recursos para vacas productivas. Mejora KPI_PCT_VACAS_ORDENIO, KPI_LITROS_VACA_DIA promedio del hato y KPI_MARGEN_NETO al reducir carga improductiva.',
  'ACTIVA',
  '["1. Identificar las vacas candidatas a descarte evaluando 4 criterios principales: a) producción menor al 60% del promedio del hato, b) más de 3 servicios sin preñez en la misma lactancia, c) mastitis crónica en más de 2 cuartos, d) cojera severa o problema locomotor sin respuesta a tratamiento.", "2. CRITERIO REPRODUCTIVO (prioridad 1): si a los 6 meses post-parto la vaca no ha quedado preñada con todos los manejos aplicados → candidata a descarte. Según Zoovetesmipasion: el criterio de 3 IA + 2-3 toro = ~6 meses de espera, muy poco rentable seguir.", "3. CRITERIO PRODUCCIÓN (prioridad 2): calcular el ingreso total de la vaca en la lactancia actual. Si el ingreso por leche no cubre el costo de alimentación + sanidad → no paga su estadía.", "4. NO descartar solo por número de partos — según CONtexto Ganadero 2024: una vaca de 9 partos pero con buena producción y preñez es más rentable que una de 3 partos con problemas.", "5. Vacas con mastitis crónica: evaluar recuento de células somáticas individual. Si supera 1.000.000 células/mL en 2 muestras consecutivas → cuarto o vaca improductiva.", "6. Momento óptimo de venta: al inicio de la lactancia siguiente o en el mejor peso corporal disponible para maximizar el valor de venta.", "7. Reemplazar los animales descartados con novillas seleccionadas por producción de la madre y buen desarrollo.", "8. Calcular anualmente la tasa de descarte del hato: debe estar entre 15-25% para mantener renovación genética sin afectar inventario."]',
  'KPI_PCT_VACAS_ORDENIO',
  'MEDIA',
  30,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P11 — MANEJO DE CARGA ANIMAL Y CAPACIDAD DE PASTOREO
-- NOTA: Esta práctica complementa P01 (pastoreo rotacional)
-- enfocándose en el cálculo y ajuste de la carga animal
-- como indicador de gestión del hato.
-- Fuentes:
--   CONtexto Ganadero / FEDEGAN Asistegán — cálculo UGG/ha:
--     contextoganadero.com/ganaderia-sostenible/asi-puede-calcular-la-carga-animal
--     "1 UGG = 450 kg peso vivo/ha. Vacas secas = 30% de vacas en producción"
--   Infortambo Andina — cálido 4.75-5.7 vacas/ha (Fase 2 sesión)
--   UPRA 2024 Valle del Cauca — 2.24 UGG/ha mediano templado (Fase 2)
--   Scielo Valle del Cauca 2018 — 3.08-3.16 UGG/ha lechería especial.
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Manejo de carga animal y capacidad de pastoreo',
  'Cálculo y ajuste permanente de la carga animal del predio en función de la disponibilidad real de forraje, para evitar el sobrepastoreo o el subutilización de las praderas.',
  'Mantener la carga animal en el rango óptimo según el trópico y la especie forrajera, maximizando la producción por hectárea sin degradar las praderas.',
  'HATO',
  'La carga animal correcta mejora KPI_LITROS_HA_ANIO y KPI_CARGA_ANIMAL hacia los benchmarks. Evita degradación de praderas que reduce producción a largo plazo.',
  'ACTIVA',
  '["1. Calcular la carga actual: convertir todos los animales a UGG (1 UGG = 450 kg de peso vivo). Ejemplo: vaca de 400 kg = 0.89 UGG; ternero de 200 kg = 0.44 UGG.", "2. Calcular UGG totales del hato y dividir entre el número de hectáreas de pastoreo disponibles.", "3. Comparar con los rangos óptimos por trópico: FRÍO 2.5-4 UGG/ha, TEMPLADO 2-3 UGG/ha (UPRA 2024: 2.24), CÁLIDO 3-5 UGG/ha.", "4. Si la carga supera el rango óptimo: reducir el hato, implementar suplementación o renovar praderas.", "5. En época seca: la capacidad de carga se reduce hasta 40-50% — ajustar el hato o aumentar la suplementación para evitar degradación.", "6. Aforar los potreros al menos 2 veces al año (inicio de lluvias y fin de lluvias): lanzar marco 1x1m, pesar el forraje disponible.", "7. Calcular la producción de forraje vs el consumo diario del hato (2.5-3% del peso vivo en materia seca por animal/día).", "8. Registrar la condición corporal promedio del hato cada 30 días: si baja de 2.5 en vacas en producción → la carga es excesiva o el forraje es insuficiente.", "9. Ajustar la carga antes de la época seca — no después de que el potrero se degrade."]',
  'KPI_CARGA_ANIMAL',
  'MEDIA',
  60,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P12 — TRAZABILIDAD ANIMAL BÁSICA
-- Fuentes:
--   ICA — SINIGAN: nueva plataforma identificación:
--     ica.gov.co/noticias/ica-sinigan-nueva-herramienta-para-ganaderos
--   MinAgricultura/ICA — SINIGAN V6 diciembre 2025:
--     minagricultura.gov.co/noticias/Paginas/sinigan-v6
--     "app móvil para ganaderos, registro digital obligatorio"
--   CONtexto Ganadero — trazabilidad deber del ICA y ganaderos:
--     contextoganadero.com/regiones/trazabilidad-bovina-en-colombia
--   Ley 914 de 2004 — Sistema Nacional de Identificación SINIGAN
--   Agricultura & Ganadería — SINIGAN eventos del animal:
--     agriculturayganaderia.com/colombia-podra-superar-la-barrera-arancelaria
--     "eventos: nacimiento, muerte, movilización, tratamiento, hurto, cambio propiedad"
--   Agrocolanta — control lechero individual, calidad leche por vaca:
--     agrocolanta.com/genetica/
-- ============================================================

-- P12 versión PEQUEÑA/MEDIANA (trazabilidad básica)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Trazabilidad animal básica',
  'Implementación del sistema de identificación individual de animales conforme a la Ley 914 de 2004 y el SINIGAN V6, con registros básicos de eventos por animal para la toma de decisiones en el hato.',
  'Cumplir con la obligación legal de trazabilidad bovina en Colombia, habilitar el acceso a mercados formales y obtener información individual que permita tomar decisiones técnicas en el hato.',
  'HATO',
  'La trazabilidad es requisito para BPG, bonificaciones por calidad sanitaria ICA y acceso a mercados formales. Mejora KPI_INGRESO_LITRO al habilitar bonificaciones adicionales.',
  'ACTIVA',
  '["1. OBLIGACIÓN LEGAL: inscribir el predio y el inventario animal en SINIGAN V6 (www.sinigan.co). Desde diciembre 2025, el sistema opera con app móvil y registro digital obligatorio.", "2. Identificar todos los animales mayores de 2 meses con el Dispositivo de Identificación Nacional (DIN) — el ICA y FEDEGAN prestan este servicio.", "3. Registrar en SINIGAN los eventos de cada animal: nacimiento (con madre y fecha), muerte, movilización, tratamiento, cambio de propiedad.", "4. Establecer un sistema básico de registro en la finca: cuaderno o tabla con columnas para identificación del animal, fecha de nacimiento, madre, fecha del parto, producción, servicios y diagnóstico.", "5. Identificar visualmente cada vaca con arete numerado visible desde lejos para facilitar el manejo diario.", "6. Registrar la producción individual de cada vaca al menos 1 vez por semana.", "7. Guardar todos los documentos veterinarios: recetas, análisis, vacunaciones — son parte de la trazabilidad.", "8. Con esta información se puede: calcular la producción acumulada por lactancia, identificar las mejores vacas, justificar ante la industria la calidad higiénica del producto."]',
  'KPI_INGRESO_LITRO',
  'BAJA',
  30,
  'PEQUEÑA',
  'TODOS'
);

-- P12 versión GRANDE/EMPRESARIAL (trazabilidad avanzada + control lechero)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Trazabilidad animal básica',
  'Sistema integral de identificación y registro individual con control lechero mensual, análisis de composición de leche y trazabilidad completa desde el nacimiento hasta el descarte.',
  'Maximizar la información disponible por animal para optimizar decisiones de alimentación, sanidad, reproducción y genética, con trazabilidad completa para mercados de exportación.',
  'HATO',
  'Control lechero mensual permite calcular índices genéticos y ajustar dietas. Mejora KPI_LITROS_VACA_DIA 10-15%, KPI_INGRESO_LITRO por bonificaciones y habilita acceso a mercados premium.',
  'ACTIVA',
  '["1. Registrar en SINIGAN V6 todos los eventos de cada animal con app móvil en tiempo real.", "2. Implementar control lechero individual mensual con laboratorio certificado (Colanta, Agrocolanta, u otro).", "3. Analizar cada muestra de leche por vaca: litros/día, grasa %, proteína %, RCS (recuento células somáticas), NUL (nitrógeno ureico en leche).", "4. Usar RCS individual para decisiones de manejo: >400.000 células/mL → revisión de cuarto; >1.000.000 → candidata a descarte.", "5. Usar NUL para ajustar proteína de la dieta: valores >16 mg/dL indican exceso de proteína, <10 indican deficiencia.", "6. Calcular la producción en 305 días por lactancia para comparar vacas y calcular índices genéticos.", "7. Reportar a SINIGAN y al programa de mejoramiento genético los datos productivos para cálculo de VGE (valor genético estimado).", "8. Mantener historial digital completo de cada animal: 5 últimas lactancias, registros reproductivos, tratamientos, genealogía.", "9. Usar la información para: selección de madres de toros, decisiones de IATF, negociación de precio de leche con la industria."]',
  'KPI_INGRESO_LITRO',
  'ALTA',
  60,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P13 — PLAN SANITARIO PREVENTIVO Y CONTROL DE MASTITIS
-- Fuentes:
--   ICA — Recomendaciones sanidad e inocuidad ganaderos:
--     ica.gov.co/getattachment/.../Recomendaciones-a-ganaderos-en-sanidad
--     "2 ciclos anuales vacunación aftosa: mayo-junio y noviembre-diciembre (Ley 395/1997)"
--   FEDEGAN — Salud y Bienestar Animal:
--     fedegan.org.co/programas/sanidad-animal
--     "vacunación aftosa y brucelosis: obligatoria. Brucelosis: terneras 3-9 meses"
--   Asocebú — Plan sanitario ganadero:
--     asocebu.com/plan-sanitario/
--   CONtexto Ganadero — guía plan básico vacunación:
--     contextoganadero.com/ganaderia-sostenible/vea-una-guia-de-plan-basico-de-vacunacion
--     "vacunas obligatorias + clostridiales + IBR/BVD según zona"
--   Scielo Cundiboyacense — BPO y mastitis (Fase 3.1):
--     scielo.org.co/scielo.php?pid=S0120-06902008000400006
--   FEDEGAN — brucelosis pérdidas 3-10 millones COP/animal infectado
-- ============================================================

-- P13 versión PEQUEÑA/MEDIANA (plan sanitario básico)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Plan sanitario preventivo y control de mastitis',
  'Implementación del calendario sanitario obligatorio (ICA) más medidas preventivas de mastitis y desparasitación para mantener la salud del hato y la calidad de la leche.',
  'Prevenir enfermedades que afectan la producción de leche, cumplir con las exigencias sanitarias del ICA para movilización y comercialización, y reducir la pérdida por mastitis.',
  'HATO',
  'La mastitis reduce producción individual 10-25%. Controlarla mejora KPI_LITROS_VACA_DIA e KPI_INGRESO_LITRO por mejor RCS. Cumplir BPG habilita bonificaciones ICA de $14.5-$29/L adicionales.',
  'ACTIVA',
  '["1. VACUNACIÓN OBLIGATORIA (Ley 395/1997): vacunar TODO el hato contra fiebre aftosa en los 2 ciclos anuales del ICA (mayo-junio y noviembre-diciembre).", "2. BRUCELOSIS (obligatorio): vacunar todas las terneras entre 3 y 9 meses con Cepa 19 o Cepa RB51 en los mismos ciclos. NO vacunar hembras adultas con Cepa 19 ni machos de ninguna edad.", "3. ENFERMEDADES CLOSTRIDIALES: vacunar contra carbón sintomático, edema maligno y enterotoxemia — 2 veces al año o según recomendación veterinaria de la zona.", "4. DESPARASITACIÓN: rotar productos antiparasitarios cada 6 meses para evitar resistencia. Aplicar al inicio de la temporada seca y al inicio de las lluvias.", "5. CONTROL DE MASTITIS — CMT mensual: aplicar prueba California Mastitis Test a todo el hato el primer día de cada mes, antes del ordeño de la mañana.", "6. Vacas con CMT positivo (+): confirmar con recuento de células somáticas (laboratorio) y tratar con antimastítico según prescripción veterinaria.", "7. TERAPIA VACA SECA: al secar cada vaca, aplicar sellador intramamario en todos los cuartos (ver P07).", "8. REGISTROS: llevar cuaderno con: fecha de vacunación, producto usado, lote, número de animales, resultado CMT mensual, tratamientos aplicados.", "9. INGRESO DE ANIMALES: todo animal que ingrese al hato debe tener resultados negativos a brucelosis y tuberculosis recientes."]',
  'KPI_LITROS_VACA_DIA',
  'BAJA',
  30,
  'PEQUEÑA',
  'TODOS'
);

-- P13 versión GRANDE/EMPRESARIAL (plan sanitario integral)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Plan sanitario preventivo y control de mastitis',
  'Programa sanitario integral con veterinario de planta, protocolo de control de mastitis por datos individuales, vigilancia epidemiológica continua y certificación BPG del ICA.',
  'Mantener un hato con RCS colectivo menor a 250.000 células/mL para acceder a todas las bonificaciones de calidad, con cobertura vacunal 100% y cero focos de brucelosis/tuberculosis.',
  'HATO',
  'RCS < 250.000 células/mL accede a bonificación máxima ICA. Certificación BPG habilita +$58/L en bonificaciones. Mejora KPI_INGRESO_LITRO y KPI_LITROS_VACA_DIA eliminando cuartos con mastitis crónica.',
  'ACTIVA',
  '["1. Contratar veterinario de planta o asistencia técnica mensual de mínimo 2 visitas/mes.", "2. Cumplir calendario vacunal completo: aftosa (obligatorio), brucelosis (obligatorio), IBR/BVD, Leptospira, Clostridiales, Rabia en zonas endémicas.", "3. Implementar análisis individual de RCS mensual vía control lechero — meta: <200.000 células/mL en 90% del hato.", "4. Protocolo mastitis clínica: cultivo bacteriológico antes de aplicar antibiótico — resistencia antimicrobiana es un problema creciente según ICA.", "5. Eliminar progresivamente vacas con mastitis crónica >1.000.000 células/mL en 3 muestras consecutivas.", "6. CERTIFICACIÓN BPG: tramitar ante ICA la certificación de Buenas Prácticas Ganaderas — habilita bonificación $14.5/L por BPG + hasta $29/L por hato libre de 2 enfermedades.", "7. CERTIFICACIÓN HATO LIBRE: iniciar proceso de certificación de predio libre de brucelosis (1 prueba ELISA negativa en todas las hembras >24 meses) y tuberculosis (2 pruebas de tuberculina con intervalo 4-6 meses).", "8. Implementar sistema HACCP básico para la leche: control de temperatura de acopio, registro de tratamientos con antibióticos y tiempo de retiro.", "9. Reportar a SINIGAN V6 todos los tratamientos aplicados, incluyendo medicamento, dosis, fecha y tiempo de retiro.", "10. Calcular mensualmente: incidencia de mastitis (nuevos casos/vacas en riesgo), tasa de cura y pérdida productiva atribuible a mastitis."]',
  'KPI_LITROS_VACA_DIA',
  'ALTA',
  90,
  'GRANDE',
  'TODOS'
);

-- ============================================================
-- FASE 3.3 — CATÁLOGO DE PRÁCTICAS FINANCIERAS
-- Hathor — 6 prácticas con variantes por escala
-- ============================================================

-- ============================================================
-- P14 — ANÁLISIS Y REDUCCIÓN DE COSTOS DE ALIMENTACIÓN
-- Fuentes:
--   FEDEGAN — El Alto Costo de Producir Leche 2024:
--     estadisticas.fedegan.org.co/DOC/download.jsp?iIdFiles=1069
--     "costos mano de obra subieron 43% entre 2021-2024 vs precio leche -6%"
--   FEDEGAN — Costos e indicadores productividad ganadería:
--     slideshare.net/slideshow/costos-e-indicadores-de-la-productividad
--     Estructura costos: suplementación 15-18%, praderas 5-6%, mano de obra 40-42%
--     Diferencia nacional vs sobresaliente: costo litro $690 vs $755 (aprox. 2012)
--   Scielo Cundinamarca — suplementación estratégica:
--     scielo.org.pe/scielo.php?pid=S1609-91172019000300014
--     "estrategias a mínimo costo reducen suplementación 16-31% en 4 fincas"
--   FINAGRO — Marco de Referencia Ganadería Leche 2017:
--     finagro.com.co/sites/default/files/node/basic-page/files/ganaderia_leche.pdf
--     Alimentación y mano de obra: rubros más altos en Antioquia
--   UPRA 2024 Valle del Cauca — alimentación $470/L + praderas $117/L = $587/L
--     de $1.607/L total (36.5% del costo)
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Análisis y reducción de costos de alimentación',
  'Revisión sistemática de la estructura de costos de alimentación del hato para identificar oportunidades de reducción sin sacrificar productividad, priorizando la producción de forraje propio y el uso eficiente de concentrados.',
  'Reducir el costo por litro de leche producido mejorando la relación costo-beneficio de la alimentación, que representa el principal rubro del costo total de producción.',
  'FINANCIERO',
  'Según Scielo Cundinamarca: formulación a mínimo costo reduce costo suplementación 16-31%. Según UPRA 2024: alimentación y praderas representan 36.5% del costo/litro — el rubro con mayor potencial de optimización. Impacta KPI_COSTO_LITRO e KPI_IOFC.',
  'ACTIVA',
  '["1. Calcular el costo actual de alimentación por litro: sumar todos los gastos del mes en concentrado + heno + ensilaje + sal + minerales y dividir entre los litros producidos ese mes.", "2. Comparar con el benchmark: según UPRA 2024, en medianos del Valle del Cauca la alimentación cuesta ~$587/L ($470 concentrado + $117 praderas). Si el tuyo es mayor, hay oportunidad de mejora.", "3. Evaluar la relación precio leche / precio concentrado: si un kilo de concentrado cuesta más de 1.5 veces el precio del litro de leche, analizar reducir la cantidad o cambiar la fuente.", "4. Análisis de sustitución: identificar qué fracción del concentrado puede reemplazarse con forraje de alto valor propio (banco proteico, ensilaje, subproductos locales).", "5. Cotizar al menos 3 proveedores de concentrado cada 3 meses — el mercado varía con los precios internacionales del maíz y soya.", "6. Considerar formulación propia a mínimo costo: mezcla de materias primas locales (palmiste, melaza, semilla de algodón en trópico cálido). Según CORPOICA reduce costo 16-31%.", "7. Calcular el costo de producir 1 kg de materia seca en el potrero vs el costo de comprarlo en concentrado — el forraje propio es típicamente 3-5 veces más barato.", "8. Registrar el gasto mensual en alimentación y calcularlo como porcentaje del ingreso bruto — meta: que no supere el 35% del ingreso."]',
  'KPI_COSTO_LITRO',
  'MEDIA',
  60,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P15 — PLANIFICACIÓN FINANCIERA MENSUAL
-- Fuentes:
--   FEDEGAN — Costos e indicadores: planeación estratégica ganadería:
--     slideshare.net/slideshow/costos-e-indicadores-de-la-productividad
--     "planeación estratégica aplicada a la ganadería + plan de seguimiento"
--   FEDEGAN — Alto costo producir leche: volatilidad costos vs ingresos
--   UPRA 2024 Valle del Cauca — desglose exacto costos directos vs indirectos
--     (alimentación, mano de obra, sanidad, reproducción, servicios, arrendamiento)
--   FINAGRO Marco Referencia Ganadería Leche 2017 — estructura financiera cuencas
-- ============================================================

-- P15 versión PEQUEÑA/MEDIANA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Planificación financiera mensual',
  'Implementación de un sistema básico de registro y seguimiento de ingresos y egresos del hato para conocer la rentabilidad real del negocio y tomar decisiones informadas.',
  'Conocer el margen neto real por litro de leche producido, identificar los meses de mayor presión de costos y planificar la liquidez para evitar endeudamiento innecesario.',
  'FINANCIERO',
  'Sin planificación financiera el ganadero no sabe si gana o pierde. Mejora KPI_MARGEN_NETO y KPI_RATIO_INGRESO_EGRESO al visibilizar dónde se escapan los recursos.',
  'ACTIVA',
  '["1. Abrir un cuaderno o tabla en Excel con 4 columnas: FECHA, DESCRIPCIÓN, INGRESO (+), EGRESO (-). Registrar TODOS los movimientos de dinero relacionados con el hato.", "2. INGRESOS típicos: venta de leche (guardar comprobantes de pago quincenales), venta de terneros, venta de animales de descarte, venta de estiércol, otros.", "3. EGRESOS típicos: concentrado, sal y minerales, vacunas y medicamentos, mano de obra, combustible, arrendamiento de tierra, mantenimiento de cercas.", "4. Al final de cada mes: sumar ingresos totales y egresos totales. La diferencia es el resultado del mes.", "5. Calcular: costo por litro = egresos totales del mes / litros producidos ese mes.", "6. Comparar el costo/litro con el precio recibido por litro para saber el margen real.", "7. Identificar el mes de mayor gasto (generalmente época seca o meses de vacunación) y reservar recursos en los meses buenos para cubrirlo.", "8. Meta de seguimiento: en 3 meses tener claridad sobre los 3 rubros de mayor gasto y haber reducido al menos uno."]',
  'KPI_MARGEN_NETO',
  'BAJA',
  30,
  'PEQUEÑA',
  'TODOS'
);

-- P15 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Planificación financiera mensual',
  'Sistema formal de contabilidad gerencial con presupuesto anual, seguimiento mensual de indicadores financieros y proyección de flujo de caja para optimizar la gestión empresarial del hato.',
  'Gestionar la empresa ganadera con información financiera en tiempo real, optimizar el capital de trabajo y tomar decisiones estratégicas de inversión y endeudamiento con datos sólidos.',
  'FINANCIERO',
  'Empresas con contabilidad formal toman mejores decisiones de inversión y acceden a crédito más fácilmente. Mejora KPI_ROA, KPI_ROTACION_ACTIVOS y KPI_MARGEN_NETO vía control de costos.',
  'ACTIVA',
  '["1. Contratar contador o usar software contable agropecuario para llevar contabilidad formal.", "2. Elaborar presupuesto anual al inicio de cada año: proyectar ingresos por leche, terneros y otros; proyectar egresos por cada rubro.", "3. Separar costos DIRECTOS (alimentación, sanidad, mano de obra de ordeño) de costos INDIRECTOS (arrendamiento, mantenimiento, administración), siguiendo la estructura de UPRA 2024.", "4. Calcular mensualmente los 6 KPIs financieros clave: costo/litro, ingreso/litro, margen bruto %, margen neto %, ratio ingreso/egreso, ROA.", "5. Proyectar el flujo de caja a 3 meses para anticipar necesidades de liquidez en época seca o de alta inversión.", "6. Calcular el punto de equilibrio mensual: ¿cuántos litros necesito producir para cubrir todos los costos fijos y variables?", "7. Revisar mensualmente la estructura de costos vs el benchmark sectorial — si algún rubro supera el benchmark más del 20%, investigar la causa.", "8. Calcular el ROA anual: ingreso neto / valor total de los activos del hato.", "9. Con estos datos: presentar proyectos de inversión con flujo de caja proyectado para acceder a crédito FINAGRO con mejores condiciones."]',
  'KPI_MARGEN_NETO',
  'MEDIA',
  30,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P16 — NEGOCIACIÓN DE PRECIO DE LECHE
-- Fuentes:
--   USP-MADR — Resolución 017 de 2012 + valores vigentes:
--     uspleche.minagricultura.gov.co/
--     Región 1 proteína: $43.22/g, grasa: $14.40/g, sólidos: $15.28/g (vigencia mar 2025)
--   CONtexto Ganadero — Precio base leche 2025:
--     contextoganadero.com/economia/precio-base-de-leche-para-2025-como-se-calcula
--     "bonificaciones obligatorias BPG: $14.5/L por hato libre de brucelosis,
--      tuberculosis o BPG. Informalidad deja en mercado de especulación."
--   Resolución 017/2012 — Art.6: BPG bonificación $10/L (actualizado $14.5/L 2025)
--   SIC — multas a Coolechera, Alimentos del Valle, Lactalis, Alquería:
--     "incumplimiento liquidación resolución 017 — derecho a quejarse ante SIC"
--   Redalyc — efectos bonificaciones calidad Ubaté 2012-2018:
--     redalyc.org/journal/6099/609977043001/html/
--     "PPC tiene efecto significativo en reducción recuentos microbiológicos"
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Negociación de precio de leche',
  'Estrategia para maximizar el precio recibido por litro de leche mediante el cumplimiento de estándares de calidad, acceso a todas las bonificaciones disponibles y negociación informada con los compradores.',
  'Incrementar el precio recibido por litro aprovechando el sistema de pago por calidad (Resolución 017/2012) y las bonificaciones voluntarias, pasando de precio informal a precio formal con todas las bonificaciones.',
  'FINANCIERO',
  'Diferencia entre precio informal especulativo y precio formal con todas bonificaciones puede ser $200-$400/L. Mejora KPI_INGRESO_LITRO directamente, sin aumentar producción.',
  'ACTIVA',
  '["1. FORMALIZAR EL ACOPIO: vender a empresa legalmente constituida registrada ante la USP-MADR. La informalidad impide recibir las bonificaciones y deja al ganadero en posición desventajosa.", "2. CONOCER LA RESOLUCIÓN 017/2012: el precio del litro se calcula con base en: gramos de proteína × $43.22 + gramos de grasa × $14.40 + gramos de sólidos × $15.28 (vigencia 2025, Región 1).", "3. MEJORAR CALIDAD COMPOSICIONAL: vacas bien alimentadas producen leche con más proteína y grasa. Cada 0.1 punto extra de proteína puede representar ~$40-50/L adicional.", "4. MEJORAR CALIDAD HIGIÉNICA: reducir UFC (unidades formadoras de colonias). Ordeño higiénico (P03) y enfriamiento de leche son las claves.", "5. BONIFICACIONES OBLIGATORIAS (Resolución 017/2012): solicitar al comprador: a) bonificación por hato libre de brucelosis: $14.5/L, b) hato libre de tuberculosis: $14.5/L, c) certificación BPG: $14.5/L. Total posible: +$43.5/L.", "6. REVISAR LA LIQUIDACIÓN QUINCENALMENTE: verificar que el comprobante de pago refleje correctamente proteína, grasa, UFC y todas las bonificaciones.", "7. Si el comprador no liquida correctamente la Resolución 017: reportar ante la Superintendencia de Industria y Comercio (SIC) — en 2024 la SIC multó a Coolechera, Alimentos del Valle, Lactalis y Alquería por incumplimiento.", "8. NEGOCIAR BONIFICACIONES VOLUNTARIAS: compradores pueden pagar bonificaciones adicionales por volumen, enfriamiento en finca, calidad composicional superior al estándar. Comparar al menos 2 compradores en tu zona.", "9. ASOCIATIVIDAD: unirse a una asociación o cooperativa de productores puede dar mayor poder de negociación, aunque estudiar las condiciones de cada esquema."]',
  'KPI_INGRESO_LITRO',
  'BAJA',
  30,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P17 — DIVERSIFICACIÓN DE INGRESOS DEL HATO
-- Fuentes:
--   CONtexto Ganadero — lechería debe combinarse con otra actividad:
--     contextoganadero.com/ganaderia-sostenible/la-lecheria-es-un-negocio-que-debe
--     "solo 20% de productores estadounidenses opera exclusivamente leche"
--     "70% pequeños colombianos siempre cultivan algo + venden terneros"
--   CONtexto Ganadero — transformar leche en queso:
--     contextoganadero.com/tendencias/transformar-leche-en-queso-apuesta-empresarial
--     "mercado queso Colombia: $620 mil millones en 2023, +28.4% valor"
--   Infortambo Andina — cálido supera frío por venta terneros machos:
--     "rentabilidad cálido superior a frío incluyendo venta terneros = $100 adicional/L"
--   Infortambo Andina — Girolando terneros machos para ceba:
--     "ternero macho Girolando en cebas confinadas: 1 kg/día ganancia peso"
-- ============================================================

-- P17 versión PEQUEÑA/MEDIANA (diversificación básica)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Diversificación de ingresos del hato',
  'Estrategia para agregar fuentes de ingreso complementarias a la venta de leche cruda, reduciendo la dependencia de un solo producto y mejorando la resiliencia financiera del negocio lechero.',
  'Reducir la vulnerabilidad a las caídas del precio de la leche incorporando ingresos adicionales que mejoren el margen neto total y el flujo de caja durante las épocas de crisis.',
  'FINANCIERO',
  'Diversificación con venta de terneros, queso artesanal o cultivos puede representar ingresos adicionales del 20-40% sobre la base láctea. Mejora KPI_INGRESO_VACA y KPI_BALANCE_NETO.',
  'ACTIVA',
  '["1. INVENTARIAR LOS RECURSOS ACTUALES: identificar qué produce la finca además de leche — terneros, animales de descarte, estiércol, espacio disponible para cultivos.", "2. VENTA DE TERNEROS: la fuente más inmediata de ingreso adicional. Terneros machos de Girolando en trópico cálido pueden alcanzar $1 kg/día en ceba. Vender al destete, a los 3-4 meses, o al año.", "3. QUESO ARTESANAL: transformar parte de la leche en queso fresco, doble crema o mozzarella puede duplicar o triplicar el valor de la misma materia prima. El mercado colombiano de quesos creció 28.4% en 2023 (Nielsen). Requiere capacitación en higiene y elaboración.", "4. SISTEMA DOBLE PROPÓSITO: si el trópico lo permite, cruzar con razas de doble aptitud (Girolando, Normando) para obtener valor de carne adicional.", "5. CULTIVOS FORRAJEROS propios para venta: si hay excedente de forraje, vender ensilaje o heno a vecinos en épocas de escasez.", "6. ABONO ORGÁNICO: el estiércol del hato puede compostar y venderse a cultivos vecinos o usarse internamente reemplazando fertilizantes comprados.", "7. Evaluar cuál opción es más viable según la ubicación, acceso a mercados y capacidades propias. No intentar todas a la vez — enfocarse en 1-2 al inicio."]',
  'KPI_BALANCE_NETO',
  'MEDIA',
  90,
  'PEQUEÑA',
  'TODOS'
);

-- P17 versión GRANDE/EMPRESARIAL (diversificación estratégica)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Diversificación de ingresos del hato',
  'Estrategia empresarial de diversificación con valor agregado a la leche, integración vertical hacia derivados lácteos de calidad y/o integración con actividades de carne para maximizar el ingreso por animal.',
  'Construir un modelo de negocio que no dependa exclusivamente del precio de la leche cruda, capturando mayor valor de la cadena mediante transformación y venta de productos diferenciados.',
  'FINANCIERO',
  'Valor agregado a la leche puede triplicar el ingreso por litro. Exportación de quesos o derivados accede a precios premium. Mejora KPI_INGRESO_VACA, KPI_ROA y KPI_MARGEN_NETO estructuralmente.',
  'ACTIVA',
  '["1. ANÁLISIS DE VIABILIDAD DE TRANSFORMACIÓN: calcular el precio de venta del queso fresco en la zona vs el costo de los 7-8 litros necesarios para producir 1 kg.", "2. Si la relación precio queso/costo leche supera 2.5x: explorar inversión en miniplantas queseras artesanales certificadas por INVIMA.", "3. CERTIFICACIÓN INVIMA: para comercializar derivados lácteos industrialmente. Costo de certificación ~$2-5 millones pero habilita canales de distribución formales.", "4. CRIA DE TERNEROS PARA CEBA: retener terneros machos hasta 6-12 meses. Con Girolando u otras razas de doble propósito, la ganancia de peso puede financiar el negocio.", "5. VENTA DIRECTA: punto de venta en finca, mercados de productores o delivery local reduce intermediarios y mejora margen.", "6. AGROTURISMO: si la finca tiene atractivos naturales o gastronómicos, cobrar por visitas, talleres de quesería o desayunos campesinos.", "7. INTEGRACIÓN HORIZONTAL: asociarse con otros productores para compartir planta de derivados, logrando economías de escala sin inversión individual elevada.", "8. Elaborar un modelo financiero de cada línea antes de invertir: calcular inversión requerida, punto de equilibrio y período de recuperación."]',
  'KPI_BALANCE_NETO',
  'ALTA',
  180,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P18 — GESTIÓN DE ACCESO A CRÉDITO Y SUBSIDIOS FINAGRO
-- Fuentes:
--   FINAGRO — ICR apertura 2024 (circular 4 de 2024):
--     contextoganadero.com/economia/importante-abren-icr-para-2024
--     "ICR hasta 40% valor inversión para pequeño productor ingresos bajos"
--   MinAgricultura/FINAGRO — ICR junio 2025 $180 mil millones:
--     finagro.com.co/noticias/articulos/minagricultura-habilita-cerca-180-mil-millones
--     "pequeño ingresos bajos: 40%, pequeño: 30%, mediano: 25%"
--   Decreto ICR MinAgricultura — condiciones generales:
--     minagricultura.gov.co/.../INCENTIVO-A-LA-CAPITALIZACION-RURAL-ICR.aspx
--   FINAGRO Manual Servicios 2025 — FAG fondo agropecuario garantías
--   Banco Agrario — canal principal crédito rural Colombia
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Gestión de acceso a crédito y subsidios FINAGRO',
  'Orientación para que el ganadero conozca y acceda a los instrumentos de financiamiento y subsidio disponibles en Colombia (ICR, FAG, líneas especiales de crédito) para financiar inversiones en el hato.',
  'Financiar inversiones productivas en el hato (cercas, praderas, equipos, genética) con crédito agropecuario de bajo costo y acceder al ICR que abona hasta el 40% del valor del crédito.',
  'FINANCIERO',
  'ICR puede abonar hasta 40% del crédito para pequeños productores. Acceder a crédito FINAGRO vs préstamo informal puede reducir la tasa de interés de 4-5% mensual a 0.8-1.5% mensual. Mejora KPI_MARGEN_NETO y permite inversiones que mejoran KPI_COSTO_LITRO.',
  'ACTIVA',
  '["1. IDENTIFICAR EL TIPO DE PRODUCTOR: según FINAGRO, eres pequeño productor si tus activos totales son menores a 284 salarios mínimos; mediano hasta 5.000 salarios mínimos.", "2. BANCO AGRARIO (canal principal): ir a la sucursal más cercana con: cédula, escritura o documento de tenencia de la finca, RUT, y descripción del proyecto a financiar.", "3. PREPARAR EL PROYECTO: describir qué vas a invertir, cuánto cuesta, cuántos litros adicionales esperas producir y en cuánto tiempo recuperarás la inversión.", "4. ICR — INCENTIVO A LA CAPITALIZACIÓN RURAL: si el crédito financia al menos el 40% del proyecto, puedes solicitar el ICR. El gobierno abona directamente a la deuda: 40% si eres pequeño productor de ingresos bajos, 30% si eres pequeño, 25% si eres mediano.", "5. INVERSIONES ELEGIBLES PARA ICR: cercas eléctricas, potreros, equipos de ordeño, praderas mejoradas, equipos de enfriamiento, infraestructura ganadera, genética.", "6. FAG — FONDO AGROPECUARIO DE GARANTÍAS: si no tienes garantías suficientes, el FAG respalda hasta el 80% del crédito ante el banco. Pregunta por el FAG al solicitar el crédito.", "7. LÍNEAS ESPECIALES DE CRÉDITO: FINAGRO abre periódicamente líneas con tasas subsidiadas. Consultar en www.finagro.com.co o llamar a la Agrolínea 018000 912 219.", "8. CUIDADO CON EL ENDEUDAMIENTO: calcular la capacidad de pago antes de endeudarse. La cuota mensual no debe superar el 30% del ingreso neto promedio del hato.", "9. SEGUIMIENTO DEL ICR: una vez ejecutada la inversión (dentro de 180 días), el intermediario financiero tramita el ICR y abona el incentivo al saldo del crédito."]',
  'KPI_MARGEN_NETO',
  'MEDIA',
  60,
  'TODAS',
  'TODOS'
);


-- ============================================================
-- P19 — ANÁLISIS DE PUNTO DE EQUILIBRIO
-- Fuentes:
--   FEDEGAN — Costos e indicadores productividad (Slideshare):
--     slideshare.net/slideshow/costos-e-indicadores-de-la-productividad
--     Nacional promedio: costo/L $690, ingreso/L $790 → margen $100/L
--     Empresa sobresaliente: costo $755, ingreso $1.008 → margen $253/L
--   UPRA 2024 Valle del Cauca — costo total $1.607/L vs precio $1.870/L
--     → punto equilibrio = $1.607/L = 11.54 L/vaca/día
--   Scielo Magangué Bolívar — punto de equilibrio doble propósito cálido:
--     scielo.org.co/scielo.php?pid=S0122-02682006000200005
--     "47.36% del precio de venta = costo de producirlo"
--     "punto equilibrio: 29.47% de producción diaria (60 L/día)"
--   FEDEGAN — Alto costo de producir leche 2024:
--     estadisticas.fedegan.org.co/DOC/download.jsp?iIdFiles=1069
--     "en julio 2024: costo entre $1.850-$2.100/L vs precio $1.960 trópico alto"
--     "mejor caso: +$110/L, peor caso: -$355/L según estructura de producción"
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Análisis de punto de equilibrio',
  'Cálculo del número mínimo de litros de leche que debe producir el hato para cubrir todos sus costos, y análisis de las variables que permiten mejorar ese umbral.',
  'Conocer exactamente cuántos litros/día necesita producir el hato para no perder dinero, y qué acciones tienen mayor impacto para mejorar la rentabilidad.',
  'FINANCIERO',
  'Conocer el punto de equilibrio permite tomar decisiones racionales sobre inversión y manejo. Según FEDEGAN 2024: en julio 2024 el mejor productor ganaba $110/L y el peor perdía $355/L. La diferencia está en la estructura de costos. Impacta KPI_BREAKEVEN_LITRO y KPI_MARGEN_NETO.',
  'ACTIVA',
  '["1. CALCULAR EL PUNTO DE EQUILIBRIO EN LITROS/MES: tomar los costos fijos mensuales (arrendamiento, mano de obra, mantenimiento) y dividirlos entre el margen de contribución por litro.", "2. Fórmula simplificada: Punto de equilibrio (L/mes) = Costos Fijos Mensuales / (Precio litro - Costo variable por litro).", "3. COSTO VARIABLE POR LITRO: incluye concentrado, sal, minerales, vacunas y medicamentos directamente relacionados con la producción de ese litro.", "4. COSTO FIJO MENSUAL: incluye arrendamiento, mano de obra base, servicios, mantenimiento — estos costos se pagan aunque no se produzca.", "5. Ejemplo de cálculo: costos fijos = $2.000.000/mes, precio litro = $1.900, costo variable = $900/litro. Margen contribución = $1.000. Punto equilibrio = 2.000 litros/mes = 67 litros/día.", "6. CALCULAR EL PUNTO DE EQUILIBRIO EN PRECIO: ¿cuál es el precio mínimo por litro para no perder dinero con la producción actual? = (Costos Totales Mensuales) / (Litros Producidos en el mes). Este es el KPI_BREAKEVEN_LITRO de Hathor.", "7. COMPARAR CON LA REALIDAD: si el punto de equilibrio en precio supera el precio actual de mercado → el hato pierde dinero. Identificar qué costo reducir o cómo aumentar la producción.", "8. ANÁLISIS DE SENSIBILIDAD: ¿qué pasa si el precio baja $100/L? ¿Cuántos litros adicionales necesitaría para compensar? ¿Qué pasaría si redujera el costo del concentrado en 20%?", "9. Según FEDEGAN julio 2024: productores con costo $1.850/L y precio $1.960/L ganaron $110/L. Los que tenían costo $2.100/L perdieron $140/L. La diferencia entre ganadores y perdedores es la gestión de costos, no el precio.", "10. Revisar el punto de equilibrio mensualmente — varía con la estacionalidad del precio y los costos."]',
  'KPI_BREAKEVEN_LITRO',
  'MEDIA',
  30,
  'TODAS',
  'TODOS'
);

-- ============================================================
-- FASE 3.4 — CATÁLOGO DE PRÁCTICAS DE EFICIENCIA
-- Hathor — 4 prácticas con variantes por escala
-- ============================================================

-- ============================================================
-- P20 — CAPACITACIÓN DEL PERSONAL EN BUENAS PRÁCTICAS
-- Fuentes:
--   CONtexto Ganadero / FEDEGAN Manual Práctico Ganadero:
--     contextoganadero.com/ganaderia-sostenible/la-capacitacion-es-un-instrumento
--     "una de las mayores deficiencias: baja escolaridad y formación de trabajadores"
--     "FEDEGAN+SENA: cursos 30-40h en reproducción, sanidad, BPG, ordeño, IA"
--   ICA — BPG Artículo 9: Programas de Capacitación obligatorios:
--     ica.gov.co/getattachment/049aef47/.../Publicacion-23.aspx
--     "salud y manejo animal, proceso de ordeño, higiene personal — capacitación continua"
--   Scielo — Clima organizacional ganadería Tundama Boyacá 2018:
--     scielo.org.co/scielo.php?pid=S0120-29522018000100048
--     "rotación de personal → incremento costos vinculación/desvinculación,
--      ausentismo → reducción eficiencia y rentabilidad"
--   Campo Galego — NMC 2022: capacitación personal en fincas lecheras:
--     campogalego.es/claves-para-formar-y-motivar
--     "capacitación continua forma equipos fuertes → vacas más sanas, leche mejor calidad"
-- ============================================================

-- P20 versión PEQUEÑA/MEDIANA
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Capacitación del personal en buenas prácticas',
  'Programa de formación continua para el personal de la finca en temas clave de producción lechera, aprovechando los recursos gratuitos del SENA y FEDEGAN para mejorar la ejecución de las labores diarias.',
  'Mejorar la calidad de ejecución de las tareas críticas (ordeño, sanidad, manejo animal) para reducir errores, mejorar la calidad de la leche y reducir la rotación de personal.',
  'EFICIENCIA',
  'Según ICA-BPG: la capacitación continua en ordeño es requisito para certificación y mejora la calidad higiénica. Según FEDEGAN: reduce la improductividad causada por errores del personal. Impacta KPI_LITROS_EMPLEADO e KPI_INGRESO_LITRO.',
  'ACTIVA',
  '["1. IDENTIFICAR las 3 labores donde el personal comete más errores: ordeño, aplicación de medicamentos, manejo de praderas. Estas son las prioridades de capacitación.", "2. SENA GRATUITO: contactar la regional del SENA más cercana o visitar www.sena.edu.co para inscribirse en cursos de ganadería bovina (30-40 horas). Son gratuitos para trabajadores del sector.", "3. FEDEGAN + SENA CERTIFICACIÓN: el personal puede obtener certificación en competencias laborales ganaderas. Contactar los 22 Tecnig@n (centros tecnológicos ganaderos) disponibles en el país.", "4. CAPACITACIÓN INTERNA MENSUAL: al menos una vez al mes, dedicar 30 minutos a repasar un protocolo específico con el personal (ej: rutina de ordeño, detección de mastitis, manejo de celo).", "5. MATERIALES ICA-FEDEGAN: descargar las guías gratuitas de BPG y BPO del ICA y usarlas como material de referencia para el personal.", "6. EVALUAR LA IMPLEMENTACIÓN: 30 días después de cada capacitación, verificar si el personal aplica correctamente lo aprendido observando directamente.", "7. INCENTIVAR: reconocer el buen trabajo del personal capacitado — reduce rotación. Según Scielo Tundama 2018: la alta rotación incrementa costos y reduce eficiencia del hato."]',
  'KPI_LITROS_EMPLEADO',
  'BAJA',
  30,
  'PEQUEÑA',
  'TODOS'
);

-- P20 versión GRANDE/EMPRESARIAL
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Capacitación del personal en buenas prácticas',
  'Programa formal de gestión del talento humano con plan de capacitación anual, certificaciones laborales, protocolos escritos y evaluación de desempeño para construir equipos de alto rendimiento.',
  'Construir un equipo estable y competente que ejecute todos los procesos del hato con calidad consistente, reduciendo errores operativos y su impacto en producción y costos.',
  'EFICIENCIA',
  'Las empresas con buena gestión de RRHH tienen menor rotación, mayor eficiencia y mejor calidad de leche. Mejora KPI_LITROS_EMPLEADO, KPI_COSTO_LABORAL_PCT e KPI_INGRESO_LITRO.',
  'ACTIVA',
  '["1. ELABORAR UN PLAN DE CAPACITACIÓN ANUAL: definir qué competencias necesita cada cargo (ordeñador, mayordomo, administrador) y qué cursos los desarrollan.", "2. PROTOCOLOS ESCRITOS: documentar los procedimientos críticos con fotos o diagramas. Un ordeñador que tiene el protocolo escrito comete menos errores cuando cambia el turno.", "3. CERTIFICACIONES SENA: enviar a todos los ordeñadores y mayordomos a certificarse en competencias laborales ganaderas (SENA certifica sin costo). Es requisito para BPG.", "4. INDUCCIÓN ESTRUCTURADA: cada nuevo empleado recibe 3-5 días de inducción supervisada antes de trabajar solo en labores críticas.", "5. EVALUACIÓN TRIMESTRAL DE DESEMPEÑO: evaluar cada empleado en las métricas de su labor: ordeñadores en RCS de las vacas a su cargo, mayordomos en indicadores del hato.", "6. INCENTIVOS POR RESULTADOS: bonificación mensual por cumplimiento de metas de producción, calidad o sanidad. Genera sentido de propiedad y reduce rotación.", "7. REUNIONES MENSUALES: reunir a todo el personal para revisar los indicadores del mes, celebrar logros y analizar problemas. Según NMC 2022: el personal que entiende su impacto en los resultados trabaja mejor.", "8. GESTIÓN BIENESTAR LABORAL: según Scielo Tundama 2018, el aislamiento y las condiciones del trabajo rural son factores de riesgo de deserción. Asegurar vivienda digna, permisos y comunicación."]',
  'KPI_LITROS_EMPLEADO',
  'MEDIA',
  60,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P21 — IMPLEMENTACIÓN DE REGISTROS DIGITALES
-- Fuentes:
--   CONtexto Ganadero / FEDEGAN — sistematización registros:
--     contextoganadero.com/ganaderia-sostenible/sistematizacion-la-mejor-manera
--     "lo que no se mide no es susceptible de ser mejorado" — Ricardo Arenas, FEDEGAN
--     "softwares especializados: TaurusWebs, Software GANADERO SG, BovControl"
--   Software GANADERO SG — USATI, líder Latinoamérica desde 1986:
--     softwareganadero.com
--     "miles de ganaderos líderes en 21 países, más de 33 años"
--   PROGAN Software Ganadero (Colombia, Insolca):
--     progansoftware.com
--     "edición GRATUITA hasta 30 animales, módulos ganadero + administrativo + nómina"
--   BovControl — gratuito pequeños y medianos:
--     contextoganadero.com/regiones/conozca-5-herramientas-digitales-idoneas
--     "elimina errores de registro, aumenta productividad significativamente"
--   Control Ganadero app:
--     play.google.com/store/apps/details?id=com.grupoarve.cganadero
-- ============================================================

-- P21 versión PEQUEÑA/MEDIANA (inicio digitalización)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Implementación de registros digitales',
  'Transición del registro en cuaderno o papel a herramientas digitales (aplicaciones móviles o software básico) para centralizar la información del hato y facilitar el cálculo automático de indicadores.',
  'Tener información confiable, organizada y accesible que permita tomar mejores decisiones sobre reproducción, sanidad, producción y finanzas sin depender de la memoria del mayordomo.',
  'EFICIENCIA',
  'Según FEDEGAN: la sistematización digital permite identificar vacas problema, calcular días abiertos automáticamente y generar alertas. Reduce errores de manejo. Mejora KPI_LITROS_VACA_DIA a través de mejores decisiones.',
  'ACTIVA',
  '["1. HERRAMIENTAS GRATUITAS DISPONIBLES EN COLOMBIA: a) PROGAN (progansoftware.com): edición gratuita para hasta 30 animales, descargable en Windows, módulos de producción y finanzas. b) BovControl: aplicación móvil gratuita para pequeños y medianos, muy fácil de usar. c) Control Ganadero: app para Android con hasta 5 perfiles de usuario.", "2. DATOS MÍNIMOS A REGISTRAR DIGITALMENTE: identificación del animal, fecha de parto, producción semanal de leche, servicios realizados y resultado, tratamientos aplicados.", "3. INICIAR CON LO BÁSICO: no intentar registrar todo de una vez. Comenzar con: a) producción diaria por vaca, b) fecha de parto y fecha de servicio.", "4. ASIGNAR UN RESPONSABLE: el mayordomo o el propietario debe encargarse de ingresar los datos diariamente — la constancia es más importante que la herramienta.", "5. USAR LAS ALERTAS AUTOMÁTICAS: configurar recordatorios para: vacas próximas a parir, vacas a secar, fecha de vacunación, confirmación de preñez.", "6. REVISAR LOS INDICADORES MENSUALES: al final de cada mes, revisar en el software: producción promedio del hato, días abiertos promedio, animales en anestro.", "7. RESPALDAR LOS DATOS: exportar o guardar la información en la nube o USB mensualmente para no perder el historial."]',
  'KPI_LITROS_VACA_DIA',
  'BAJA',
  30,
  'PEQUEÑA',
  'TODOS'
);

-- P21 versión GRANDE/EMPRESARIAL (digitalización avanzada)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Implementación de registros digitales',
  'Sistema integral de gestión digital con software especializado, conectividad con equipos (básculas, medidores de leche, chapetas electrónicas) y análisis avanzado de indicadores para la toma de decisiones basada en datos.',
  'Transformar el hato en una empresa dirigida por datos: decisiones de alimentación, reproducción, sanidad y descarte basadas en información precisa, actualizada y analizada automáticamente.',
  'EFICIENCIA',
  'GANADERO SG con ranking de vacas permite identificar el 20% menos productivo para descarte y el 20% más productivo para madre de reemplazos. Mejora KPI_LITROS_VACA_DIA, KPI_COSTO_LITRO y KPI_MARGEN_NETO vía decisiones optimizadas.',
  'ACTIVA',
  '["1. SELECCIONAR SOFTWARE ESPECIALIZADO: evaluar GANADERO SG (USATI — líder Latinoamérica desde 1986, disponible en Colombia) o PROGAN profesional según el tamaño y necesidades.", "2. MIGRACIÓN DE DATOS: si existen registros en papel o Excel, cargar el historial de los últimos 2-3 años al software para disponer de datos de referencia.", "3. CONECTAR EQUIPOS: integrar con básculas electrónicas (para pesajes automáticos), lectores de chip DIN/SINIGAN, medidores de producción individual en la sala de ordeño.", "4. MÓDULO DE RANKING DE VACAS: configurar el índice de selección y generar mensualmente el ranking de vacas de mejor a peor. Usar para decisiones de descarte y mejoramiento genético.", "5. MÓDULO FINANCIERO: registrar TODOS los costos e ingresos para calcular costo/litro real del hato mensualmente.", "6. ALERTAS INTELIGENTES: configurar alertas para: vacas con RCS elevado, vacas en anestro post-espera voluntaria, cambios bruscos de producción individual.", "7. ANÁLISIS DE CURVA DE LACTANCIA: comparar la curva real de cada vaca con la curva predicha para detectar problemas nutricionales o de salud tempranamente.", "8. EXPORTAR DATOS A SINIGAN: integrar el software con SINIGAN V6 para cumplir la trazabilidad legal sin doble registro.", "9. REVISIÓN MENSUAL DE INDICADORES: el gerente o propietario debe revisar mensualmente los 6-8 KPIs principales del hato y comparar con el benchmark del software."]',
  'KPI_LITROS_VACA_DIA',
  'ALTA',
  60,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P22 — EVALUACIÓN DE MECANIZACIÓN DEL ORDEÑO
-- Fuentes:
--   Agronegocios.co — costos plantas ordeño Colombia Feb 2026:
--     agronegocios.co/finca/entre-4-y-100-millones-le-cuesta-una-planta-de-ordeno
--     Portátil 1 puesto: $4-7M, 2 puestos: $8-11M (8-20 vacas/hora)
--     Sala espina de pescado: hasta $100M+
--   Ordecol Colombia — precios máquinas ordeño:
--     ordecol.com/cuanto-cuesta-una-maquina-de-ordeno-en-colombia/
--     "desde $5M hasta $50M dependiendo capacidad y tecnología"
--   CONtexto Ganadero / Durordeños — +10% producción con máquina:
--     contextoganadero.com/ganaderia-sostenible/con-3-millones-puede-comprar
--     "con máquina: casi 10% más producción reflejado en ganancias"
--   Teuken Bidikay — Antioquia 2017 ordeño manual vs mecánico:
--     dialnet.unirioja.es/descarga/articulo/8761006.pdf
--     Manual: 13.83 L/vaca/día. Mecánico: 15.62 L/vaca/día (+13%)
--   La Patría Caldas — 7-8 minutos/vaca en mecánico:
--     lapatria.com/tenga-en-cuenta/ordeno-mecanico-mas-rapido-e-higienico
--     "manual 2-3x más tiempo. Mecánico: solo 2 personas para el proceso"
-- ============================================================

-- P22 versión PEQUEÑA (evaluación portátil básico)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Evaluación de mecanización del ordeño',
  'Análisis de la viabilidad económica y técnica de migrar del ordeño manual al mecánico, y guía para seleccionar el equipo más adecuado según el tamaño del hato y el presupuesto disponible.',
  'Aumentar la productividad laboral del ordeño, mejorar la calidad higiénica de la leche y reducir el esfuerzo físico del personal, evaluando si la inversión se justifica con el retorno esperado.',
  'EFICIENCIA',
  'Según Teuken Bidikay Antioquia 2017: hatos con ordeño mecánico producen 15.62 vs 13.83 L/vaca/día (+13%). Según Durordeños/CONtexto: +10% producción adicional. Mejora KPI_LITROS_EMPLEADO, KPI_LITROS_VACA_DIA e KPI_INGRESO_LITRO (mejor RCS).',
  'ACTIVA',
  '["1. DETERMINAR SI LA INVERSIÓN ES VIABLE: calcular el umbral mínimo de rentabilidad. Un equipo portátil de 1 puesto ($4-7 millones en Colombia, Ordecol 2026) que ordeña 8-10 vacas/hora vs el costo del ordeñador adicional que reemplaza.", "2. CALCULAR EL RETORNO: si la máquina aumenta la producción 10% y el hato produce 100 L/día: +10 L × $1.800/L × 365 días = $6.570.000/año adicional. El equipo se paga en menos de 1 año.", "3. PARA PEQUEÑOS (hasta 25 vacas): evaluar equipo portátil 1 puesto ($4-7 millones). Ordeña 8-10 vacas/hora. Motor eléctrico si hay energía o gasolina para zonas sin red.", "4. PARA MEDIANOS (26-60 vacas): evaluar equipo portátil 2 puestos ($8-11 millones) que permite ordeñar 20 vacas/hora. Alternativa: sala básica de 2-4 puestos.", "5. VERIFICAR DISPONIBILIDAD DE ENERGÍA: equipos eléctricos requieren conexión estable. En zonas sin red eléctrica: opción a gasolina o diésel.", "6. PROVEEDORES NACIONALES: Ordecol (fabricante colombiano con soporte técnico nacional), Durordeños, Dimap — verificar disponibilidad de repuestos locales antes de comprar.", "7. CAPACITAR AL PERSONAL: el ordeño mecánico requiere saber calibrar el vacío (42-45 kPa), limpiar las pezoneras y detectar mal funcionamiento. Sin capacitación, los beneficios se anulan.", "8. EVALUAR ACCESO A ICR/FINAGRO: los equipos de ordeño son inversión elegible para el ICR. Un pequeño productor puede recuperar hasta el 30-40% del costo vía FINAGRO (ver P18)."]',
  'KPI_LITROS_EMPLEADO',
  'MEDIA',
  30,
  'PEQUEÑA',
  'TODOS'
);

-- P22 versión GRANDE/EMPRESARIAL (sala de ordeño tecnificada)
INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Evaluación de mecanización del ordeño',
  'Diseño e implementación de sala de ordeño especializada (espina de pescado, paralela o tándem) con sistema de conducción de leche a tanque de enfriamiento, integrada al software de gestión.',
  'Maximizar la eficiencia del proceso de ordeño, garantizar la más alta calidad higiénica de la leche, y obtener datos individuales de producción por vaca en tiempo real.',
  'EFICIENCIA',
  'Sala de ordeño tecnificada con medidores individuales: datos de producción por vaca en tiempo real. Según La Patría Caldas: 7-8 min/vaca vs manual 2-3x más tiempo. Solo 2 personas para ordeñar. Mejora KPI_LITROS_EMPLEADO, KPI_INGRESO_LITRO (RCS) y KPI_COSTO_LABORAL_PCT.',
  'ACTIVA',
  '["1. DISEÑO TÉCNICO: contratar ingeniero o técnico especializado para diseñar la sala según el número de vacas, flujo de producción y espacio disponible.", "2. TIPOS DE SALA SEGÚN ESCALA: espina de pescado (6-16 puestos) es la más eficiente para hatos de 80-300 vacas; tándem para hatos donde la calidad individual es prioritaria; paralela para alta velocidad en hatos grandes.", "3. PRESUPUESTO SALA TECNIFICADA: salas de 6-12 puestos con conducción a tanque: $30-100 millones (Agronegocios.co 2026). Incluir obra civil, tuberías sanitarias, tanque de enfriamiento.", "4. TANQUE DE ENFRIAMIENTO: imprescindible para acceder a bonificación por frío ($14.5/L según Resolución 017/2012). Capacidad según litros/día.", "5. MEDIDORES INDIVIDUALES: integrar medidores de producción por vaca que se sincronizan con el software de gestión — elimina el control lechero manual.", "6. ACCESO A ICR: la sala de ordeño con equipos es inversión elegible para ICR FINAGRO. Tramitar ANTES de iniciar la obra para asegurar el incentivo.", "7. MANTENIMIENTO PREVENTIVO: calibración de vacío cada 6 meses (Ordecol recomienda este ciclo). Cambio de pezoneras según desgaste. Limpieza CIP (Cleaning In Place) diaria.", "8. INTEGRAR CON SOFTWARE: conectar la sala con GANADERO SG u otro software para recibir automáticamente los litros de cada vaca en cada ordeño.", "9. META DE EFICIENCIA: con sala de 8 puestos y 2 personas, ordeñar 120 vacas en 2.5 horas = meta de eficiencia para hatos grandes en Colombia."]',
  'KPI_LITROS_EMPLEADO',
  'ALTA',
  180,
  'GRANDE',
  'TODOS'
);


-- ============================================================
-- P23 — OPTIMIZACIÓN DE ESTRUCTURA DE PERSONAL
-- Fuentes:
--   FEDEGAN — estructura costos lechería nacional vs sobresaliente:
--     slideshare.net/slideshow/costos-e-indicadores-de-la-productividad
--     Nacional: mano de obra 40-42%. Empresa sobresaliente: 15.3%
--   UPRA 2024 Valle del Cauca — mano de obra $464/L de $1.607 = 28.86%
--   FEDEGAN — Alto Costo de Producir Leche 2024:
--     "costo laboral entre 2021-2024 incrementó 43% vs precio leche -6%"
--   Teuken Bidikay — Antioquia 2017 (Don Matías y La Unión):
--     dialnet.unirioja.es/descarga/articulo/8761006.pdf
--     "indicadores: vacas ordeñadas por trabajador/día, litros/hombre/día"
--   Scielo — Clima organizacional Tundama Boyacá 2018:
--     scielo.org.co/scielo.php?pid=S0120-29522018000100048
--     "rotación personal → altos costos vinculación, reducción eficiencia"
--   Jefo Nutrition — costos laborales 20%+ del total producción
-- ============================================================

INSERT INTO practica (nombre, descripcion, objetivo, categoria, impacto_esperado, estado, pasos, kpi_impactado, dificultad, duracion_dias, escala, tropico_aplicable)
VALUES (
  'Optimización de estructura de personal',
  'Revisión de la estructura y asignación de tareas del personal del hato para mejorar la productividad laboral, reducir el porcentaje que representa la mano de obra en el costo total y retener a los mejores trabajadores.',
  'Reducir el costo laboral por litro de leche producido al nivel de las empresas sobresalientes del sector (15% del costo total vs el 40-42% del promedio nacional), sin sacrificar la calidad del trabajo.',
  'EFICIENCIA',
  'La diferencia en costo laboral entre empresa promedio (40-42%) y empresa sobresaliente (15.3%) según FEDEGAN es la mayor brecha en la estructura de costos lechera. Mejora KPI_COSTO_LABORAL_PCT, KPI_LITROS_EMPLEADO y KPI_COSTO_LITRO.',
  'ACTIVA',
  '["1. CALCULAR EL INDICADOR ACTUAL: costo total de nómina mensual / litros producidos ese mes = costo laboral por litro. Comparar con KPI_COSTO_LABORAL_PCT del benchmark del hato.", "2. CALCULAR LITROS POR EMPLEADO: litros producidos al mes / número de empleados de planta. Según benchmarks Hathor: pequeño cálido = 40-100 L/empleado/día; empresarial fío = >200 L/empleado/día.", "3. MAPEAR LAS TAREAS: listar TODAS las actividades que realizan los empleados en una semana típica. Identificar qué actividades generan valor directo (ordeño, manejo animal) vs actividades de apoyo (transporte, mantenimiento).", "4. ELIMINAR TIEMPOS MUERTOS: analizar si el personal tiene tiempo improductivo entre ordeños. Reasignar labores de mantenimiento, praderas o registros para esos intervalos.", "5. POLIVALENCIA: capacitar a cada empleado en 2-3 tareas críticas para no depender de una persona para una función específica. Reduce vulnerabilidad por ausencias.", "6. DIMENSIONAMIENTO CORRECTO: benchmarks de referencia para Colombia: un ordeñador manual puede manejar 20-25 vacas en 2 horas; con ordeño mecánico: 40-60 vacas con 2 personas.", "7. CONTRATACIÓN TEMPORAL vs PLANTA: evaluar si es más eficiente tener un empleado de planta o contratar jornales para labores estacionales (vacunación, praderas, mantenimiento cercas).", "8. RETENCIÓN DEL BUEN PERSONAL: según Scielo Tundama 2018, la rotación de personal en lecherías es costosa — se pierde conocimiento y aumentan errores. Invertir en condiciones de vivienda, permisos y reconocimiento.", "9. MEDIR MENSUALMENTE: calcular litros/empleado/día al cierre de cada mes y comparar con el mes anterior. Meta: crecimiento sostenido de este indicador sin aumentar la nómina."]',
  'KPI_COSTO_LABORAL_PCT',
  'MEDIA',
  60,
  'TODAS',
  'TODOS'
);