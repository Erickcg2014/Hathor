# 🐮 HATHOR — Proyecto de Gestión Financiera en Hathor lecheros de colombia

Proyecto de Gestión financiera con Arquitectura SBA para el manejo de la gestión financiera y productiva a través del uso de metodologías como benchmarking con estrategias de uso de KPIs. Además, se integra un motor de reglas para asociar la funcionalidad de generar prácticas de manera automática acorde a los resultados de los KPIs del usuarios. Se integra también el uso de una API de herramienta LLM para el uso de un asistente tipo Chatbot para servir de asistente al usuario y generar prácticas automáticamente.

---

## ✨ Funcionalidades

- **Inicio de sesión y Registro** — Gestionado por el frontend y supabase
- **Registro de información de hato, inventarios, productiva y financiera** — El usuario registra su primer hato mediante formularios separados por secciones.
- **Manejos de información** — El usuario puede actualizar la información del hato, inventarios, producción y de finanzas.
- **Consulta de estadísticas** — EL usuario puede consultar estadísticas relacionadas a sus finanzas, producción y inventarios.
- **Cálculo y consulta de KPIs** — El usuario puede consultar los kpis calculados en el sistema.
- **Consulta de Benchmarking y Ranking** — El usuario puede el estado actual del percentil de acuerdo a cada KPI y su posición en el mercado.
- **Generación de prácticas y Recomendaciones** — El usuario puede consultar un conjunto de prácticas a realizar y un conjunto de recomendaciones de acuerdo a los resultados de los KPIs.
- **Información constante con el usuario** — Notificaciones de alertas, consultar su progreso y evolución de mejora y consultar la mejora en su rendimiento.
- **Asistente de inteligencia artificial** — El usuario tiene un asistente con el cual puede realizar preguntas relacionadas al uso del sistema, su información, como mejorar y también puede generar prácticas de acuerdo a los KPIs especificos que desee mejorar.

---

## 📁 Estructura del proyecto

A continuación, se presenta la estructura del proyecto a nivel general

```
HATHOR/
├── ngrok.yml            ← despliegue del apigateway en ngrok
│
├── pruebas-carga/           ← Scripts de python, para generar, limpiar y extraer los tokens de los usuarios creados para prueba.
├── apigateway/
│   ├── src/
|   |     ├── main/
|   |     |     ├── config/  ← archivos de configuración del API
|   |     |     └──ApiGatewayApplication.java
|   |     └──resources/
|   |           └──application.yml   ← inicialización y estrcutura del API
|   |
│   └── .env
│
├── hathor-app/
|   ├── public/  ← Archivos estáticos del sistema.
|   ├── src/
|   |     ├── app/
|   |     |     ├── core/
|   |     |     |     ├── config/
|   |     |     |     |    ├── supabase.config.ts   ← Configuración supabase auth (credenciales de acceso)
|   |     |     |     ├── directives/               ← Directiva para scroll
|   |     |     |     ├── directives/               ← Guards para admin y usuario
|   |     |     |     ├── interceptors/             ← Interceptar peticiones para agregar el token
|   |     |     |     ├── services/                 ← consumo de servicios de cada uno de los endpoints del backend.
|   |     |     ├── features/                       ← features tanto públicos como privados (de usuario y administrador)
|   |     |     |     ├── acceso/                   ← features público para login y registro
|   |     |     |     ├── Usuario/                  ← features privados de usuario
|   |     |     |     ├── admin/                    ← features privados de administrador
|   |     |     ├── layout/                         ← layout donde se renderizan todos los componentes
|   |     |     |     ├── dashboard-layout.component
|   |     |     ├── shared/                         ← módulos compartidos, que aparecen desde cualquier sección: como el navbar, el sidebar, etc.
|   |     |     |     ├── navbar/
|   |     |     |     ├── sidebar/
|   |     |     |     ├── asistente-chat/
|   |     |     |     ├── spinner/
|   |     ├── environments/                         ← Configuración de URL para acceso local o de producción
|   |     |     ├── environment.production.ts       ← URL Producción
|   |     |     ├── environment.ts                  ← URL Local
|   ├── app.routes.ts                               ← Ruteo para navegación entre pantallas
|   ├── app.routes.server.ts                        ← Renderizado en el servidor (solo para algunas pantallas)
│
├── hathorback/
|   ├── src/
|   |     ├── main/
|   |     |    ├── java/com/hathor/hathorback
|   |     |    |      ├── config/                 ← Configuraciones generales (Redis, validación de administrador o usuario, peticiones asíncronas)
|   |     |    |      ├── Entities/               ← Entidades del modelo relacional de Hathor para uso del backend
|   |     |    |      ├── Servicios/              ← Carpeta de Servicios
agrupados
|   |     |    |      |     ├── Admin/            ← Servicios especializados para el admin
|   |     |    |      |     ├── Usuario/          ← Servicios especializados para el usuario
|   |     |    |      |     ├── Seed/             ← Servicios para la carga de usuarios a través de los endpoints de las funciones de usuario.
|   |     |    ├── resources
|   |     |    |      ├── corpus/                            ← Documentos de contexto para la herramienta de inteligencia artificial
|   |     |    |      ├── application.properties             ← Credenciales y configuraciones de acceso a los sistemas externos.
├── SQL/
|   ├── DDL/              ← Data Definition Language del Sistema Hathor
|   ├── Inserts/          ← Inserts de Inicio de configuración en el sistema de la BD
|   ├── Procedure/        ← Procedures o funciones para funcionalidades específicas de BD.
|   ├── Schedule/         ← JOBS programados en la BD para realizar tareas específicos a cierta hora de días específicos.
|   ├── ngrok.yml         ← Tunel HTTP para el backend (generación de instancias públicas con ngrok)
```

