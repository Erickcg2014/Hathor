import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface NavSubItem {
  label: string;
  route: string;
  soloUsuario?: boolean;
}

export interface NavbarSection {
  seccion: string;
  items: NavSubItem[];
}

export const NAVBAR_SECTIONS: NavbarSection[] = [
  {
    seccion: '/home',
    items: [],
  },
  {
    seccion: '/progreso',
    items: [{ label: 'Mi progreso', route: '/progreso' }],
  },
  { seccion: '/alertas', items: [] },
  {
    seccion: '/hato',
    items: [
      { label: 'Información del hato', route: '/hato' },
      { label: 'Registrar nuevo item', route: '/inventarios/general/registro', soloUsuario: true },
    ],
  },
  {
    seccion: '/inventarios',
    items: [
      { label: 'Inventario general', route: '/inventarios/general/resumen' },
      { label: 'Inventario ganado', route: '/inventarios/ganado/resumen' },
    ],
  },
  {
    seccion: '/inventarios/general',
    items: [
      { label: 'Resumen general', route: '/inventarios/general/resumen' },
      { label: 'Registrar nuevo item', route: '/inventarios/general/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/inventarios/general/estadisticas' },
    ],
  },
  {
    seccion: '/inventarios/ganado',
    items: [
      { label: 'Resumen ganado', route: '/inventarios/ganado/resumen' },
      { label: 'Registrar nuevo item', route: '/inventarios/ganado/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/inventarios/ganado/estadisticas' },
    ],
  },
  {
    seccion: '/finanzas',
    items: [
      { label: 'Resumen financiero', route: '/finanzas' },
      { label: 'Nuevo registro', route: '/finanzas/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/finanzas/estadisticas' },
      { label: 'Proyecciones', route: '/finanzas/proyecciones' },
    ],
  },
  {
    seccion: '/produccion',
    items: [
      { label: 'Resumen producción', route: '/produccion' },
      { label: 'Nuevo registro', route: '/produccion/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/produccion/estadisticas' },
    ],
  },
  {
    seccion: '/benchmarking',
    items: [
      { label: 'Mis KPIs', route: '/benchmarking' },
      { label: 'Benchmarking KPIs', route: '/benchmarking/kpis' },
      { label: 'Ranking', route: '/benchmarking/ranking' },
    ],
  },
  {
    seccion: '/practicas',
    items: [
      { label: 'Sugeridas', route: '/practicas/sugeridas' },
      { label: 'Mis prácticas', route: '/practicas/mis-practicas' },
      { label: 'Recomendaciones', route: '/practicas/recomendaciones' },
      { label: 'Progreso', route: '/practicas/progreso' },
    ],
  },
  {
    seccion: '/reportes',
    items: [
      { label: 'Mis reportes', route: '/reportes' },
      { label: 'Generar reporte', route: '/reportes/generar' },
    ],
  },
  {
    seccion: '/admin',
    items: [
      { label: 'Dashboard', route: '/admin' },
      { label: 'Hatos', route: '/admin/hatos' },
      { label: 'Reglas', route: '/admin/reglas' },
      { label: 'Prácticas', route: '/admin/practicas' },
      { label: 'Recomendaciones', route: '/admin/recomendaciones' },
    ],
  },
];

@Injectable({ providedIn: 'root' })
export class NavbarStateService {
  private seccionActiva = new BehaviorSubject<NavbarSection | null>(null);
  seccionActiva$ = this.seccionActiva.asObservable();

  setSeccionPorRuta(ruta: string): void {
    const normalized = ruta.startsWith('/') ? ruta : `/${ruta}`;
    const found =
      NAVBAR_SECTIONS.filter((s) => normalized.startsWith(s.seccion)).sort(
        (a, b) => b.seccion.length - a.seccion.length,
      )[0] ?? null;
    this.seccionActiva.next(found);
  }

  limpiar(): void {
    this.seccionActiva.next(null);
  }
}
