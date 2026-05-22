-- =============================================
-- 3. CATEGORÍAS FINANCIERAS
-- =============================================

-- Categoría GENERAL (raíz)
-- =============================================
-- 3. CATEGORÍAS FINANCIERAS (MEJORADO - INGRESOS)
-- =============================================

-- INGRESOS (Nodo Principal)
WITH general AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GENERAL')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'INGRESOS', 'Todo tipo de entradas de capital', 'INGRESO', (SELECT id_categoria FROM general), true, 1);

-- Subcategorías de INGRESOS
WITH ingresos_root AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'INGRESOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    -- 1. Producción Primaria
    (NULL, 'VENTA DE LECHE', 'Venta de leche cruda a industria o terceros', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 2),
    (NULL, 'VENTA DE ANIMALES', 'Venta de ganado (descarte, terneros, novillas)', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 3),
    
    -- 2. Valor Agregado (Clave para diversificación)
    (NULL, 'VENTA DE DERIVADOS LÁCTEOS', 'Ingresos por quesos, yogures, mantequilla, etc.', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 4),
    
    -- 3. Genética y Reproducción
    (NULL, 'VENTA DE GENÉTICA', 'Venta de pajillas, embriones o sementales', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 5),
    
    -- 4. Incentivos y Calidad (Muy importante en Colombia)
    (NULL, 'BONIFICACIONES Y PREMIOS', 'Extras por calidad higiénica, sólidos o volumen', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 6),
    (NULL, 'INCENTIVOS GUBERNAMENTALES', 'Subsidios, ICR o apoyos gremiales', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 7),
    
    -- 5. Servicios y Subproductos
    (NULL, 'ALQUILER DE MAQUINARIA Y EQUIPOS', 'Servicios prestados a terceros con activos del hato', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 8),
    (NULL, 'VENTA DE SUBPRODUCTOS', 'Venta de abono orgánico, compost, madera, etc.', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 9),
    (NULL, 'OTROS INGRESOS', 'Ingresos varios no clasificados', 'INGRESO', (SELECT id_categoria FROM ingresos_root), true, 10);
    
-- GASTOS DE PRODUCCIÓN
WITH general AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GENERAL')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'GASTOS DE PRODUCCIÓN', 'Costos directos de producción', 'GASTO', (SELECT id_categoria FROM general), true, 20);

-- Alimentación (subcategoría)
WITH prod AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS DE PRODUCCIÓN')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'ALIMENTACIÓN', 'Gastos en alimentación animal', 'GASTO', (SELECT id_categoria FROM prod), true, 21),
    (NULL, 'CONCENTRADO', 'Alimento balanceado', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ALIMENTACIÓN'), true, 22),
    (NULL, 'SAL MINERALIZADA', 'Sales minerales', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ALIMENTACIÓN'), true, 23),
    (NULL, 'SUPLEMENTOS', 'Suplementos nutricionales', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ALIMENTACIÓN'), true, 24),
    (NULL, 'ENSILAJE', 'Forraje conservado', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ALIMENTACIÓN'), true, 25),
    (NULL, 'PASTO CULTIVADO', 'Pastos y forrajes', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ALIMENTACIÓN'), true, 26);

-- Sanidad Animal
WITH prod AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS DE PRODUCCIÓN')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'SANIDAD ANIMAL', 'Gastos en salud animal', 'GASTO', (SELECT id_categoria FROM prod), true, 30),
    (NULL, 'MEDICAMENTOS', 'Productos farmacéuticos', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'SANIDAD ANIMAL'), true, 31),
    (NULL, 'VACUNAS', 'Inmunizaciones', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'SANIDAD ANIMAL'), true, 32),
    (NULL, 'SERVICIOS VETERINARIOS', 'Honorarios y procedimientos', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'SANIDAD ANIMAL'), true, 33);

-- Reproducción
WITH prod AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS DE PRODUCCIÓN')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'REPRODUCCIÓN', 'Gastos reproductivos', 'GASTO', (SELECT id_categoria FROM prod), true, 40),
    (NULL, 'INSEMINACIÓN ARTIFICIAL', 'Servicios y materiales para IA', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'REPRODUCCIÓN'), true, 41),
    (NULL, 'SERVICIOS REPRODUCTIVOS', 'Otros servicios', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'REPRODUCCIÓN'), true, 42);

-- GASTOS OPERATIVOS
WITH general AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GENERAL')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'GASTOS OPERATIVOS', 'Costos de operación del hato', 'GASTO', (SELECT id_categoria FROM general), true, 50);

-- Mano de obra
WITH op AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS OPERATIVOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'MANO DE OBRA', 'Personal y salarios', 'GASTO', (SELECT id_categoria FROM op), true, 51),
    (NULL, 'SALARIOS', 'Salarios fijos', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'MANO DE OBRA'), true, 52),
    (NULL, 'PRESTACIONES', 'Beneficios laborales', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'MANO DE OBRA'), true, 53),
    (NULL, 'HONORARIOS', 'Servicios profesionales', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'MANO DE OBRA'), true, 54);

