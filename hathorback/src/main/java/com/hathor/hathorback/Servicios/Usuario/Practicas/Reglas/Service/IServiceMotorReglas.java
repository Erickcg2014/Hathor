package com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Service;

import java.util.Map;

import com.hathor.hathorback.Entities.Hato.Hato;

public interface IServiceMotorReglas {

    // Punto de entrada principal del motor. Recibe el hato y el mapa
    // de KPIs recién calculados (codigo → valor Float).
    // Internamente hace todo el ciclo: elimina recomendaciones activas
    // anteriores, evalúa cada regla contra su benchmark contextualizado
    // y genera las nuevas recomendaciones con sus prácticas asignadas.
    // Lo llama ServiceKpi al final de calcularYGuardarKpis().
    void evaluar(Hato hato, Map<String, Float> kpisCalculados);

    // Evalúa si la condición de una regla se cumple dado el valor
    // del KPI y el benchmark de referencia disponible.
    // Soporta todos los operadores: MENOR_QUE, MAYOR_QUE, ENTRE,
    // MENOR_PCT_PROMEDIO, MAYOR_PCT_PROMEDIO, MAYOR_PCT_TOP.
    // Es un método auxiliar interno pero se declara en la interfaz
    // para facilitar pruebas unitarias del evaluador de condiciones.
    boolean cumpleCondicion(String operador, Double umbral1, Double umbral2,
                            Float valorKpi, Float benchmarkPromedio,
                            Float benchmarkTop);
}