import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  ReporteService,
  ReporteHistorialDTO,
  ReporteConfigDTO,
} from '../../../../core/services/reportes/reporte.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-historial-reportes',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './historial-reportes.component.html',
  styleUrl: './historial-reportes.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistorialReportesComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  loading = true;
  regenerando: number | null = null;
  error = '';
  exito = '';
  hatoActivo: Hato | null = null;

  // ── Datos ─────────────────────────────────────────────────────────────
  reportes: ReporteHistorialDTO[] = [];

  // ── Filtro ────────────────────────────────────────────────────────────
  filtroTipo: string = 'TODOS';

  readonly tipos = [
    { valor: 'TODOS', label: 'Todos' },
    { valor: 'MANUAL', label: '🔵 Manuales' },
    { valor: 'MENSUAL', label: '🟢 Mensuales' },
    { valor: 'TRIMESTRAL', label: '🟡 Trimestrales' },
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private reporteService: ReporteService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarHistorial(hato.idHato);
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

  private cargarHistorial(idHato: string): void {
    this.loading = true;
    this.cdr.markForCheck();

    this.reporteService.getHistorial(idHato).subscribe({
      next: (reportes) => {
        this.reportes = reportes;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al cargar el historial de reportes.';
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Filtro ────────────────────────────────────────────────────────────

  get reportesFiltrados(): ReporteHistorialDTO[] {
    if (this.filtroTipo === 'TODOS') return this.reportes;
    return this.reportes.filter((r) => r.tipo === this.filtroTipo);
  }

  setFiltro(tipo: string): void {
    this.filtroTipo = tipo;
    this.cdr.markForCheck();
  }

  // ── Regenerar ─────────────────────────────────────────────────────────

  regenerar(reporte: ReporteHistorialDTO): void {
    this.regenerando = reporte.idReporte;
    this.error = '';
    this.exito = '';
    this.cdr.markForCheck();

    this.reporteService.regenerarReporte(reporte.idReporte).subscribe({
      next: (blob) => {
        this.reporteService.descargarPdf(blob, this.hatoActivo?.nombreHato ?? 'hato');
        this.exito = '✅ Reporte regenerado y descargado correctamente.';
        this.regenerando = null;
        if (this.hatoActivo?.idHato) {
          this.cargarHistorial(this.hatoActivo.idHato);
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al regenerar el reporte.';
        this.regenerando = null;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Navegación ────────────────────────────────────────────────────────

  irAGenerarReporte(): void {
    this.router.navigate(['/reportes']);
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  iconoTipo(tipo: string): string {
    const map: Record<string, string> = {
      MANUAL: '🔵',
      MENSUAL: '🟢',
      TRIMESTRAL: '🟡',
    };
    return map[tipo] ?? '📄';
  }

  labelTipo(tipo: string): string {
    const map: Record<string, string> = {
      MANUAL: 'Manual',
      MENSUAL: 'Mensual',
      TRIMESTRAL: 'Trimestral',
    };
    return map[tipo] ?? tipo;
  }

  claseTipo(tipo: string): string {
    const map: Record<string, string> = {
      MANUAL: 'tipo-manual',
      MENSUAL: 'tipo-mensual',
      TRIMESTRAL: 'tipo-trimestral',
    };
    return map[tipo] ?? '';
  }

  formatearFecha(fecha: string | null): string {
    if (!fecha) return '—';
    try {
      const d = new Date(fecha);
      return d.toLocaleDateString('es-CO', {
        day: '2-digit',
        month: 'long',
        year: 'numeric',
      });
    } catch {
      return fecha;
    }
  }

  formatearPeriodo(desde: string | null, hasta: string | null): string {
    if (!desde && !hasta) return 'Histórico completo';
    if (!desde) return `Hasta ${hasta}`;
    if (!hasta) return `Desde ${desde}`;
    return `${desde} → ${hasta}`;
  }

  get countPorTipo(): Record<string, number> {
    return {
      TODOS: this.reportes.length,
      MANUAL: this.reportes.filter((r) => r.tipo === 'MANUAL').length,
      MENSUAL: this.reportes.filter((r) => r.tipo === 'MENSUAL').length,
      TRIMESTRAL: this.reportes.filter((r) => r.tipo === 'TRIMESTRAL').length,
    };
  }

  contarSecciones(config: ReporteConfigDTO): number {
    return [
      config.incluirResumenEjecutivo,
      config.incluirKpis,
      config.incluirBenchmarking,
      config.incluirRanking,
      config.incluirFinanzas,
      config.incluirProduccion,
      config.incluirPracticas,
    ].filter(Boolean).length;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
