import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  AfterViewInit,
  SimpleChanges,
  ViewChildren,
  QueryList,
  NgZone,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables, TooltipItem } from 'chart.js';
import { GraficaDetalleModalComponent } from '../grafica-detalle-modal/grafica-detalle-modal.component';

import { BenchmarkHatoResultado } from '../../../../../core/services/benchmarking/benchmarking.service';

Chart.register(...registerables);

interface KpiGrupo {
  categoria: string;
  label: string;
  icono: string;
  items: BenchmarkHatoResultado[];
}

interface GraficaDetalle {
  id: string;
  titulo: string;
  subtitulo: string;
  descripcion: string;
  guiaLectura: string;
}

@Component({
  selector: 'app-graficas-benchmarking',
  standalone: true,
  imports: [CommonModule, GraficaDetalleModalComponent],
  templateUrl: './graficas-benchmarking.component.html',
  styleUrl: './graficas-benchmarking.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraficasBenchmarkingComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() resultados: BenchmarkHatoResultado[] = [];
  @Input() modo: 'REFERENCIA' | 'PLATAFORMA' = 'REFERENCIA';
  @Input() categoriaFiltro: string | null = null;

  @ViewChildren('chartCanvas')
  chartCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  @ViewChildren('chartSemaforo')
  chartSemaforoRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  @ViewChildren('chartRadarCategoria')
  chartRadarCategoriaRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  @ViewChildren('chartPotencial')
  chartPotencialRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  private charts: Chart<any, any, any>[] = [];
  private viewReady = false;

  graficaAbierta: GraficaDetalle | null = null;

  readonly categoriasConfig = [
    { categoria: 'PRODUCTIVIDAD', label: 'Productividad', icono: '🥛' },
    { categoria: 'HATO', label: 'Hato', icono: '🐄' },
    { categoria: 'FINANCIERO', label: 'Financiero', icono: '💰' },
    { categoria: 'EFICIENCIA', label: 'Eficiencia', icono: '⚙️' },
  ];

  readonly graficas = [
    {
      id: 'percentiles',
      titulo: 'Posición competitiva por KPI',
      subtitulo: 'Percentil 0–100: qué tan bien posicionado está tu hato en cada indicador.',
      descripcion:
        'Muestra en qué percentil se encuentra tu hato para cada KPI respecto al sector. Un percentil alto significa que superas a la mayoría de hatos comparables.',
      guiaLectura:
        'P0–49: por debajo del promedio del sector · P50: en el promedio · P51–100: por encima del promedio',
    },
    {
      id: 'indice',
      titulo: 'Tu hato vs Promedio y Top referencia',
      subtitulo: 'Rendimiento contra el promedio y el nivel top del sector.',
      descripcion:
        'Compara el percentil de tu hato contra la línea del promedio del sector (P50). Las barras verdes superan el promedio, las amarillas están cerca y las rojas están por debajo.',
      guiaLectura:
        'Línea punteada = promedio del sector (P50). Barras más largas que la línea = por encima del promedio.',
    },
    {
      id: 'radar',
      titulo: 'Perfil de desempeño',
      subtitulo:
        'Visión global en todos los KPIs. La línea punteada es el promedio del sector (P50).',
      descripcion:
        'Representa de forma visual el perfil completo de tu hato. Cada eje es un KPI y la superficie coloreada muestra qué tan lejos o cerca estás del promedio en cada dimensión.',
      guiaLectura:
        'Superficie más grande = mejor desempeño general. La línea punteada es el promedio del sector. Los puntos de color indican el nivel: verde (alto), amarillo (medio), rojo (bajo).',
    },
    {
      id: 'desviacion',
      titulo: 'Desviación vs promedio de referencia',
      subtitulo: 'Qué tan por encima (+) o por debajo (−) del promedio está tu hato.',
      descripcion:
        'Muestra la diferencia porcentual entre tu valor y el promedio del sector para cada KPI. Valores positivos indican que superas el promedio, negativos que estás por debajo.',
      guiaLectura:
        'Verde = por encima del promedio. Rojo = por debajo. El tamaño de la barra indica qué tan grande es la brecha.',
    },
    {
      id: 'semaforo',
      titulo: 'Distribución competitiva',
      subtitulo: 'Cuántos KPIs están en nivel alto, medio o bajo.',
      descripcion:
        'Resume en cuántos KPIs tu hato está en nivel alto (P≥70), medio (P40–69) o bajo (P<40). Es un diagnóstico rápido del estado general del hato.',
      guiaLectura:
        'Alto (verde): percentil ≥ 70 · Medio (amarillo): percentil 40–69 · Bajo (rojo): percentil < 40.',
    },
    {
      id: 'potencial',
      titulo: 'Potencial de mejora hacia el Top',
      subtitulo: 'Brecha de percentil restante para alcanzar el nivel óptimo.',
      descripcion:
        'Indica cuántos puntos de percentil le faltan a cada KPI para llegar a P100. Los KPIs con mayor barra son los que más impacto tendrían si se mejoran.',
      guiaLectura:
        'Brecha pequeña (verde): cerca del top. Brecha grande (rojo): mayor oportunidad de mejora. Prioriza los KPIs con barras más largas.',
    },
  ];

  constructor(
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
  ) {}

  ngAfterViewInit(): void {
    this.viewReady = true;

    this.chartCanvases.changes.subscribe(() => {
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => this.construirGraficas(), 100);
      });
    });

    this.chartRadarCategoriaRef.changes.subscribe(() => {
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => this.construirGraficas(), 100);
      });
    });

    this.chartSemaforoRef.changes.subscribe(() => {
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => this.construirGraficas(), 100);
      });
    });

    this.chartPotencialRef.changes.subscribe(() => {
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => this.construirGraficas(), 100);
      });
    });

    if (this.resultados.length > 0) {
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => this.construirGraficas(), 100);
      });
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['resultados'] || changes['modo'] || changes['categoriaFiltro']) {
      this.cdr.markForCheck();
      this.cdr.detectChanges();

      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => {
          if (this.viewReady && this.resultados.length > 0) {
            this.construirGraficas();
            this.cdr.markForCheck();
          }
        }, 150);
      });
    }
  }

  // ── Agrupación de datos ──────────────────────────────────

  get grupos(): KpiGrupo[] {
    console.log('🔍 grupos getter — resultados[0]:', JSON.stringify(this.resultados[0], null, 2));
    console.log('🔍 categorías encontradas:', [
      ...new Set(this.resultados.map((r) => r.kpi?.categoria)),
    ]);
    return this.categoriasConfig

      .map((c) => ({
        ...c,
        items: this.resultados
          .filter((r) => r.kpi?.categoria === c.categoria)
          .sort((a, b) => (b.percentil ?? 0) - (a.percentil ?? 0)),
      }))
      .filter(
        (g) =>
          g.items.length > 0 &&
          (this.categoriaFiltro === null || g.categoria === this.categoriaFiltro),
      );
  }

  get gruposVisibles(): KpiGrupo[] {
    return this.grupos;
  }

  get datosPotencial(): {
    nombre: string;
    potencial: number;
    codigo: string;
  }[] {
    const dedup = new Map<string, BenchmarkHatoResultado>();

    for (const r of this.resultados) {
      if (!r.kpi?.codigo) continue;
      if (!dedup.has(r.kpi.codigo)) dedup.set(r.kpi.codigo, r);
    }
    return Array.from(dedup.values())
      .map((r) => ({
        nombre: r.kpi?.nombre ?? '',
        codigo: r.kpi?.codigo ?? '',
        potencial: this.calcularPotencial(r),
      }))
      .filter((x) => Number.isFinite(x.potencial) && x.potencial > 0)
      .sort((a, b) => b.potencial - a.potencial)
      .slice(0, 12);
  }

  get datosSemaforo(): { alto: number; medio: number; bajo: number; total: number } {
    const todos = this.resultados;
    return {
      alto: todos.filter((r) => (r.percentil ?? 0) >= 70).length,
      medio: todos.filter((r) => {
        const p = r.percentil ?? 0;
        return p >= 40 && p < 70;
      }).length,
      bajo: todos.filter((r) => (r.percentil ?? 0) < 40).length,
      total: todos.length,
    };
  }

  get tieneResultados(): boolean {
    return this.resultados.length > 0;
  }

  getGraficaCategoria(categoria: string): GraficaDetalle {
    const config = this.categoriasConfig.find((c) => c.categoria === categoria);
    return {
      id: `radar_${categoria.toLowerCase()}`,
      titulo: `${config?.icono ?? ''} ${config?.label ?? categoria}`,
      subtitulo: `Perfil de desempeño · ${config?.label?.toLowerCase() ?? categoria}`,
      descripcion: `Muestra el percentil de cada KPI de ${config?.label?.toLowerCase() ?? categoria} respecto al sector.`,
      guiaLectura:
        'P0–49: por debajo del promedio · P50: promedio del sector · P51–100: por encima del promedio',
    };
  }

  getGraficaBarrasCategoria(categoria: string): GraficaDetalle {
    const config = this.categoriasConfig.find((c) => c.categoria === categoria);
    return {
      id: `barras_${categoria.toLowerCase()}`,
      titulo: `${config?.icono ?? ''} ${config?.label ?? categoria}`,
      subtitulo: `Posición competitiva · ${config?.label?.toLowerCase() ?? categoria}`,
      descripcion: `Muestra el percentil de cada KPI de ${config?.label?.toLowerCase() ?? categoria} respecto al sector. Las barras verdes superan el promedio, amarillas están cerca y rojas están por debajo.`,
      guiaLectura:
        'P0–49: por debajo del promedio · P50: promedio del sector · P51–100: por encima del promedio',
    };
  }

  // ── Helpers ───────────────────────────────────────────────

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

  calcularPotencial(r: BenchmarkHatoResultado): number {
    return Math.max(0, 100 - Number(r.percentil ?? 0));
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

  trackByGrupo(_: number, g: KpiGrupo): string {
    return g.categoria;
  }

  trackByKpi(_: number, r: BenchmarkHatoResultado): number {
    return r.idBenchmarkHato;
  }

  // ── Construcción de gráficas ──────────────────────────────

  private destruirGraficas(): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];
  }

  private setAltura(canvas: HTMLCanvasElement, items: number): void {
    const h = Math.max(220, items * 42);
    canvas.parentElement!.style.height = h + 'px';
  }

  private construirGraficas(): void {
    console.log('🔧 construirGraficas() llamado');
    console.log('🔧 viewReady:', this.viewReady);
    console.log('🔧 resultados.length:', this.resultados.length);

    const canvases = this.chartCanvases.toArray();
    const semaforos = this.chartSemaforoRef.toArray();
    const potenciales = this.chartPotencialRef.toArray();
    const radarCats = this.chartRadarCategoriaRef.toArray();

    console.log(
      '📊 canvases (barras):',
      canvases.length,
      canvases.map((c) => ({
        el: c.nativeElement,
        enDOM: document.contains(c.nativeElement),
        w: c.nativeElement.offsetWidth,
        h: c.nativeElement.offsetHeight,
      })),
    );

    console.log(
      '📊 semaforos:',
      semaforos.length,
      semaforos.map((c) => ({
        enDOM: document.contains(c.nativeElement),
        w: c.nativeElement.offsetWidth,
      })),
    );

    console.log(
      '📊 radarCategorias:',
      radarCats.length,
      radarCats.map((c) => ({
        enDOM: document.contains(c.nativeElement),
        w: c.nativeElement.offsetWidth,
      })),
    );

    console.log(
      '📊 potenciales:',
      potenciales.length,
      potenciales.map((c) => ({
        enDOM: document.contains(c.nativeElement),
        w: c.nativeElement.offsetWidth,
      })),
    );

    console.log(
      '📊 grupos:',
      this.grupos.length,
      this.grupos.map((g) => g.categoria),
    );

    console.log('gruposVisibles:', this.gruposVisibles.length);
    console.log('tieneResultados:', this.tieneResultados);

    this.destruirGraficas();

    // Semáforo
    if (semaforos[0]?.nativeElement) {
      console.log('Construyendo semáforo...');
      try {
        this.buildSemaforo(semaforos[0].nativeElement);
        console.log('Semáforo construido');
      } catch (e) {
        console.error('Error semáforo:', e);
      }
    } else {
      console.warn('Canvas semáforo no disponible');
    }

    // Radares por categoría
    this.grupos.forEach((grupo, idx) => {
      const canvas = radarCats[idx]?.nativeElement;
      console.log(
        `📡 Radar ${grupo.categoria} — canvas[${idx}]:`,
        canvas ? 'disponible' : 'NO disponible',
        canvas ? `${canvas.offsetWidth}x${canvas.offsetHeight}` : '',
      );
      if (canvas) {
        try {
          this.buildRadarCategoria(canvas, grupo.items);
          console.log(`✅ Radar ${grupo.categoria} construido`);
        } catch (e) {
          console.error(`❌ Error radar ${grupo.categoria}:`, e);
        }
      }
    });

    // Barras por categoría
    this.grupos.forEach((grupo, idx) => {
      const canvas = canvases[idx]?.nativeElement;
      console.log(
        `📊 Barras ${grupo.categoria} — canvas[${idx}]:`,
        canvas ? 'disponible' : 'NO disponible',
        canvas ? `${canvas.offsetWidth}x${canvas.offsetHeight}` : '',
      );
      if (canvas) {
        try {
          this.buildCategoria(canvas, grupo);
          console.log(`✅ Barras ${grupo.categoria} construida`);
        } catch (e) {
          console.error(`❌ Error barras ${grupo.categoria}:`, e);
        }
      }
    });

    // Potencial
    if (potenciales[0]?.nativeElement && this.datosPotencial.length > 0) {
      console.log('✅ Construyendo potencial...');
      try {
        this.buildPotencial(potenciales[0].nativeElement);
        console.log('✅ Potencial construido');
      } catch (e) {
        console.error('❌ Error potencial:', e);
      }
    } else {
      console.warn(
        '⚠️ Canvas potencial no disponible o sin datos.',
        'potenciales.length:',
        potenciales.length,
        'datosPotencial.length:',
        this.datosPotencial.length,
      );
    }

    console.log('🏁 construirGraficas() finalizado — charts activos:', this.charts.length);
  }

  private buildCategoria(canvas: HTMLCanvasElement, grupo: KpiGrupo): void {
    if (!grupo.items.length) return;
    this.setAltura(canvas, grupo.items.length);

    const labels = grupo.items.map((r) => r.kpi?.nombre ?? '');
    const data = grupo.items.map((r) => Number(r.percentil ?? 0));
    const colors = data.map((v) => this.colorPorPercentil(v));
    const borders = data.map((v) => this.borderPorPercentil(v));

    this.charts.push(
      new Chart(canvas, {
        type: 'bar',
        data: {
          labels,
          datasets: [
            {
              label: 'Tu hato (percentil)',
              data,
              backgroundColor: colors,
              borderColor: borders,
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
              borderDash: [5, 4],
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
                label: (ctx: import('chart.js').TooltipItem<'bar'>) => {
                  if (ctx.datasetIndex === 1) return 'Promedio sector: P50';
                  const r = grupo.items[ctx.dataIndex];
                  const pct = Number(ctx.parsed.x ?? 0).toFixed(1);
                  const val = this.formatValor(r.valorHato, r.kpi?.unidad);
                  return [`Percentil: ${pct}`, `Tu valor: ${val}`, `Estado: ${r.interpretacion}`];
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
                text: 'Percentil (0 = por debajo del sector · 50 = promedio · 100 = mejor)',
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
      }),
    );
  }
  private buildRadarCategoria(canvas: HTMLCanvasElement, items: BenchmarkHatoResultado[]): void {
    if (!items.length) return;

    const values = items.map((r) => Number(r.percentil ?? 0));

    this.charts.push(
      new Chart<'radar', number[], string>(canvas, {
        type: 'radar',
        data: {
          labels: items.map((r) => r.kpi?.nombre ?? ''),
          datasets: [
            {
              label: 'Tu hato',
              data: values,
              backgroundColor: 'rgba(21,128,61,0.15)',
              borderColor: '#15803d',
              borderWidth: 2,
              pointBackgroundColor: values.map((v) => this.colorPorPercentil(v)),
              pointBorderColor: '#fff',
              pointRadius: 5,
              pointHoverRadius: 7,
            },
            {
              label: 'Promedio (P50)',
              data: values.map(() => 50),
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
            legend: { display: false },
            tooltip: {
              callbacks: {
                label: (ctx: TooltipItem<'radar'>) => {
                  const r = items[ctx.dataIndex];
                  const pct = Number(ctx.parsed.r ?? 0).toFixed(1);
                  return [
                    `P${pct}`,
                    `${this.formatValor(r.valorHato, r.kpi?.unidad)}`,
                    `${r.interpretacion}`,
                  ];
                },
              },
            },
          },
          scales: {
            r: {
              min: 0,
              max: 100,
              ticks: {
                stepSize: 25,
                backdropColor: 'transparent',
                color: '#9ca3af',
                font: { size: 9 },
              },
              grid: { color: 'rgba(148,163,184,0.2)' },
              angleLines: { color: 'rgba(148,163,184,0.2)' },
              pointLabels: { font: { size: 10 }, color: '#374151' },
            },
          },
        },
      }),
    );
  }

  private buildSemaforo(canvas: HTMLCanvasElement): void {
    const { alto, medio, bajo, total } = this.datosSemaforo;

    this.charts.push(
      new Chart<'doughnut', number[], string>(canvas, {
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
              hoverOffset: 8,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '62%',
          plugins: {
            legend: {
              position: 'bottom',
              labels: { color: '#374151', font: { size: 11 }, padding: 16 },
            },
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
      }),
    );
  }

  private buildPotencial(canvas: HTMLCanvasElement): void {
    const items = this.datosPotencial;
    if (!items.length) return;
    this.setAltura(canvas, items.length);

    this.charts.push(
      new Chart(canvas, {
        type: 'bar',
        data: {
          labels: items.map((x) => x.nombre),
          datasets: [
            {
              label: 'Brecha hacia el top (%)',
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
                label: (ctx) => `${Number(ctx.parsed.x ?? 0).toFixed(1)}% para alcanzar el top`,
              },
            },
          },
          scales: {
            x: {
              beginAtZero: true,
              grid: { color: 'rgba(0,0,0,0.04)' },
              ticks: { color: '#6b7280', font: { size: 11 }, callback: (v) => `${v}%` },
              title: {
                display: true,
                text: 'Brecha restante hacia el top (%)',
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
      }),
    );
  }

  abrirDetalle(grafica: GraficaDetalle): void {
    this.graficaAbierta = grafica;
    this.cdr.markForCheck();
  }

  cerrarDetalle(): void {
    this.graficaAbierta = null;
    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
  }
}
