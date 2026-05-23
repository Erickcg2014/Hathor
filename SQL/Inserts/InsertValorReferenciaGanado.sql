-- =============================================
-- INSERCIÓN EN valorreferenciaganado
-- ASIGNANDO PRECIOS POR RAZA (VACA DE PRODUCCIÓN ADULTA)
-- =============================================

INSERT INTO public.valorreferenciaganado 
    (id_raza, id_categoria, valor_promedio, region, anio)
VALUES
-- Holstein-Friesian (ID 2)
(2, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 8500000, 'Nacional', 2025),

-- Jersey (ID 3)
(3, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7800000, 'Nacional', 2025),

-- Pardo Suizo (ID 4)
(4, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 8200000, 'Nacional', 2025),

-- Ayrshire (ID 5)
(5, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7900000, 'Nacional', 2025),

-- Guernsey (ID 6)
(6, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7700000, 'Nacional', 2025),

-- Milking Shorthorn (ID 7)
(7, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7500000, 'Nacional', 2025),

-- Angeln (Red Cattle) (ID 8)
(8, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7300000, 'Nacional', 2025),

-- Red Poll (ID 9)
(9, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7200000, 'Nacional', 2025),

-- Dutch Belted (ID 10)
(10, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7400000, 'Nacional', 2025);

-- II. Razas Criollas (IDs 11 al 20)
INSERT INTO public.valorreferenciaganado 
    (id_raza, id_categoria, valor_promedio, region, anio)
VALUES
-- Hartón del Valle (ID 11)
(11, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5500000, 'Nacional', 2025),

-- Lucerna (ID 12)
(12, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5800000, 'Nacional', 2025),

-- Blanco Orejinegro (BON) (ID 13)
(13, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5200000, 'Nacional', 2025),

-- Costeño con Cuernos (ID 14)
(14, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 4800000, 'Nacional', 2025),

-- Chino Santanderiano (ID 15)
(15, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5000000, 'Nacional', 2025),

-- Carora (ID 16)
(16, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6200000, 'Nacional', 2025),

-- Limonero (ID 17)
(17, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5100000, 'Nacional', 2025),

-- Reyna (ID 18)
(18, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5300000, 'Nacional', 2025),

-- Criollo Lechero Centroamericano (ID 19)
(19, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5400000, 'Nacional', 2025),

-- Criollo Uruguayo (ID 20)
(20, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 5600000, 'Nacional', 2025);

-- III. Razas Cebuinas y Cruces (IDs 21 al 28)
INSERT INTO public.valorreferenciaganado 
    (id_raza, id_categoria, valor_promedio, region, anio)
VALUES
-- Gyr Lechero (ID 21)
(21, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6800000, 'Nacional', 2025),

-- Sahiwal (ID 22)
(22, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6500000, 'Nacional', 2025),

-- Red Sindhi (ID 23)
(23, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6400000, 'Nacional', 2025),

-- Guzerá (ID 24)
(24, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6300000, 'Nacional', 2025),

-- Girolando (ID 25)
(25, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7200000, 'Nacional', 2025),

-- Jerhol (ID 26)
(26, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7100000, 'Nacional', 2025),

-- Guzolando (ID 27)
(27, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6900000, 'Nacional', 2025),

-- Ayrgirolando (ID 28)
(28, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7000000, 'Nacional', 2025);

-- IV. Razas Doble Propósito (IDs 29 al 34)
INSERT INTO public.valorreferenciaganado 
    (id_raza, id_categoria, valor_promedio, region, anio)
VALUES
-- Normanda (ID 29)
(29, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6000000, 'Nacional', 2025),

-- Simmental / Fleckvieh (ID 30)
(30, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7500000, 'Nacional', 2025),

-- Montbéliarde (ID 31)
(31, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 7300000, 'Nacional', 2025),

-- Tarentaise (ID 32)
(32, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6700000, 'Nacional', 2025),

-- Meuse-Rhine-Issel (MRI) (ID 33)
(33, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6600000, 'Nacional', 2025),

-- Abondance (ID 34)
(34, (SELECT id_categoria FROM public.categoria_ganado WHERE nombre_categoria = 'Vaca de producción'), 6800000, 'Nacional', 2025);