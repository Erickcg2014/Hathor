import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  EstadoPractica,
  HatoPracticaDTO,
  PracticasService,
} from '../../../../core/services/practicas/practicas.service';
import { KpiService, KpiResultado } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

//Avance pasos
import { AvancePasosComponent } from '../../../../shared/avance-pasos/avance-pasos.component';
import { HatoPracticaPasoResponseDTO } from '../../../../core/services/practicas/practicas.service';

@Component({
  selector: 'app-mis-practicas',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent, AvancePasosComponent],
  templateUrl: './mis-practicas.component.html',
  styleUrl: './mis-practicas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisPracticasComponent implements OnInit, OnDestroy {
  @Input() modoLectura = false;

  // ── Estado ────────────────────────────────────────────────────────────
  hatoActivo: Hato | null = null;
  loading = false;
  loadingKpis = false;
  error = '';

  // ── Datos — solo EN_CURSO y COMPLETADAS ───────────────────────────────
  practicasEnCurso: HatoPracticaDTO[] = [];
  practicasCompletadas: HatoPracticaDTO[] = [];

  // ── Prácticas innecesarias ────────────────────────────────────────────
  // Prácticas EN_CURSO cuyo KPI ya mejoró (estado OPTIMO o ACEPTABLE)
  practicasInnecesarias: HatoPracticaDTO[] = [];

  // ── Tab activo ────────────────────────────────────────────────────────
  tabActivo: 'en-curso' | 'completadas' = 'en-curso';

  // ── Avance inline ─────────────────────────────────────────────────────
  practicaAvanceAbierta: string | null = null;
  avanceEditando = 0;
  guardandoAvance = false;

  // ── Acción de estado ──────────────────────────────────────────────────
  avanzandoEstado: string | null = null;

  // ── Cerrar práctica innecesaria ───────────────────────────────────────
  cerrandoPractica: string | null = null;

  overlayAvanceVisible = false;
  practicaAvanceActual: HatoPracticaDTO | null = null;
  pasosTextoActual: string[] = [];
  cargandoPasos = false;

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private practicasService: PracticasService,
    private kpiService: KpiService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarTodo(hato.idHato);
      } else {
        this.practicasEnCurso = [];
        this.practicasCompletadas = [];
      }
      this.cdr.markForCheck();
    });

    if (!this.hatoState.getHatoActivo()) {
      this.hatoService.getMisHatos().subscribe({
        next: (hatos) => {
          if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
          this.cdr.markForCheck();
        },
      });
    }
  }

  // ── Carga ─────────────────────────────────────────────────────────────

  private cargarTodo(idHato: string): void {
    this.loading = true;
    this.loadingKpis = true;
    this.error = '';
    this.cdr.markForCheck();

    // Cargar prácticas
    this.practicasService
      .getPracticasByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          // Separar por estado
          this.practicasEnCurso = data.filter((p) => p.estado === 'EN_CURSO');
          this.practicasCompletadas = data.filter((p) => p.estado === 'COMPLETADA');
          this.loading = false;

          // Cargar KPIs para detectar innecesarias
          this.kpiService.getKpis(idHato).subscribe({
            next: () => {
              this.loadingKpis = false;
              this.detectarInnecesarias();
              this.cdr.markForCheck();
            },
            error: () => {
              this.loadingKpis = false;
              this.cdr.markForCheck();
            },
          });

          this.cdr.markForCheck();
        },
        error: () => {
          this.practicasEnCurso = [];
          this.practicasCompletadas = [];
          this.loading = false;
          this.error = 'Error al cargar las prácticas.';
          this.cdr.markForCheck();
        },
      });
  }

  // ── Detectar prácticas innecesarias ───────────────────────────────────

  private detectarInnecesarias(): void {
    this.practicasInnecesarias = this.practicasEnCurso.filter((p) => {
      if (!p.kpiImpactado) return false;
      const kpi = this.kpiService.getKpiDesdeCache(p.kpiImpactado);
      // La práctica es innecesaria si el KPI ya está OPTIMO o ACEPTABLE
      return kpi?.estado === 'OPTIMO' || kpi?.estado === 'ACEPTABLE';
    });
  }

  // ── Cerrar práctica innecesaria ───────────────────────────────────────

  cerrarPracticaInnecesaria(practica: HatoPracticaDTO): void {
    if (this.modoLectura) return;
    this.cerrandoPractica = practica.idHatoPractica;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarEstado(practica.idHatoPractica, 'COMPLETADA')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          // Mover de en curso a completadas
          this.practicasEnCurso = this.practicasEnCurso.filter(
            (p) => p.idHatoPractica !== practica.idHatoPractica,
          );
          this.practicasCompletadas = [actualizada, ...this.practicasCompletadas];
          this.practicasInnecesarias = this.practicasInnecesarias.filter(
            (p) => p.idHatoPractica !== practica.idHatoPractica,
          );
          this.cerrandoPractica = null;
          this.cdr.markForCheck();
        },
        error: () => {
          this.cerrandoPractica = null;
          this.cdr.markForCheck();
        },
      });
  }

  ignorarInnecesaria(practica: HatoPracticaDTO): void {
    this.practicasInnecesarias = this.practicasInnecesarias.filter(
      (p) => p.idHatoPractica !== practica.idHatoPractica,
    );
    this.cdr.markForCheck();
  }

  // ── Avance inline ─────────────────────────────────────────────────────

  abrirSliderAvance(practica: HatoPracticaDTO): void {
    if (this.modoLectura) return;
    this.practicaAvanceAbierta = practica.idHatoPractica;
    this.avanceEditando = practica.porcentajeAvance ?? 0;
    this.cdr.markForCheck();
  }

  cerrarSliderAvance(): void {
    this.practicaAvanceAbierta = null;
    this.cdr.markForCheck();
  }

  guardarAvance(practica: HatoPracticaDTO): void {
    if (this.modoLectura) return;
    this.guardandoAvance = true;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarAvance(practica.idHatoPractica, this.avanceEditando)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          this.practicasEnCurso = this.practicasEnCurso.map((p) =>
            p.idHatoPractica === actualizada.idHatoPractica ? actualizada : p,
          );
          this.practicaAvanceAbierta = null;
          this.guardandoAvance = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.guardandoAvance = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Completar práctica ────────────────────────────────────────────────

  completarPractica(practica: HatoPracticaDTO): void {
    if (this.modoLectura) return;
    this.avanzandoEstado = practica.idHatoPractica;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarEstado(practica.idHatoPractica, 'COMPLETADA')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          this.practicasEnCurso = this.practicasEnCurso.filter(
            (p) => p.idHatoPractica !== practica.idHatoPractica,
          );
          this.practicasCompletadas = [actualizada, ...this.practicasCompletadas];
          this.practicasInnecesarias = this.practicasInnecesarias.filter(
            (p) => p.idHatoPractica !== practica.idHatoPractica,
          );
          this.avanzandoEstado = null;
          this.cdr.markForCheck();
        },
        error: () => {
          this.avanzandoEstado = null;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Navegación ────────────────────────────────────────────────────────

  verDetalle(practica: HatoPracticaDTO): void {
    this.router.navigate(['/practicas/detalle', practica.idHatoPractica], { state: { practica } });
  }

  // ── Tabs ──────────────────────────────────────────────────────────────

  setTab(tab: 'en-curso' | 'completadas'): void {
    this.tabActivo = tab;
    this.cdr.markForCheck();
  }

  // ── KPI helpers ───────────────────────────────────────────────────────

  getKpiDePractica(codigoKpi: string): KpiResultado | null {
    if (!codigoKpi) return null;
    return this.kpiService.getKpiDesdeCache(codigoKpi);
  }

  colorEstadoKpi(estado: string | undefined): string {
    const map: Record<string, string> = {
      OPTIMO: 'kpi-optimo',
      ACEPTABLE: 'kpi-aceptable',
      CRITICO: 'kpi-critico',
      SIN_DATOS: 'kpi-sin-datos',
    };
    return map[estado ?? ''] ?? 'kpi-sin-datos';
  }

  iconoEstadoKpi(estado: string | undefined): string {
    const map: Record<string, string> = {
      OPTIMO: '✅',
      ACEPTABLE: '🟡',
      CRITICO: '🔴',
      SIN_DATOS: '—',
    };
    return map[estado ?? ''] ?? '—';
  }

  esInnecesaria(practica: HatoPracticaDTO): boolean {
    return this.practicasInnecesarias.some((p) => p.idHatoPractica === practica.idHatoPractica);
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  iconoCategoria(categoria: string): string {
    const map: Record<string, string> = {
      PRODUCTIVIDAD: '🥛',
      HATO: '🐄',
      FINANCIERO: '💰',
      EFICIENCIA: '⚙️',
    };
    return map[categoria] ?? '🌱';
  }

  colorDificultad(dificultad: string): string {
    const map: Record<string, string> = {
      BAJA: 'dif-baja',
      MEDIA: 'dif-media',
      ALTA: 'dif-alta',
    };
    return map[dificultad] ?? '';
  }

  get promedioAvanceEnCurso(): number {
    if (!this.practicasEnCurso.length) return 0;
    const suma = this.practicasEnCurso.reduce((acc, p) => acc + (p.porcentajeAvance ?? 0), 0);
    return Math.round(suma / this.practicasEnCurso.length);
  }

  abrirOverlayAvance(practica: HatoPracticaDTO): void {
    this.practicaAvanceActual = practica;
    this.cargandoPasos = true;
    this.cdr.markForCheck();

    this.practicasService.getDetalle(practica.idPractica).subscribe({
      next: (detalle) => {
        this.pasosTextoActual = detalle.pasos ?? [];
        this.overlayAvanceVisible = true;
        this.cargandoPasos = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.pasosTextoActual = [];
        this.overlayAvanceVisible = true;
        this.cargandoPasos = false;
        this.cdr.markForCheck();
      },
    });
  }

  cerrarOverlayAvance(): void {
    this.overlayAvanceVisible = false;
    this.practicaAvanceActual = null;
    this.pasosTextoActual = [];
    this.cdr.markForCheck();
  }

  onAvanceActualizado(response: HatoPracticaPasoResponseDTO): void {
    if (!this.practicaAvanceActual) return;
    const id = this.practicaAvanceActual.idHatoPractica;
    this.practicasEnCurso = this.practicasEnCurso.map((p) =>
      p.idHatoPractica === id
        ? { ...p, porcentajeAvance: response.porcentajeAvance, estado: response.estado as any }
        : p,
    );
    this.cdr.markForCheck();
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
