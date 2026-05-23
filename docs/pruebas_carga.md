# 🧪 Pruebas de Carga — Documentación

El módulo de pruebas de carga de **Hathor** fue desarrollado con **Apache JMeter** para evaluar el comportamiento del backend bajo escenarios de concurrencia y procesamiento intensivo. Las pruebas cubren consultas concurrentes, procesamiento de KPIs, evaluación del motor de reglas, consultas analíticas y financieras, y validación de estabilidad general del sistema.

---

## 🗂️ Estructura General

El archivo principal del plan de pruebas es:

```text
pruebas_carga.jmx
```

Este archivo concentra toda la configuración del plan: variables globales, configuración HTTP, usuarios simulados, endpoints evaluados, assertions y reportes de resultados.

---

## ⚙️ Configuración del Entorno

### Variables Globales

Se definieron variables reutilizables para facilitar la configuración del entorno. Todos los requests HTTP del plan las consumen:

| Variable      | Valor       |
| :------------ | :---------- |
| **BASE_URL**  | `localhost` |
| **BASE_PORT** | `8081`      |

### Autenticación

Las pruebas utilizan autenticación Bearer Token mediante un `Header Manager`:

```http
Authorization: Bearer ${token}
Content-Type: application/json
```

El token es cargado dinámicamente desde un archivo CSV.

### Usuarios Simulados

Las credenciales utilizadas durante las pruebas se almacenan en:

```text
usuarios_jmeter.csv
```

```csv
email,token,idHato
```

Cada hilo utiliza credenciales distintas para simular tráfico concurrente real.

---

## 🛣️ Endpoints Evaluados

Los Thread Groups pueden habilitarse o deshabilitarse individualmente, permitiendo ejecutar escenarios de forma aislada o combinada.

### 1. Consulta de KPIs

```http
GET /api/Kpi/hato/{idHato}
```

| Parámetro       | Valor            |
| :-------------- | :--------------- |
| **Usuarios**    | 100 concurrentes |
| **Ramp-Up**     | 30 segundos      |
| **Iteraciones** | 3                |
| **Estado**      | Deshabilitado    |

Evalúa consultas masivas de KPIs y valida tiempos de respuesta bajo carga.

---

### 2. Cálculo de KPIs

```http
POST /api/Kpi/calcular/{idHato}
```

| Parámetro       | Valor           |
| :-------------- | :-------------- |
| **Usuarios**    | 50 concurrentes |
| **Ramp-Up**     | 60 segundos     |
| **Iteraciones** | 1               |
| **Estado**      | ✅ Habilitado   |

Mide el rendimiento del procesamiento de KPIs y la estabilidad durante cálculos complejos.

> ⚠️ **Escenario crítico:** Este endpoint representa una de las cargas más exigentes del sistema debido al procesamiento interno requerido. Por ello se configura con menor concurrencia y mayor ramp-up que el resto.

---

### 3. Evaluación del Motor de Reglas

```http
POST /api/Kpi/evaluar/{idHato}
```

| Parámetro       | Valor            |
| :-------------- | :--------------- |
| **Usuarios**    | 100 concurrentes |
| **Iteraciones** | 2                |
| **Estado**      | Deshabilitado    |

Mide el desempeño del motor de inferencia y las reglas de negocio.

---

### 4. Benchmarking

```http
GET /api/Benchmarking/{idHato}
```

| Parámetro       | Valor            |
| :-------------- | :--------------- |
| **Usuarios**    | 100 concurrentes |
| **Iteraciones** | 3                |
| **Estado**      | Deshabilitado    |

Valida el desempeño de consultas comparativas y analíticas.

---

### 5. Consulta Financiera

```http
GET /api/Finanzas/{idHato}
```

| Parámetro       | Valor            |
| :-------------- | :--------------- |
| **Usuarios**    | 100 concurrentes |
| **Iteraciones** | 3                |
| **Estado**      | Deshabilitado    |

Evalúa el comportamiento del sistema bajo consultas financieras concurrentes.

---

### 6. Consulta de Prácticas

```http
GET /api/practicas/{idHato}
```

| Parámetro       | Valor            |
| :-------------- | :--------------- |
| **Usuarios**    | 100 concurrentes |
| **Iteraciones** | 3                |
| **Estado**      | Deshabilitado    |

Valida el desempeño de consultas informativas de prácticas productivas.

---

## ✅ Assertions

Todos los requests implementan una assertion de código HTTP para detectar errores durante las ejecuciones:

```text
HTTP Status Code = 200
```

---

## 📊 Reportes Configurados

El plan incluye múltiples listeners para análisis de resultados. Los resultados pueden almacenarse en:

```text
resultados.jtl
```

| Reporte                 | Descripción                 |
| :---------------------- | :-------------------------- |
| **Summary Report**      | Resumen estadístico general |
| **Aggregate Report**    | Métricas agregadas          |
| **View Results Tree**   | Inspección detallada        |
| **Response Time Graph** | Visualización de tiempos    |

> 🐢 **Nota de rendimiento:** Los listeners gráficos consumen memoria considerable. No se recomiendan para pruebas prolongadas; en esos casos se prefiere almacenar los resultados en `.jtl` y analizarlos posteriormente.

---

## ⏱️ Configuración de Timeouts

Los endpoints tienen tiempos de espera diferenciados según su complejidad de procesamiento:

| Tipo                   | Valor             |
| :--------------------- | :---------------- |
| **Connection Timeout** | 10 segundos       |
| **Response Timeout**   | 30 a 180 segundos |

Los endpoints de cálculo intensivo utilizan los valores máximos de timeout para acomodar su procesamiento interno.

---

## 🔩 Consideraciones Técnicas

El sistema utiliza conexiones HTTP **Keep-Alive** para reducir overhead en escenarios de alta concurrencia. Las pruebas están diseñadas para ambientes locales o controlados; no deben ejecutarse directamente contra entornos productivos sin ajustar previamente las variables de entorno.

---

## 🚀 Ejecución

La ejecución puede realizarse mediante interfaz gráfica de JMeter o directamente por consola:

```bash
jmeter -t pruebas_carga.jmx
```
