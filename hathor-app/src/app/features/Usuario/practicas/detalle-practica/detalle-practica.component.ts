import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import {
  PracticasService,
  HatoPracticaDTO,
  PracticaDetalleDTO,
  EstadoPractica,
} from '../../../../core/services/practicas/practicas.service';
import { KpiService, KpiResultado, KpiHistorico } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

// AVANCE PASOS
import { AvancePasosComponent } from '../../../../shared/avance-pasos/avance-pasos.component';
import { HatoPracticaPasoResponseDTO } from '../../../../core/services/practicas/practicas.service';
@Component({
  selector: 'app-detalle-practica',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent, AvancePasosComponent],
  templateUrl: './detalle-practica.component.html',
  styleUrl: './detalle-practica.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetallePracticaComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  loading = true;
  loadingDetalle = false;
  loadingHistorico = false;
  error = '';
  guardandoAvance = false;
  avanzandoEstado = false;

  // ── Datos ─────────────────────────────────────────────────────────────
  idHatoPractica: string = '';
  practica: HatoPracticaDTO | null = null;
  detalle: PracticaDetalleDTO | null = null;
  kpiActual: KpiResultado | null = null;
  historicoKpi: KpiHistorico[] = [];

  // ── Avance ────────────────────────────────────────────────────────────
  avanceEditando = 0;
  mostrarSlider = false;

  // Propiedades - avance pasos
  overlayAvanceVisible = false;
  practicaAvanceActual: HatoPracticaDTO | null = null;
  pasosTextoActual: string[] = [];
  cargandoPasos = false;

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hatoState: HatoStateService,
    private practicasService: PracticasService,
    private kpiService: KpiService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.idHatoPractica = this.route.snapshot.paramMap.get('idHatoPractica') ?? '';

    if (!this.idHatoPractica) {
      this.error = 'ID de práctica no válido.';
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    // Intentar recuperar práctica desde el state del router
    const state = history.state as { practica?: HatoPracticaDTO };

    if (state?.practica) {
      this.practica = state.practica;
      this.avanceEditando = this.practica.porcentajeAvance ?? 0;
      this.loading = false;
      this.cargarDetalle(this.practica.idPractica);
      this.cargarKpi(this.practica.kpiImpactado);
      this.cdr.markForCheck();
    } else {
      // Fallback — cargar todas las prácticas del hato y filtrar
      this.cargarDesdeHato();
    }
  }

  // ── Carga de datos ────────────────────────────────────────────────────

  private cargarDesdeHato(): void {
    const hato = this.hatoState.getHatoActivo();
    if (!hato?.idHato) {
      this.error = 'No hay hato activo. Vuelve a la lista de prácticas.';
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    this.practicasService
      .getPracticasByHato(hato.idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (practicas) => {
          const encontrada = practicas.find((p) => p.idHatoPractica === this.idHatoPractica);

          if (!encontrada) {
            this.error = 'Práctica no encontrada.';
            this.loading = false;
            this.cdr.markForCheck();
            return;
          }

          this.practica = encontrada;
          this.avanceEditando = encontrada.porcentajeAvance ?? 0;
          this.loading = false;
          this.cargarDetalle(encontrada.idPractica);
          this.cargarKpi(encontrada.kpiImpactado);
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al cargar la práctica.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  private cargarDetalle(idPractica: number): void {
    this.loadingDetalle = true;
    this.cdr.markForCheck();

    this.practicasService
      .getDetalle(idPractica)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (detalle) => {
          this.detalle = detalle;
          this.loadingDetalle = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loadingDetalle = false;
          this.cdr.markForCheck();
        },
      });
  }

  private cargarKpi(codigoKpi: string): void {
    if (!codigoKpi) return;

    // Intentar desde caché primero
    const kpiCache = this.kpiService.getKpiDesdeCache(codigoKpi);
    if (kpiCache) {
      this.kpiActual = kpiCache;
      this.cargarHistoricoKpi(codigoKpi);
      this.cdr.markForCheck();
      return;
    }

    // Si no hay caché cargar KPIs del hato
    const hato = this.hatoState.getHatoActivo();
    if (!hato?.idHato) return;

    this.kpiService
      .getKpis(hato.idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.kpiActual = this.kpiService.getKpiDesdeCache(codigoKpi);
          this.cargarHistoricoKpi(codigoKpi);
          this.cdr.markForCheck();
        },
      });
  }

  private cargarHistoricoKpi(codigoKpi: string): void {
    const hato = this.hatoState.getHatoActivo();
    if (!hato?.idHato) return;

    this.loadingHistorico = true;
    this.cdr.markForCheck();

    this.kpiService
      .getHistorico(hato.idHato, codigoKpi)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (historico) => {
          // Últimos 6 registros ordenados por fecha
          this.historicoKpi = historico
            .sort((a, b) => new Date(a.fechaCalculo).getTime() - new Date(b.fechaCalculo).getTime())
            .slice(-6);
          this.loadingHistorico = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loadingHistorico = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Acciones ──────────────────────────────────────────────────────────

  avanzarEstado(): void {
    if (!this.practica) return;
    const siguiente = this.getSiguienteEstado(this.practica.estado);
    if (!siguiente) return;

    this.avanzandoEstado = true;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarEstado(this.idHatoPractica, siguiente)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          this.practica = actualizada;
          this.avanzandoEstado = false;
          this.mostrarSlider = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.avanzandoEstado = false;
          this.cdr.markForCheck();
        },
      });
  }

  toggleSlider(): void {
    this.mostrarSlider = !this.mostrarSlider;
    this.avanceEditando = this.practica?.porcentajeAvance ?? 0;
    this.cdr.markForCheck();
  }

  guardarAvance(): void {
    if (!this.practica) return;
    this.guardandoAvance = true;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarAvance(this.idHatoPractica, this.avanceEditando)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          this.practica = actualizada;
          this.guardandoAvance = false;
          this.mostrarSlider = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.guardandoAvance = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Navegación ────────────────────────────────────────────────────────

  volver(): void {
    this.router.navigate(['/practicas']);
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  private getSiguienteEstado(estado: EstadoPractica): EstadoPractica | null {
    if (estado === 'PENDIENTE') return 'EN_CURSO';
    if (estado === 'EN_CURSO') return 'COMPLETADA';
    return null;
  }

  get botonEstadoLabel(): string {
    if (!this.practica) return '';
    if (this.practica.estado === 'PENDIENTE') return '▶ Iniciar práctica';
    if (this.practica.estado === 'EN_CURSO') return '✓ Marcar como completada';
    return 'Completada';
  }

  get puedeAvanzar(): boolean {
    return this.practica?.estado !== 'COMPLETADA';
  }

  calcularAlturaBar(valor: number): number {
    if (!this.historicoKpi.length) return 0;
    const valores = this.historicoKpi.map((h) => h.valor).filter((v) => v != null);
    const max = Math.max(...valores);
    const min = Math.min(...valores);
    if (max === min) return 50;
    return Math.round(((valor - min) / (max - min)) * 80) + 10;
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

  claseEstado(estado: EstadoPractica): string {
    const map: Record<EstadoPractica, string> = {
      PENDIENTE: 'estado-pendiente',
      EN_CURSO: 'estado-curso',
      COMPLETADA: 'estado-completada',
    };
    return map[estado];
  }

  estadoLabel(estado: EstadoPractica): string {
    const map: Record<EstadoPractica, string> = {
      PENDIENTE: 'Pendiente',
      EN_CURSO: 'En curso',
      COMPLETADA: 'Completada',
    };
    return map[estado];
  }

  iconoCategoria(categoria: string): string {
    const map: Record<string, string> = {
      PRODUCTIVIDAD: '🥛',
      HATO: '🐄',
      FINANCIERO: '💰',
      EFICIENCIA: '⚙️',
    };
    return map[categoria] ?? '🌱';
  }

  get tendenciaKpi(): 'subiendo' | 'bajando' | 'estable' | null {
    if (this.historicoKpi.length < 2) return null;
    const ultimo = this.historicoKpi[this.historicoKpi.length - 1].valor;
    const penultimo = this.historicoKpi[this.historicoKpi.length - 2].valor;
    const diferencia = ultimo - penultimo;
    if (Math.abs(diferencia) < 0.01) return 'estable';
    return diferencia > 0 ? 'subiendo' : 'bajando';
  }

  get iconoTendencia(): string {
    const map: Record<string, string> = {
      subiendo: '↗️',
      bajando: '↘️',
      estable: '→',
    };
    return map[this.tendenciaKpi ?? ''] ?? '';
  }

  // Métodos - avance pasos
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
    if (!this.practica) return;
    this.practica = {
      ...this.practica,
      porcentajeAvance: response.porcentajeAvance,
      estado: response.estado as any,
    };
    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
