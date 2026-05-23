import {
  Component,
  Input,
  OnChanges,
  OnInit,
  Output,
  EventEmitter,
  SimpleChanges,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  RankingPorKpiDTO,
  HatoRankingItem,
} from '../../../../../core/services/benchmarking/ranking.service';
import { KpiResultado } from '../../../../../core/services/kpi/kpi.service';

@Component({
  selector: 'app-ranking-por-kpi',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ranking-por-kpi.component.html',
  styleUrl: './ranking-por-kpi.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RankingPorKpiComponent implements OnChanges {
  @Input() datos: RankingPorKpiDTO | null = null;
  @Input() kpisDisponibles: KpiResultado[] = [];
  @Input() codigoSeleccionado: string = '';
  @Input() region: string | null = null;
  @Input() cargando = false;

  @Output() kpiCambiado = new EventEmitter<string>();
  @Output() regionCambiada = new EventEmitter<string | null>();

  readonly PAGE_SIZE = 10;
  paginaActual = 1;
  itemsMostrados: HatoRankingItem[] = [];

  filtroBusqueda = '';

  readonly categorias = [
    { valor: 'TODOS', label: 'Todos' },
    { valor: 'PRODUCTIVIDAD', label: '🥛 Productividad' },
    { valor: 'HATO', label: '🐄 Hato' },
    { valor: 'FINANCIERO', label: '💰 Financiero' },
    { valor: 'EFICIENCIA', label: '⚙️ Eficiencia' },
  ];
  filtroCategoria = 'TODOS';

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['datos']) {
      this.paginaActual = 1;
      this.actualizarPagina();
    }
  }

  private actualizarPagina(): void {
    if (!this.datos?.ranking) {
      this.itemsMostrados = [];
      return;
    }
    this.itemsMostrados = this.datos.ranking.slice(0, this.paginaActual * this.PAGE_SIZE);
    this.cdr.markForCheck();
  }

  cargarMas(): void {
    this.paginaActual++;
    this.actualizarPagina();
  }

  get hayMas(): boolean {
    if (!this.datos?.ranking) return false;
    return this.itemsMostrados.length < this.datos.ranking.length;
  }

  // ── Selector de KPI ────────────────────────────────────────────────────────

  get kpisFiltrados(): KpiResultado[] {
    let lista = this.kpisDisponibles;
    if (this.filtroCategoria !== 'TODOS') {
      lista = lista.filter((k) => k.categoria === this.filtroCategoria);
    }
    if (this.filtroBusqueda.trim()) {
      const q = this.filtroBusqueda.trim().toLowerCase();
      lista = lista.filter((k) => k.nombre.toLowerCase().includes(q));
    }
    return lista;
  }

  get kpiActual(): KpiResultado | null {
    return this.kpisDisponibles.find((k) => k.codigo === this.codigoSeleccionado) ?? null;
  }

  seleccionarKpi(codigo: string): void {
    if (codigo === this.codigoSeleccionado) return;
    this.paginaActual = 1;
    this.kpiCambiado.emit(codigo);
  }

  setFiltroCategoria(cat: string): void {
    this.filtroCategoria = cat;
    this.cdr.markForCheck();
  }

  // ── Helpers visuales ───────────────────────────────────────────────────────

  get percentilMiHato(): number {
    if (!this.datos || this.datos.totalHatos <= 1) return 0;
    return (
      ((this.datos.totalHatos - this.datos.posicionMiHato) / (this.datos.totalHatos - 1)) * 100
    );
  }

  get valorMaxRanking(): number {
    if (!this.datos?.ranking?.length) return 1;
    return Math.max(...this.datos.ranking.map((i) => i.valor ?? 0));
  }

  anchoBarra(item: HatoRankingItem): number {
    if (this.valorMaxRanking === 0) return 0;
    return Math.min(((item.valor ?? 0) / this.valorMaxRanking) * 100, 100);
  }

  colorBarra(item: HatoRankingItem): string {
    if (item.esMiHato) return '#19e64d';
    const pct = this.anchoBarra(item);
    if (pct >= 75) return '#15803d';
    if (pct >= 50) return '#3b82f6';
    if (pct >= 25) return '#f59e0b';
    return '#ef4444';
  }

  medallaTop3(posicion: number): string {
    if (posicion === 1) return '🥇';
    if (posicion === 2) return '🥈';
    return '🥉';
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

  estadoKpi(estado: string): string {
    const map: Record<string, string> = {
      OPTIMO: 'Óptimo',
      ACEPTABLE: 'Aceptable',
      CRITICO: 'Crítico',
      SIN_DATOS: 'Sin datos',
    };
    return map[estado] ?? estado;
  }

  formatValor(valor: number | null, unidad: string): string {
    if (valor === null) return '—';
    if (unidad?.includes('COP')) {
      return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(valor));
    }
    if (unidad === '%') return valor.toFixed(1) + '%';
    if (unidad === 'ratio' || unidad === 'veces') return valor.toFixed(2) + 'x';
    if (Number.isInteger(valor)) return valor.toString() + ' ' + unidad;
    return valor.toFixed(1) + ' ' + (unidad ?? '');
  }

  labelRegion(): string {
    return this.region ? `Región: ${this.region}` : 'Nacional';
  }
}
