import { Component } from '@angular/core';
import { Routes } from '@angular/router';

import { LoginComponent } from './features/acceso/login/login.component';
import { RegisterComponent } from './features/acceso/register/register.component';
import { AuthGuard } from './core/guards/auth.guard';

import { DashboardLayoutComponent } from './layouts/dashboard-layout/dashboard-layout.component';
import { ResetPasswordComponent } from './features/acceso/reset-password/reset-password.component';
import { HomeComponent } from './features/Usuario/home/home.component';
import { LandingComponent } from './features/landing/landing.component';
import { NotFoundComponent } from './features/not-found/not-found.component';
import { UsuarioComponent } from './features/Usuario/usuario/usuario.component';
import { MiProgresoComponent } from './features/Usuario/mi-progreso/mi-progreso.component';

// Alertas
import { AlertasComponent } from './features/Usuario/alertas/alertas.component';

// Hato
import { HatoComponent } from './features/Usuario/hato/hato.component';
import { NuevoHatoComponent } from './features/Usuario/hato/nuevo-hato/nuevo-hato.component';
import { InfoHatoComponent } from './features/Usuario/hato/info-hato/info-hato.component';
import { InventariosHatoComponent } from './features/Usuario/hato/inventarios-hato/inventarios-hato.component';
import { InventarioGeneralComponent } from './features/Usuario/hato/inventarios-hato/general/inventario-general.component';
import { InventarioGeneralRegistroComponent } from './features/Usuario/hato/inventarios-hato/general/registro/inventario-general-registro.component';
import { InventarioGeneralResumenComponent } from './features/Usuario/hato/inventarios-hato/general/resumen/inventario-general-resumen.component';
import { InventarioGeneralEstadisticasComponent } from './features/Usuario/hato/inventarios-hato/general/estadisticas/inventario-general-estadisticas.component';
import { InventarioGanadoComponent } from './features/Usuario/hato/inventarios-hato/ganado/inventario-ganado.component';
import { InventarioGanadoRegistroComponent } from './features/Usuario/hato/inventarios-hato/ganado/registro/inventario-ganado-registro.component';
import { InventarioGanadoResumenComponent } from './features/Usuario/hato/inventarios-hato/ganado/resumen/inventario-ganado-resumen.component';
import { InventarioGanadoEstadisticasComponent } from './features/Usuario/hato/inventarios-hato/ganado/estadisticas/inventario-ganado-estadisticas.component';

// Finanzas
import { FinanzasComponent } from './features/Usuario/finanzas/finanzas.component';
import { InfoFinanzasComponent } from './features/Usuario/finanzas/info-finanzas/info-finanzas.component';
import { RegistroFinancieroComponent } from './features/Usuario/finanzas/registro-financiero/registro-financiero.component';
import { EstadisticasFinanzasComponent } from './features/Usuario/finanzas/estadisticas-finanzas/estadisticas-finanzas.component';
import { ProyeccionesComponent } from './features/Usuario/finanzas/proyecciones/proyecciones.component';

// Producción
import { ProduccionComponent } from './features/Usuario/produccion/produccion.component';
import { InfoProduccionComponent } from './features/Usuario/produccion/info-produccion/info-produccion.component';
import { RegistroProduccionComponent } from './features/Usuario/produccion/registro-produccion/registro-produccion.component';
import { EstadisticasProduccionComponent } from './features/Usuario/produccion/estadisticas-produccion/estadisticas-produccion.component';

// Benchmarking
import { BenchmarkingComponent } from './features/Usuario/benchmarking/benchmarking.component';
import { InfoBenchmarkingComponent } from './features/Usuario/benchmarking/info-benchmarking/info-benchmarking.component';
import { ComparativaKpisComponent } from './features/Usuario/benchmarking/comparativa-kpis/comparativa-kpis.component';
import { KpiDetalleComponent } from './features/Usuario/benchmarking/kpi-detalle/kpi-detalle.component';
import { RankingComponent } from './features/Usuario/benchmarking/ranking/ranking.component';

// Prácticas
import { PracticasComponent } from './features/Usuario/practicas/practicas.component';
import { MisPracticasComponent } from './features/Usuario/practicas/mis-practicas/mis-practicas.component';
import { RecomendacionesComponent } from './features/Usuario/practicas/recomendaciones/recomendaciones.component';
import { ProgresoPracticasComponent } from './features/Usuario/practicas/progreso-practicas/progreso-practicas.component';
import { DetallePracticaComponent } from './features/Usuario/practicas/detalle-practica/detalle-practica.component';

// Reportes
import { ReportesComponent } from './features/Usuario/reportes/reportes.component';
import { CrearReporteComponent } from './features/Usuario/reportes/crear-reporte/crear-reporte.component';
import { HistorialReportesComponent } from './features/Usuario/reportes/historial-reportes/historial-reportes.component';

