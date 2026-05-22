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
import { Chart, registerables, ChartConfiguration, TooltipItem } from 'chart.js';

import { SpinnerComponent } from '../../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

export interface DistribucionGanado {
  categoria: string;
  cantidad: number;
}

const COLORES_GANADO = [
  'rgba(25,230,77,0.82)',
  'rgba(21,128,61,0.82)',
  'rgba(59,130,246,0.82)',
  'rgba(245,158,11,0.82)',
  'rgba(239,68,68,0.82)',
  'rgba(168,85,247,0.82)',
];

@Component({
  selector: 'app-mod-ganado',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './mod-ganado.component.html',
  styleUrl: './mod-ganado.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModGanadoComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() totalAnimales = 0;
  @Input() distribucionGanado: DistribucionGanado[] = [];
  @Input() loading = true;
  @Input() ruta = '/inventarios/ganado/resumen';

  @ViewChild('chartCanvas') chartCanvasRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;
  private viewReady = false;

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.buildChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['distribucionGanado'] || changes['loading']) && this.viewReady) {
      setTimeout(() => this.buildChart(), 0);
    }
  }

  private buildChart(): void {
    const canvas = this.chartCanvasRef?.nativeElement;
    if (!canvas || !this.distribucionGanado.length) return;

    this.destroyChart();

    const config: ChartConfiguration<'doughnut', number[], string> = {
      type: 'doughnut',
      data: {
        labels: this.distribucionGanado.map((d) => d.categoria),
        datasets: [
          {
            data: this.distribucionGanado.map((d) => d.cantidad),
            backgroundColor: COLORES_GANADO.slice(0, this.distribucionGanado.length),
            borderWidth: 2,
            borderColor: '#ffffff',
            hoverOffset: 4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '60%',
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: {
              color: '#6b7280',
              font: { size: 9 },
              boxWidth: 10,
              padding: 6,
            },
          },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'doughnut'>) => ` ${ctx.label}: ${ctx.parsed} animales`,
            },
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
