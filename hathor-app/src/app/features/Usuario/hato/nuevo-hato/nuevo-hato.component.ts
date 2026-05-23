import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { UserService } from '../../../../core/services/usuario/user.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { firstValueFrom } from 'rxjs';

import { SeccionInfoHatoComponent } from './secciones/seccion-info-hato/seccion-info-hato.component';
import { SeccionInventarioComponent } from './secciones/seccion-inventario/seccion-inventario.component';
import { SeccionProduccionComponent } from './secciones/seccion-produccion/seccion-produccion.component';
import { SeccionFinancieroComponent } from './secciones/seccion-financiero/seccion-financiero.component';

import { KpiService } from '../../../../core/services/kpi/kpi.service';

interface Paso {
  numero: number;
  titulo: string;
  descripcion: string;
  icono: string;
}

type ModoPaso = 'editable' | 'readonly' | 'bloqueado';

@Component({
  selector: 'app-nuevo-hato',
  standalone: true,
  imports: [
    CommonModule,
    SpinnerComponent,
    SeccionInfoHatoComponent,
    SeccionInventarioComponent,
    SeccionProduccionComponent,
    SeccionFinancieroComponent,
  ],
  templateUrl: './nuevo-hato.component.html',
  styleUrl: './nuevo-hato.component.css',
})
export class NuevoHatoComponent implements OnInit {
  pasoActual = 1;
  loading = true;
  idUsuario = '';
  hatoActual: Hato | null = null;

  pasos: Paso[] = [
    {
      numero: 1,
      titulo: 'Información del hato',
      descripcion: 'Datos generales y ubicación',
      icono: '🏡',
    },
    {
      numero: 2,
      titulo: 'Inventario general',
      descripcion: 'Ganado e infraestructura',
      icono: '📦',
    },
    { numero: 3, titulo: 'Producción lechera', descripcion: 'Capacidad y producción', icono: '🥛' },
    {
      numero: 4,
      titulo: 'Registros financieros',
      descripcion: 'Costos e ingresos base',
      icono: '💰',
    },
  ];

  private DRAFT_KEY = '';

  constructor(
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private userService: UserService,
    private kpiService: KpiService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.userService.getUsuarioLogueado().subscribe({
      next: (usuario) => {
        this.idUsuario = usuario.idUsuario ?? '';
        this.DRAFT_KEY = `hathor_hato_draft_${this.idUsuario}`;
        this.verificarEstadoInicial();
        this.cdr.markForCheck();
      },
      error: () => {
        this.DRAFT_KEY = 'hathor_hato_draft_anonimo';
        this.verificarEstadoInicial();
        this.cdr.markForCheck();
      },
    });
  }

  // ===== VERIFICACIÓN INICIAL =====

  private async verificarEstadoInicial(): Promise<void> {
    this.loading = true;
    try {
      const draft = this.leerDraft();

      if (draft?.idHato) {
        const hato = await firstValueFrom(this.hatoService.getHatoById(draft.idHato));
        if (hato && (hato.porcentajeCompletitud ?? 0) < 100) {
          this.hatoActual = hato;
          this.pasoActual = this.calcularPasoActual(hato.porcentajeCompletitud!);
          this.loading = false;
          this.cdr.markForCheck();
          return;
        }
      }

      const hatos = await firstValueFrom(this.hatoService.getMisHatos());
      const hatoIncompleto = hatos.find((h) => (h.porcentajeCompletitud ?? 0) < 100);

      if (hatoIncompleto) {
        this.hatoActual = hatoIncompleto;
        this.pasoActual = this.calcularPasoActual(hatoIncompleto.porcentajeCompletitud!);
        this.guardarDraft();
      }
    } catch {
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  // ===== DRAFT =====

  private leerDraft(): any {
    if (!this.DRAFT_KEY) return null;
    const raw = localStorage.getItem(this.DRAFT_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }

  guardarDraft(): void {
    if (!this.DRAFT_KEY) return;
    const draft = {
      idHato: this.hatoActual?.idHato ?? null,
      pasoActual: this.pasoActual,
      porcentajeCompletitud: this.hatoActual?.porcentajeCompletitud ?? 0,
    };
    localStorage.setItem(this.DRAFT_KEY, JSON.stringify(draft));
  }

  limpiarDraft(): void {
    localStorage.removeItem(this.DRAFT_KEY);
  }

  // ===== UTILIDADES =====

  private calcularPasoActual(porcentaje: number): number {
    if (porcentaje >= 75) return 4;
    if (porcentaje >= 50) return 3;
    if (porcentaje >= 25) return 2;
    return 1;
  }

  getModoPaso(numeroPaso: number): ModoPaso {
    const completitud = this.hatoActual?.porcentajeCompletitud ?? 0;
    const pasoCompletadoHasta = Math.floor(completitud / 25);
    if (numeroPaso <= pasoCompletadoHasta) return 'readonly';
    if (numeroPaso === pasoCompletadoHasta + 1) return 'editable';
    return 'bloqueado';
  }

  get porcentaje(): number {
    return (this.pasoActual / this.pasos.length) * 100;
  }

  onSeccionCompletada(hatoActualizado: Hato): void {
    this.hatoActual = hatoActualizado;
    this.hatoState.setHatoActivo(hatoActualizado);
    this.guardarDraft();

    if ((hatoActualizado.porcentajeCompletitud ?? 0) >= 100) {
      this.limpiarDraft();

      if (hatoActualizado.idHato) {
        this.kpiService.calcularKpis(hatoActualizado.idHato).subscribe({
          next: () => this.router.navigate(['/benchmarking']),
          error: () => this.router.navigate(['/benchmarking']),
        });
      } else {
        this.router.navigate(['/benchmarking']);
      }
      return;
    }

    this.pasoActual = this.calcularPasoActual(hatoActualizado.porcentajeCompletitud!);
    this.cdr.markForCheck();
  }

  onIrSiguiente(): void {
    if (this.pasoActual < this.pasos.length) {
      this.pasoActual++;
      this.guardarDraft();
      this.cdr.markForCheck();
    }
  }

  onIrAnterior(): void {
    if (this.pasoActual > 1) {
      this.pasoActual--;
      this.guardarDraft();
      this.cdr.markForCheck();
    }
  }

  cancelar(): void {
    this.router.navigate(['/hato']);
  }
}
