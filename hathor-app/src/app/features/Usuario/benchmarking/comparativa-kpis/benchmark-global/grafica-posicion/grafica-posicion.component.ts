import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables, TooltipItem, ChartConfiguration } from 'chart.js';

import {
  KpiResumenGlobalDTO,
  HatoValorDTO,
} from '../../../../../../core/services/benchmarking/benchmarking.service';

Chart.register(...registerables);

@Component({
  selector: 'app-grafica-posicion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grafica-posicion.component.html',
  styleUrl: './grafica-posicion.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraficaPosicionComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() kpi!: KpiResumenGlobalDTO;

  @ViewChild('chartCanvas')
  chartCanvasRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;
  private viewReady = false;

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.kpi) setTimeout(() => this.construir(), 0);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.viewReady) return;
    if (changes['kpi']) setTimeout(() => this.construir(), 0);
  }

  // ── Getters ───────────────────────────────────────────────

  get miHato(): HatoValorDTO | null {
    return this.kpi?.rankingHatos?.find((h) => h.esMiHato) ?? null;
  }

  get posicionTexto(): string {
    if (!this.miHato) return '—';
    const total = this.kpi.rankingHatos.length;
    return `#${this.miHato.posicion} de ${total}`;
  }

  get diferenciaPromedio(): string {
    if (!this.miHato || this.kpi.promedioGrupo == null) return '—';
    const diff = this.miHato.valor - this.kpi.promedioGrupo;
    const pct = this.kpi.promedioGrupo !== 0 ? (diff / this.kpi.promedioGrupo) * 100 : 0;
    const signo = pct >= 0 ? '+' : '';
    return `${signo}${pct.toFixed(1)}% vs promedio`;
  }

  get interpretacionClass(): string {
    const map: Record<string, string> = {
      OPTIMO: 'badge-optimo',
      BUENO: 'badge-bueno',
      ACEPTABLE: 'badge-aceptable',
      CRITICO: 'badge-critico',
    };
    return map[this.kpi?.interpretacion ?? ''] ?? 'badge-default';
  }

  // ── Helpers ──────────────────────────────────────────────

  private formatValor(valor: number | null): string {
    if (valor === null || valor === undefined) return '—';
    const unidad = this.kpi?.unidadKpi ?? '';
    if (unidad.includes('COP') || unidad === 'COP/vaca' || unidad === 'COP/L')
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(Number(valor)));
    if (unidad === '%') return Number(valor).toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return Number(valor).toFixed(2) + 'x';
    return `${Number(valor).toFixed(2)} ${unidad}`.trim();
  }

  // ── Construcción ─────────────────────────────────────────

  private destruir(): void {
    this.chart?.destroy();
    this.chart = null;
  }

  private construir(): void {
    this.destruir();
    const canvas = this.chartCanvasRef?.nativeElement;
    if (!canvas || !this.kpi?.rankingHatos?.length) return;

    const ranking = [...this.kpi.rankingHatos].sort((a, b) => b.valor - a.valor);

    const labels = ranking.map((h) => (h.esMiHato ? 'Tu hato' : h.alias));
    const valores = ranking.map((h) => h.valor);

    const puntosColores = ranking.map((h) => (h.esMiHato ? '#15803d' : 'rgba(148,163,184,0.7)'));
    const puntosRadios = ranking.map((h) => (h.esMiHato ? 10 : 5));
    const puntosBorde = ranking.map((h) => (h.esMiHato ? '#ffffff' : 'rgba(148,163,184,1)'));

    const promedioData = ranking.map(() => this.kpi.promedioGrupo ?? 0);

    const config: ChartConfiguration<'line', number[], string> = {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: this.kpi.nombreKpi,
            data: valores,
            borderColor: '#15803d',
            borderWidth: 2,
            backgroundColor: 'rgba(21,128,61,0.06)',
            fill: true,
            tension: 0.3,
            pointBackgroundColor: puntosColores,
            pointBorderColor: puntosBorde,
            pointBorderWidth: 2,
            pointRadius: puntosRadios,
            pointHoverRadius: puntosRadios.map((r) => r + 2),
          },
          {
            label: 'Promedio del grupo',
            data: promedioData,
            borderColor: 'rgba(99,102,241,0.6)',
            borderWidth: 1.5,
            borderDash: [5, 4],
            pointRadius: 0,
            backgroundColor: 'rgba(0,0,0,0)',
            fill: false,
            tension: 0,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: { color: '#374151', font: { size: 11 }, boxWidth: 20 },
          },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'line'>) => {
                if (ctx.datasetIndex === 1) {
                  return `Promedio: ${this.formatValor(this.kpi.promedioGrupo)}`;
                }
                const h = ranking[ctx.dataIndex];
                return [
                  `${h.esMiHato ? '📍 Tu hato' : h.alias}`,
                  `Valor: ${this.formatValor(h.valor)}`,
                  `Posición: #${h.posicion}`,
                ];
              },
            },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: {
              color: '#6b7280',
              font: { size: 11 },
              maxRotation: 45,
            },
          },
          y: {
            beginAtZero: false,
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: {
              color: '#6b7280',
              font: { size: 11 },
              callback: (v: string | number) => this.formatValor(Number(v)),
            },
            title: {
              display: !!this.kpi.unidadKpi,
              text: this.kpi.unidadKpi ?? '',
              color: '#9ca3af',
              font: { size: 10 },
            },
          },
        },
      },
    };
    this.chart = new Chart(canvas, config) as unknown as Chart;

    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.destruir();
  }
}
