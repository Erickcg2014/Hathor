import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  ReporteService,
  ReporteConfigDTO,
  REPORTE_CONFIG_DEFAULT,
} from '../../../../core/services/reportes/reporte.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

interface SeccionReporte {
  key: keyof ReporteConfigDTO;
  label: string;
  icono: string;
  desc: string;
}

@Component({
  selector: 'app-crear-reporte',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './crear-reporte.component.html',
  styleUrl: './crear-reporte.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CrearReporteComponent implements OnInit, OnDestroy {
  loading = false;
  generando = false;
  error = '';
  exito = '';
  hatoActivo: Hato | null = null;

  config: ReporteConfigDTO = { ...REPORTE_CONFIG_DEFAULT };

  // ── Secciones disponibles ─────────────────────────────────────────────
  readonly secciones: SeccionReporte[] = [
    {
      key: 'incluirResumenEjecutivo',
      label: 'Resumen ejecutivo',
      icono: '📋',
      desc: 'Posición competitiva, estado de KPIs, balance financiero y perfil productivo en una sola vista.',
    },
    {
      key: 'incluirKpis',
      label: 'KPIs',
      icono: '📈',
      desc: 'Tabla completa de los 24 indicadores agrupados por categoría con estado y comparativa sectorial.',
    },
    {
      key: 'incluirBenchmarking',
      label: 'Benchmarking',
      icono: '🔍',
      desc: 'Percentiles y comparativa contra valores de referencia sectoriales colombianos.',
    },
    {
      key: 'incluirRanking',
      label: 'Ranking',
      icono: '🏆',
      desc: 'Posición del hato en la plataforma Hathor con score compuesto y tabla de posiciones.',
    },
    {
      key: 'incluirFinanzas',
      label: 'Finanzas',
      icono: '💰',
      desc: 'Resumen financiero, evolución mensual, distribución por categoría y detalle de registros.',
    },
    {
      key: 'incluirProduccion',
      label: 'Producción lechera',
      icono: '🥛',
      desc: 'Perfil productivo, estadísticas del período y evolución mensual de litros producidos.',
    },
    {
      key: 'incluirPracticas',
      label: 'Prácticas y recomendaciones',
      icono: '🌱',
      desc: 'Recomendaciones activas del sistema de reglas y estado de prácticas de mejora.',
    },
  ];

  // ── Opciones de nivel benchmarking ────────────────────────────────────
  readonly nivelesKenchmark = [
    {
      valor: 'NACIONAL',
      label: '🇨🇴 Nacional',
      desc: 'Comparativa contra todos los hatos de Colombia',
    },
    {
      valor: 'TROPICO',
      label: '🌿 Por trópico',
      desc: 'Comparativa contra hatos del mismo trópico',
    },
    {
      valor: 'TROPICO_ESCALA',
      label: '📐 Trópico + escala',
      desc: 'Comparativa más específica por trópico y escala',
    },
  ];

  // ── Presets de período ────────────────────────────────────────────────
  readonly periodos = [
    { label: 'Histórico completo', desde: null, hasta: null },
    { label: 'Último mes', desde: this.haceMeses(1), hasta: this.hoy() },
    { label: 'Últimos 3 meses', desde: this.haceMeses(3), hasta: this.hoy() },
    { label: 'Últimos 6 meses', desde: this.haceMeses(6), hasta: this.hoy() },
    { label: 'Último año', desde: this.haceMeses(12), hasta: this.hoy() },
    { label: 'Personalizado', desde: null, hasta: null },
  ];

  periodoSeleccionado = 0;
  periodoPersonalizado = false;

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private reporteService: ReporteService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
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

  // ── Toggle secciones ──────────────────────────────────────────────────

  toggleSeccion(key: keyof ReporteConfigDTO): void {
    (this.config as any)[key] = !(this.config as any)[key];
    this.cdr.markForCheck();
  }

  isSeccionActiva(key: keyof ReporteConfigDTO): boolean {
    return !!(this.config as any)[key];
  }

  seleccionarTodo(): void {
    this.secciones.forEach((s) => ((this.config as any)[s.key] = true));
    this.cdr.markForCheck();
  }

  deseleccionarTodo(): void {
    this.secciones.forEach((s) => ((this.config as any)[s.key] = false));
    this.cdr.markForCheck();
  }

  // ── Nivel benchmarking ────────────────────────────────────────────────

  setNivelBenchmark(nivel: 'NACIONAL' | 'TROPICO' | 'TROPICO_ESCALA'): void {
    this.config.nivelBenchmark = nivel;
    this.cdr.markForCheck();
  }

  // ── Período ───────────────────────────────────────────────────────────

  setPeriodo(idx: number): void {
    this.periodoSeleccionado = idx;
    this.periodoPersonalizado = idx === this.periodos.length - 1;

    if (!this.periodoPersonalizado) {
      this.config.periodoDesde = this.periodos[idx].desde;
      this.config.periodoHasta = this.periodos[idx].hasta;
    }
    this.cdr.markForCheck();
  }

  onPeriodoDesdeChange(valor: string): void {
    this.config.periodoDesde = valor || null;
    this.cdr.markForCheck();
  }

  onPeriodoHastaChange(valor: string): void {
    this.config.periodoHasta = valor || null;
    this.cdr.markForCheck();
  }

  // ── Título personalizado ──────────────────────────────────────────────

  onTituloChange(valor: string): void {
    this.config.tituloPersonalizado = valor.trim() || null;
    this.cdr.markForCheck();
  }

  // ── Generar reporte ───────────────────────────────────────────────────

  generarReporte(): void {
    if (!this.hatoActivo?.idHato) return;
    if (!this.haySeccionesSeleccionadas) return;

    this.generando = true;
    this.error = '';
    this.exito = '';
    this.cdr.markForCheck();

    this.reporteService.generarReporte(this.hatoActivo.idHato, this.config).subscribe({
      next: (blob) => {
        this.reporteService.descargarPdf(blob, this.hatoActivo?.nombreHato ?? 'hato');
        this.exito = '✅ Reporte generado y descargado correctamente.';
        this.generando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al generar el reporte. Intenta de nuevo.';
        this.generando = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Getters ───────────────────────────────────────────────────────────

  get haySeccionesSeleccionadas(): boolean {
    return this.secciones.some((s) => this.isSeccionActiva(s.key));
  }

  get seccionesSeleccionadasCount(): number {
    return this.secciones.filter((s) => this.isSeccionActiva(s.key)).length;
  }

  get labelPeriodo(): string {
    const p = this.periodos[this.periodoSeleccionado];
    if (!p.desde && !p.hasta) return 'Histórico completo';
    if (this.periodoPersonalizado) {
      const desde = this.config.periodoDesde ?? '—';
      const hasta = this.config.periodoHasta ?? '—';
      return `${desde} → ${hasta}`;
    }
    return p.label;
  }

  // ── Helpers de fecha ──────────────────────────────────────────────────

  private hoy(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private haceMeses(meses: number): string {
    const d = new Date();
    d.setMonth(d.getMonth() - meses);
    return d.toISOString().slice(0, 10);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
