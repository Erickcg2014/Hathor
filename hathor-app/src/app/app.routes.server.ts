import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Públicas — Prerender
  { path: '', renderMode: RenderMode.Prerender },
  { path: 'login', renderMode: RenderMode.Prerender },
  { path: 'register', renderMode: RenderMode.Prerender },
  { path: 'auth/reset-password', renderMode: RenderMode.Client },
  { path: 'progreso', renderMode: RenderMode.Client },

  // Privadas — Client
  { path: 'home', renderMode: RenderMode.Client },
  { path: 'miCuenta', renderMode: RenderMode.Client },
  { path: 'admin', renderMode: RenderMode.Client },
  { path: 'alertas', renderMode: RenderMode.Client },

  // Hato
  { path: 'hato', renderMode: RenderMode.Client },
  { path: 'hato/inventarios', renderMode: RenderMode.Client },
  { path: 'hato/nuevo', renderMode: RenderMode.Client },

  // Finanzas
  { path: 'finanzas', renderMode: RenderMode.Client },
  { path: 'finanzas/registro', renderMode: RenderMode.Client },
  { path: 'finanzas/estadisticas', renderMode: RenderMode.Client },
  { path: 'finanzas/proyecciones', renderMode: RenderMode.Client },

  // Producción
  { path: 'produccion', renderMode: RenderMode.Client },
  { path: 'produccion/registro', renderMode: RenderMode.Client },
  { path: 'produccion/estadisticas', renderMode: RenderMode.Client },

  // Benchmarking
  { path: 'benchmarking', renderMode: RenderMode.Client },
  { path: 'benchmarking/kpis', renderMode: RenderMode.Client },
  { path: 'benchmarking/kpi/:codigo', renderMode: RenderMode.Client },
  { path: 'benchmarking/ranking', renderMode: RenderMode.Client },

  // Prácticas
  { path: 'practicas', renderMode: RenderMode.Client },
  { path: 'practicas/recomendaciones', renderMode: RenderMode.Client },
  { path: 'practicas/sugeridas', renderMode: RenderMode.Client },
  { path: 'practicas/mis-practicas', renderMode: RenderMode.Client },
  { path: 'practicas/progreso', renderMode: RenderMode.Client },
  { path: 'practicas/detalle/:idHatoPractica', renderMode: RenderMode.Client },

  // Reportes
  { path: 'reportes', renderMode: RenderMode.Client },
  { path: 'reportes/generar', renderMode: RenderMode.Client },
  // Admin
  { path: 'admin', renderMode: RenderMode.Client },
  { path: 'admin/hatos', renderMode: RenderMode.Client },
  { path: 'admin/hatos/:idHato', renderMode: RenderMode.Client },
  { path: 'admin/reglas', renderMode: RenderMode.Client },
  { path: 'admin/practicas', renderMode: RenderMode.Client },
  { path: 'admin/recomendaciones', renderMode: RenderMode.Client },

  // Fallback
  { path: '**', renderMode: RenderMode.Prerender },
];