// ADMIN
import { AdminComponent } from './features/admin/admin.component';
import { DashboardAdminComponent } from './features/admin/dashboard-admin/dashboard-admin.component';
import { GestionHatosComponent } from './features/admin/gestion-hatos/gestion-hatos.component';
import { DetalleHatoAdminComponent } from './features/admin/detalle-hato-admin/detalle-hato-admin.component';
import { GestionReglasComponent } from './features/admin/gestion-reglas/gestion-reglas.component';
import { GestionPracticasComponent } from './features/admin/gestion-practicas/gestion-practicas.component';
import { GestionRecomendacionesComponent } from './features/admin/gestion-recomendaciones/gestion-recomendaciones.component';
import { AdminGuard } from './core/guards/admin.guard';
import { PracticasSugeridasComponent } from './features/Usuario/practicas/practicas-sugeridas/practicas-sugeridas.component';

export const routes: Routes = [
  { path: '', component: LandingComponent, pathMatch: 'full' },

  // Públicas
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'auth/reset-password', component: ResetPasswordComponent },

  // Privadas
  {
    path: '',
    component: DashboardLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'miCuenta', component: UsuarioComponent },

      // Hato
      {
        path: 'hato',
        component: HatoComponent,
        children: [
          { path: '', component: InfoHatoComponent },
          { path: 'inventarios', pathMatch: 'full', redirectTo: '/inventarios/general/resumen' },
          { path: 'nuevo', component: NuevoHatoComponent },
        ],
      },

      { path: 'alertas', component: AlertasComponent },

      { path: 'progreso', component: MiProgresoComponent },

      // Inventarios
      {
        path: 'inventarios',
        component: InventariosHatoComponent,
        children: [
          { path: '', pathMatch: 'full', redirectTo: 'general/resumen' },
          {
            path: 'general',
            component: InventarioGeneralComponent,
            children: [
              { path: '', pathMatch: 'full', redirectTo: 'resumen' },
              { path: 'registro', component: InventarioGeneralRegistroComponent },
              { path: 'resumen', component: InventarioGeneralResumenComponent },
              { path: 'estadisticas', component: InventarioGeneralEstadisticasComponent },
            ],
          },
          {
            path: 'ganado',
            component: InventarioGanadoComponent,
            children: [
              { path: '', pathMatch: 'full', redirectTo: 'resumen' },
              { path: 'registro', component: InventarioGanadoRegistroComponent },
              { path: 'resumen', component: InventarioGanadoResumenComponent },
              { path: 'estadisticas', component: InventarioGanadoEstadisticasComponent },
            ],
          },
        ],
      },

      // Finanzas
      {
        path: 'finanzas',
        component: FinanzasComponent,
        children: [
          { path: '', component: InfoFinanzasComponent },
          { path: 'registro', component: RegistroFinancieroComponent },
          { path: 'estadisticas', component: EstadisticasFinanzasComponent },
          { path: 'proyecciones', component: ProyeccionesComponent },
        ],
      },

      // Producción de leche
      {
        path: 'produccion',
        component: ProduccionComponent,
        children: [
          { path: '', component: InfoProduccionComponent },
          { path: 'registro', component: RegistroProduccionComponent },
          { path: 'estadisticas', component: EstadisticasProduccionComponent },
        ],
      },

      // Benchmarking
      {
        path: 'benchmarking',
        component: BenchmarkingComponent,
        children: [
          { path: '', component: InfoBenchmarkingComponent },
          { path: 'kpis', component: ComparativaKpisComponent },
          { path: 'kpi/:codigo', component: KpiDetalleComponent },
          { path: 'ranking', component: RankingComponent },
        ],
      },

      // Prácticas
      {
        path: 'practicas',
        component: PracticasComponent,
        children: [
          { path: '', redirectTo: 'sugeridas', pathMatch: 'full' },
          { path: 'sugeridas', component: PracticasSugeridasComponent },
          { path: 'mis-practicas', component: MisPracticasComponent },
          { path: 'recomendaciones', component: RecomendacionesComponent },
          { path: 'progreso', component: ProgresoPracticasComponent },
          { path: 'detalle/:idHatoPractica', component: DetallePracticaComponent },
        ],
      },

      // Reportes
      {
        path: 'reportes',
        component: ReportesComponent,
        children: [
          { path: '', component: HistorialReportesComponent },
          { path: 'generar', component: CrearReporteComponent },
          { path: 'historial', redirectTo: '', pathMatch: 'full' },
        ],
      },
      // Admin

      {
        path: 'admin',
        component: AdminComponent,
        canActivate: [AdminGuard],
        children: [
          {
            path: '',
            component: DashboardAdminComponent,
          },
          {
            path: 'hatos',
            component: GestionHatosComponent,
          },
          {
            path: 'hatos/:idHato',
            component: DetalleHatoAdminComponent,
          },
          {
            path: 'reglas',
            component: GestionReglasComponent,
          },
          {
            path: 'practicas',
            component: GestionPracticasComponent,
          },
          {
            path: 'recomendaciones',
            component: GestionRecomendacionesComponent,
          },
        ],
      },
    ],
  },

  { path: '**', component: NotFoundComponent },
];
