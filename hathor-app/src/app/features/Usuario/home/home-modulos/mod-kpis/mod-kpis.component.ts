import {
  Component,
  Input,
  OnChanges,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  ElementRef,
  ChangeDetectionStrategy,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables, ChartConfiguration } from 'chart.js';

import { KpiResultado } from '../../../../../core/services/kpi/kpi.service';
import { BenchmarkHatoResultado } from '../../../../../core/services/benchmarking/benchmarking.service';
import { RankingResumenDTO } from '../../../../../core/services/benchmarking/ranking.service';
import { SpinnerComponent } from '../../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

const CATEGORIAS_RADAR = ['PRODUCTIVIDAD', 'HATO', 'FINANCIERO', 'EFICIENCIA'] as const;
const LABELS_RADAR = ['Productividad', 'Hato', 'Financiero', 'Eficiencia'];

@Component({
  selector: 'app-mod-kpis',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './mod-kpis.component.html',
  styleUrl: './mod-kpis.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModKpisComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() kpis: KpiResultado[] = [];
  @Input() benchmarks: BenchmarkHatoResultado[] = [];
  @Input() rankingResumen: RankingResumenDTO | null = null;
  @Input() loading = true;
  @Input() ruta = '/benchmarking';

  @ViewChild('chartCanvas') chartCanvasRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;
  private viewReady = false;

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.buildChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['benchmarks'] || changes['loading']) && this.viewReady) {
      setTimeout(() => this.buildChart(), 0);
    }
  }

  private buildChart(): void {
    const canvas = this.chartCanvasRef?.nativeElement;
    if (!canvas || !this.benchmarks.length) return;

    this.destroyChart();

    const valores = CATEGORIAS_RADAR.map((cat) => {
      const items = this.benchmarks.filter((b) => b.kpi?.categoria === cat && b.percentil !== null);
      if (!items.length) return 0;
      return items.reduce((s, b) => s + (b.percentil ?? 0), 0) / items.length;
    });

    const config: ChartConfiguration<'radar', number[], string> = {
      type: 'radar',
      data: {
        labels: LABELS_RADAR,
        datasets: [
          {
            label: 'Tu hato',
            data: valores,
            backgroundColor: 'rgba(21,128,61,0.15)',
            borderColor: '#15803d',
            borderWidth: 2,
            pointBackgroundColor: '#19e64d',
            pointBorderColor: '#ffffff',
            pointRadius: 4,
          },
          {
            label: 'Promedio (P50)',
            data: valores.map(() => 50),
            backgroundColor: 'rgba(148,163,184,0.06)',
            borderColor: 'rgba(148,163,184,0.5)',
            borderWidth: 1.5,
            borderDash: [4, 4],
            pointRadius: 0,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          r: {
            min: 0,
            max: 100,
            ticks: {
              stepSize: 25,
              backdropColor: 'transparent',
              color: '#9ca3af',
              font: { size: 8 },
            },
            grid: { color: 'rgba(148,163,184,0.2)' },
            angleLines: { color: 'rgba(148,163,184,0.2)' },
            pointLabels: { font: { size: 9 }, color: '#374151' },
          },
        },
      },
    };

    this.chart = new Chart(canvas, config);
  }

  private destroyChart(): void {
    this.chart?.destroy();
    this.chart = null;
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }
}
