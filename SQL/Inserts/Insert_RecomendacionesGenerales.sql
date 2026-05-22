-- Solo agregar la columna estado PLANTILLA si no está
-- El campo estado ya existe con DEFAULT 'ACTIVA'

INSERT INTO recomendacion_general
  (tipo, subtipo, titulo, mensaje, icono,
   url_accion, label_accion, estado, prioridad,
   leida, fecha_creacion)
VALUES

-- LLUVIA INTENSA
('CLIMA','LLUVIA_INTENSA',
 'Lluvia intensa en tu zona',
 'Con lluvias superiores a 20mm: revisa canales de drenaje para evitar encharcamiento que genera mastitis ambiental, retira el ganado de zonas bajas, suspende aplicaciones de fertilizantes nitrogenados y verifica el estado de techos y cercas eléctricas.',
 '🌧️','/practicas','Ver prácticas','PLANTILLA','ALTA',true,NOW()),

('CLIMA','LLUVIA_INTENSA',
 'Riesgo de mastitis por humedad excesiva',
 'La humedad prolongada incrementa hasta 3 veces el riesgo de mastitis ambiental. Mantén las camas secas, realiza el ordeño con ubres completamente secas y aplica sellador post-ordeño. Si tienes pruebas recientes de células somáticas, revísalas.',
 '🐄','/produccion','Ver producción','PLANTILLA','ALTA',true,NOW()),

-- CALOR EXTREMO
('CLIMA','CALOR_EXTREMO',
 'Estrés calórico — reducción de producción',
 'Temperaturas sobre 32°C reducen la producción de leche hasta un 25%. Asegura acceso permanente a agua fresca (una vaca puede consumir 150 litros/día en calor), evita el ordeño entre 11am y 3pm, proporciona sombra y reduce densidad animal en establos cerrados.',
 '🌡️','/produccion','Ver producción','PLANTILLA','ALTA',true,NOW()),

('CLIMA','CALOR_EXTREMO',
 'Alimentación y minerales en calor extremo',
 'El ganado reduce consumo de materia seca hasta un 10% en calor extremo. Ofrece alimentos de alta densidad energética en horas frescas (antes 8am, después 6pm). La sudoración excesiva genera pérdida de minerales — suplementa con sal mineralizada.',
 '🌿','/inventarios/general/resumen','Ver inventario','PLANTILLA','MEDIA',true,NOW()),

-- HELADA
('CLIMA','HELADA',
 'Riesgo de helada — protege tu hato',
 'Con temperaturas bajo 4°C en trópico frío: aloja terneros menores de 30 días en espacios cerrados, aumenta ración energética, verifica bebederos y tuberías expuestas, y evita el pastoreo antes de las 8am cuando el frío es mayor.',
 '🧊','/inventarios/ganado/resumen','Ver ganado','PLANTILLA','ALTA',true,NOW()),

('CLIMA','HELADA',
 'Pasturas dañadas por helada',
 'No pastorear hasta que la pastura se descongele completamente (después de las 10am) para evitar timpanismo espumoso. Si la pastura tiene más de 3 días de daño por helada, suplementa con heno o ensilaje.',
 '🌱','/practicas','Ver prácticas','PLANTILLA','MEDIA',true,NOW()),

-- VIENTO FUERTE
('CLIMA','VIENTO_FUERTE',
 'Vientos fuertes — revisión de instalaciones',
 'Con vientos sobre 50 km/h: revisa techos de establos y bodegas, verifica tensión y aisladores de cercas eléctricas, asegura equipos de ordeño al aire libre y mantén el ganado alejado de árboles con riesgo de caída de ramas.',
 '💨','/hato','Ver hato','PLANTILLA','ALTA',true,NOW()),

-- SEQUIA
('CLIMA','SEQUIA',
 'Alerta de sequía — gestión del agua',
 'Sin lluvia por 5 o más días: verifica niveles de reservorios y jagüeyes (una vaca necesita 80-120 litros/día), reduce carga animal en potreros secos para evitar sobrepastoreo permanente y considera corte de forrajes mientras haya pasto verde.',
 '🏜️','/finanzas','Ver finanzas','PLANTILLA','ALTA',true,NOW()),

('CLIMA','SEQUIA',
 'Suplementación nutricional en sequía',
 'Durante sequías la calidad nutricional de pasturas cae significativamente. Suplementa con bloques multinutricionales, sales mineralizadas con azufre y monitorea el peso del ganado semanalmente para detectar pérdidas tempranas.',
 '🌿','/inventarios/general/resumen','Ver inventario','PLANTILLA','MEDIA',true,NOW());