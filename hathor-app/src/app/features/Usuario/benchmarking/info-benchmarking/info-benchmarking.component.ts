import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, firstValueFrom } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import { KpiService, KpiResultado } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-info-benchmarking',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './info-benchmarking.component.html',
  styleUrl: './info-benchmarking.component.css',
})
export class InfoBenchmarkingComponent implements OnInit, OnDestroy {
  loading = true;
  calculando = false;
  error = '';
  hatoActivo: Hato | null = null;
  kpis: KpiResultado[] = [];
  filtroCategoria = 'TODOS';
  fechaUltimoCalculo = '';

  readonly categorias = [
    { valor: 'TODOS', label: 'Todos' },
    { valor: 'PRODUCTIVIDAD', label: '🥛 Productividad' },
    { valor: 'HATO', label: '🐄 Hato' },
    { valor: 'FINANCIERO', label: '💰 Financiero' },
    { valor: 'EFICIENCIA', label: '⚙️ Eficiencia' },
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private kpiService: KpiService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarKpis(hato.idHato);
      } else {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
    this.cargarHatoInicial();
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

  private cargarKpis(idHato: string): void {
    this.loading = true;
    this.kpiService.getKpis(idHato).subscribe({
      next: (kpis) => {
        this.kpis = kpis;
        if (kpis.length > 0 && kpis[0].fechaCalculo) {
          this.fechaUltimoCalculo = kpis[0].fechaCalculo;
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  async recalcular(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;
    this.calculando = true;
    this.error = '';
    this.kpiService.invalidarCache();
    try {
      const resultado = await firstValueFrom(this.kpiService.calcularKpis(this.hatoActivo.idHato));
      this.kpis = resultado;
      if (this.kpis.length > 0) {
        this.fechaUltimoCalculo = this.kpis[0].fechaCalculo;
      }
    } catch {
      this.error = 'Error al calcular los KPIs. Intenta de nuevo.';
    } finally {
      this.calculando = false;
      this.cdr.markForCheck();
    }
  }

  verDetalle(codigo: string): void {
    this.router.navigate(['/benchmarking/kpi', codigo]);
  }

  setFiltro(categoria: string): void {
    this.filtroCategoria = categoria;
    this.cdr.markForCheck();
  }

  get kpisFiltrados(): KpiResultado[] {
    if (this.filtroCategoria === 'TODOS') return this.kpis;
    return this.kpis.filter((k) => k.categoria === this.filtroCategoria);
  }

  get kpisPorCategoria(): Record<string, KpiResultado[]> {
    const grupos: Record<string, KpiResultado[]> = {};
    const lista = this.filtroCategoria === 'TODOS' ? this.kpis : this.kpisFiltrados;
    for (const kpi of lista) {
      if (!grupos[kpi.categoria]) grupos[kpi.categoria] = [];
      grupos[kpi.categoria].push(kpi);
    }
    return grupos;
  }

  get categoriasConDatos(): string[] {
    return Object.keys(this.kpisPorCategoria);
  }

  get resumenEstados(): Record<string, number> {
    const r: Record<string, number> = { OPTIMO: 0, ACEPTABLE: 0, CRITICO: 0, SIN_DATOS: 0 };
    for (const k of this.kpis) r[k.estado] = (r[k.estado] ?? 0) + 1;
    return r;
  }

  get hayKpis(): boolean {
    return this.kpis.length > 0;
  }

  iconoCategoria(cat: string): string {
    const map: Record<string, string> = {
      PRODUCTIVIDAD: '🥛',
      HATO: '🐄',
      FINANCIERO: '💰',
      EFICIENCIA: '⚙️',
    };
    return map[cat] ?? '📊';
  }

  labelEstado(estado: string): string {
    const map: Record<string, string> = {
      OPTIMO: 'Óptimo',
      ACEPTABLE: 'Aceptable',
      CRITICO: 'Crítico',
      SIN_DATOS: 'Sin datos',
    };
    return map[estado] ?? estado;
  }

  formatValor(kpi: KpiResultado): string {
    if (kpi.valor === null || kpi.valor === undefined) return '—';
    const v = kpi.valor;
    const unidad = kpi.unidad ?? '';

    if (
      unidad.includes('COP') ||
      unidad === 'COP/vaca' ||
      unidad === 'COP/L' ||
      unidad === 'COP/ha/año'
    ) {
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(v));
    }
    if (unidad === '%') return v.toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return v.toFixed(2) + 'x';
    if (Number.isInteger(v)) return v.toString() + ' ' + unidad;
    return v.toFixed(1) + ' ' + unidad;
  }

  formatDiferencia(pct: number | null): string {
    if (pct === null) return '';
    const signo = pct >= 0 ? '+' : '';
    return `${signo}${pct.toFixed(1)}% vs promedio`;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
