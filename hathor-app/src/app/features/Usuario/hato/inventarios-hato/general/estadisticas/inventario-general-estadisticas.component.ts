import {
  Component,
  OnDestroy,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  AfterViewInit,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { Chart, registerables } from 'chart.js';

import { Hato } from '../../../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../../../core/services/ui-state/hato-state.service';
import {
  InventarioGeneral,
  InventarioGeneralService,
} from '../../../../../../core/services/inventario/inventario-general.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

interface CategoriaStat {
  categoria: string;
  total: number;
  pct: number;
  cantidad: number;
}

@Component({
  selector: 'app-inventario-general-estadisticas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './inventario-general-estadisticas.component.html',
  styleUrl: './inventario-general-estadisticas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventarioGeneralEstadisticasComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('chartDona') chartDonaRef!: ElementRef;
  @ViewChild('chartBarras') chartBarrasRef!: ElementRef;
  @ViewChild('chartItems') chartItemsRef!: ElementRef;

  hatoActivo: Hato | null = null;
  loading = false;
  error = '';
  items: InventarioGeneral[] = [];

  private chartDona: Chart | null = null;
  private chartBarras: Chart | null = null;
  private chartItems: Chart | null = null;
  private destroy$ = new Subject<void>();
  private viewIniciado = false;

  readonly Math = Math;

  private readonly COLORES = [
    '#19e64d',
    '#15803d',
    '#052e0f',
    '#86efac',
    '#4ade80',
    '#bbf7d0',
    '#166534',
    '#dcfce7',
    '#22c55e',
  ];

  constructor(
    private cdr: ChangeDetectorRef,
    private hatoState: HatoStateService,
    private inventarioService: InventarioGeneralService,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargar(hato.idHato);
      } else {
        this.items = [];
        this.destruirGraficas();
        this.cdr.markForCheck();
      }
    });
  }

  ngAfterViewInit(): void {
    this.viewIniciado = true;
    if (this.items.length > 0) {
      this.renderizarGraficas();
    }
  }

  // ── Getters KPI ───────────────────────────────────────────

  get valorTotal(): number {
    return this.items.reduce((acc, i) => acc + i.valorTotal, 0);
  }

  get promedioValorUnitario(): number {
    if (!this.items.length) return 0;
    return this.items.reduce((acc, i) => acc + i.valorUnitario, 0) / this.items.length;
  }

  get cantidadTotalUnidades(): number {
    return this.items.reduce((acc, i) => acc + i.cantidad, 0);
  }

  get categoriaLider(): string {
    return this.topCategorias[0]?.categoria ?? 'Sin datos';
  }

  // ── Getters gráficas ──────────────────────────────────────

  get topCategorias(): CategoriaStat[] {
    const map = new Map<string, { total: number; cantidad: number }>();
    this.items.forEach((i) => {
      const key = i.categoriaInventario.categoriaPadre?.nombre ?? i.categoriaInventario.nombre;
      const actual = map.get(key) ?? { total: 0, cantidad: 0 };
      actual.total += i.valorTotal;
      actual.cantidad += i.cantidad;
      map.set(key, actual);
    });
    const raw = Array.from(map.entries()).map(([categoria, data]) => ({
      categoria,
      total: data.total,
      cantidad: data.cantidad,
    }));
    const max = raw.reduce((acc, r) => Math.max(acc, r.total), 0);
    return raw
      .sort((a, b) => b.total - a.total)
      .slice(0, 6)
      .map((r) => ({
        ...r,
        pct: max ? (r.total / max) * 100 : 0,
      }));
  }

  get topItems(): InventarioGeneral[] {
    return [...this.items].sort((a, b) => b.valorTotal - a.valorTotal).slice(0, 8);
  }

  // ── Helpers ───────────────────────────────────────────────

  formatCOP(valor: number | null | undefined): string {
    if (valor === null || valor === undefined) return '$ 0';
    return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(valor));
  }

  getNombreCategoria(item: InventarioGeneral): string {
    return item.categoriaInventario.categoriaPadre?.nombre ?? item.categoriaInventario.nombre;
  }

  // ── Carga ─────────────────────────────────────────────────

  private cargar(idHato: string): void {
    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.inventarioService
      .getByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (items) => {
          this.items = items;
          this.loading = false;
          this.cdr.markForCheck();
          setTimeout(() => this.renderizarGraficas(), 100);
        },
        error: () => {
          this.error = 'No fue posible calcular estadísticas.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Gráficas ──────────────────────────────────────────────

  private renderizarGraficas(): void {
    if (!this.viewIniciado || !this.items.length) return;
    this.destruirGraficas();
    this.renderDona();
    this.renderBarras();
    this.renderItems();
  }

  private renderDona(): void {
    if (!this.chartDonaRef?.nativeElement) return;
    const stats = this.topCategorias;
    const labels = stats.map((s) => s.categoria);
    const data = stats.map((s) => s.total);

    this.chartDona = new Chart(this.chartDonaRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [
          {
            data,
            backgroundColor: this.COLORES.slice(0, labels.length),
            borderWidth: 2,
            borderColor: '#ffffff',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              font: { size: 11, weight: 'bold' },
              color: '#374151',
              padding: 14,
            },
          },
          tooltip: {
            callbacks: {
              label: (ctx) =>
                ` ${ctx.label}: $ ${new Intl.NumberFormat('es-CO').format(ctx.parsed ?? 0)}`,
            },
          },
        },
        cutout: '62%',
      },
    });
  }

  private renderBarras(): void {
    if (!this.chartBarrasRef?.nativeElement) return;
    const stats = this.topCategorias;
    const labels = stats.map((s) => s.categoria);

    this.chartBarras = new Chart(this.chartBarrasRef.nativeElement, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Valor total (COP)',
            data: stats.map((s) => s.total),
            backgroundColor: this.COLORES[1],
            borderRadius: 6,
            borderSkipped: false,
            yAxisID: 'yValor',
          },
          {
            label: 'Unidades',
            data: stats.map((s) => s.cantidad),
            backgroundColor: this.COLORES[3],
            borderRadius: 6,
            borderSkipped: false,
            yAxisID: 'yUnidades',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              font: { size: 12 },
              color: '#374151',
            },
          },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                if (ctx.datasetIndex === 0)
                  return ` $ ${new Intl.NumberFormat('es-CO').format(ctx.parsed.y ?? 0)}`;
                return ` ${ctx.parsed.y ?? 0} unidades`;
              },
            },
          },
        },
        scales: {
          yValor: {
            type: 'linear',
            position: 'left',
            grid: { color: '#f0fdf4' },
            ticks: {
              callback: (v) =>
                '$ ' + new Intl.NumberFormat('es-CO').format(Number(v) / 1000000) + 'M',
              color: '#6b7280',
              font: { size: 11 },
            },
          },
          yUnidades: {
            type: 'linear',
            position: 'right',
            grid: { display: false },
            ticks: {
              color: '#6b7280',
              font: { size: 11 },
            },
          },
          x: {
            grid: { display: false },
            ticks: {
              color: '#374151',
              font: { size: 11 },
              maxRotation: 30,
            },
          },
        },
      },
    });
  }

  private renderItems(): void {
    if (!this.chartItemsRef?.nativeElement) return;
    const top = this.topItems;
    const labels = top.map((i) => i.nombreItem);
    const data = top.map((i) => i.valorTotal);

    this.chartItems = new Chart(this.chartItemsRef.nativeElement, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Valor total (COP)',
            data,
            backgroundColor: this.COLORES[0],
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
              label: (ctx) => ` $ ${new Intl.NumberFormat('es-CO').format(ctx.parsed.x ?? 0)}`,
            },
          },
        },
        scales: {
          x: {
            grid: { color: '#f0fdf4' },
            ticks: {
              callback: (v) =>
                '$ ' + new Intl.NumberFormat('es-CO').format(Number(v) / 1000000) + 'M',
              color: '#6b7280',
              font: { size: 11 },
            },
          },
          y: {
            grid: { display: false },
            ticks: {
              color: '#374151',
              font: { size: 11 },
              maxRotation: 0,
            },
          },
        },
      },
    });
  }

  private destruirGraficas(): void {
    this.chartDona?.destroy();
    this.chartBarras?.destroy();
    this.chartItems?.destroy();
    this.chartDona = null;
    this.chartBarras = null;
    this.chartItems = null;
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
