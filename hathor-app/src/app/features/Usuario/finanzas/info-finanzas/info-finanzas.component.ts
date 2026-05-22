import { Component, OnInit, OnDestroy, ChangeDetectorRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil, combineLatest } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import {
  RegistroFinancieroService,
  RegistroFinanciero,
} from '../../../../core/services/finanzas/registro-financiero.service';
import {
  PerfilFinancieroService,
  PerfilFinanciero,
} from '../../../../core/services/finanzas/perfil-financiero.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-info-finanzas',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './info-finanzas.component.html',
  styleUrl: './info-finanzas.component.css',
})
export class InfoFinanzasComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  registros: RegistroFinanciero[] = [];
  historicos: RegistroFinanciero[] = [];
  perfiles: PerfilFinanciero[] = [];
  loading = false;

  @Input() modoLectura = false;

  filtroTipo: string = 'TODOS';
  searchQuery: string = '';
  perfilExpandido: string | null = null;

  sortColumna: string = 'fecha';
  sortDireccion: 'asc' | 'desc' = 'desc';

  paginaActual: number = 1;
  registrosPorPagina: number = 20;

  registroAEliminar: RegistroFinanciero | null = null;
  eliminando = false;

  periodoDesde: string = '';
  periodoHasta: string = '';
  registrosPeriodo: RegistroFinanciero[] = [];
  loadingPeriodo = false;
  mesesDisponibles: { valor: string; label: string }[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private registroService: RegistroFinancieroService,
    private perfilService: PerfilFinancieroService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato) {
        this.cargarDatos(hato.idHato as string);
      } else {
        this.registros = [];
        this.perfiles = [];
      }
      this.cdr.markForCheck();
    });
  }

  // ------------------- PERIODO -----------------

  private generarMesesDisponibles(): void {
    const meses: { valor: string; label: string }[] = [];
    const inicio = new Date(2025, 9, 1);
    const fin = new Date();
    fin.setMonth(fin.getMonth() + 1);

    const fmt = new Intl.DateTimeFormat('es-CO', {
      month: 'short',
      year: 'numeric',
    });

    let cursor = new Date(inicio);
    while (cursor <= fin) {
      const valor = `${cursor.getFullYear()}-${String(cursor.getMonth() + 1).padStart(2, '0')}`;
      meses.push({
        valor,
        label: fmt.format(cursor).replace(/^(.)/, (c) => c.toUpperCase()),
      });
      cursor.setMonth(cursor.getMonth() + 1);
    }
    this.mesesDisponibles = meses;

    this.periodoDesde = meses[0].valor;
    this.periodoHasta = meses[meses.length - 2].valor;
  }

  // Cargar por período:
  cargarPorPeriodo(idHato: string): void {
    if (!this.periodoDesde || !this.periodoHasta) return;
    this.loadingPeriodo = true;
    this.cdr.markForCheck();

    this.registroService
      .getRegistrosPorPeriodo(idHato, this.periodoDesde, this.periodoHasta)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (registros) => {
          this.registrosPeriodo = registros;
          this.loadingPeriodo = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loadingPeriodo = false;
          this.cdr.markForCheck();
        },
      });
  }

  private cargarDatos(idHato: string): void {
    this.loading = true;
    this.generarMesesDisponibles();
    this.cdr.markForCheck();

    combineLatest([
      this.registroService.getRegistrosReales(idHato),
      this.registroService.getRegistrosHistoricos(idHato),
      this.perfilService.getPerfilesByHato(idHato),
    ])
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ([reales, historicos, perfiles]) => {
          this.registros = reales;
          this.historicos = historicos;
          this.perfiles = perfiles;
          this.loading = false;
          this.cdr.markForCheck();
          this.cargarPorPeriodo(idHato);
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  onPeriodoChange(): void {
    if (this.hatoActivo?.idHato) {
      this.cargarPorPeriodo(this.hatoActivo.idHato as string);
    }
  }

  // ------------ GETTER PERIODO -----------
  get totalIngresosPeriodo(): number {
    return this.registrosPeriodo
      .filter((r) => r.tipoMovimiento === 'INGRESO')
      .reduce((sum, r) => sum + r.monto, 0);
  }

  get totalEgresosPeriodo(): number {
    return this.registrosPeriodo
      .filter((r) => r.tipoMovimiento !== 'INGRESO')
      .reduce((sum, r) => sum + r.monto, 0);
  }

  get balanceNetoPeriodo(): number {
    return this.totalIngresosPeriodo - this.totalEgresosPeriodo;
  }

  // Indicador de fuente del período:
  get fuentePeriodo(): 'REAL' | 'HISTORICO' | 'MIXTO' | 'VACIO' {
    if (this.registrosPeriodo.length === 0) return 'VACIO';
    const tieneReales = this.registrosPeriodo.some((r) => !r.esHistorico);
    const tieneHistoricos = this.registrosPeriodo.some((r) => r.esHistorico);
    if (tieneReales && tieneHistoricos) return 'MIXTO';
    if (tieneReales) return 'REAL';
    return 'HISTORICO';
  }

  get labelFuentePeriodo(): string {
    const map: Record<string, string> = {
      REAL: '✅ Datos reales',
      HISTORICO: '🗂️ Datos históricos',
      MIXTO: '⚡ Datos mixtos (reales + históricos)',
      VACIO: '— Sin datos',
    };
    return map[this.fuentePeriodo] ?? '—';
  }

  get claseFuentePeriodo(): string {
    const map: Record<string, string> = {
      REAL: 'fuente-real',
      HISTORICO: 'fuente-historico',
      MIXTO: 'fuente-mixto',
      VACIO: 'fuente-vacio',
    };
    return map[this.fuentePeriodo] ?? '';
  }

  get registrosFiltrados(): RegistroFinanciero[] {
    let lista = this.registrosPeriodo.filter((r) => !r.esHistorico); // ← solo reales

    if (this.filtroTipo !== 'TODOS') {
      lista = lista.filter((r) => r.tipoMovimiento === this.filtroTipo);
    }

    const q = this.searchQuery.trim().toLowerCase();
    if (q) {
      lista = lista.filter((r) => {
        const enTitulo = r.titulo?.toLowerCase().includes(q);
        const enCategoria = r.categoriaFinanciera?.nombre?.toLowerCase().includes(q);
        return enTitulo || enCategoria;
      });
    }

    return lista;
  }

  get registrosOrdenados(): RegistroFinanciero[] {
    const lista = [...this.registrosFiltrados];
    const dir = this.sortDireccion === 'asc' ? 1 : -1;

    lista.sort((a, b) => {
      switch (this.sortColumna) {
        case 'fecha':
          return dir * (new Date(a.fecha).getTime() - new Date(b.fecha).getTime());
        case 'titulo':
          return dir * (a.titulo ?? '').localeCompare(b.titulo ?? '', 'es');
        case 'categoria':
          return (
            dir *
            (a.categoriaFinanciera?.nombre ?? '').localeCompare(
              b.categoriaFinanciera?.nombre ?? '',
              'es',
            )
          );
        case 'tipo':
          return dir * (a.tipoMovimiento ?? '').localeCompare(b.tipoMovimiento ?? '');
        case 'monto':
          return dir * (a.monto - b.monto);
        default:
          return 0;
      }
    });

    return lista;
  }

  get historicosPorMes(): { mes: string; label: string; registros: RegistroFinanciero[] }[] {
    const grupos = new Map<string, RegistroFinanciero[]>();

    for (const r of this.historicos) {
      const mes = r.fecha.substring(0, 7);
      if (!grupos.has(mes)) grupos.set(mes, []);
      grupos.get(mes)!.push(r);
    }

    return Array.from(grupos.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([mes, registros]) => ({
        mes,
        label: new Intl.DateTimeFormat('es-CO', {
          month: 'long',
          year: 'numeric',
        })
          .format(new Date(mes + '-01T12:00:00'))
          .replace(/^(.)/, (c) => c.toUpperCase()),
        registros,
      }));
  }

  // ===== PAGINACIÓN ======
  get registrosPaginados(): RegistroFinanciero[] {
    const inicio = (this.paginaActual - 1) * this.registrosPorPagina;
    return this.registrosOrdenados.slice(inicio, inicio + this.registrosPorPagina);
  }

  get totalPaginas(): number {
    return Math.ceil(this.registrosOrdenados.length / this.registrosPorPagina);
  }

  get paginas(): number[] {
    const total = this.totalPaginas;
    if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);

    const paginas: number[] = [1];
    if (this.paginaActual > 3) paginas.push(-1);
    for (
      let i = Math.max(2, this.paginaActual - 1);
      i <= Math.min(total - 1, this.paginaActual + 1);
      i++
    ) {
      paginas.push(i);
    }
    if (this.paginaActual < total - 2) paginas.push(-1);
    paginas.push(total);
    return paginas;
  }

  ordenarPor(columna: string): void {
    if (this.sortColumna === columna) {
      this.sortDireccion = this.sortDireccion === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumna = columna;
      this.sortDireccion = 'asc';
    }
    this.paginaActual = 1;
    this.cdr.markForCheck();
  }

  irAPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
    this.cdr.markForCheck();
  }

  getSortIcon(columna: string): string {
    if (this.sortColumna !== columna) return '↕';
    return this.sortDireccion === 'asc' ? '↑' : '↓';
  }

  setFiltroTipo(tipo: string): void {
    this.filtroTipo = tipo;
    this.paginaActual = 1;
    this.cdr.markForCheck();
  }

  onSearchChange(): void {
    this.paginaActual = 1;
    this.cdr.markForCheck();
  }

  // ===== KPIs =====
  get totalIngresos(): number {
    return this.registros
      .filter((r) => r.tipoMovimiento === 'INGRESO')
      .reduce((sum, r) => sum + r.monto, 0);
  }

  get totalEgresos(): number {
    return this.registros
      .filter((r) => r.tipoMovimiento !== 'INGRESO')
      .reduce((sum, r) => sum + r.monto, 0);
  }
  // ===== ELIMINAR =====
  solicitarEliminacion(registro: RegistroFinanciero): void {
    this.registroAEliminar = registro;
  }

  cancelarEliminacion(): void {
    if (this.eliminando) return;
    this.registroAEliminar = null;
  }

  confirmarEliminacion(): void {
    if (!this.registroAEliminar || this.eliminando) return;

    const id = this.registroAEliminar.idRegistro;
    this.eliminando = true;
    this.cdr.markForCheck();

    this.registroService.eliminarRegistro(id).subscribe({
      next: () => {
        this.registros = this.registros.filter((r) => r.idRegistro !== id);
        this.eliminando = false;
        this.registroAEliminar = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.eliminando = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ===== PERFILES =====
  togglePerfil(id: string): void {
    this.perfilExpandido = this.perfilExpandido === id ? null : id;
  }

  // ===== HELPERS =====
  formatCOP(valor: number | null | undefined): string {
    if (valor === null || valor === undefined) return '$ 0';
    return '$ ' + new Intl.NumberFormat('es-CO').format(valor);
  }

  labelTipo(tipo: string): string {
    const map: Record<string, string> = {
      INGRESO: 'Ingreso',
      GASTO: 'Gasto',
      INVERSION: 'Inversión',
    };
    return map[tipo] ?? tipo;
  }

  labelMetodo(metodo: string): string {
    const map: Record<string, string> = {
      EXCEL: '📊 Excel',
      MANUAL: '✏️ Manual',
      OMITIDO: '— Omitido',
    };
    return map[metodo] ?? metodo;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
