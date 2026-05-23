import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../core/services/ui-state/hato-state.service';
import { AdminService } from '../../../core/services/admin/admin.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

// Componentes reutilizados
import { InfoHatoComponent } from '../../Usuario/hato/info-hato/info-hato.component';
import { InfoBenchmarkingComponent } from '../../Usuario/benchmarking/info-benchmarking/info-benchmarking.component';
import { ComparativaKpisComponent } from '../../Usuario/benchmarking/comparativa-kpis/comparativa-kpis.component';
import { InfoFinanzasComponent } from '../../Usuario/finanzas/info-finanzas/info-finanzas.component';
import { InfoProduccionComponent } from '../../Usuario/produccion/info-produccion/info-produccion.component';
import { InventarioGeneralResumenComponent } from '../../Usuario/hato/inventarios-hato/general/resumen/inventario-general-resumen.component';
import { InventarioGanadoResumenComponent } from '../../Usuario/hato/inventarios-hato/ganado/resumen/inventario-ganado-resumen.component';
import { MisPracticasComponent } from '../../Usuario/practicas/mis-practicas/mis-practicas.component';

// ── Secciones disponibles ─────────────────────────────────────────────────
export interface SeccionDetalle {
  id: string;
  label: string;
  icono: string;
  activa: boolean;
}

@Component({
  selector: 'app-detalle-hato-admin',
  standalone: true,
  imports: [
    CommonModule,
    SpinnerComponent,
    InfoHatoComponent,
    InfoBenchmarkingComponent,
    ComparativaKpisComponent,
    InfoFinanzasComponent,
    InfoProduccionComponent,
    InventarioGeneralResumenComponent,
    InventarioGanadoResumenComponent,
    MisPracticasComponent,
  ],
  templateUrl: './detalle-hato-admin.component.html',
  styleUrl: './detalle-hato-admin.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetalleHatoAdminComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  loading = true;
  error = '';
  nombreHato = '';
  idHato = '';

  // ── Secciones ───────────────────────────────────────────
  secciones: SeccionDetalle[] = [
    { id: 'info', label: 'Información del hato', icono: '🏡', activa: true },
    { id: 'kpis', label: 'KPIs', icono: '📈', activa: true },
    { id: 'benchmarking', label: 'Benchmarking', icono: '🔍', activa: false },
    { id: 'finanzas', label: 'Finanzas', icono: '💰', activa: true },
    { id: 'produccion', label: 'Producción', icono: '🥛', activa: true },
    { id: 'inv-general', label: 'Inventario general', icono: '📦', activa: false },
    { id: 'inv-ganado', label: 'Inventario ganado', icono: '🐄', activa: false },
    { id: 'practicas', label: 'Prácticas', icono: '🌱', activa: false },
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hatoState: HatoStateService,
    private adminService: AdminService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.idHato = this.route.snapshot.paramMap.get('idHato') ?? '';

    if (!this.idHato) {
      this.error = 'ID de hato no válido.';
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    this.cargarHato();
  }

  private cargarHato(): void {
    this.loading = true;
    this.cdr.markForCheck();

    this.adminService.getDetalleHato(this.idHato).subscribe({
      next: (detalle) => {
        this.nombreHato = detalle.nombreHato;

        this.hatoState.setHatoActivo({
          idHato: detalle.idHato,
          nombreHato: detalle.nombreHato,
          departamento: detalle.departamento,
          ciudad: detalle.ciudad ?? '',
          altitud: detalle.altitud ?? 0,
          tropico: detalle.tropico ?? '',
          areaHato: detalle.areaHato ?? 0,
          areaPastoreo: detalle.areaPastoreo ?? 0,
          cantCorrales: detalle.cantCorrales ?? 0,
          cantSalasOrdenio: detalle.cantSalasOrdenio ?? 0,
          capacidadAlmacenarLeche: detalle.capacidadAlmacenarLeche ?? 0,
          cantEmpleadosPermanentes: detalle.cantEmpleadosPermanentes ?? 0,
          cantEmpleadosTemporales: detalle.cantEmpleadosTemporales ?? 0,
          tipoHato: detalle.tipoHato ?? '',
          porcentajeCompletitud: detalle.porcentajeCompletitud,
          gastoMensualNomina: detalle.gastoMensualNomina ?? 0,
          gastoMensualAlimentacion: detalle.gastoMensualAlimentacion ?? 0,
          escala:
            (detalle.escala as 'PEQUEÑA' | 'MEDIANA' | 'GRANDE' | 'EMPRESARIAL' | undefined) ??
            undefined,
          latitud: detalle.latitud ?? undefined,
          longitud: detalle.longitud ?? undefined,
        });

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al cargar el hato. Verifica que existe.';
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Gestión de secciones ──────────────────────────────────────────────

  toggleSeccion(id: string): void {
    const seccion = this.secciones.find((s) => s.id === id);
    if (seccion) {
      seccion.activa = !seccion.activa;
      this.cdr.markForCheck();
    }
  }

  activarTodo(): void {
    this.secciones.forEach((s) => (s.activa = true));
    this.cdr.markForCheck();
  }

  desactivarTodo(): void {
    this.secciones.forEach((s) => (s.activa = false));
    this.cdr.markForCheck();
  }

  isActiva(id: string): boolean {
    return this.secciones.find((s) => s.id === id)?.activa ?? false;
  }

  get seccionesActivasCount(): number {
    return this.secciones.filter((s) => s.activa).length;
  }

  volver(): void {
    this.router.navigate(['/admin/hatos']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
