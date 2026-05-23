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

import { PerfilProductivo } from '../../../../../core/services/produccion/perfil-productivo.service';
import { ProduccionLeche } from '../../../../../core/services/produccion/produccion-leche.service';
import { SpinnerComponent } from '../../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

@Component({
  selector: 'app-mod-produccion',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './mod-produccion.component.html',
  styleUrl: './mod-produccion.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModProduccionComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() perfilProductivo: PerfilProductivo | null = null;
  @Input() produccionUltimos30: ProduccionLeche[] = [];
  @Input() loading = true;
  @Input() ruta = '/produccion';

  @ViewChild('chartCanvas') chartCanvasRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;
  private viewReady = false;

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.buildChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['produccionUltimos30'] || changes['loading']) && this.viewReady) {
      setTimeout(() => this.buildChart(), 0);
    }
  }

  private buildChart(): void {
    const canvas = this.chartCanvasRef?.nativeElement;
    if (!canvas || !this.produccionUltimos30.length) return;

    this.destroyChart();

    const labels = this.produccionUltimos30.map((p) => {
      const [, m, d] = p.fecha.split('-');
      return `${d}/${m}`;
    });
    const data = this.produccionUltimos30.map((p) => p.litrosProducidos);

    const config: ChartConfiguration<'line', number[], string> = {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Litros producidos',
            data,
            borderColor: '#15803d',
            backgroundColor: 'rgba(21,128,61,0.08)',
            borderWidth: 2,
            fill: true,
            tension: 0.3,
            pointRadius: 2,
            pointHoverRadius: 5,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: '#9ca3af', font: { size: 9 }, maxTicksLimit: 6 },
          },
          y: {
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: { color: '#9ca3af', font: { size: 9 } },
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
