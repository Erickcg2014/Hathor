import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef,
  AfterViewInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil, firstValueFrom } from 'rxjs';
import { Chart, registerables } from 'chart.js';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  ProduccionLeche,
  ProduccionLecheService,
} from '../../../../core/services/produccion/produccion-leche.service';
import {
  VentaLeche,
  VentaLecheService,
} from '../../../../core/services/finanzas/venta-leche.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

Chart.register(...registerables);

const MESES = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

@Component({
  selector: 'app-estadisticas-produccion',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './estadisticas-produccion.component.html',
  styleUrl: './estadisticas-produccion.component.css',
})
export class EstadisticasProduccionComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('chartProduccion') chartProduccionRef!: ElementRef;
  @ViewChild('chartComparativa') chartComparativaRef!: ElementRef;
  @ViewChild('chartPrecio') chartPrecioRef!: ElementRef;
  @ViewChild('chartIngreso') chartIngresoRef!: ElementRef;

  loading = true;
  error = '';
  hatoActivo: Hato | null = null;
  produccion: ProduccionLeche[] = [];
  ventas: VentaLeche[] = [];

  private charts: Chart<any, any, any>[] = [];
  private viewReady = false;
  private dataReady = false;
  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private produccionService: ProduccionLecheService,
    private ventaService: VentaLecheService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarDatos(hato.idHato);
      } else {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
    this.cargarHatoInicial();
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.dataReady) this.construirGraficas();
  }

  private cargarHatoInicial(): void {
    if (this.hatoState.getHatoActivo()) return;
    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
        else {
          this.loading = false;
          this.cdr.markForCheck();
        }
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private async cargarDatos(idHato: string): Promise<void> {
    this.loading = true;
    this.error = '';
    this.destruirGraficas();

    try {
      const [prod, vent] = await Promise.allSettled([
        firstValueFrom(this.produccionService.getByHato(idHato)),
        firstValueFrom(this.ventaService.getByHato(idHato)),
      ]);
      this.produccion = prod.status === 'fulfilled' ? prod.value : [];
      this.ventas = vent.status === 'fulfilled' ? vent.value : [];
    } catch {
      this.error = 'Error cargando estadísticas.';
    } finally {
      this.loading = false;
      this.dataReady = true;
      this.cdr.markForCheck();
      setTimeout(() => {
        if (this.viewReady) this.construirGraficas();
      }, 100);
    }
  }

  // ===== PROCESAMIENTO DE DATOS =====

  private getUltimos6Meses(): { clave: string; etiqueta: string }[] {
    const resultado = [];
    const ahora = new Date();
    for (let i = 5; i >= 0; i--) {
      const fecha = new Date(ahora.getFullYear(), ahora.getMonth() - i, 1);
      resultado.push({
        clave: `${fecha.getFullYear()}-${fecha.getMonth()}`,
        etiqueta: `${MESES[fecha.getMonth()]} ${fecha.getFullYear()}`,
      });
    }
    return resultado;
  }

  private produccionPorMes(): number[] {
    const meses = this.getUltimos6Meses();
    return meses.map((m) => {
      return this.produccion
        .filter((r) => {
          const f = new Date(r.fecha);
          return `${f.getFullYear()}-${f.getMonth()}` === m.clave;
        })
        .reduce((acc, r) => acc + (r.litrosProducidos ?? 0), 0);
    });
  }

  private ventasPorMes(): number[] {
    const meses = this.getUltimos6Meses();
    return meses.map((m) => {
      return this.ventas
        .filter((v) => {
          const f = new Date(v.fecha);
          return `${f.getFullYear()}-${f.getMonth()}` === m.clave;
        })
        .reduce((acc, v) => acc + (v.litrosVendidos ?? 0), 0);
    });
  }

  private precioPorMes(): number[] {
    const meses = this.getUltimos6Meses();
    return meses.map((m) => {
      const ventasMes = this.ventas.filter((v) => {
        const f = new Date(v.fecha);
        return `${f.getFullYear()}-${f.getMonth()}` === m.clave;
      });
      if (!ventasMes.length) return 0;
      const totalLitros = ventasMes.reduce((acc, v) => acc + v.litrosVendidos, 0);
      const totalIngreso = ventasMes.reduce((acc, v) => acc + v.litrosVendidos * v.precioLitro, 0);
      return totalLitros > 0 ? Math.round(totalIngreso / totalLitros) : 0;
    });
  }

  private ingresoPorMes(): number[] {
    const meses = this.getUltimos6Meses();
    return meses.map((m) => {
      return this.ventas
        .filter((v) => {
          const f = new Date(v.fecha);
          return `${f.getFullYear()}-${f.getMonth()}` === m.clave;
        })
        .reduce((acc, v) => acc + v.litrosVendidos * v.precioLitro, 0);
    });
  }

  // ===== CONSTRUCCIÓN DE GRÁFICAS =====

  private construirGraficas(): void {
    const labels = this.getUltimos6Meses().map((m) => m.etiqueta);

    this.crearGraficaProduccion(labels);
    this.crearGraficaComparativa(labels);
    this.crearGraficaPrecio(labels);
    this.crearGraficaIngreso(labels);
  }

  private crearGraficaProduccion(labels: string[]): void {
    const ctx = this.chartProduccionRef?.nativeElement;
    if (!ctx) return;
    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Litros producidos',
            data: this.produccionPorMes(),
            backgroundColor: 'rgba(25, 230, 77, 0.7)',
            borderColor: '#15c944',
            borderWidth: 2,
            borderRadius: 8,
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
              label: (ctx) => `${(ctx.parsed.y ?? 0).toLocaleString('es-CO')} L`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: '#f0fdf4' },
            ticks: { callback: (val) => `${val} L` },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaComparativa(labels: string[]): void {
    const ctx = this.chartComparativaRef?.nativeElement;
    if (!ctx) return;
    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Producido',
            data: this.produccionPorMes(),
            backgroundColor: 'rgba(25, 230, 77, 0.7)',
            borderColor: '#15c944',
            borderWidth: 2,
            borderRadius: 8,
          },
          {
            label: 'Vendido',
            data: this.ventasPorMes(),
            backgroundColor: 'rgba(59, 130, 246, 0.7)',
            borderColor: '#2563eb',
            borderWidth: 2,
            borderRadius: 8,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: { font: { size: 12 }, padding: 16 },
          },
          tooltip: {
            callbacks: {
              label: (ctx) =>
                `${ctx.dataset.label}: ${(ctx.parsed.y ?? 0).toLocaleString('es-CO')} L`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: '#f0fdf4' },
            ticks: { callback: (val) => `${val} L` },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaPrecio(labels: string[]): void {
    const ctx = this.chartPrecioRef?.nativeElement;
    if (!ctx) return;
    const chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Precio promedio por litro',
            data: this.precioPorMes(),
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245, 158, 11, 0.1)',
            borderWidth: 2.5,
            pointBackgroundColor: '#f59e0b',
            pointRadius: 5,
            fill: true,
            tension: 0.4,
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
              label: (ctx) => `$${(ctx.parsed.y ?? 0).toLocaleString('es-CO')} COP/L`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: false,
            grid: { color: '#fefce8' },
            ticks: {
              callback: (val) => `$${Number(val).toLocaleString('es-CO')}`,
            },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private crearGraficaIngreso(labels: string[]): void {
    const ctx = this.chartIngresoRef?.nativeElement;
    if (!ctx) return;
    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Ingreso por ventas de leche',
            data: this.ingresoPorMes(),
            backgroundColor: 'rgba(99, 102, 241, 0.7)',
            borderColor: '#4f46e5',
            borderWidth: 2,
            borderRadius: 8,
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
              label: (ctx) => `$${(ctx.parsed.y ?? 0).toLocaleString('es-CO')} COP`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: '#f5f3ff' },
            ticks: {
              callback: (val) => `$${Number(val).toLocaleString('es-CO')}`,
            },
          },
          x: { grid: { display: false } },
        },
      },
    });
    this.charts.push(chart);
  }

  private destruirGraficas(): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];
  }

  // ===== GETTERS RESUMEN =====

  get totalProducidoHistorico(): number {
    return this.produccion.reduce((acc, r) => acc + (r.litrosProducidos ?? 0), 0);
  }

  get totalVendidoHistorico(): number {
    return this.ventas.reduce((acc, v) => acc + (v.litrosVendidos ?? 0), 0);
  }

  get ingresoTotalHistorico(): number {
    return this.ventas.reduce((acc, v) => acc + v.litrosVendidos * v.precioLitro, 0);
  }

  get hayDatos(): boolean {
    return this.produccion.length > 0 || this.ventas.length > 0;
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
