import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import {
  BenchmarkHatoResultado,
  BenchmarkingService,
  ComparativaHatosDTO,
} from '../../../../../core/services/benchmarking/benchmarking.service';

Chart.register(...registerables);

@Component({
  selector: 'app-comparativa-hatos',
  standalone: true,
  templateUrl: './comparativa-hatos.component.html',
  styleUrl: './comparativa-hatos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComparativaHatosComponent implements OnInit, OnChanges, OnDestroy {
  @Input() idHato: string = '';
  @Input() kpisDisponibles: BenchmarkHatoResultado[] = [];

  @ViewChild('chartLineas') chartLineasRef!: ElementRef<HTMLCanvasElement>;

  kpiSeleccionado: string = '';
  topSeleccionado: 5 | 10 | 15 = 10;
  datos: ComparativaHatosDTO | null = null;
  loading = false;
  error = '';

  private chart: Chart<any, any, any> | null = null;

  readonly opcionesTop: { valor: 5 | 10 | 15; label: string }[] = [
    { valor: 5, label: 'Top 5' },
    { valor: 10, label: 'Top 10' },
    { valor: 15, label: 'Top 15' },
  ];

  get kpisUnicos(): { codigo: string; nombre: string }[] {
    const seen = new Map<string, string>();
    for (const r of this.kpisDisponibles) {
      if (r.kpi?.codigo && !seen.has(r.kpi.codigo)) {
        seen.set(r.kpi.codigo, r.kpi.nombre);
      }
    }
    return Array.from(seen.entries()).map(([codigo, nombre]) => ({ codigo, nombre }));
  }

  get tieneDatos(): boolean {
    return this.datos !== null && (this.datos.comparables?.length ?? 0) > 0;
  }

  get sinComparables(): boolean {
    return this.datos !== null && (this.datos.comparables?.length ?? 0) === 0;
  }

  get posicionHato(): number {
    if (!this.datos) return 0;
    const idx = this.datos.comparables.findIndex((c) => c.valor <= this.datos!.valorHatoActual);
    return idx >= 0 ? idx + 1 : this.datos.comparables.length + 1;
  }

  constructor(
    private benchmarkingService: BenchmarkingService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    if (this.kpisUnicos.length > 0) {
      this.kpiSeleccionado = this.kpisUnicos[0].codigo;
      this.cargarComparativa();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['kpisDisponibles'] && !changes['kpisDisponibles'].firstChange) {
      if (this.kpisUnicos.length > 0 && !this.kpiSeleccionado) {
        this.kpiSeleccionado = this.kpisUnicos[0].codigo;
      }
      if (this.kpiSeleccionado) this.cargarComparativa();
    }
  }

  setKpi(codigo: string): void {
    if (this.kpiSeleccionado === codigo) return;
    this.kpiSeleccionado = codigo;
    this.cargarComparativa();
  }

  setTop(top: 5 | 10 | 15): void {
    if (this.topSeleccionado === top) return;
    this.topSeleccionado = top;
    this.cargarComparativa();
  }

  private cargarComparativa(): void {
    if (!this.idHato || !this.kpiSeleccionado) return;

    this.loading = true;
    this.error = '';
    this.datos = null;
    this.destruirChart();
    this.cdr.markForCheck();

    this.benchmarkingService
      .getComparativaHatos(this.idHato, this.kpiSeleccionado, this.topSeleccionado)
      .subscribe({
        next: (resultado) => {
          this.datos = resultado;
          this.loading = false;
          this.cdr.markForCheck();
          setTimeout(() => this.construirChart(), 0);
        },
        error: () => {
          this.loading = false;
          this.error = 'No se pudo cargar la comparativa con otros hatos.';
          this.cdr.markForCheck();
        },
      });
  }

  private destruirChart(): void {
    this.chart?.destroy();
    this.chart = null;
  }

  private construirChart(): void {
    if (!this.chartLineasRef?.nativeElement || !this.datos) return;

    this.destruirChart();

    const comparables = this.datos.comparables ?? [];
    const miValor = this.datos.valorHatoActual;
    const miNombre = this.datos.nombreHatoActual;

    const labels = [...comparables.map((c) => c.etiqueta), miNombre];

    const valores = [...comparables.map((c) => c.valor), miValor];

    const colores = [...comparables.map(() => 'rgba(148,163,184,0.7)'), 'rgba(21,128,61,0.9)'];

    const bordeColores = [...comparables.map(() => 'rgba(148,163,184,1)'), '#15803d'];

    const radios = [...comparables.map(() => 5), 9];

    this.chart = new Chart(this.chartLineasRef.nativeElement, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: this.datos.nombreKpi,
            data: valores,
            backgroundColor: colores,
            borderColor: '#15803d',
            borderWidth: 2,
            pointBackgroundColor: colores,
            pointBorderColor: bordeColores,
            pointBorderWidth: 2,
            pointRadius: radios,
            pointHoverRadius: radios.map((r) => r + 2),
            tension: 0.35,
            fill: {
              target: 'origin',
              above: 'rgba(21,128,61,0.05)',
            },
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
              label: (ctx) => {
                const idx = ctx.dataIndex;
                const total = labels.length - 1;
                const label = idx === total ? `${miNombre} (Tu hato)` : labels[idx];
                const val = this.formatValor(ctx.parsed.y);
                return `${label}: ${val}`;
              },
            },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: {
              color: (ctx) => {
                return ctx.index === labels.length - 1 ? '#15803d' : '#6b7280';
              },
              font: (ctx) => ({
                size: 11,
                weight: ctx.index === labels.length - 1 ? 'bold' : 'normal',
              }),
            },
          },
          y: {
            beginAtZero: false,
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: {
              color: '#6b7280',
              font: { size: 11 },
              callback: (v) => this.formatValor(Number(v)),
            },
            title: {
              display: true,
              text: this.datos.unidadKpi ?? '',
              color: '#9ca3af',
              font: { size: 11 },
            },
          },
        },
      },
    });
  }

  formatValor(valor: number | null): string {
    if (valor === null || valor === undefined) return '—';
    const unidad = this.datos?.unidadKpi ?? '';
    if (unidad.includes('COP') || unidad === 'COP/vaca' || unidad === 'COP/L')
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(valor));
    if (unidad === '%') return valor.toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return valor.toFixed(2) + 'x';
    return `${valor.toFixed(2)} ${unidad ?? ''}`.trim();
  }

  ngOnDestroy(): void {
    this.destruirChart();
  }
}
