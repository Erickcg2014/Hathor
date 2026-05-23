import {
  Component,
  Input,
  OnChanges,
  OnDestroy,
  AfterViewInit,
  SimpleChanges,
  ViewChild,
  ElementRef,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables, TooltipItem } from 'chart.js';
import {
  EvolucionPosicionDTO,
  PuntoEvolucion,
} from '../../../../../core/services/benchmarking/ranking.service';

Chart.register(...registerables);

@Component({
  selector: 'app-grafica-evolucion-posicion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grafica-evolucion-posicion.component.html',
  styleUrl: './grafica-evolucion-posicion.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraficaEvolucionPosicionComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() datos: EvolucionPosicionDTO | null = null;
  @Input() cargando = false;

  @ViewChild('chartEvolucion')
  chartRef!: ElementRef<HTMLCanvasElement>;

  @ViewChild('chartPercentil')
  chartPercentilRef!: ElementRef<HTMLCanvasElement>;

  private charts: Chart<any, any, any>[] = [];
  private viewReady = false;

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.datos && !this.datos.datosInsuficientes) {
      setTimeout(() => this.construirGraficas(), 0);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.viewReady) return;
    if (changes['datos']) {
      setTimeout(() => this.construirGraficas(), 0);
    }
  }

  // ── Getters de resumen ────────────────────────────────────────────────────

  get mejorPosicion(): number | null {
    if (!this.datos?.puntos?.length) return null;
    return Math.min(...this.datos.puntos.map((p) => p.posicion));
  }

  get peorPosicion(): number | null {
    if (!this.datos?.puntos?.length) return null;
    return Math.max(...this.datos.puntos.map((p) => p.posicion));
  }

  get posicionActual(): PuntoEvolucion | null {
    if (!this.datos?.puntos?.length) return null;
    return this.datos.puntos[this.datos.puntos.length - 1];
  }

  get posicionInicial(): PuntoEvolucion | null {
    if (!this.datos?.puntos?.length) return null;
    return this.datos.puntos[0];
  }

  get tendencia(): 'MEJORO' | 'EMPEORO' | 'ESTABLE' {
    const actual = this.posicionActual?.posicion ?? 0;
    const inicial = this.posicionInicial?.posicion ?? 0;
    if (actual < inicial - 1) return 'MEJORO';
    if (actual > inicial + 1) return 'EMPEORO';
    return 'ESTABLE';
  }

  get iconoTendencia(): string {
    if (this.tendencia === 'MEJORO') return '📈';
    if (this.tendencia === 'EMPEORO') return '📉';
    return '➡️';
  }

  get labelTendencia(): string {
    if (this.tendencia === 'MEJORO') return 'Mejoraste posición';
    if (this.tendencia === 'EMPEORO') return 'Bajaste posición';
    return 'Posición estable';
  }

  get claseTendencia(): string {
    if (this.tendencia === 'MEJORO') return 'tendencia-mejoro';
    if (this.tendencia === 'EMPEORO') return 'tendencia-empeoro';
    return 'tendencia-estable';
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  formatFecha(fecha: string): string {
    const [y, m, d] = fecha.split('-');
    const meses = [
      'Ene',
      'Feb',
      'Mar',
      'Abr',
      'May',
      'Jun',
      'Jul',
      'Ago',
      'Sep',
      'Oct',
      'Nov',
      'Dic',
    ];
    return `${d} ${meses[parseInt(m) - 1]} ${y}`;
  }

  formatFechaCorta(fecha: string): string {
    const [y, m] = fecha.split('-');
    const meses = [
      'Ene',
      'Feb',
      'Mar',
      'Abr',
      'May',
      'Jun',
      'Jul',
      'Ago',
      'Sep',
      'Oct',
      'Nov',
      'Dic',
    ];
    return `${meses[parseInt(m) - 1]} ${y}`;
  }

  // ── Construcción de gráficas ──────────────────────────────────────────────

  private destruirGraficas(): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];
  }

  private construirGraficas(): void {
    this.destruirGraficas();

    if (!this.datos || this.datos.datosInsuficientes || !this.datos.puntos.length) return;

    if (this.chartRef?.nativeElement) {
      this.buildEvolucionPosicion(this.chartRef.nativeElement);
    }

    if (this.chartPercentilRef?.nativeElement) {
      this.buildEvolucionPercentil(this.chartPercentilRef.nativeElement);
    }
  }

  private buildEvolucionPosicion(canvas: HTMLCanvasElement): void {
    const puntos = this.datos!.puntos;
    const labels = puntos.map((p) => this.formatFechaCorta(p.fecha));
    const valores = puntos.map((p) => p.posicion);
    const totales = puntos.map((p) => p.totalHatos);

    const colores = puntos.map((p) => {
      const pct = p.totalHatos > 1 ? ((p.totalHatos - p.posicion) / (p.totalHatos - 1)) * 100 : 50;
      if (pct >= 70) return '#19e64d';
      if (pct >= 40) return '#f59e0b';
      return '#ef4444';
    });

    this.charts.push(
      new Chart<'line', number[], string>(canvas, {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: 'Tu posición',
              data: valores,
              borderColor: '#15803d',
              backgroundColor: 'rgba(21,128,61,0.08)',
              borderWidth: 2.5,
              fill: true,
              tension: 0.3,
              pointBackgroundColor: colores,
              pointBorderColor: '#ffffff',
              pointBorderWidth: 2,
              pointRadius: 6,
              pointHoverRadius: 9,
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
                label: (ctx: TooltipItem<'line'>) => {
                  const p = puntos[ctx.dataIndex];
                  const total = totales[ctx.dataIndex];
                  const percentil =
                    total > 1 ? (((total - p.posicion) / (total - 1)) * 100).toFixed(0) : '50';
                  return [
                    `Posición: #${p.posicion} de ${total}`,
                    `Percentil: ${percentil}°`,
                    `Valor: ${p.valorHato?.toFixed(2) ?? '—'} ${this.datos?.unidadKpi ?? ''}`,
                  ];
                },
              },
            },
          },
          scales: {
            y: {
              reverse: true,
              beginAtZero: false,
              grid: { color: 'rgba(0,0,0,0.04)' },
              ticks: {
                color: '#6b7280',
                font: { size: 11 },
                stepSize: 1,
                callback: (v) => `#${v}`,
              },
              title: {
                display: true,
                text: 'Posición en ranking (menor = mejor)',
                color: '#9ca3af',
                font: { size: 10 },
              },
            },
            x: {
              grid: { display: false },
              ticks: { color: '#374151', font: { size: 11 } },
            },
          },
        },
      }),
    );
  }

  private buildEvolucionPercentil(canvas: HTMLCanvasElement): void {
    const puntos = this.datos!.puntos;
    const labels = puntos.map((p) => this.formatFechaCorta(p.fecha));
    const valores = puntos.map((p) =>
      p.percentilEnFecha !== null ? Number(p.percentilEnFecha.toFixed(1)) : 0,
    );

    const colores = valores.map((v) => {
      if (v >= 70) return 'rgba(25,230,77,0.82)';
      if (v >= 40) return 'rgba(245,182,66,0.85)';
      return 'rgba(239,68,68,0.78)';
    });

    const borderColores = valores.map((v) => {
      if (v >= 70) return '#15c944';
      if (v >= 40) return '#f59e0b';
      return '#dc2626';
    });

    this.charts.push(
      new Chart<any, any, any>(canvas, {
        type: 'bar',
        data: {
          labels,
          datasets: [
            {
              type: 'bar',
              label: 'Percentil en fecha',
              data: valores,
              backgroundColor: colores,
              borderColor: borderColores,
              borderWidth: 1.5,
              borderRadius: 6,
              borderSkipped: false,
              order: 1,
            },
            {
              type: 'line',
              label: 'Promedio sector (P50)',
              data: valores.map(() => 50),
              borderColor: 'rgba(99,102,241,0.6)',
              borderWidth: 1.5,
              borderDash: [5, 4],
              backgroundColor: 'rgba(0,0,0,0)',
              pointRadius: 0,
              order: 0,
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
                label: (ctx: TooltipItem<any>) => {
                  if (ctx.datasetIndex === 1) return 'Promedio sector: P50';
                  const p = puntos[ctx.dataIndex];
                  return [
                    `Percentil: P${Number(ctx.parsed.y).toFixed(1)}`,
                    `Posición: #${p.posicion} de ${p.totalHatos}`,
                  ];
                },
              },
            },
          },
          scales: {
            y: {
              min: 0,
              max: 100,
              grid: { color: 'rgba(0,0,0,0.04)' },
              ticks: {
                color: '#6b7280',
                font: { size: 11 },
                callback: (v: any) => `P${v}`,
              },
              title: {
                display: true,
                text: 'Percentil (P50 = promedio del sector)',
                color: '#9ca3af',
                font: { size: 10 },
              },
            },
            x: {
              grid: { display: false },
              ticks: { color: '#374151', font: { size: 11 } },
            },
          },
        },
      }),
    );
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
  }
}
