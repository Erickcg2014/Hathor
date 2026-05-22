import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ChangeDetectorRef,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subject, takeUntil, firstValueFrom } from 'rxjs';
import { Chart, registerables } from 'chart.js';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import { KpiService, KpiResultado, KpiHistorico } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

@Component({
  selector: 'app-kpi-detalle',
  standalone: true,
  imports: [CommonModule, SpinnerComponent, RouterLink],
  templateUrl: './kpi-detalle.component.html',
  styleUrl: './kpi-detalle.component.css',
})
export class KpiDetalleComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartHistorico') chartHistoricoRef!: ElementRef;
  @ViewChild('chartComparativa') chartComparativaRef!: ElementRef;

  loading = true;
  hatoActivo: Hato | null = null;
  kpi: KpiResultado | null = null;
  historico: KpiHistorico[] = [];
  codigo = '';

  private charts: Chart<any, any, any>[] = [];
  private viewReady = false;
  private dataReady = false;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hatoState: HatoStateService,
    private kpiService: KpiService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.codigo = this.route.snapshot.paramMap.get('codigo') ?? '';

    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato && this.codigo) {
        this.cargarDatos(hato.idHato);
      }
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.dataReady) this.construirGraficas();
  }

  private async cargarDatos(idHato: string): Promise<void> {
    this.loading = true;
    this.destruirGraficas();

    try {
      // Si hay caché, retorna el caché, sino va al back.
      const [kpis, historico] = await Promise.allSettled([
        firstValueFrom(this.kpiService.getKpis(idHato)),
        firstValueFrom(this.kpiService.getHistorico(idHato, this.codigo)),
      ]);

      if (kpis.status === 'fulfilled') {
        this.kpi = kpis.value.find((k) => k.codigo === this.codigo) ?? null;
      }

      if (historico.status === 'fulfilled') {
        this.historico = historico.value.slice(0, 6).reverse();
      }
    } finally {
      this.loading = false;
      this.dataReady = true;
      this.cdr.markForCheck();
      setTimeout(() => {
        if (this.viewReady) this.construirGraficas();
      }, 100);
    }
  }

  private construirGraficas(): void {
    this.destruirGraficas();
    if (!this.kpi) return;

    if (this.historico.length > 1) this.crearGraficaHistorico();
    this.crearGraficaComparativa();
  }

  private crearGraficaHistorico(): void {
    const ctx = this.chartHistoricoRef?.nativeElement;
    if (!ctx) return;

    const colorLinea =
      this.kpi!.estado === 'CRITICO'
        ? '#ef4444'
        : this.kpi!.estado === 'ACEPTABLE'
          ? '#eab308'
          : '#19e64d';

    const chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.historico.map((h) => h.periodo),
        datasets: [
          {
            label: this.kpi!.nombre,
            data: this.historico.map((h) => h.valor),
            borderColor: colorLinea,
            backgroundColor: colorLinea + '18',
            borderWidth: 2.5,
            pointBackgroundColor: colorLinea,
            pointRadius: 5,
            fill: true,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.parsed.y ?? 0} ${this.kpi!.unidad}`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: false,
            grid: { color: '#f0fdf4' },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaComparativa(): void {
    const ctx = this.chartComparativaRef?.nativeElement;
    if (!ctx || !this.kpi?.benchmarkPromedio) return;

    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Tu hato', 'Promedio nacional', 'Top Colombia'],
        datasets: [
          {
            data: [
              this.kpi!.valor ?? 0,
              this.kpi!.benchmarkPromedio ?? 0,
              this.kpi!.benchmarkTop ?? 0,
            ],
            backgroundColor: [
              this.kpi!.estado === 'CRITICO'
                ? 'rgba(239,68,68,0.8)'
                : this.kpi!.estado === 'ACEPTABLE'
                  ? 'rgba(234,179,8,0.8)'
                  : 'rgba(25,230,77,0.8)',
              'rgba(107,114,128,0.6)',
              'rgba(21,128,61,0.6)',
            ],
            borderColor: [
              this.kpi!.estado === 'CRITICO'
                ? '#dc2626'
                : this.kpi!.estado === 'ACEPTABLE'
                  ? '#ca8a04'
                  : '#15c944',
              '#6b7280',
              '#15803d',
            ],
            borderWidth: 2,
            borderRadius: 8,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.parsed.y ?? 0} ${this.kpi!.unidad}`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: '#f0fdf4' },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private destruirGraficas(): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];
  }
  getRutaParaKpi(codigo: string): string {
    const rutas: Record<string, string> = {
      KPI_LITROS_VACA_DIA: '/produccion',
      KPI_FRECUENCIA_ORDENIO: '/produccion',
      KPI_LACTANCIA_VS_ESTANDAR: '/produccion',
      KPI_PCT_VACAS_ORDENIO: '/produccion',
      KPI_PRODUCCION_HA_DIA: '/produccion',
      KPI_LITROS_HA_ANIO: '/produccion',
      KPI_CAP_ALMAC_UTILIZADA: '/produccion',
      KPI_LITROS_EMPLEADO: '/produccion',
      KPI_COSTO_LITRO: '/produccion/registro',
      KPI_INGRESO_LITRO: '/finanzas/registro',
      KPI_MARGEN_NETO: '/finanzas/registro',
      KPI_MARGEN_BRUTO_PCT: '/finanzas/registro',
      KPI_RATIO_INGRESO_EGRESO: '/finanzas/registro',
      KPI_BALANCE_NETO: '/finanzas/registro',
      KPI_INGRESO_VACA: '/finanzas/registro',
      KPI_ROA: '/finanzas/registro',
      KPI_ROTACION_ACTIVOS: '/finanzas/registro',
      KPI_INGRESO_HA_ANIO: '/finanzas/registro',
      KPI_CARGA_ANIMAL: '/hato/inventarios',
      KPI_HEMBRAS_RECRIA_VACA: '/hato/inventarios',
      KPI_IOFC: '/finanzas/registro',
      KPI_COSTO_LABORAL_PCT: '/finanzas/registro',
      KPI_BREAKEVEN_LITRO: '/produccion/registro',
    };
    return rutas[codigo] ?? '/home';
  }

  volver(): void {
    this.router.navigate(['/benchmarking']);
  }

  get labelEstado(): string {
    const map: Record<string, string> = {
      OPTIMO: 'Óptimo',
      ACEPTABLE: 'Aceptable',
      CRITICO: 'Crítico',
      SIN_DATOS: 'Sin datos',
    };
    return map[this.kpi?.estado ?? ''] ?? '';
  }

  formatValor(valor: number | null, unidad: string): string {
    if (valor === null || valor === undefined) return '—';
    if (unidad.includes('COP') || unidad === 'COP/vaca' || unidad === 'COP/L')
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(valor));
    if (unidad === '%') return valor.toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return valor.toFixed(2) + 'x';
    if (Number.isInteger(valor)) return valor.toString() + ' ' + unidad;
    return valor.toFixed(1) + ' ' + unidad;
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
