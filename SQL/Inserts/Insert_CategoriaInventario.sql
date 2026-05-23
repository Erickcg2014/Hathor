-- =============================================
-- CATEGORÍAS PREDEFINIDAS PARA INVENTARIO GENERAL
-- =============================================

-- 1. INFRAESTRUCTURA E INSTALACIONES (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('INFRAESTRUCTURA E INSTALACIONES', 'Instalaciones físicas del hato', 'PADRE', true, 10, true);

-- Hijas de Infraestructura
WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'INFRAESTRUCTURA E INSTALACIONES')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Cuartos de leche', 'Área de almacenamiento y enfriamiento', 'HIJA', (SELECT id_categoria FROM padre), true, 'metros²', 13, true),
    ('Bodega de insumos', 'Almacenamiento de concentrados y medicamentos', 'HIJA', (SELECT id_categoria FROM padre), true, 'metros²', 14, true),
    ('Manga y embarcadero', 'Estructuras para manejo de ganado', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 15, true);

-- 2. EQUIPOS DE ORDEÑO (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('EQUIPOS DE ORDEÑO', 'Maquinaria para el proceso de ordeño', 'PADRE', true, 20, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'EQUIPOS DE ORDEÑO')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Unidad de ordeño', 'Equipo completo de ordeño mecánico', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 21, true),
    ('Bomba de vacío', 'Generador de vacío para el sistema', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 22, true),
    ('Pezoneras', 'Conjunto de pezoneras por punto', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 23, true),
    ('Líneas de conducción', 'Tuberías de leche y vacío', 'HIJA', (SELECT id_categoria FROM padre), true, 'metros', 24, true);

-- 3. EQUIPOS DE ENFRIAMIENTO (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('EQUIPOS DE ENFRIAMIENTO', 'Sistemas de conservación de leche', 'PADRE', true, 30, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'EQUIPOS DE ENFRIAMIENTO')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Tanque de enfriamiento', 'Tanque para almacenar y enfriar leche', 'HIJA', (SELECT id_categoria FROM padre), true, 'litros', 31, true),
    ('Compresor', 'Unidad de refrigeración', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 32, true),
    ('Agitador', 'Sistema de agitación automática', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 33, true);

-- 4. MAQUINARIA AGRÍCOLA (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('MAQUINARIA AGRÍCOLA', 'Equipos para labores de campo', 'PADRE', true, 40, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'MAQUINARIA AGRÍCOLA')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Tractores', 'Tractor agrícola', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 41, true),
    ('Picadora de pasto', 'Equipo para picar forraje', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 42, true),
    ('Ensiladora', 'Equipo para ensilar', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 43, true),
    ('Rastrillo', 'Implemento para recolección', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 44, true),
    ('Carro mezclador (TMR)', 'Mezclador de raciones', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 45, true);

-- 5. EQUIPOS DE SOPORTE (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('EQUIPOS DE SOPORTE', 'Equipos auxiliares para operación', 'PADRE', true, 50, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'EQUIPOS DE SOPORTE')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Generador eléctrico', 'Planta eléctrica de respaldo', 'HIJA', (SELECT id_categoria FROM padre), true, 'kVA', 51, true),
    ('Motobomba', 'Bomba para agua', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 52, true),
    ('Báscula', 'Equipo de pesaje de ganado', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 53, true),
    ('Equipo de fumigación', 'Aspersor o bomba de espalda', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 54, true);

-- 6. VEHÍCULOS (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('VEHÍCULOS', 'Vehículos de transporte', 'PADRE', true, 60, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'VEHÍCULOS')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Carro tanque', 'Vehículo para transporte de leche', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 61, true),
    ('Camioneta', 'Vehículo de uso general', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 62, true);

-- 7. HERRAMIENTAS Y UTENSILIOS (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('HERRAMIENTAS Y UTENSILIOS', 'Herramientas manuales y utensilios de trabajo', 'PADRE', true, 70, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'HERRAMIENTAS Y UTENSILIOS')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Palas y picas', 'Herramientas de labranza', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 71, true),
    ('Baldes y cantinas', 'Recipientes para leche y ordeño manual', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 72, true),
    ('Mangueras', 'Conducción de agua para limpieza', 'HIJA', (SELECT id_categoria FROM padre), true, 'metros', 73, true),
    ('Equipo de inseminación', 'Kit de inseminación artificial', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 74, true);

-- 8. SISTEMAS DE AGUA Y ENERGÍA (PADRE)
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, es_predefinida, orden, activa)
VALUES 
    ('SISTEMAS DE AGUA Y ENERGÍA', 'Infraestructura hídrica y eléctrica', 'PADRE', true, 80, true);

WITH padre AS (SELECT id_categoria FROM public.categoria_inventario WHERE nombre = 'SISTEMAS DE AGUA Y ENERGÍA')
INSERT INTO public.categoria_inventario 
    (nombre, descripcion, tipo, id_categoria_padre, es_predefinida, unidad_medida, orden, activa)
VALUES
    ('Tanque de agua', 'Almacenamiento de agua potable', 'HIJA', (SELECT id_categoria FROM padre), true, 'litros', 81, true),
    ('Sistema de riego', 'Equipo de riego para potreros', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 82, true),
    ('Paneles solares', 'Sistema de energía solar', 'HIJA', (SELECT id_categoria FROM padre), true, 'unidad', 83, true),
    ('Cerca eléctrica', 'Sistema de cercado eléctrico', 'HIJA', (SELECT id_categoria FROM padre), true, 'metros', 84, true);