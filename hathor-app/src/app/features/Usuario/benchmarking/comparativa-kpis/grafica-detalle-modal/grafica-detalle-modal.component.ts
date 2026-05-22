import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables, TooltipItem } from 'chart.js';
import { BenchmarkHatoResultado } from '../../../../../core/services/benchmarking/benchmarking.service';

Chart.register(...registerables);

interface GraficaDetalle {
  id: string;
  titulo: string;
  subtitulo: string;
  descripcion: string;
  guiaLectura: string;
}

interface KpiGrupo {
  categoria: string;
  label: string;
  icono: string;
  items: BenchmarkHatoResultado[];
}

@Component({
  selector: 'app-grafica-detalle-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grafica-detalle-modal.component.html',
  styleUrl: './grafica-detalle-modal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraficaDetalleModalComponent implements AfterViewInit, OnDestroy {
  @Input() grafica!: GraficaDetalle;
  @Input() resultados: BenchmarkHatoResultado[] = [];
  @Input() grupos: KpiGrupo[] = [];
  @Output() cerrar = new EventEmitter<void>();

  @ViewChild('chartDetalle') chartDetalleRef!: ElementRef<HTMLCanvasElement>;

  private chart: Chart<any, any, any> | null = null;
  rangoRadar: 100 | 75 | 50 | 25 = 100;

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    setTimeout(() => this.construirChart(), 100);
  }

  @HostListener('document:keydown.escape')
  onEsc(): void {
    this.cerrar.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.cerrar.emit();
    }
  }

  // ── Helpers ──────────────────────────────────────────────

  colorPorPercentil(p: number | null): string {
    const n = Number(p ?? 0);
    if (n >= 70) return 'rgba(25,230,77,0.82)';
    if (n >= 40) return 'rgba(245,182,66,0.85)';
    return 'rgba(239,68,68,0.78)';
  }

  borderPorPercentil(p: number | null): string {
    const n = Number(p ?? 0);
    if (n >= 70) return '#15c944';
    if (n >= 40) return '#f59e0b';
    return '#dc2626';
  }

  formatValor(valor: number | null, unidad?: string): string {
    if (valor === null || valor === undefined) return '—';
    if (!unidad) return Number(valor).toFixed(2);
    if (unidad.includes('COP') || unidad === 'COP/vaca' || unidad === 'COP/L')
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(Number(valor)));
    if (unidad === '%') return Number(valor).toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return Number(valor).toFixed(2) + 'x';
    return `${Number(valor).toFixed(2)} ${unidad}`;
  }

  interpretacionClass(interp: string | null): string {
    const map: Record<string, string> = {
      OPTIMO: 'badge-optimo',
      BUENO: 'badge-bueno',
      ACEPTABLE: 'badge-aceptable',
      CRITICO: 'badge-critico',
    };
    return map[interp ?? ''] ?? 'badge-default';
  }

  get guiaItems(): { rango: string; descripcion: string; color: string }[] {
    return [
      { rango: 'P0 – 49', descripcion: 'Por debajo del promedio del sector', color: '#dc2626' },
      { rango: 'P50', descripcion: 'En el promedio del sector', color: '#f59e0b' },
      { rango: 'P51 – 100', descripcion: 'Por encima del promedio del sector', color: '#15803d' },
    ];
  }

  setRangoRadar(rango: 100 | 75 | 50 | 25): void {
    if (this.rangoRadar === rango) return;
    this.rangoRadar = rango;
    this.construirChart();
  }

  get datosParaTabla(): BenchmarkHatoResultado[] {
    if (this.grafica.id === 'semaforo' || this.grafica.id === 'radar') {
      return this.resultados;
    }
    const grupo = this.grupos.find((g) =>
      this.grafica.id.toLowerCase().includes(g.categoria.toLowerCase()),
    );
    return grupo?.items ?? this.resultados;
  }

  get top3Mejores(): BenchmarkHatoResultado[] {
    return [...this.resultados]
      .filter((r) => r.percentil !== null)
      .sort((a, b) => Number(b.percentil) - Number(a.percentil))
      .slice(0, 3);
  }

  get top3AMejorar(): BenchmarkHatoResultado[] {
    return [...this.resultados]
      .filter((r) => r.percentil !== null)
      .sort((a, b) => Number(a.percentil) - Number(b.percentil))
      .slice(0, 3);
  }

  // ── Construcción gráfica ────────────────────────────────

  private destruirChart(): void {
    this.chart?.destroy();
    this.chart = null;
  }

  private construirChart(): void {
    this.destruirChart();
    if (!this.chartDetalleRef?.nativeElement) return;

    const canvas = this.chartDetalleRef.nativeElement;
    const id = this.grafica.id;

    if (id === 'percentiles') {
      this.buildPercentiles(canvas);
    } else if (id === 'radar') {
      this.buildRadar(canvas);
    } else if (id === 'semaforo') {
      this.buildSemaforo(canvas);
    } else if (id === 'potencial') {
      this.buildPotencial(canvas);
    } else if (id.startsWith('radar_')) {
      this.buildRadarCategoria(canvas, id.replace('radar_', '').toUpperCase());
    } else if (id.startsWith('barras_')) {
      this.buildBarrasCategoria(canvas, id.replace('barras_', '').toUpperCase());
    } else {
      this.buildCategoria(canvas);
    }

    this.cdr.markForCheck();
  }

  private getDedupData(): BenchmarkHatoResultado[] {
    const seen = new Map<string, BenchmarkHatoResultado>();
    for (const r of this.resultados) {
      if (r.kpi?.codigo && !seen.has(r.kpi.codigo)) seen.set(r.kpi.codigo, r);
    }
    return Array.from(seen.values());
  }

  private buildPercentiles(canvas: HTMLCanvasElement): void {
    const data = this.getDedupData()
      .filter((r) => r.percentil !== null)
      .sort((a, b) => Number(b.percentil) - Number(a.percentil));

    if (!data.length) return;
    canvas.parentElement!.style.height = Math.max(400, data.length * 44) + 'px';

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: data.map((r) => r.kpi?.nombre ?? ''),
        datasets: [
          {
            label: 'Tu hato (percentil)',
            data: data.map((r) => Number(r.percentil ?? 0)),
            backgroundColor: data.map((r) => this.colorPorPercentil(r.percentil)),
            borderColor: data.map((r) => this.borderPorPercentil(r.percentil)),
            borderWidth: 1.5,
            borderRadius: 6,
            borderSkipped: false,
          },
          {
            label: 'Promedio sector',
            data: data.map(() => 50),
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(99,102,241,0.6)',
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
          legend: { display: true, position: 'bottom' },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'bar'>) => {
                if (ctx.datasetIndex === 1) return 'Promedio sector: P50';
                const r = data[ctx.dataIndex];
                const pct = Number(ctx.parsed.x ?? 0).toFixed(1);
                return [
                  `Percentil: ${pct}`,
                  `Valor: ${this.formatValor(r.valorHato, r.kpi?.unidad)}`,
                  `Estado: ${r.interpretacion}`,
                ];
              },
            },
          },
        },
        scales: {
          x: { min: 0, max: 100, ticks: { callback: (v: string | number) => `P${v}` } },
          y: { ticks: { font: { size: 12 } } },
        },
      },
    });
  }

  private buildCategoria(canvas: HTMLCanvasElement): void {
    const grupo = this.grupos.find((g) =>
      this.grafica.id.toLowerCase().includes(g.categoria.toLowerCase()),
    );
    const items = grupo?.items ?? [];
    if (!items.length) return;

    canvas.parentElement!.style.height = Math.max(400, items.length * 44) + 'px';

    const data = items.map((r) => Number(r.percentil ?? 0));
    const colors = data.map((v) => this.colorPorPercentil(v));

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: items.map((r) => r.kpi?.nombre ?? ''),
        datasets: [
          {
            label: 'Tu hato (percentil)',
            data,
            backgroundColor: colors,
            borderRadius: 6,
            borderSkipped: false,
          },
          {
            label: 'Promedio sector',
            data: data.map(() => 50),
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(99,102,241,0.6)',
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
          legend: { display: true, position: 'bottom' },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'bar'>) => {
                if (ctx.datasetIndex === 1) return 'Promedio sector: P50';
                const r = items[ctx.dataIndex];
                const pct = Number(ctx.parsed.x ?? 0).toFixed(1);
                return [
                  `Percentil: ${pct}`,
                  `Valor: ${this.formatValor(r.valorHato, r.kpi?.unidad)}`,
                  `Estado: ${r.interpretacion}`,
                ];
              },
            },
          },
        },
        scales: {
          x: { min: 0, max: 100, ticks: { callback: (v: string | number) => `P${v}` } },
          y: { ticks: { font: { size: 13 } } },
        },
      },
    });
  }

  private buildBarrasCategoria(canvas: HTMLCanvasElement, categoria: string): void {
    const grupo = this.grupos.find((g) => g.categoria === categoria);
    const items = grupo?.items ?? [];
    if (!items.length) return;

    canvas.parentElement!.style.height = Math.max(400, items.length * 44) + 'px';

    const data = items.map((r) => Number(r.percentil ?? 0));
    const colors = data.map((v) => this.colorPorPercentil(v));

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: items.map((r) => r.kpi?.nombre ?? ''),
        datasets: [
          {
            label: 'Tu hato (percentil)',
            data,
            backgroundColor: colors,
            borderRadius: 6,
            borderSkipped: false,
          },
          {
            label: 'Promedio sector',
            data: data.map(() => 50),
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(99,102,241,0.6)',
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
          legend: { display: true, position: 'bottom' },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'bar'>) => {
                if (ctx.datasetIndex === 1) return 'Promedio sector: P50';
                const r = items[ctx.dataIndex];
                const pct = Number(ctx.parsed.x ?? 0).toFixed(1);
                return [
                  `Percentil: P${pct}`,
                  `Valor: ${this.formatValor(r.valorHato, r.kpi?.unidad)}`,
                  `Estado: ${r.interpretacion}`,
                ];
              },
            },
          },
        },
        scales: {
          x: {
            min: 0,
            max: 100,
            ticks: { callback: (v: string | number) => `P${v}` },
          },
          y: { ticks: { font: { size: 13 } } },
        },
      },
    });
  }

  private buildRadar(canvas: HTMLCanvasElement): void {
    const data = this.getDedupData();
    const values = data.map((r) => Math.min(Number(r.percentil ?? 0), this.rangoRadar));
    const realValues = data.map((r) => Number(r.percentil ?? 0));

    this.chart = new Chart<'radar', number[], string>(canvas, {
      type: 'radar',
      data: {
        labels: data.map((r) => r.kpi?.nombre ?? ''),
        datasets: [
          {
            label: 'Tu hato',
            data: values,
            backgroundColor: 'rgba(21,128,61,0.15)',
            borderColor: '#15803d',
            borderWidth: 2.5,
            pointBackgroundColor: realValues.map((v) => this.colorPorPercentil(v)),
            pointBorderColor: '#fff',
            pointRadius: 6,
          },
          {
            label: 'Promedio sector (P50)',
            data: values.map(() => Math.min(50, this.rangoRadar)),
            backgroundColor: 'rgba(148,163,184,0.06)',
            borderColor: 'rgba(148,163,184,0.5)',
            borderWidth: 1.5,
            borderDash: [5, 4],
            pointRadius: 0,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'bottom' } },
        scales: {
          r: {
            min: this.rangoRadar,
            max: 100,
            ticks: {
              stepSize: this.rangoRadar <= 25 ? 5 : this.rangoRadar / 4,
              backdropColor: 'transparent',
              color: '#9ca3af',
              font: { size: 10 },
            },
          },
        },
      },
    });
  }

  private buildRadarCategoria(canvas: HTMLCanvasElement, categoria: string): void {
    const grupo = this.grupos.find((g) => g.categoria === categoria);
    const items = grupo?.items ?? [];
    if (!items.length) return;

    const values = items.map((r) => Math.min(Number(r.percentil ?? 0), this.rangoRadar));
    const realValues = items.map((r) => Number(r.percentil ?? 0));

    this.chart = new Chart<'radar', number[], string>(canvas, {
      type: 'radar',
      data: {
        labels: items.map((r) => r.kpi?.nombre ?? ''),
        datasets: [
          {
            label: 'Tu hato',
            data: values,
            backgroundColor: 'rgba(21,128,61,0.15)',
            borderColor: '#15803d',
            borderWidth: 2.5,
            pointBackgroundColor: realValues.map((v) => this.colorPorPercentil(v)),
            pointBorderColor: '#fff',
            pointRadius: 7,
            pointHoverRadius: 9,
          },
          {
            label: 'Promedio sector (P50)',
            data: values.map(() => Math.min(50, this.rangoRadar)),
            backgroundColor: 'rgba(148,163,184,0.06)',
            borderColor: 'rgba(148,163,184,0.5)',
            borderWidth: 1.5,
            borderDash: [5, 4],
            pointRadius: 0,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: { color: '#374151', font: { size: 11 } },
          },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'radar'>) => {
                if (ctx.datasetIndex === 1) return 'Promedio: P50';
                const r = items[ctx.dataIndex];
                const pct = Number(ctx.parsed.r ?? 0).toFixed(1);
                return [
                  `Percentil: P${pct}`,
                  `Valor: ${this.formatValor(r.valorHato, r.kpi?.unidad)}`,
                  `Estado: ${r.interpretacion}`,
                ];
              },
            },
          },
        },
        scales: {
          r: {
            min: 0,
            max: this.rangoRadar,
            ticks: {
              stepSize: this.rangoRadar <= 25 ? 5 : this.rangoRadar / 4,
              backdropColor: 'transparent',
              color: '#9ca3af',
              font: { size: 10 },
            },
            grid: { color: 'rgba(148,163,184,0.2)' },
            angleLines: { color: 'rgba(148,163,184,0.2)' },
            pointLabels: { font: { size: 12 }, color: '#374151' },
          },
        },
      },
    });
  }

  private buildSemaforo(canvas: HTMLCanvasElement): void {
    const data = this.getDedupData();
    const alto = data.filter((r) => Number(r.percentil ?? 0) >= 70).length;
    const medio = data.filter((r) => {
      const p = Number(r.percentil ?? 0);
      return p >= 40 && p < 70;
    }).length;
    const bajo = data.filter((r) => Number(r.percentil ?? 0) < 40).length;
    const total = data.length;

    this.chart = new Chart<'doughnut', number[], string>(canvas, {
      type: 'doughnut',
      data: {
        labels: ['Alto (≥70)', 'Medio (40–69)', 'Bajo (<40)'],
        datasets: [
          {
            data: [alto, medio, bajo],
            backgroundColor: [
              'rgba(25,230,77,0.85)',
              'rgba(245,182,66,0.85)',
              'rgba(239,68,68,0.82)',
            ],
            borderColor: ['#15c944', '#f59e0b', '#dc2626'],
            borderWidth: 2,
            hoverOffset: 10,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '58%',
        plugins: {
          legend: { position: 'bottom' },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'doughnut'>) => {
                const v = Number(ctx.parsed ?? 0);
                const pct = total > 0 ? ((v / total) * 100).toFixed(1) : '0';
                return `${v} KPI(s) — ${pct}% del total`;
              },
            },
          },
        },
      },
    });
  }

  private buildPotencial(canvas: HTMLCanvasElement): void {
    const items = this.getDedupData()
      .map((r) => ({
        nombre: r.kpi?.nombre ?? '',
        potencial: Math.max(0, 100 - Number(r.percentil ?? 0)),
      }))
      .filter((x) => x.potencial > 0)
      .sort((a, b) => b.potencial - a.potencial)
      .slice(0, 15);

    if (!items.length) return;
    canvas.parentElement!.style.height = Math.max(400, items.length * 44) + 'px';

    this.chart = new Chart<'bar', number[], string>(canvas, {
      type: 'bar',
      data: {
        labels: items.map((x) => x.nombre),
        datasets: [
          {
            label: 'Brecha hacia el top',
            data: items.map((x) => x.potencial),
            backgroundColor: items.map((x) =>
              x.potencial <= 15
                ? 'rgba(34,197,94,0.78)'
                : x.potencial <= 50
                  ? 'rgba(245,182,66,0.85)'
                  : 'rgba(239,68,68,0.78)',
            ),
            borderRadius: 6,
            borderSkipped: false,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'bar'>) =>
                `${Number(ctx.parsed.x ?? 0).toFixed(1)} puntos de mejora`,
            },
          },
        },
        scales: {
          x: { min: 0, max: 100, ticks: { callback: (v: string | number) => `${v}pts` } },
          y: { ticks: { font: { size: 13 } } },
        },
      },
    });
  }

  ngOnDestroy(): void {
    this.destruirChart();
  }
}
