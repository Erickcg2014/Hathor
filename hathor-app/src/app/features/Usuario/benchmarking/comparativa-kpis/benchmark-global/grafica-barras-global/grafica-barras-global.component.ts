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
import { Chart, registerables, TooltipItem } from 'chart.js';

import { KpiResumenGlobalDTO } from '../../../../../../core/services/benchmarking/benchmarking.service';

Chart.register(...registerables);

@Component({
  selector: 'app-grafica-barras-global',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grafica-barras-global.component.html',
  styleUrl: './grafica-barras-global.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraficaBarrasGlobalComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() kpisResumen: KpiResumenGlobalDTO[] = [];

  @ViewChild('chartCanvas')
  chartCanvasRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;
  private viewReady = false;

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.kpisResumen.length > 0) {
      setTimeout(() => this.construir(), 0);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.viewReady) return;
    if (changes['kpisResumen']) {
      setTimeout(() => this.construir(), 0);
    }
  }

  // ── Helpers ──────────────────────────────────────────────

  private colorPorInterpretacion(interp: string | null): string {
    const map: Record<string, string> = {
      OPTIMO: 'rgba(21,201,68,0.82)',
      BUENO: 'rgba(59,130,246,0.78)',
      ACEPTABLE: 'rgba(245,158,11,0.82)',
      CRITICO: 'rgba(239,68,68,0.78)',
    };
    return map[interp ?? ''] ?? 'rgba(156,163,175,0.6)';
  }

  private formatValor(valor: number | null, unidad?: string): string {
    if (valor === null || valor === undefined) return '—';
    if (!unidad) return Number(valor).toFixed(2);
    if (unidad.includes('COP') || unidad === 'COP/vaca' || unidad === 'COP/L')
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(Number(valor)));
    if (unidad === '%') return Number(valor).toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return Number(valor).toFixed(2) + 'x';
    return `${Number(valor).toFixed(2)} ${unidad}`;
  }

  // ── Construcción ─────────────────────────────────────────

  private destruir(): void {
    this.chart?.destroy();
    this.chart = null;
  }

  private construir(): void {
    this.destruir();
    const canvas = this.chartCanvasRef?.nativeElement;
    if (!canvas || !this.kpisResumen.length) return;

    const datos = [...this.kpisResumen]
      .filter((k) => k.valorHatoActual !== null && k.promedioGrupo !== null)
      .sort((a, b) => {
        const difA = Math.abs((a.valorHatoActual! - a.promedioGrupo!) / (a.promedioGrupo! || 1));
        const difB = Math.abs((b.valorHatoActual! - b.promedioGrupo!) / (b.promedioGrupo! || 1));
        return difB - difA;
      });

    if (!datos.length) return;

    canvas.parentElement!.style.height = Math.max(300, datos.length * 44) + 'px';

    const labels = datos.map((k) => k.nombreKpi);
    const hatoData = datos.map((k) => Number(k.percentilEnGrupo ?? 0));
    const promedioData = datos.map(() => 50);
    const colors = datos.map((k) => this.colorPorInterpretacion(k.interpretacion));

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Tu hato (percentil en grupo)',
            data: hatoData,
            backgroundColor: colors,
            borderRadius: 6,
            borderSkipped: false,
            barPercentage: 0.65,
          },
          {
            label: 'Promedio del grupo',
            data: promedioData,
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(99,102,241,0.7)',
            borderWidth: 1.5,
            type: 'line' as any,
            pointRadius: 0,
            order: 0,
          },
        ],
      },
      options: {
        indexAxis: 'y',
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
              label: (ctx: TooltipItem<'bar'>) => {
                if (ctx.datasetIndex === 1) return 'Promedio del grupo: P50';
                const k = datos[ctx.dataIndex];
                const pct = Number(ctx.parsed.x ?? 0).toFixed(1);
                return [
                  `Percentil en grupo: P${pct}`,
                  `Tu valor: ${this.formatValor(k.valorHatoActual, k.unidadKpi)}`,
                  `Promedio grupo: ${this.formatValor(k.promedioGrupo, k.unidadKpi)}`,
                  `Top grupo: ${this.formatValor(k.topGrupo, k.unidadKpi)}`,
                  `Hatos comparados: ${k.totalHatosGrupo}`,
                  `Estado: ${k.interpretacion}`,
                ];
              },
            },
          },
        },
        scales: {
          x: {
            min: 0,
            max: 100,
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: {
              color: '#6b7280',
              font: { size: 11 },
              callback: (v: string | number) => `P${v}`,
            },
            title: {
              display: true,
              text: 'Percentil dentro del grupo (0 = peor · 50 = promedio · 100 = mejor)',
              color: '#9ca3af',
              font: { size: 10 },
            },
          },
          y: {
            grid: { display: false },
            ticks: { color: '#374151', font: { size: 11 } },
          },
        },
      },
    });

    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.destruir();
  }
}