---

## ⚙️ Instalación

### 1. Requisitos

- Windows 10/11
- Java 17+
- Node JS + Angular 19+
- Python 3.10 o superior (Python para Windows)
- Cuenta en Supabase
- Cuenta en Claude Platform
- Cuenta en Upstash - Instancia Redis.

### 2. Configuración Inicial

#### Supabase

Debes crear una cuenta en Supabase y crear una base de datos y agregar una configuración de Supabase AUTH. Asignar las credenciales a los siguientes archivos.

```bash
hathor-app/supabase.config.ts       ← Credenciales Supabase AUTH, REVISAR ARCHIVO supabase.config.example.ts
hathorback/application.properties   ← Credenciales Supabase DB, REVISAR ARCHIVO application.properties.example
```

Luego de configuradas las instancias, debes ingresar al **SQL EDITOR** y ejecutar en el siguiente orden para creación de base de datos y creación de las tablas y procedures.

- En la carpeta **SQL/DDL**, copie el código _BD_Hathor.sql_, y pegalo en el **SQL EDITOR** y lo ejecutas.
- En la carpeta **SQL/Inserts**, debes copiar cada código _\*.sql_, y pegalo en el **SQL EDITOR** y los ejecutas uno por uno.
- En la carpeta **SQL/Procedures**, debes copiar cada código _\*.sql_, y pegalo en el **SQL EDITOR** y los ejecutas uno por uno.
- En la carpeta **SQL/Shedule**, debes copiar cada código _\*.sql_, y pegalo en el **SQL EDITOR** y los ejecutas uno por uno.

#### Herramienta De Claude Platform (LLM)

Crear cuenta en Claude Platform y recargar con Tokes (Con $5 dolares en tokes es suficiente). Crear un proyecto y obtener la API KEY y adjuntar a

```bash
hathorback/application.properties   ← Credenciales Claude, REVISAR ARCHIVO application.properties.example
```

#### Configuración de REDIS

Crear cuenta en Upstash - REDIS, se recomienda crear **2 cuentas separadas** para no generar conflictos de información y conflictos de limites de prueba gratuita.

- Configurar una instancia REDIS, para el rate limiter, y las credenciales deben ir para el archivo
  .env **(REVISAR ARCHIVO apigateway/.env.example)**
- Configurar una instancia REDIS, para el contexto de la herramienta de inteligencia artificial. Agregarlo al archivo **hathorback/application.properties** Revisar archivo _application.properties.example_

### 3. Ejecutar

Luego de haber realizado la configuración, abrir 3 terminales tipo **CMD**, y acceder a las 3 carpetas principales del proyecto

En la primer terminal, ingresa a la carpeta `hathor/hathorback`, y ejecuta el siguiente omando en la terminal:

```env
mvnw.cmd spring-boot:run
```

En la segunda terminal, ingresa a la carpeta `hathor/apigateway`, y ejecuta el siguiente omando en la terminal:

```env
mvnw.cmd spring-boot:run
```

En la tercera terminal, ingresa a la carpeta `hathor/hathor-app`, y ejecuta el siguiente omando en la terminal:

```env
ng serve                                  ← Para probar local
```

En dado caso que quieras buildear para producción el frontend y utilizar la URL para un entorno productivo (como vercel, render, railway, etc) ejecuta el siguiente comando, si no sabes de lo que hablo ejecuta solo el comando anterior:

```env
ng build --configuration production       ← Para buildear y configurar una URL de producción
```

## Consideraciones

- Necesitas configurar los sistemas externos y adjuntar las credenciales a los archivos declarados.
- Para acceder al sistema (si lo pruebas local), debes ingresar al siguiente enlace

```env
http://localhost:4200/
```

En caso de que lo tengas en un puerto diferente solo cambia el puerto. Ten en cuenta que para probar la totalidad de la herramientas, deberás tener corriendo el `Backend`, ejecutando la carpeta `hathor/hathorback` y el `Api Gateway`, ejecutando la carpeta `hathor/apigateway`.

- Para probar todas las funcionalidades, deberás primero crear un usuario en la sección de registrarse del proyecto. O si no, en la carpeta `pruebas carga`, puedes ejecutar la función `create_users_and_seed.py` y configurar la cantidad de Usuarios a cuántos quieras (si es solo para 1, solo configura la cantidad a 1).
