-- =============================================
-- INSERCIÓN DE RAZAS BOVINAS
-- Clasificadas por tipo_raza
-- =============================================

-- I. Razas Puras Especializadas (Bos Taurus)
INSERT INTO public.raza (tipo_raza, nombre) VALUES
('PURA ESPECIALIZADA', 'Holstein-Friesian'),
('PURA ESPECIALIZADA', 'Jersey'),
('PURA ESPECIALIZADA', 'Pardo Suizo (Brown Swiss)'),
('PURA ESPECIALIZADA', 'Ayrshire'),
('PURA ESPECIALIZADA', 'Guernsey'),
('PURA ESPECIALIZADA', 'Milking Shorthorn'),
('PURA ESPECIALIZADA', 'Angeln (Red Cattle)'),
('PURA ESPECIALIZADA', 'Red Poll'),
('PURA ESPECIALIZADA', 'Dutch Belted (Lakenvelder)');

-- II. Razas Criollas Latinoamericanas
INSERT INTO public.raza (tipo_raza, nombre) VALUES
('CRIOLLA', 'Hartón del Valle'),
('CRIOLLA', 'Lucerna'),
('CRIOLLA', 'Blanco Orejinegro (BON)'),
('CRIOLLA', 'Costeño con Cuernos'),
('CRIOLLA', 'Chino Santanderiano'),
('CRIOLLA', 'Carora'),
('CRIOLLA', 'Limonero'),
('CRIOLLA', 'Reyna'),
('CRIOLLA', 'Criollo Lechero Centroamericano'),
('CRIOLLA', 'Criollo Uruguayo');

-- III. Razas Bos Indicus (Cebuínas) y Cruces Especializados
INSERT INTO public.raza (tipo_raza, nombre) VALUES
('CEBUINA', 'Gyr Lechero'),
('CEBUINA', 'Sahiwal'),
('CEBUINA', 'Red Sindhi'),
('CEBUINA', 'Guzerá'),
('CRUCE ESPECIALIZADO', 'Girolando (Gyr x Holstein)'),
('CRUCE ESPECIALIZADO', 'Jerhol (Jersey x Holstein)'),
('CRUCE ESPECIALIZADO', 'Guzolando (Guzerá x Holstein)'),
('CRUCE ESPECIALIZADO', 'Ayrgirolando (Ayrshire x Gyr)');

-- IV. Razas de Doble Propósito (Aptitud Láctea Superior)
INSERT INTO public.raza (tipo_raza, nombre) VALUES
('DOBLE PROPÓSITO', 'Normanda'),
('DOBLE PROPÓSITO', 'Simmental / Fleckvieh'),
('DOBLE PROPÓSITO', 'Montbéliarde'),
('DOBLE PROPÓSITO', 'Tarentaise'),
('DOBLE PROPÓSITO', 'Meuse-Rhine-Issel (MRI)'),
('DOBLE PROPÓSITO', 'Abondance');