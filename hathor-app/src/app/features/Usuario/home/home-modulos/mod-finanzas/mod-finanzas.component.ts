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

import { SpinnerComponent } from '../../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

export interface FinanzaMes {
  mes: string;
  ingresos: number;
  egresos: number;
}

@Component({
  selector: 'app-mod-finanzas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './mod-finanzas.component.html',
  styleUrl: './mod-finanzas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModFinanzasComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() totalIngresos = 0;
  @Input() totalEgresos = 0;
  @Input() balanceNeto = 0;
  @Input() finanzasPorMes: FinanzaMes[] = [];
  @Input() loading = true;
  @Input() ruta = '/finanzas';

  @ViewChild('chartCanvas') chartCanvasRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;
  private viewReady = false;

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.buildChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['finanzasPorMes'] || changes['loading']) && this.viewReady) {
      setTimeout(() => this.buildChart(), 0);
    }
  }

  get colorBalanceNeto(): string {
    return this.balanceNeto >= 0 ? 'valor-ingreso' : 'valor-egreso';
  }

  private buildChart(): void {
    const canvas = this.chartCanvasRef?.nativeElement;
    if (!canvas || !this.finanzasPorMes.length) return;

    this.destroyChart();

    const config: ChartConfiguration<'bar', number[], string> = {
      type: 'bar',
      data: {
        labels: this.finanzasPorMes.map((f) => f.mes),
        datasets: [
          {
            label: 'Ingresos',
            data: this.finanzasPorMes.map((f) => f.ingresos),
            backgroundColor: 'rgba(25,230,77,0.75)',
            borderColor: '#15c944',
            borderWidth: 1,
            borderRadius: 4,
          },
          {
            label: 'Egresos',
            data: this.finanzasPorMes.map((f) => f.egresos),
            backgroundColor: 'rgba(239,68,68,0.75)',
            borderColor: '#dc2626',
            borderWidth: 1,
            borderRadius: 4,
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
            labels: { color: '#6b7280', font: { size: 9 }, boxWidth: 10, padding: 8 },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: '#9ca3af', font: { size: 9 } },
          },
          y: {
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: {
              color: '#9ca3af',
              font: { size: 9 },
              callback: (v: any) => {
                if (v >= 1_000_000) return `$${(v / 1_000_000).toFixed(1)}M`;
                if (v >= 1_000) return `$${(v / 1_000).toFixed(0)}K`;
                return `$${v}`;
              },
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

  formatCOP(valor: number): string {
    if (valor >= 1_000_000) return '$ ' + (valor / 1_000_000).toFixed(1) + 'M';
    if (valor >= 1_000) return '$ ' + (valor / 1_000).toFixed(0) + 'K';
    return '$ ' + new Intl.NumberFormat('es-CO').format(valor);
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }
}
