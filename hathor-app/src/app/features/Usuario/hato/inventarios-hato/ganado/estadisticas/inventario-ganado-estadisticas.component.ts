import {
  Component,
  OnDestroy,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  AfterViewInit,
  ViewChild,
  ElementRef,
  OnChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { Chart, registerables } from 'chart.js';

import { Hato } from '../../../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../../../core/services/ui-state/hato-state.service';
import {
  InventarioGanado,
  InventarioGanadoService,
} from '../../../../../../core/services/inventario/inventario-ganado.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

import {
  ValorReferenciaGanadoService,
  ValorReferenciaGanado,
} from '../../../../../../core/services/inventario/valor-referencia-ganado.service';

Chart.register(...registerables);

interface TipoStat {
  tipo: string;
  cantidad: number;
  pct: number;
}

interface CategoriaStat {
  categoria: string;
  total: number;
  pct: number;
}

interface RazaValorStat {
  raza: string;
  valor: number;
  cantidad: number;
}

interface EdadGrupoStat {
  label: string;
  edadActual: number;
}

interface ComparativaValorStat {
  raza: string;
  categoria: string;
  valorUsuario: number;
  valorMercado: number | null;
  diferenciaCOP: number | null;
  diferenciaPct: number | null;
  estado: 'SOBRE_MERCADO' | 'EN_MERCADO' | 'BAJO_MERCADO' | 'SIN_REFERENCIA';
}

@Component({
  selector: 'app-inventario-ganado-estadisticas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './inventario-ganado-estadisticas.component.html',
  styleUrl: './inventario-ganado-estadisticas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventarioGanadoEstadisticasComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('chartDona') chartDonaRef!: ElementRef;
  @ViewChild('chartCategorias') chartCategoriasRef!: ElementRef;
  @ViewChild('chartValor') chartValorRef!: ElementRef;
  @ViewChild('chartEdad') chartEdadRef!: ElementRef;

  readonly Math = Math;

  hatoActivo: Hato | null = null;
  loading = false;
  error = '';
  registros: InventarioGanado[] = [];
  cargandoReferencias = true;

  valoresReferencia: ValorReferenciaGanado[] = [];

  private chartDona: Chart | null = null;
  private chartCategorias: Chart | null = null;
  private chartValor: Chart | null = null;
  private chartEdad: Chart | null = null;
  private destroy$ = new Subject<void>();
  private viewIniciado = false;

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
    private inventarioService: InventarioGanadoService,
    private valorReferenciaService: ValorReferenciaGanadoService,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargar(hato.idHato);
      } else {
        this.registros = [];
        this.destruirGraficas();
        this.cdr.markForCheck();
      }
    });
    this.valorReferenciaService
      .getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (valores) => {
          this.valoresReferencia = valores;
          this.cargandoReferencias = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.cargandoReferencias = false;
          this.cdr.markForCheck();
        },
      });
  }

  ngAfterViewInit(): void {
    this.viewIniciado = true;
    if (this.registros.length > 0) {
      this.renderizarGraficas();
    }
  }

  // ── Getters KPI cards ─────────────────────────────────────

  get valorTotal(): number {
    return this.registros.reduce((acc, r) => acc + r.valorTotal, 0);
  }

  get cabezasTotales(): number {
    return this.registros.reduce((acc, r) => acc + r.cantidad, 0);
  }

  get edadPromedioPonderada(): number {
    const totalCabezas = this.cabezasTotales;
    if (!totalCabezas) return 0;
    const suma = this.registros.reduce(
      (acc, r) => acc + this.calcularEdadActual(r.edadPromedioMeses, r.fechaRegistro) * r.cantidad,
      0,
    );
    return suma / totalCabezas;
  }

  get tipoLider(): string {
    return this.topTiposRaza[0]?.tipo ?? 'Sin datos';
  }

  get valorPromedioPorCabeza(): number {
    if (!this.cabezasTotales) return 0;
    return this.valorTotal / this.cabezasTotales;
  }

  // ── Getters para gráficas ─────────────────────────────────

  get topTiposRaza(): TipoStat[] {
    const map = new Map<string, number>();
    this.registros.forEach((r) =>
      map.set(r.raza.tipoRaza, (map.get(r.raza.tipoRaza) ?? 0) + r.cantidad),
    );
    const raw = Array.from(map.entries()).map(([tipo, cantidad]) => ({ tipo, cantidad }));
    const max = raw.reduce((acc, r) => Math.max(acc, r.cantidad), 0);
    return raw
      .sort((a, b) => b.cantidad - a.cantidad)
      .map((r) => ({
        tipo: r.tipo,
        cantidad: r.cantidad,
        pct: max ? (r.cantidad / max) * 100 : 0,
      }));
  }

  get topCategoriasValor(): CategoriaStat[] {
    const map = new Map<string, number>();
    this.registros.forEach((r) =>
      map.set(
        r.categoriaGanado.nombreCategoria,
        (map.get(r.categoriaGanado.nombreCategoria) ?? 0) + r.valorTotal,
      ),
    );
    const raw = Array.from(map.entries()).map(([categoria, total]) => ({ categoria, total }));
    const max = raw.reduce((acc, r) => Math.max(acc, r.total), 0);
    return raw
      .sort((a, b) => b.total - a.total)
      .slice(0, 6)
      .map((r) => ({
        categoria: r.categoria,
        total: r.total,
        pct: max ? (r.total / max) * 100 : 0,
      }));
  }

  get razasValor(): RazaValorStat[] {
    const map = new Map<string, { valor: number; cantidad: number }>();
    this.registros.forEach((r) => {
      const actual = map.get(r.raza.nombre) ?? { valor: 0, cantidad: 0 };
      actual.valor += r.valorTotal;
      actual.cantidad += r.cantidad;
      map.set(r.raza.nombre, actual);
    });
    return Array.from(map.entries())
      .map(([raza, data]) => ({
        raza,
        valor: data.valor,
        cantidad: data.cantidad,
      }))
      .sort((a, b) => b.valor - a.valor)
      .slice(0, 6);
  }

  get edadPorGrupo(): EdadGrupoStat[] {
    return this.registros
      .map((r) => ({
        label: `${r.raza.nombre} (${r.categoriaGanado.nombreCategoria})`,
        edadActual: this.calcularEdadActual(r.edadPromedioMeses, r.fechaRegistro),
      }))
      .sort((a, b) => b.edadActual - a.edadActual);
  }

  get comparativaValores(): ComparativaValorStat[] {
    return this.registros.map((r) => {
      const referencia = this.valoresReferencia.find(
        (v) =>
          v.raza.idRaza === r.raza.idRaza &&
          v.categoriaGanado.idCategoria === r.categoriaGanado.idCategoria,
      );

      if (!referencia) {
        return {
          raza: r.raza.nombre,
          categoria: r.categoriaGanado.nombreCategoria,
          valorUsuario: r.valorUnitario,
          valorMercado: null,
          diferenciaCOP: null,
          diferenciaPct: null,
          estado: 'SIN_REFERENCIA' as const,
        };
      }

      const diferenciaCOP = r.valorUnitario - referencia.valorPromedio;
      const diferenciaPct = (diferenciaCOP / referencia.valorPromedio) * 100;

      let estado: ComparativaValorStat['estado'];
      if (diferenciaPct > 5) estado = 'SOBRE_MERCADO';
      else if (diferenciaPct < -5) estado = 'BAJO_MERCADO';
      else estado = 'EN_MERCADO';

      return {
        raza: r.raza.nombre,
        categoria: r.categoriaGanado.nombreCategoria,
        valorUsuario: r.valorUnitario,
        valorMercado: referencia.valorPromedio,
        diferenciaCOP,
        diferenciaPct,
        estado,
      };
    });
  }

  get totalSobreMercado(): number {
    return this.comparativaValores.filter((c) => c.estado === 'SOBRE_MERCADO').length;
  }
  get totalEnMercado(): number {
    return this.comparativaValores.filter((c) => c.estado === 'EN_MERCADO').length;
  }
  get totalBajoMercado(): number {
    return this.comparativaValores.filter((c) => c.estado === 'BAJO_MERCADO').length;
  }
  // ── Edad dinámica ─────────────────────────────────────────

  calcularEdadActual(edadPromedioMeses: number, fechaRegistro: string): number {
    if (!fechaRegistro) return edadPromedioMeses;
    const registro = new Date(fechaRegistro);
    const hoy = new Date();
    const mesesTranscurridos =
      (hoy.getFullYear() - registro.getFullYear()) * 12 + (hoy.getMonth() - registro.getMonth());
    return edadPromedioMeses + Math.max(0, mesesTranscurridos);
  }

  // ── Helpers ───────────────────────────────────────────────

  formatCOP(valor: number | null | undefined): string {
    if (valor === null || valor === undefined) return '$ 0';
    return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(valor));
  }

  colorEdad(meses: number): string {
    if (meses < 12) return '#86efac';
    if (meses < 36) return '#19e64d';
    if (meses < 72) return '#15803d';
    return '#052e0f';
  }

  labelEstadoValor(estado: ComparativaValorStat['estado']): string {
    const map: Record<ComparativaValorStat['estado'], string> = {
      SOBRE_MERCADO: 'Superior al Mercado',
      EN_MERCADO: 'Igual al mercado',
      BAJO_MERCADO: 'Inferior al mercado',
      SIN_REFERENCIA: 'Sin referencia',
    };
    return map[estado];
  }

  claseEstadoValor(estado: ComparativaValorStat['estado']): string {
    const map: Record<ComparativaValorStat['estado'], string> = {
      SOBRE_MERCADO: 'estado-sobre',
      EN_MERCADO: 'estado-mercado',
      BAJO_MERCADO: 'estado-bajo',
      SIN_REFERENCIA: 'estado-sin',
    };
    return map[estado];
  }

  // ── Carga de datos ────────────────────────────────────────

  private cargar(idHato: string): void {
    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.inventarioService
      .getByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (registros) => {
          this.registros = registros;
          this.loading = false;
          this.cdr.markForCheck();

          // Renderizar después del ciclo de detección
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
    if (!this.viewIniciado || !this.registros.length) return;
    this.destruirGraficas();
    this.renderDona();
    this.renderCategorias();
    this.renderValor();
    this.renderEdad();
  }

  private renderDona(): void {
    if (!this.chartDonaRef?.nativeElement) return;
    const labels = this.topTiposRaza.map((t) => t.tipo);
    const data = this.topTiposRaza.map((t) => t.cantidad);

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
              font: { size: 12, weight: 'bold' },
              color: '#374151',
              padding: 16,
            },
          },
          tooltip: {
            callbacks: {
              label: (ctx) => ` ${ctx.label}: ${ctx.parsed} cabezas`,
            },
          },
        },
        cutout: '62%',
      },
    });
  }

  private renderCategorias(): void {
    if (!this.chartCategoriasRef?.nativeElement) return;
    const stats = this.topCategoriasValor;
    const labels = stats.map((s) => s.categoria);
    const data = stats.map((s) => s.total);

    this.chartCategorias = new Chart(this.chartCategoriasRef.nativeElement, {
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
              font: { size: 12, weight: 'bold' },
            },
          },
        },
      },
    });
  }

  private renderValor(): void {
    if (!this.chartValorRef?.nativeElement) return;
    const stats = this.razasValor;
    const labels = stats.map((s) => s.raza);

    this.chartValor = new Chart(this.chartValorRef.nativeElement, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Valor total (COP)',
            data: stats.map((s) => s.valor),
            backgroundColor: this.COLORES[1],
            borderRadius: 6,
            borderSkipped: false,
            yAxisID: 'yValor',
          },
          {
            label: 'Cabezas',
            data: stats.map((s) => s.cantidad),
            backgroundColor: this.COLORES[3],
            borderRadius: 6,
            borderSkipped: false,
            yAxisID: 'yCabezas',
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
                return ` ${ctx.parsed.y ?? 0} cabezas`;
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
          yCabezas: {
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
              font: { size: 12 },
            },
          },
        },
      },
    });
  }

  private renderEdad(): void {
    if (!this.chartEdadRef?.nativeElement) return;
    const stats = this.edadPorGrupo;
    const labels = stats.map((s) => s.label);
    const data = stats.map((s) => s.edadActual);
    const colors = data.map((m) => this.colorEdad(m));

    this.chartEdad = new Chart(this.chartEdadRef.nativeElement, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Edad actual (meses)',
            data,
            backgroundColor: colors,
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
              label: (ctx) => ` ${ctx.parsed.x} meses`,
            },
          },
        },
        scales: {
          x: {
            grid: { color: '#f0fdf4' },
            ticks: {
              callback: (v) => `${v} m`,
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
    this.chartCategorias?.destroy();
    this.chartValor?.destroy();
    this.chartEdad?.destroy();
    this.chartDona = null;
    this.chartCategorias = null;
    this.chartValor = null;
    this.chartEdad = null;
  }

  ngOnDestroy(): void {
    this.destruirGraficas();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
