import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil, firstValueFrom } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import {
  ProduccionLecheService,
  ProduccionLeche,
  RegistroProduccionLecheDTO,
} from '../../../../core/services/produccion/produccion-leche.service';
import { KpiService } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

interface RegistroLocal {
  fecha: string;
  litrosProducidos: number | null;
  vacasOrdenadas: number | null;
}

@Component({
  selector: 'app-registro-produccion',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './registro-produccion.component.html',
  styleUrl: './registro-produccion.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistroProduccionComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  hatoActivo: Hato | null = null;
  loading = true;
  guardando = false;
  error = '';
  exito = '';

  // ── Registros existentes ──────────────────────────────────────────────
  registros: ProduccionLeche[] = [];
  loadingRegistros = false;

  // ── Paginación ────────────────────────────────────────────────────────
  readonly PAGE_SIZE = 10;
  paginaActual = 1;

  // ── Formulario ────────────────────────────────────────────────────────
  nuevo: RegistroLocal = {
    fecha: new Date().toISOString().split('T')[0],
    litrosProducidos: null,
    vacasOrdenadas: null,
  };

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private produccionService: ProduccionLecheService,
    private kpiService: KpiService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarRegistros(hato.idHato);
      } else {
        this.loading = false;
      }
      this.cdr.markForCheck();
    });
  }

  // ── Carga de registros existentes ─────────────────────────────────────

  private cargarRegistros(idHato: string): void {
    this.loadingRegistros = true;
    this.cdr.markForCheck();

    this.produccionService.getByHato(idHato).subscribe({
      next: (registros) => {
        this.registros = registros.sort(
          (a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime(),
        );
        this.loadingRegistros = false;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingRegistros = false;
        this.loading = false;
        this.error = 'Error al cargar los registros existentes.';
        this.cdr.markForCheck();
      },
    });
  }

  // ── Guardar registro ──────────────────────────────────────────────────

  async guardar(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;

    if (!this.nuevo.litrosProducidos || this.nuevo.litrosProducidos <= 0) {
      this.error = 'Ingresa una cantidad válida de litros producidos.';
      this.cdr.markForCheck();
      return;
    }

    if (!this.nuevo.fecha) {
      this.error = 'Selecciona una fecha válida.';
      this.cdr.markForCheck();
      return;
    }

    this.guardando = true;
    this.error = '';
    this.exito = '';
    this.cdr.markForCheck();

    const dto: RegistroProduccionLecheDTO = {
      idHato: this.hatoActivo.idHato as any,
      fecha: this.nuevo.fecha as any,
      litrosProducidos: this.nuevo.litrosProducidos,
      vacasOrdenadas: this.nuevo.vacasOrdenadas ?? undefined,
    };

    try {
      await firstValueFrom(this.produccionService.crearRegistro(dto));

      this.exito = '✅ Registro guardado correctamente.';
      this.resetForm();
      this.cargarRegistros(this.hatoActivo.idHato);

      // Recalcular KPIs en segundo plano
      this.kpiService.calcularKpis(this.hatoActivo.idHato).subscribe();
    } catch {
      this.error = 'Error al guardar el registro. Intenta de nuevo.';
    } finally {
      this.guardando = false;
      this.cdr.markForCheck();
    }
  }

  private resetForm(): void {
    this.nuevo = {
      fecha: new Date().toISOString().split('T')[0],
      litrosProducidos: null,
      vacasOrdenadas: null,
    };
  }

  // ── Paginación ────────────────────────────────────────────────────────

  get registrosPaginados(): ProduccionLeche[] {
    const inicio = (this.paginaActual - 1) * this.PAGE_SIZE;
    return this.registros.slice(inicio, inicio + this.PAGE_SIZE);
  }

  get totalPaginas(): number {
    return Math.ceil(this.registros.length / this.PAGE_SIZE);
  }

  irAPagina(p: number): void {
    if (p < 1 || p > this.totalPaginas) return;
    this.paginaActual = p;
    this.cdr.markForCheck();
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  get litrosPorVaca(): number | null {
    if (!this.nuevo.litrosProducidos || !this.nuevo.vacasOrdenadas) return null;
    return Math.round((this.nuevo.litrosProducidos / this.nuevo.vacasOrdenadas) * 10) / 10;
  }

  get promedioMensual(): number {
    if (!this.registros.length) return 0;
    const ahora = new Date();
    const esteMes = this.registros.filter((r) => {
      const f = new Date(r.fecha);
      return f.getMonth() === ahora.getMonth() && f.getFullYear() === ahora.getFullYear();
    });
    if (!esteMes.length) return 0;
    const total = esteMes.reduce((s, r) => s + (r.litrosProducidos ?? 0), 0);
    return Math.round((total / esteMes.length) * 10) / 10;
  }

  get totalEsteMes(): number {
    const ahora = new Date();
    return this.registros
      .filter((r) => {
        const f = new Date(r.fecha);
        return f.getMonth() === ahora.getMonth() && f.getFullYear() === ahora.getFullYear();
      })
      .reduce((s, r) => s + (r.litrosProducidos ?? 0), 0);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
