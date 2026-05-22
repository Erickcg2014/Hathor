import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ChangeDetectorRef,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { Chart, registerables } from 'chart.js';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import {
  RegistroFinancieroService,
  RegistroFinanciero,
} from '../../../../core/services/finanzas/registro-financiero.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

interface TipoStat {
  tipo: string;
  total: number;
  cantidad: number;
  pct: number;
}

interface CategoriaStat {
  nombre: string;
  tipo: string;
  total: number;
  pct: number;
}

interface MesStat {
  mes: string;
  mesKey: string;
  ingresos: number;
  egresos: number;
  balance: number;
  pctIngreso: number;
  pctEgreso: number;
}

interface Stats {
  totalRegistros: number;
  totalIngresos: number;
  totalEgresos: number;
  balanceNeto: number;
  ratioRentabilidad: number;
  mesesConActividad: number;
  promedioMensual: number;
  porTipo: TipoStat[];
  topCategorias: CategoriaStat[];
  topCategoriaGastos: CategoriaStat[];
  evolucionMensual: MesStat[];
  mejorMes: MesStat | null;
  mesMayorGasto: MesStat | null;
  categoriaLider: CategoriaStat | null;
}

@Component({
  selector: 'app-estadisticas-finanzas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './estadisticas-finanzas.component.html',
  styleUrl: './estadisticas-finanzas.component.css',
})
export class EstadisticasFinanzasComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartEvolucion') chartEvolucionRef!: ElementRef;
  @ViewChild('chartDona') chartDonaRef!: ElementRef;
  @ViewChild('chartTopIngresos') chartTopIngresosRef!: ElementRef;
  @ViewChild('chartTopGastos') chartTopGastosRef!: ElementRef;

  hatoActivo: Hato | null = null;
  loading = false;
  stats: Stats = this.statsVacias();

  private charts: Chart<any, any, any>[] = [];
  private viewReady = false;
  private dataReady = false;
  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private registroService: RegistroFinancieroService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato) {
        this.cargarYCalcular(hato.idHato as string);
      } else {
        this.stats = this.statsVacias();
        this.destruirGraficas();
      }
      this.cdr.markForCheck();
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.dataReady) this.construirGraficas();
  }

  private cargarYCalcular(idHato: string): void {
    this.loading = true;
    this.destruirGraficas();
    this.cdr.markForCheck();

    this.registroService
      .getRegistrosByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (registros) => {
          this.stats = this.calcularStats(registros);
          this.loading = false;
          this.dataReady = true;
          this.cdr.markForCheck();
          setTimeout(() => {
            if (this.viewReady) this.construirGraficas();
          }, 150);
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  //  ======== MOTOR DE CÁLCULO ==========================
  private calcularStats(registros: RegistroFinanciero[]): Stats {
    if (!registros.length) return this.statsVacias();

    const TIPOS_EGRESO = ['GASTO', 'COSTO', 'INVERSION'];

    const totalIngresos = this.sumar(registros, 'INGRESO');
    const totalEgresos = TIPOS_EGRESO.reduce((s, t) => s + this.sumar(registros, t), 0);
    const balanceNeto = totalIngresos - totalEgresos;
    const ratioRentabilidad =
      totalEgresos > 0 ? totalIngresos / totalEgresos : totalIngresos > 0 ? Infinity : 0;

    const TODOS_TIPOS = ['INGRESO', 'GASTO', 'COSTO', 'INVERSION'];
    const maxTipoTotal = Math.max(...TODOS_TIPOS.map((t) => this.sumar(registros, t)));

    const porTipo: TipoStat[] = TODOS_TIPOS.map((tipo) => {
      const grupo = registros.filter((r) => r.tipoMovimiento === tipo);
      const total = grupo.reduce((s, r) => s + r.monto, 0);
      return {
        tipo,
        total,
        cantidad: grupo.length,
        pct: maxTipoTotal > 0 ? (total / maxTipoTotal) * 100 : 0,
      };
    })
      .filter((t) => t.cantidad > 0)
      .sort((a, b) => b.total - a.total);

    // Top categorías ingresos
    const catIngMap = new Map<string, { nombre: string; tipo: string; total: number }>();
    for (const r of registros.filter((r) => r.tipoMovimiento === 'INGRESO')) {
      const nombre = r.categoriaFinanciera?.nombre ?? 'Sin categoría';
      const prev = catIngMap.get(nombre);
      catIngMap.set(nombre, {
        nombre,
        tipo: r.tipoMovimiento,
        total: (prev?.total ?? 0) + r.monto,
      });
    }
    const catIngArr = Array.from(catIngMap.values()).sort((a, b) => b.total - a.total);
    const maxCatIng = catIngArr[0]?.total ?? 0;
    const topCategorias: CategoriaStat[] = catIngArr.slice(0, 5).map((c) => ({
      ...c,
      pct: maxCatIng > 0 ? (c.total / maxCatIng) * 100 : 0,
    }));

    // Top categorías gastos
    const catGasMap = new Map<string, { nombre: string; tipo: string; total: number }>();
    for (const r of registros.filter((r) => TIPOS_EGRESO.includes(r.tipoMovimiento))) {
      const nombre = r.categoriaFinanciera?.nombre ?? 'Sin categoría';
      const prev = catGasMap.get(nombre);
      catGasMap.set(nombre, {
        nombre,
        tipo: r.tipoMovimiento,
        total: (prev?.total ?? 0) + r.monto,
      });
    }
    const catGasArr = Array.from(catGasMap.values()).sort((a, b) => b.total - a.total);
    const maxCatGas = catGasArr[0]?.total ?? 0;
    const topCategoriaGastos: CategoriaStat[] = catGasArr.slice(0, 5).map((c) => ({
      ...c,
      pct: maxCatGas > 0 ? (c.total / maxCatGas) * 100 : 0,
    }));

    // Evolución mensual
    const mesMap = new Map<string, { ingresos: number; egresos: number }>();
    for (const r of registros) {
      const key = r.fecha.substring(0, 7);
      const prev = mesMap.get(key) ?? { ingresos: 0, egresos: 0 };
      if (r.tipoMovimiento === 'INGRESO') {
        mesMap.set(key, { ...prev, ingresos: prev.ingresos + r.monto });
      } else {
        mesMap.set(key, { ...prev, egresos: prev.egresos + r.monto });
      }
    }

    const mesEntries = Array.from(mesMap.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .slice(-12);

    const maxVal = Math.max(...mesEntries.flatMap(([, v]) => [v.ingresos, v.egresos]), 1);

    const evolucionMensual: MesStat[] = mesEntries.map(([key, v]) => ({
      mes: this.formatMesLabel(key),
      mesKey: key,
      ingresos: v.ingresos,
      egresos: v.egresos,
      balance: v.ingresos - v.egresos,
      pctIngreso: (v.ingresos / maxVal) * 100,
      pctEgreso: (v.egresos / maxVal) * 100,
    }));

    const mejorMes = evolucionMensual.length
      ? [...evolucionMensual].sort((a, b) => b.ingresos - a.ingresos)[0]
      : null;
    const mesMayorGasto = evolucionMensual.length
      ? [...evolucionMensual].sort((a, b) => b.egresos - a.egresos)[0]
      : null;
    const categoriaLider = topCategorias[0] ?? null;
    const mesesConActividad = mesMap.size;
    const promedioMensual = mesesConActividad > 0 ? balanceNeto / mesesConActividad : 0;

    return {
      totalRegistros: registros.length,
      totalIngresos,
      totalEgresos,
      balanceNeto,
      ratioRentabilidad,
      mesesConActividad,
      promedioMensual,
      porTipo,
      topCategorias,
      topCategoriaGastos,
      evolucionMensual,
      mejorMes,
      mesMayorGasto,
      categoriaLider,
    };
  }

  // =========== GRÁFICAS CHART.JS ===========

  private construirGraficas(): void {
    this.destruirGraficas();
    if (!this.stats.totalRegistros) return;

    this.crearGraficaEvolucion();
    this.crearGraficaDona();
    this.crearGraficaTopIngresos();
    this.crearGraficaTopGastos();
  }

  private crearGraficaEvolucion(): void {
    const ctx = this.chartEvolucionRef?.nativeElement;
    if (!ctx) return;

    const labels = this.stats.evolucionMensual.map((m) => m.mes);
    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Ingresos',
            data: this.stats.evolucionMensual.map((m) => m.ingresos),
            backgroundColor: 'rgba(25, 230, 77, 0.75)',
            borderColor: '#15c944',
            borderWidth: 2,
            borderRadius: 6,
            order: 2,
          },
          {
            label: 'Egresos',
            data: this.stats.evolucionMensual.map((m) => m.egresos),
            backgroundColor: 'rgba(239, 68, 68, 0.75)',
            borderColor: '#dc2626',
            borderWidth: 2,
            borderRadius: 6,
            order: 2,
          },
          {
            label: 'Balance neto',
            data: this.stats.evolucionMensual.map((m) => m.balance),
            type: 'line',
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245, 158, 11, 0.08)',
            borderWidth: 2.5,
            pointBackgroundColor: '#f59e0b',
            pointRadius: 4,
            fill: false,
            tension: 0.4,
            order: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: { font: { size: 12 }, padding: 16, usePointStyle: true },
          },
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.dataset.label}: ${this.formatCOP(ctx.parsed.y ?? 0)}`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: '#f0fdf4' },
            ticks: { callback: (val) => `$${Number(val).toLocaleString('es-CO')}` },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaDona(): void {
    const ctx = this.chartDonaRef?.nativeElement;
    if (!ctx) return;

    const chart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: this.stats.porTipo.map((t) => this.labelTipo(t.tipo)),
        datasets: [
          {
            data: this.stats.porTipo.map((t) => t.total),
            backgroundColor: [
              'rgba(25, 230, 77, 0.8)',
              'rgba(239, 68, 68, 0.8)',
              'rgba(251, 191, 36, 0.8)',
              'rgba(167, 139, 250, 0.8)',
            ],
            borderColor: ['#15c944', '#dc2626', '#b45309', '#7c3aed'],
            borderWidth: 2,
            hoverOffset: 8,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '65%',
        plugins: {
          legend: {
            position: 'bottom',
            labels: { font: { size: 12 }, padding: 16, usePointStyle: true },
          },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                const total = (ctx.dataset.data as number[]).reduce((a, b) => a + b, 0);
                const val = (ctx.parsed ?? 0) as number;
                const pct = total > 0 ? ((val / total) * 100).toFixed(1) : '0';
                return `${ctx.label}: ${this.formatCOP(val)} (${pct}%)`;
              },
            },
          },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaTopIngresos(): void {
    const ctx = this.chartTopIngresosRef?.nativeElement;
    if (!ctx || !this.stats.topCategorias.length) return;

    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.stats.topCategorias.map((c) => c.nombre),
        datasets: [
          {
            label: 'Total ingresos',
            data: this.stats.topCategorias.map((c) => c.total),
            backgroundColor: 'rgba(25, 230, 77, 0.75)',
            borderColor: '#15c944',
            borderWidth: 2,
            borderRadius: 8,
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
              label: (ctx) => `${this.formatCOP(ctx.parsed.x ?? 0)}`,
            },
          },
        },
        scales: {
          x: {
            beginAtZero: true,
            grid: { color: '#f0fdf4' },
            ticks: { callback: (val) => `$${Number(val).toLocaleString('es-CO')}` },
          },
          y: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaTopGastos(): void {
    const ctx = this.chartTopGastosRef?.nativeElement;
    if (!ctx || !this.stats.topCategoriaGastos.length) return;

    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.stats.topCategoriaGastos.map((c) => c.nombre),
        datasets: [
          {
            label: 'Total gastos',
            data: this.stats.topCategoriaGastos.map((c) => c.total),
            backgroundColor: 'rgba(239, 68, 68, 0.75)',
            borderColor: '#dc2626',
            borderWidth: 2,
            borderRadius: 8,
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
              label: (ctx) => `${this.formatCOP(ctx.parsed.x ?? 0)}`,
            },
          },
        },
        scales: {
          x: {
            beginAtZero: true,
            grid: { color: '#fef2f2' },
            ticks: { callback: (val) => `$${Number(val).toLocaleString('es-CO')}` },
          },
          y: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private destruirGraficas(): void {
    this.charts.forEach((c: Chart<any, any, any>) => c.destroy());
    this.charts = [];
  }

  // =============== HELPERS ===============

  private sumar(registros: RegistroFinanciero[], tipo: string): number {
    return registros.filter((r) => r.tipoMovimiento === tipo).reduce((s, r) => s + r.monto, 0);
  }

  private formatMesLabel(key: string): string {
    const [year, month] = key.split('-');
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
    return `${meses[parseInt(month) - 1]} ${year.slice(2)}`;
  }

  private statsVacias(): Stats {
    return {
      totalRegistros: 0,
      totalIngresos: 0,
      totalEgresos: 0,
      balanceNeto: 0,
      ratioRentabilidad: 0,
      mesesConActividad: 0,
      promedioMensual: 0,
      porTipo: [],
      topCategorias: [],
      topCategoriaGastos: [],
      evolucionMensual: [],
      mejorMes: null,
      mesMayorGasto: null,
      categoriaLider: null,
    };
  }

  formatCOP(valor: number | null | undefined): string {
    if (valor === null || valor === undefined) return '$ 0';
    const abs = Math.abs(valor);
    return (valor < 0 ? '-$ ' : '$ ') + new Intl.NumberFormat('es-CO').format(abs);
  }

  labelTipo(tipo: string): string {
    const map: Record<string, string> = {
      INGRESO: 'Ingreso',
      GASTO: 'Gasto',
      COSTO: 'Costo',
      INVERSION: 'Inversión',
    };
    return map[tipo] ?? tipo;
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
