import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface NavSubItem {
  label: string;
  route: string;
  soloUsuario?: boolean;
}

export interface NavbarSection {
  seccion: string;
  title: string;
  items: NavSubItem[];
}

export const NAVBAR_SECTIONS: NavbarSection[] = [
  { seccion: '/home', title: 'Inicio', items: [] },
  {
    seccion: '/progreso',
    title: 'Progreso',
    items: [{ label: 'Mi progreso', route: '/progreso' }],
  },
  { seccion: '/alertas', title: 'Alertas', items: [] },
  {
    seccion: '/hato',
    title: 'Gestión del Hato',
    items: [
      { label: 'Información del hato', route: '/hato' },

      { label: 'Registrar nuevo item', route: '/inventarios/general/registro', soloUsuario: true },
    ],
  },
  {
    seccion: '/inventarios/general',
    title: 'Inventario General',
    items: [
      { label: 'Resumen general', route: '/inventarios/general/resumen' },
      { label: 'Registrar nuevo item', route: '/inventarios/general/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/inventarios/general/estadisticas' },
    ],
  },
  {
    seccion: '/inventarios/ganado',
    title: 'Inventario de Ganado',
    items: [
      { label: 'Resumen ganado', route: '/inventarios/ganado/resumen' },
      { label: 'Registrar nuevo item', route: '/inventarios/ganado/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/inventarios/ganado/estadisticas' },
    ],
  },
  {
    seccion: '/finanzas',
    title: 'Finanzas Reference',
    items: [
      { label: 'Resumen financiero', route: '/finanzas' },
      { label: 'Nuevo registro', route: '/finanzas/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/finanzas/estadisticas' },
      { label: 'Proyecciones', route: '/finanzas/proyecciones' },
    ],
  },
  {
    seccion: '/produccion',
    title: 'Producción',
    items: [
      { label: 'Resumen producción', route: '/produccion' },
      { label: 'Nuevo registro', route: '/produccion/registro', soloUsuario: true },
      { label: 'Estadísticas', route: '/produccion/estadisticas' },
    ],
  },
  {
    seccion: '/benchmarking',
    title: 'Benchmarking Global',
    items: [
      { label: 'Mis KPIs', route: '/benchmarking' },
      { label: 'Benchmarking KPIs', route: '/benchmarking/kpis' },
      { label: 'Ranking', route: '/benchmarking/ranking' },
    ],
  },
  {
    seccion: '/practicas',
    title: 'Buenas Prácticas',
    items: [
      { label: 'Sugeridas', route: '/practicas/sugeridas' },
      { label: 'Mis prácticas', route: '/practicas/mis-practicas' },
      { label: 'Recomendaciones', route: '/practicas/recomendaciones' },
      { label: 'Progreso', route: '/practicas/progreso' },
    ],
  },
  {
    seccion: '/reportes',
    title: 'Reportes y Exportación',
    items: [
      { label: 'Mis reportes', route: '/reportes' },
      { label: 'Generar reporte', route: '/reportes/generar' },
    ],
  },
  {
    seccion: '/admin',
    title: 'Administración',
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
    if (!ruta) {
      this.limpiar();
      return;
    }

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