-- Servicios públicos
WITH op AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS OPERATIVOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'SERVICIOS PÚBLICOS', 'Energía, agua, combustible', 'GASTO', (SELECT id_categoria FROM op), true, 60),
    (NULL, 'ENERGÍA ELÉCTRICA', 'Consumo de electricidad', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'SERVICIOS PÚBLICOS'), true, 61),
    (NULL, 'AGUA', 'Consumo de agua', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'SERVICIOS PÚBLICOS'), true, 62),
    (NULL, 'COMBUSTIBLE', 'Gasolina, ACPM', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'SERVICIOS PÚBLICOS'), true, 63);

-- Transporte
WITH op AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS OPERATIVOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'TRANSPORTE', 'Logística y movilidad', 'GASTO', (SELECT id_categoria FROM op), true, 70),
    (NULL, 'TRANSPORTE DE LECHE', 'Fletes de leche', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'TRANSPORTE'), true, 71),
    (NULL, 'TRANSPORTE DE ANIMALES', 'Movilización de ganado', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'TRANSPORTE'), true, 72);

-- Arriendos
WITH op AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS OPERATIVOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'ARRIENDOS', 'Alquileres', 'GASTO', (SELECT id_categoria FROM op), true, 80),
    (NULL, 'ARRIENDO DE TIERRA', 'Alquiler de terrenos', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ARRIENDOS'), true, 81),
    (NULL, 'ARRIENDO DE FINCA', 'Alquiler completo de finca', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'ARRIENDOS'), true, 82);

-- Mantenimiento
WITH op AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS OPERATIVOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'MANTENIMIENTO', 'Reparaciones y conservación', 'GASTO', (SELECT id_categoria FROM op), true, 90),
    (NULL, 'MANTENIMIENTO DE MAQUINARIA', 'Reparación de equipos', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'MANTENIMIENTO'), true, 91),
    (NULL, 'MANTENIMIENTO DE INFRAESTRUCTURA', 'Reparación de instalaciones', 'GASTO', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'MANTENIMIENTO'), true, 92);

-- INVERSIONES
WITH general AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GENERAL')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'INVERSIONES', 'Activos de capital', 'INVERSION', (SELECT id_categoria FROM general), true, 100),
    (NULL, 'COMPRA DE ANIMALES', 'Adquisición de ganado', 'INVERSION', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'INVERSIONES'), true, 101),
    (NULL, 'COMPRA DE EQUIPOS', 'Adquisición de maquinaria', 'INVERSION', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'INVERSIONES'), true, 102),
    (NULL, 'MEJORAS DE INFRAESTRUCTURA', 'Inversiones en instalaciones', 'INVERSION', (SELECT id_categoria FROM categoria_financiera WHERE nombre = 'INVERSIONES'), true, 103);
-- OTROS GASTOS 
WITH op AS (SELECT id_categoria FROM public.categoria_financiera WHERE nombre = 'GASTOS OPERATIVOS')
INSERT INTO public.categoria_financiera 
    (id_usuario, nombre, descripcion, tipo, id_categoria_padre, es_predefinida, orden)
VALUES 
    (NULL, 'OTROS GASTOS', 'Gastos varios no clasificados', 'GASTO', (SELECT id_categoria FROM op), true, 99);

-- SUBCATEGORÍAS DE INVERSIÓN
WITH inv AS (
  SELECT id_categoria FROM public.categoria_financiera
  WHERE nombre = 'INVERSIONES'
)
INSERT INTO public.categoria_financiera
  (id_usuario, nombre, descripcion, tipo,
   id_categoria_padre, es_predefinida, orden)
VALUES
  -- Ganado
  (NULL, 'COMPRA DE GANADO DE CRÍA',
   'Adquisición de vacas y novillas para reproducción',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 104),

  (NULL, 'COMPRA DE GANADO DE LEVANTE',
   'Adquisición de terneros para cría',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 105),

  (NULL, 'MEJORAMIENTO GENÉTICO',
   'Pajillas, embriones, sementales',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 106),

  -- Infraestructura
  (NULL, 'CONSTRUCCIÓN DE ESTABLOS',
   'Construcción o mejora de instalaciones para ganado',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 107),

  (NULL, 'SISTEMAS DE RIEGO',
   'Instalación de riego para pasturas',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 108),

  (NULL, 'CERCAS Y DIVISIONES',
   'Cercas eléctricas, físicas y potreros',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 109),

  -- Equipos
  (NULL, 'EQUIPOS DE ORDEÑO',
   'Ordeñadoras mecánicas, tanques de frío',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 110),

  (NULL, 'MAQUINARIA AGRÍCOLA',
   'Tractores, picadoras, enfardadoras',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 111),

  (NULL, 'VEHÍCULOS',
   'Vehículos para transporte de leche o ganado',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 112),

  -- Pasturas
  (NULL, 'RENOVACIÓN DE PASTURAS',
   'Semillas, preparación de suelo, siembra',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 113),

  (NULL, 'OTRAS INVERSIONES',
   'Inversiones no clasificadas',
   'INVERSION',
   (SELECT id_categoria FROM inv), true, 114);