import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

import {
  BenchmarkGlobalDTO,
  BenchmarkingService,
  KpiResumenGlobalDTO,
} from '../../../../../core/services/benchmarking/benchmarking.service';

import { MapaHatosComponent } from './mapa-hatos/mapa-hatos.component';
import { GraficaBarrasGlobalComponent } from './grafica-barras-global/grafica-barras-global.component';
import { GraficaPosicionComponent } from './grafica-posicion/grafica-posicion.component';

interface GrupoCategoria {
  config: { categoria: string; label: string; icono: string };
  kpis: KpiResumenGlobalDTO[];
}

@Component({
  selector: 'app-benchmark-global',
  standalone: true,
  imports: [
    CommonModule,
    MapaHatosComponent,
    GraficaBarrasGlobalComponent,
    GraficaPosicionComponent,
  ],
  templateUrl: './benchmark-global.component.html',
  styleUrl: './benchmark-global.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BenchmarkGlobalComponent implements OnInit, OnChanges, OnDestroy {
  @Input() idHato!: string;

  // ── Estado de filtros ─────────────────────────────────────
  filtroTropico = true;
  filtroEscala = false;
  filtroRegion = false;
  cantidad: 5 | 10 | 15 = 10;

  // ── Estado de datos ───────────────────────────────────────
  datos: BenchmarkGlobalDTO | null = null;
  loading = false;
  error = '';

  kpiSeleccionado: KpiResumenGlobalDTO | null = null;

  readonly opcionesCantidad: { valor: 5 | 10 | 15; label: string }[] = [
    { valor: 5, label: 'Top 5' },
    { valor: 10, label: 'Top 10' },
    { valor: 15, label: 'Top 15' },
  ];

  readonly categoriasConfig = [
    { categoria: 'PRODUCTIVIDAD', label: 'Productividad', icono: '🥛' },
    { categoria: 'HATO', label: 'Hato', icono: '🐄' },
    { categoria: 'FINANCIERO', label: 'Financiero', icono: '💰' },
    { categoria: 'EFICIENCIA', label: 'Eficiencia', icono: '⚙️' },
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private benchmarkingService: BenchmarkingService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    if (this.idHato) this.cargar();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['idHato'] && !changes['idHato'].firstChange) {
      this.cargar();
    }
  }

  // ── Métodos de filtros ────────────────────────────────────

  toggleTropico(): void {
    this.filtroTropico = !this.filtroTropico;
    this.cargar();
  }

  toggleEscala(): void {
    this.filtroEscala = !this.filtroEscala;
    this.cargar();
  }

  toggleRegion(): void {
    this.filtroRegion = !this.filtroRegion;
    this.cargar();
  }

  setCantidad(c: 5 | 10 | 15): void {
    if (this.cantidad === c) return;
    this.cantidad = c;
    this.cargar();
  }

  seleccionarKpi(kpi: KpiResumenGlobalDTO): void {
    this.kpiSeleccionado = kpi;
    this.cdr.markForCheck();
  }

  // ── Getters de datos ──────────────────────────────────────

  get tieneResultados(): boolean {
    return (this.datos?.kpisResumen?.length ?? 0) > 0;
  }

  get tieneMapa(): boolean {
    return (this.datos?.hatosEnMapa?.length ?? 0) > 0;
  }

  get kpisPorCategoria(): GrupoCategoria[] {
    if (!this.datos?.kpisResumen) return [];
    return this.categoriasConfig
      .map((config) => ({
        config,
        kpis: this.datos!.kpisResumen.filter((k) => k.categoria === config.categoria),
      }))
      .filter((g) => g.kpis.length > 0);
  }

  get resumenEstados(): { optimo: number; bueno: number; aceptable: number; critico: number } {
    const kpis = this.datos?.kpisResumen ?? [];
    return {
      optimo: kpis.filter((k) => k.interpretacion === 'OPTIMO').length,
      bueno: kpis.filter((k) => k.interpretacion === 'BUENO').length,
      aceptable: kpis.filter((k) => k.interpretacion === 'ACEPTABLE').length,
      critico: kpis.filter((k) => k.interpretacion === 'CRITICO').length,
    };
  }

  get filtrosActivosTexto(): string[] {
    const f: string[] = [];
    if (this.filtroTropico && this.datos?.filtrosAplicados?.tropico)
      f.push(`Trópico: ${this.datos.filtrosAplicados.tropico}`);
    if (this.filtroEscala && this.datos?.filtrosAplicados?.escala)
      f.push(`Escala: ${this.datos.filtrosAplicados.escala}`);
    if (this.filtroRegion && this.datos?.filtrosAplicados?.region)
      f.push(`Región: ${this.datos.filtrosAplicados.region}`);
    return f;
  }

  // ── Helpers de formato ────────────────────────────────────

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

  colorPorInterpretacion(interp: string | null): string {
    const map: Record<string, string> = {
      OPTIMO: '#15c944',
      BUENO: '#3b82f6',
      ACEPTABLE: '#f59e0b',
      CRITICO: '#ef4444',
    };
    return map[interp ?? ''] ?? '#9ca3af';
  }

  trackByKpi(_: number, k: KpiResumenGlobalDTO): string {
    return k.codigoKpi;
  }

  // ── Carga de datos ────────────────────────────────────────

  private cargar(): void {
    if (!this.idHato) return;

    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.benchmarkingService
      .getBenchmarkGlobal(
        this.idHato,
        this.filtroTropico,
        this.filtroEscala,
        this.filtroRegion,
        this.cantidad,
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resultado) => {
          this.datos = resultado;
          this.loading = false;

          // Seleccionar primer KPI por defecto para las gráficas
          if (resultado.kpisResumen?.length > 0 && !this.kpiSeleccionado) {
            this.kpiSeleccionado = resultado.kpisResumen[0];
          } else if (this.kpiSeleccionado) {
            const actualizado = resultado.kpisResumen.find(
              (k) => k.codigoKpi === this.kpiSeleccionado!.codigoKpi,
            );
            this.kpiSeleccionado = actualizado ?? resultado.kpisResumen[0] ?? null;
          }

          this.cdr.markForCheck();
        },
        error: () => {
          this.loading = false;
          this.error = 'No se pudo cargar el benchmark global.';
          this.cdr.markForCheck();
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
