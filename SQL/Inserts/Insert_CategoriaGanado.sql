-- =============================================
-- INSERCIÓN DE CATEGORÍAS DE GANADO
-- =============================================

INSERT INTO public.categoria_ganado (nombre_categoria, descripcion) VALUES
-- Categorías por edad/sexo
('Ternero', 'Machos desde el nacimiento hasta el destete (0-8 meses aprox.)'),
('Ternera', 'Hembras desde el nacimiento hasta el destete (0-8 meses aprox.)'),
('Macho de levante', 'Machos desde el destete hasta los 24 meses'),
('Novilla', 'Hembras desde el destete hasta el primer parto (8-24 meses)'),
('Novillo', 'Machos castrados en etapa de ceba'),
('Vaca horra', 'Vaca que no está preñada ni lactando'),
('Vaca de producción', 'Vaca en etapa de lactancia'),
('Vaca seca', 'Vaca en periodo seco (no lactando, próxima a parir)'),
('Toro', 'Reproductor macho adulto'),
('Buey', 'Macho castrado para trabajo'),

('Vaca de reemplazo', 'Hembra destinada a reemplazar vacas en producción'),
('Vaca de descarte', 'Vaca destinada a venta por baja productividad o edad'),
('Cría', 'Animal menor de 1 año (término genérico)'),
('Mauta/Mauto', 'Término regional para animal entre 1-2 años (Venezuela/Colombia)');

INSERT INTO public.categoria_ganado (nombre_categoria, descripcion) VALUES
('Vaca de doble propósito', 'Vaca utilizada tanto para leche como para carne'),
('Novilla de vientre', 'Novilla destinada a reproducción');