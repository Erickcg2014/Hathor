# 🏛️ Documentación General del Código Fuente - Proyecto Hathor

¡Bienvenido a la documentación técnica de **Hathor**! Este espacio está diseñado para desarrolladores y colaboradores del proyecto. Aquí encontrarás la arquitectura del sistema, la explicación de los componentes del código fuente y los flujos de datos que hacen funcionar la plataforma.

---

## 🚀 ¿Qué es Hathor?

**Hathor** es una plataforma integral de gestión agropecuaria y financiera, diseñada para optimizar el control de inventarios (ganado, insumos generales), el seguimiento de prácticas productivas y la administración financiera (inversiones y presupuestos) de los usuarios.

El sistema está construido bajo una arquitectura moderna, desacoplada y escalable basada en microservicios/componentes independientes.

---

## 🗺️ Estructura General del Repositorio

El proyecto está organizado en un único repositorio (Monorepo) que divide claramente las responsabilidades del sistema:

- **`hathorback/`**: El núcleo del negocio. Un backend robusto desarrollado en **Java (Spring Boot)** que expone una API REST para gestionar la lógica de inventarios, finanzas y roles.
- **`hathorfront/`** _(o el nombre de tu carpeta frontend)_: La interfaz de usuario. Desarrollada en **[Tecnología: ej. React/Angular]**, enfocada en ser intuitiva, rápida y responsive.
- **`apigateway/`**: El punto de entrada único para el cliente, encargado de redirigir las peticiones, seguridad básica y balanceo.
- **`docs/`**: Este sitio de documentación estática gestionado con **MkDocs**.

---

## 🛠️ Stack Tecnológico Principal

| Componente        | Tecnología / Framework          | Propósito                                    |
| :---------------- | :------------------------------ | :------------------------------------------- |
| **Backend**       | Java 17+ / Spring Boot          | API REST, lógica de negocio y persistencia.  |
| **Frontend**      | [Tu Frontend]                   | Interfaz gráfica y experiencia de usuario.   |
| **Base de Datos** | [Tu BD: ej. PostgreSQL / MySQL] | Almacenamiento relacional de datos.          |
| **Documentación** | MkDocs / Material Theme         | Manuales técnicos y documentación de código. |

---

## 📖 Guía de Navegación de la Documentación

Para entender a fondo el código fuente, te recomendamos seguir este orden de lectura en el menú lateral:

1.  **[Arquitectura](arquitectura.md)**: Vista de pájaro del sistema, diagramas de flujo y cómo se comunican el Front, el API Gateway y el Back.
2.  **[Backend (Java/Spring)](backend.md)**: Detalle del código de `hathorback`. Explicación de Servicios, Controladores, Entidades y los DTOs (Data Transfer Objects).
3.  **[Frontend](frontend.md)**: Estructura de componentes, manejo de estados globales y consumo de servicios.
4.  **[API Gateway](apigateway.md)**: Configuración de rutas, filtros de seguridad y middleware.
5.  **[Base de Datos (SQL)](sql.md)**: Modelo Entidad-Relación, scripts de migración y diseño de tablas.
6.  **[Scripts / Python](python.md)**: Herramientas adicionales, automatizaciones o análisis de datos integrados en el ecosistema.

---

## 🏁 Requisitos Previos para Desarrolladores

Para levantar el proyecto completo en tu entorno local, asegúrate de tener instalado:

- **Java JDK 17** o superior.
- **Node.js** (versión LTS) para el frontend.
- **Python 3.x** y **Pip** (para scripts y esta documentación).
- Un gestor de Base de Datos compatible.

> 💡 **Tip de desarrollo:** Recuerda que puedes compilar y verificar el Javadoc del Backend ejecutando `mvn javadoc:javadoc` dentro de la carpeta `hathorback` para ver el detalle técnico autogenerado de las clases de Java.
