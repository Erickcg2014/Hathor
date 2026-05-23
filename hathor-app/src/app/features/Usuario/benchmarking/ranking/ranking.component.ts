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
import { KpiService, KpiResultado } from '../../../../core/services/kpi/kpi.service';
import {
  RankingService,
  RankingResumenDTO,
  RankingCompuestoDTO,
  RankingPorKpiDTO,
  EvolucionPosicionDTO,
} from '../../../../core/services/benchmarking/ranking.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { RankingResumenComponent } from './ranking-resumen/ranking-resumen.component';
import { RankingCompuestoComponent } from './ranking-compuesto/ranking-compuesto.component';
import { RankingPorKpiComponent } from './ranking-por-kpi/ranking-por-kpi.component';
import { GraficaEvolucionPosicionComponent } from './grafica-evolucion-posicion/grafica-evolucion-posicion.component';

type TabActivo = 'resumen' | 'compuesto' | 'por-kpi' | 'evolucion';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [
    CommonModule,
    SpinnerComponent,
    RankingResumenComponent,
    RankingCompuestoComponent,
    RankingPorKpiComponent,
    GraficaEvolucionPosicionComponent,
  ],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RankingComponent implements OnInit, OnDestroy {
  // ── Estado general ────────────────────────────────────────────────────────
  loading = true;
  error = '';
  hatoActivo: Hato | null = null;

  tabActivo: TabActivo = 'resumen';

  readonly tabs: { id: TabActivo; label: string; icono: string }[] = [
    { id: 'resumen', label: 'Resumen', icono: '🏅' },
    { id: 'compuesto', label: 'Posición General', icono: '🏆' },
    { id: 'por-kpi', label: 'Por KPI', icono: '📊' },
    { id: 'evolucion', label: 'Evolución', icono: '📈' },
  ];

  // ── Datos por tab ─────────────────────────────────────────────────────────
  resumen: RankingResumenDTO | null = null;
  compuesto: RankingCompuestoDTO | null = null;
  porKpi: RankingPorKpiDTO | null = null;
  evolucion: EvolucionPosicionDTO | null = null;
  kpis: KpiResultado[] = [];
  readonly opcionesMeses: (3 | 6 | 12)[] = [3, 6, 12];

  // ── Estado de carga por tab ───────────────────────────────────────────────
  cargandoResumen = false;
  cargandoCompuesto = false;
  cargandoPorKpi = false;
  cargandoEvolucion = false;

  // ── Filtros ───────────────────────────────────────────────────────────────
  regionFiltro: string | null = null;
  codigoKpiSeleccionado = '';
  codigoKpiEvolucion = '';
  mesesEvolucion: 3 | 6 | 12 = 6;

  private tabsCargados = new Set<TabActivo>();

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private kpiService: KpiService,
    private rankingService: RankingService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.inicializar(hato.idHato);
      } else {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

    this.cargarHatoInicial();
  }

  private cargarHatoInicial(): void {
    if (this.hatoState.getHatoActivo()) return;
    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
        else {
          this.loading = false;
          this.cdr.markForCheck();
        }
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private inicializar(idHato: string): void {
    this.loading = true;
    this.tabsCargados.clear();
    this.error = '';

    this.kpiService.getKpis(idHato).subscribe({
      next: (kpis) => {
        this.kpis = kpis;
        const primero = kpis.find((k) => k.valor !== null);
        if (primero) {
          this.codigoKpiSeleccionado = primero.codigo;
          this.codigoKpiEvolucion = primero.codigo;
        }
        this.loading = false;
        this.cdr.markForCheck();
        this.cargarTab('resumen');
      },
      error: () => {
        this.loading = false;
        this.error = 'Error al cargar los KPIs del hato.';
        this.cdr.markForCheck();
      },
    });
  }

  // ── Navegación de tabs ────────────────────────────────────────────────────

  setTab(tab: TabActivo): void {
    this.tabActivo = tab;
    this.cdr.markForCheck();
    if (!this.tabsCargados.has(tab)) {
      this.cargarTab(tab);
    }
  }

  private cargarTab(tab: TabActivo): void {
    const idHato = this.hatoActivo?.idHato;
    if (!idHato) return;

    switch (tab) {
      case 'resumen':
        this.cargarResumen(idHato);
        break;
      case 'compuesto':
        this.cargarCompuesto(idHato);
        break;
      case 'por-kpi':
        this.cargarPorKpi(idHato);
        break;
      case 'evolucion':
        this.cargarEvolucion(idHato);
        break;
    }
  }

  // ── Loaders por tab ───────────────────────────────────────────────────────

  private cargarResumen(idHato: string): void {
    this.cargandoResumen = true;
    this.cdr.markForCheck();

    this.rankingService.getResumenRanking(idHato).subscribe({
      next: (data) => {
        this.resumen = data;
        this.cargandoResumen = false;
        this.tabsCargados.add('resumen');
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargandoResumen = false;
        this.error = 'Error al cargar el resumen de ranking.';
        this.cdr.markForCheck();
      },
    });
  }

  private cargarCompuesto(idHato: string): void {
    this.cargandoCompuesto = true;
    this.cdr.markForCheck();

    this.rankingService.getRankingCompuesto(idHato, this.regionFiltro ?? undefined).subscribe({
      next: (data) => {
        this.compuesto = data;
        this.cargandoCompuesto = false;
        this.tabsCargados.add('compuesto');
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargandoCompuesto = false;
        this.error = 'Error al cargar el ranking compuesto.';
        this.cdr.markForCheck();
      },
    });
  }

  private cargarPorKpi(idHato: string): void {
    if (!this.codigoKpiSeleccionado) return;
    this.cargandoPorKpi = true;
    this.cdr.markForCheck();

    this.rankingService
      .getRankingPorKpi(idHato, this.codigoKpiSeleccionado, this.regionFiltro ?? undefined)
      .subscribe({
        next: (data) => {
          this.porKpi = data;
          this.cargandoPorKpi = false;
          this.tabsCargados.add('por-kpi');
          this.cdr.markForCheck();
        },
        error: () => {
          this.cargandoPorKpi = false;
          this.error = 'Error al cargar el ranking por KPI.';
          this.cdr.markForCheck();
        },
      });
  }

  private cargarEvolucion(idHato: string): void {
    if (!this.codigoKpiEvolucion) return;
    this.cargandoEvolucion = true;
    this.cdr.markForCheck();

    this.rankingService
      .getEvolucionPosicion(idHato, this.codigoKpiEvolucion, this.mesesEvolucion)
      .subscribe({
        next: (data) => {
          this.evolucion = data;
          this.cargandoEvolucion = false;
          this.tabsCargados.add('evolucion');
          this.cdr.markForCheck();
        },
        error: () => {
          this.cargandoEvolucion = false;
          this.error = 'Error al cargar la evolución de posición.';
          this.cdr.markForCheck();
        },
      });
  }

  // ── Eventos desde hijos ───────────────────────────────────────────────────

  onKpiCambiado(codigo: string): void {
    this.codigoKpiSeleccionado = codigo;
    this.tabsCargados.delete('por-kpi');
    this.porKpi = null;
    const idHato = this.hatoActivo?.idHato;
    if (idHato) this.cargarPorKpi(idHato);
  }

  onKpiEvolucionCambiado(codigo: string): void {
    this.codigoKpiEvolucion = codigo;
    this.tabsCargados.delete('evolucion');
    this.evolucion = null;
    const idHato = this.hatoActivo?.idHato;
    if (idHato) this.cargarEvolucion(idHato);
  }

  onMesesCambiado(meses: 3 | 6 | 12): void {
    this.mesesEvolucion = meses;
    this.tabsCargados.delete('evolucion');
    this.evolucion = null;
    const idHato = this.hatoActivo?.idHato;
    if (idHato) this.cargarEvolucion(idHato);
  }

  onRegionCambiada(region: string | null): void {
    this.regionFiltro = region;
    this.tabsCargados.delete('compuesto');
    this.tabsCargados.delete('por-kpi');
    this.compuesto = null;
    this.porKpi = null;
    const idHato = this.hatoActivo?.idHato;
    if (!idHato) return;
    if (this.tabActivo === 'compuesto') this.cargarCompuesto(idHato);
    if (this.tabActivo === 'por-kpi') this.cargarPorKpi(idHato);
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  get hayKpis(): boolean {
    return this.kpis.length > 0;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
