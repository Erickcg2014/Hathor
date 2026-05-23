import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  HatoPracticaDTO,
  PracticasService,
} from '../../../../core/services/practicas/practicas.service';
import { KpiService, KpiResultado, KpiHistorico } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

// ── Interfaces ────────────────────────────────────────────────────────────

interface EventoTimeline {
  practica: HatoPracticaDTO;
  fechaReferencia: Date;
  kpiActual: KpiResultado | null;
  historico: KpiHistorico[];
  deltaValor: number | null;
  deltaPct: number | null;
}

@Component({
  selector: 'app-progreso-practicas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './progreso-practicas.component.html',
  styleUrl: './progreso-practicas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProgresoPracticasComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  hatoActivo: Hato | null = null;
  loading = true;
  loadingKpis = false;
  error = '';

  // ── Datos ─────────────────────────────────────────────────────────────
  practicas: HatoPracticaDTO[] = [];
  timeline: EventoTimeline[] = [];

  // ── Filtro ────────────────────────────────────────────────────────────
  filtro: 'TODAS' | 'EN_CURSO' | 'COMPLETADA' = 'TODAS';

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
        this.practicas = [];
        this.timeline = [];
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

  private cargarTodo(idHato: string): void {
    this.loading = true;
    this.loadingKpis = true;
    this.error = '';
    this.cdr.markForCheck();

    this.practicasService
      .getPracticasByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          // Solo prácticas que ya iniciaron (EN_CURSO o COMPLETADA)
          this.practicas = data.filter((p) => p.estado === 'EN_CURSO' || p.estado === 'COMPLETADA');
          this.loading = false;

          // Cargar KPIs y construir timeline
          this.kpiService.getKpis(idHato).subscribe({
            next: () => {
              this.loadingKpis = false;
              this.construirTimeline(idHato);
              this.cdr.markForCheck();
            },
            error: () => {
              this.loadingKpis = false;
              this.construirTimeline(idHato);
              this.cdr.markForCheck();
            },
          });

          this.cdr.markForCheck();
        },
        error: () => {
          this.practicas = [];
          this.timeline = [];
          this.loading = false;
          this.error = 'Error al cargar las prácticas.';
          this.cdr.markForCheck();
        },
      });
  }

  // ── Construir timeline ────────────────────────────────────────────────

  private construirTimeline(idHato: string): void {
    const eventos: EventoTimeline[] = this.practicas.map((p) => {
      const kpiActual = p.kpiImpactado ? this.kpiService.getKpiDesdeCache(p.kpiImpactado) : null;

      // Fecha de referencia — usar fechaInicio si existe, si no fechaFin
      const fechaRef = p.fechaInicio
        ? new Date(p.fechaInicio)
        : p.fechaFin
          ? new Date(p.fechaFin)
          : new Date();

      return {
        practica: p,
        fechaReferencia: fechaRef,
        kpiActual,
        historico: [],
        deltaValor: null,
        deltaPct: null,
      };
    });

    // Ordenar por fecha descendente (más reciente primero)
    eventos.sort((a, b) => b.fechaReferencia.getTime() - a.fechaReferencia.getTime());

    this.timeline = eventos;

    // Cargar histórico de KPIs para cada práctica con KPI impactado
    const codigosUnicos = [...new Set(this.practicas.map((p) => p.kpiImpactado).filter(Boolean))];

    for (const codigo of codigosUnicos) {
      this.kpiService
        .getHistorico(idHato, codigo)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (historico) => {
            const ordenado = historico.sort(
              (a, b) => new Date(a.fechaCalculo).getTime() - new Date(b.fechaCalculo).getTime(),
            );

            // Calcular delta para eventos con este KPI
            this.timeline = this.timeline.map((ev) => {
              if (ev.practica.kpiImpactado !== codigo) return ev;

              const historicoCortado = ordenado.slice(-6);
              let deltaValor: number | null = null;
              let deltaPct: number | null = null;

              if (historicoCortado.length >= 2) {
                const primero = historicoCortado[0].valor;
                const ultimo = historicoCortado[historicoCortado.length - 1].valor;
                deltaValor = Math.round((ultimo - primero) * 100) / 100;
                deltaPct = primero !== 0 ? Math.round(((ultimo - primero) / primero) * 100) : null;
              }

              return {
                ...ev,
                historico: historicoCortado,
                deltaValor,
                deltaPct,
              };
            });

            this.cdr.markForCheck();
          },
        });
    }
  }

  // ── Filtro ────────────────────────────────────────────────────────────

  setFiltro(f: typeof this.filtro): void {
    this.filtro = f;
    this.cdr.markForCheck();
  }

  get timelineFiltrado(): EventoTimeline[] {
    if (this.filtro === 'TODAS') return this.timeline;
    return this.timeline.filter((ev) => ev.practica.estado === this.filtro);
  }

  // ── Navegación ────────────────────────────────────────────────────────

  verDetalle(practica: HatoPracticaDTO): void {
    this.router.navigate(['/practicas/detalle', practica.idHatoPractica], { state: { practica } });
  }

  // ── Stats globales ────────────────────────────────────────────────────

  get totalCompletadas(): number {
    return this.practicas.filter((p) => p.estado === 'COMPLETADA').length;
  }

  get totalEnCurso(): number {
    return this.practicas.filter((p) => p.estado === 'EN_CURSO').length;
  }

  get promedioAvance(): number {
    const enCurso = this.practicas.filter((p) => p.estado === 'EN_CURSO');
    if (!enCurso.length) return 0;
    const suma = enCurso.reduce((acc, p) => acc + (p.porcentajeAvance ?? 0), 0);
    return Math.round(suma / enCurso.length);
  }

  get practicasConMejora(): number {
    return this.timeline.filter((ev) => ev.deltaPct !== null && ev.deltaPct > 0).length;
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

  claseEstado(estado: string): string {
    const map: Record<string, string> = {
      EN_CURSO: 'estado-curso',
      COMPLETADA: 'estado-completada',
    };
    return map[estado] ?? '';
  }

  colorDelta(delta: number | null): string {
    if (delta === null) return '';
    if (delta > 0) return 'delta-positivo';
    if (delta < 0) return 'delta-negativo';
    return 'delta-neutro';
  }

  iconoDelta(delta: number | null): string {
    if (delta === null) return '—';
    if (delta > 0) return '↗️';
    if (delta < 0) return '↘️';
    return '→';
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

  calcularAlturaBar(valor: number, historico: KpiHistorico[]): number {
    if (!historico.length) return 0;
    const valores = historico.map((h) => h.valor);
    const max = Math.max(...valores);
    const min = Math.min(...valores);
    if (max === min) return 50;
    return Math.round(((valor - min) / (max - min)) * 80) + 10;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
