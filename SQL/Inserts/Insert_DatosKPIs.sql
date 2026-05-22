INSERT INTO kpi (nombre, descripcion, formula, unidad, codigo) VALUES

-- PRODUCTIVIDAD LECHERA
('Litros/Vaca/Día', 
 'Productividad diaria por vaca en ordeño. Promedio nacional Colombia: 6.3 L. Zonas alta productividad: 12 L.', 
 'produccion_diaria_litros / vacas_en_ordenio', 
 'L/vaca/día', 'KPI_LITROS_VACA_DIA'),

('Litros/Ha/Año', 
 'Producción anual de leche por hectárea de pastoreo. Referencia Valle del Cauca especializado: 7.965 L/ha/año.', 
 'SUM(litros_producidos_anio) / area_pastoreo', 
 'L/ha/año', 'KPI_LITROS_HA_ANIO'),

('Producción/Ha/Día', 
 'Litros producidos por hectárea de pastoreo por día.', 
 'produccion_diaria_litros / area_pastoreo', 
 'L/ha/día', 'KPI_PRODUCCION_HA_DIA'),

-- MANEJO DE HATO
('Carga Animal', 
 'Animales por hectárea de pastoreo. Aproximación en cabezas/ha. Ideal con conversión UGG: Σ(cantidad × factor_UGG) / area_pastoreo.', 
 'total_animales / area_pastoreo', 
 'animales/ha', 'KPI_CARGA_ANIMAL'),

('% Vacas en Ordeño', 
 'Porcentaje del hato total que está en producción. Ideal entre 55% y 70%.', 
 '(vacas_en_ordenio / total_animales_hato) * 100', 
 '%', 'KPI_PCT_VACAS_ORDENIO'),

('Hembras Recría/Vaca', 
 'Índice de reemplazo del hato. Ideal entre 0.32 y 0.45. Exceso reduce utilidad operativa.', 
 '(novillas + terneras_hembra) / vacas_en_ordenio', 
 'ratio', 'KPI_HEMBRAS_RECRIA_VACA'),

-- FINANCIEROS
('Margen Neto %', 
 'Rentabilidad neta sobre ingresos totales. Ideal > 15% en ganadería lechera colombiana.', 
 '((total_ingresos - total_egresos) / total_ingresos) * 100', 
 '%', 'KPI_MARGEN_NETO'),

('Margen Bruto', 
 'Ingresos totales menos costos variables de producción (GASTO + COSTO, sin INVERSION).', 
 'total_ingresos - (total_gastos + total_costos)', 
 'COP', 'KPI_MARGEN_BRUTO'),

('Margen Bruto %', 
 'Margen bruto expresado como porcentaje de los ingresos.', 
 '((total_ingresos - (total_gastos + total_costos)) / total_ingresos) * 100', 
 '%', 'KPI_MARGEN_BRUTO_PCT'),

('Ratio Ingreso/Egreso', 
 'Relación entre ingresos y egresos totales. > 1.0 = superávit. Ideal > 1.2.', 
 'total_ingresos / total_egresos', 
 'ratio', 'KPI_RATIO_INGRESO_EGRESO'),

('Ingreso por Vaca', 
 'Ingreso total generado por cada vaca en ordeño.', 
 'total_ingresos / vacas_en_ordenio', 
 'COP/vaca', 'KPI_INGRESO_VACA'),

('Ingreso por Litro Vendido', 
 'Precio promedio real obtenido por litro vendido. Calculado desde registros reales de venta.', 
 'SUM(litros_vendidos * precio_litro) / SUM(litros_vendidos)', 
 'COP/L', 'KPI_INGRESO_LITRO'),

('ROA', 
 'Retorno sobre activos totales. beneficio_neto = ingresos - (gastos + costos). activos = inventario_general + inventario_ganado. Banco exige ROA > tasa de interés.', 
 '((total_ingresos - total_egresos_sin_inversion) / (valor_inventario_general + valor_inventario_ganado)) * 100', 
 '%', 'KPI_ROA'),

('Rotación de Activos', 
 'Cuántos pesos de ingreso genera cada peso invertido en activos.', 
 'total_ingresos / (valor_inventario_general + valor_inventario_ganado)', 
 'veces', 'KPI_ROTACION_ACTIVOS'),

('Costo por Litro Producido', 
 'Costo total de producción dividido entre litros producidos reales.', 
 '(total_gastos + total_costos) / SUM(litros_producidos)', 
 'COP/L', 'KPI_COSTO_LITRO'),

-- EFICIENCIA OPERATIVA
('Empleados/Ha', 
 'Densidad de mano de obra sobre área de pastoreo. < 0.15 emp/ha considerado eficiente.', 
 '(cant_empleadospermanentes + cant_empleadostemporales) / area_pastoreo', 
 'emp/ha', 'KPI_EMPLEADOS_HA'),

('Litros/Empleado/Día', 
 'Productividad de la mano de obra en producción de leche.', 
 'produccion_diaria_litros / (cant_empleadospermanentes + cant_empleadostemporales)', 
 'L/emp/día', 'KPI_LITROS_EMPLEADO'),

('Capacidad Almac. Utilizada', 
 'Porcentaje de uso de la capacidad de almacenamiento de leche.', 
 '(produccion_diaria_litros / capacidad_almacenar_leche) * 100', 
 '%', 'KPI_CAP_ALMAC_UTILIZADA');

-- ADICIONALES 
INSERT INTO kpi (nombre, descripcion, formula, unidad, codigo) VALUES

('Ingreso/Ha/Año', 
 'Ingreso total anual generado por hectárea de pastoreo. KPI clave para comparar fincas de diferente tamaño.', 
 'total_ingresos_anio / area_pastoreo', 
 'COP/ha/año', 'KPI_INGRESO_HA_ANIO'),

('Balance Neto', 
 'Diferencia absoluta entre ingresos y egresos totales del período.', 
 'total_ingresos - total_egresos', 
 'COP', 'KPI_BALANCE_NETO'),

('Periodo Lactancia vs Estándar', 
 'Comparación del período de lactancia registrado vs estándar de 305 días.', 
 'periodo_lactancia_promedio - 305', 
 'días', 'KPI_LACTANCIA_VS_ESTANDAR'),

('Frecuencia Ordeño', 
 'Número de ordeños diarios. Impacta directamente en producción total.', 
 'frecuencia_ordenio', 
 'ordeños/día', 'KPI_FRECUENCIA_ORDENIO');
 
-- KPI: Ingreso sobre costo de alimentación 
INSERT INTO kpi (nombre, descripcion, formula, unidad, codigo) VALUES
('IOFC',
 'Ingreso sobre costo de alimentación. KPI reina en lechería técnica. Cuánto queda después de pagar la comida.',
 'SUM(ingresos_leche) - SUM(gastos_alimentacion)',
 'COP', 'KPI_IOFC'),
-- Porcentaje de los ingresos totales destinado a pagar nómina y mano de obra. 
('Costo Laboral %',
 'Porcentaje de los ingresos totales destinado a pagar nómina y mano de obra. Superar 20-25% indica ineficiencia.',
 '(SUM(gastos_nomina) / total_ingresos) * 100',
 '%', 'KPI_COSTO_LABORAL_PCT'),
-- Precio mínimo por litro para no perder ni ganar. 
('Precio de Equilibrio',
 'Precio mínimo por litro para no perder ni ganar. Si precio_real < breakeven el hato destruye valor.',
 '(total_gastos + total_costos) / SUM(litros_producidos)',
 'COP/L', 'KPI_BREAKEVEN_LITRO');