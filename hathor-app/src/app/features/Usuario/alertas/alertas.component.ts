import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../core/services/hato/hato.service';
import {
  AlertasService,
  AlertaHatoDTO,
  AlertasResumenDTO,
} from '../../../core/services/alertas/alertas.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-alertas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './alertas.component.html',
  styleUrl: './alertas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlertasComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  loading = true;
  evaluando = false;
  error = '';
  hatoActivo: Hato | null = null;

  // ── Datos ─────────────────────────────────────────────────────────────
  resumen: AlertasResumenDTO | null = null;

  // ── Filtro ────────────────────────────────────────────────────────────
  filtroActivo: 'TODAS' | 'CRITICA' | 'PREVENTIVA' | 'OPORTUNIDAD' = 'TODAS';

  // ── Alerta expandida ──────────────────────────────────────────────────
  alertaExpandida: number | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private alertasService: AlertasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarAlertas(hato.idHato as string);
      } else {
        this.loading = false;
      }
      this.cdr.markForCheck();
    });

    if (!this.hatoState.getHatoActivo()) {
      this.hatoService.getMisHatos().subscribe({
        next: (hatos) => {
          if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
          else this.loading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  // ── Carga ─────────────────────────────────────────────────────────────

  private cargarAlertas(idHato: string): void {
    this.loading = true;
    this.cdr.markForCheck();

    this.alertasService.getResumen(idHato).subscribe({
      next: (resumen) => {
        this.resumen = resumen;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al cargar las alertas.';
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Acciones ──────────────────────────────────────────────────────────

  marcarLeida(alerta: AlertaHatoDTO): void {
    if (alerta.leida) return;
    this.alertasService
      .marcarLeida(alerta.idAlerta)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          alerta.leida = true;
          if (this.resumen) {
            this.resumen.totalNoLeidas = Math.max(0, this.resumen.totalNoLeidas - 1);
          }
          this.alertasService.notificarCambio();
          this.cdr.markForCheck();
        },
      });
  }

  marcarTodasLeidas(): void {
    if (!this.hatoActivo?.idHato) return;
    this.alertasService
      .marcarTodasLeidas(this.hatoActivo.idHato as string)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.cargarAlertas(this.hatoActivo!.idHato as string);
          this.alertasService.notificarCambio();
          this.cdr.markForCheck();
        },
      });
  }

  evaluarAlertas(): void {
    if (!this.hatoActivo?.idHato) return;
    this.evaluando = true;
    this.cdr.markForCheck();

    this.alertasService
      .evaluarAlertas(this.hatoActivo.idHato as string)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.cargarAlertas(this.hatoActivo!.idHato as string);
          this.evaluando = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.evaluando = false;
          this.cdr.markForCheck();
        },
      });
  }

  toggleExpandida(idAlerta: number): void {
    this.alertaExpandida = this.alertaExpandida === idAlerta ? null : idAlerta;
    this.cdr.markForCheck();
  }

  setFiltro(filtro: 'TODAS' | 'CRITICA' | 'PREVENTIVA' | 'OPORTUNIDAD'): void {
    this.filtroActivo = filtro;
    this.cdr.markForCheck();
  }

  // ── Getters ───────────────────────────────────────────────────────────

  get alertasFiltradas(): AlertaHatoDTO[] {
    if (!this.resumen) return [];
    if (this.filtroActivo === 'TODAS') {
      return [...this.resumen.criticas, ...this.resumen.preventivas, ...this.resumen.oportunidades];
    }
    if (this.filtroActivo === 'CRITICA') return this.resumen.criticas;
    if (this.filtroActivo === 'PREVENTIVA') return this.resumen.preventivas;
    return this.resumen.oportunidades;
  }

  get totalAlertas(): number {
    if (!this.resumen) return 0;
    return (
      this.resumen.criticas.length +
      this.resumen.preventivas.length +
      this.resumen.oportunidades.length
    );
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  iconoSeveridad(severidad: string): string {
    const map: Record<string, string> = {
      CRITICA: '🔴',
      PREVENTIVA: '🟡',
      OPORTUNIDAD: '🟢',
    };
    return map[severidad] ?? '📋';
  }

  claseAlerta(severidad: string): string {
    const map: Record<string, string> = {
      CRITICA: 'alerta-critica',
      PREVENTIVA: 'alerta-preventiva',
      OPORTUNIDAD: 'alerta-oportunidad',
    };
    return map[severidad] ?? '';
  }

  claseBadgeSeveridad(severidad: string): string {
    const map: Record<string, string> = {
      CRITICA: 'badge-critica',
      PREVENTIVA: 'badge-preventiva',
      OPORTUNIDAD: 'badge-oportunidad',
    };
    return map[severidad] ?? '';
  }

  labelSeveridad(severidad: string): string {
    const map: Record<string, string> = {
      CRITICA: 'Crítica',
      PREVENTIVA: 'Preventiva',
      OPORTUNIDAD: 'Oportunidad',
    };
    return map[severidad] ?? severidad;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
