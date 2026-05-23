import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

import {
  GestionReglasService,
  ReglaAdminDTO,
  VincularPracticaDTO,
} from '../../../core/services/admin/gestion-reglas.service';
import {
  GestionPracticasService,
  PracticaAdminDTO,
} from '../../../core/services/admin/gestion-practicas.service';
import { KpiService, KpiCatalogo } from '../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

type TabActivo = 'lista' | 'form';
type ModoForm = 'crear' | 'editar';

@Component({
  selector: 'app-gestion-reglas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './gestion-reglas.component.html',
  styleUrl: './gestion-reglas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GestionReglasComponent implements OnInit, OnDestroy {
  // ── Estado general ────────────────────────────────────────────────────
  tabActivo: TabActivo = 'lista';
  modoForm: ModoForm = 'crear';
  loading = false;
  guardando = false;
  error = '';
  exito = '';

  // ── Datos ─────────────────────────────────────────────────────────────
  reglas: ReglaAdminDTO[] = [];
  practicas: PracticaAdminDTO[] = [];
  kpis: KpiCatalogo[] = [];

  // ── Filtros ───────────────────────────────────────────────────────────
  filtroEstado: string = '';
  filtroEscala: string = '';
  filtroKpi: string = '';
  filtroBusqueda: string = '';

  reglaSeleccionada: ReglaAdminDTO | null = null;

  // ── Formulario ────────────────────────────────────────────────────────
  form = {
    idKpi: null as number | null,
    operador: 'MENOR_QUE',
    umbral1: null as number | null,
    umbral2: null as number | null,
    umbralTipo: 'ABSOLUTO',
    estadoKpiObjetivo: 'CRITICO',
    escalaAplicable: 'TODAS',
    mensaje: '',
    prioridad: null as number | null,
    estado: 'ACTIVA',
  };

  // Prácticas seleccionadas para la regla
  practicasSeleccionadas: VincularPracticaDTO[] = [];

  // ── Opciones de selectores ────────────────────────────────────────────
  readonly operadores = [
    { valor: 'MENOR_QUE', label: 'Menor que (<)' },
    { valor: 'MAYOR_QUE', label: 'Mayor que (>)' },
    { valor: 'ENTRE', label: 'Entre (umbral1 y umbral2)' },
    { valor: 'MENOR_PCT_PROMEDIO', label: 'Menor que % del promedio' },
    { valor: 'MAYOR_PCT_PROMEDIO', label: 'Mayor que % del promedio' },
    { valor: 'MAYOR_PCT_TOP', label: 'Mayor que % del top' },
  ];

  readonly umbralTipos = [
    { valor: 'ABSOLUTO', label: 'Absoluto (valor real)' },
    { valor: 'PCT_PROMEDIO', label: 'Porcentaje del promedio' },
    { valor: 'PCT_TOP', label: 'Porcentaje del top' },
  ];

  readonly escalas = ['TODAS', 'PEQUEÑA', 'MEDIANA', 'GRANDE', 'EMPRESARIAL'];

  readonly estadosKpi = [
    { valor: 'CRITICO', label: 'Crítico' },
    { valor: 'ACEPTABLE', label: 'Aceptable' },
  ];

  readonly categorias = [
    { valor: 'PRODUCTIVIDAD', label: '🥛 Productividad' },
    { valor: 'HATO', label: '🐄 Hato' },
    { valor: 'FINANCIERO', label: '💰 Financiero' },
    { valor: 'EFICIENCIA', label: '⚙️ Eficiencia' },
  ];

  filtroCatPractica = '';

  private destroy$ = new Subject<void>();

  constructor(
    private reglasService: GestionReglasService,
    private practicasService: GestionPracticasService,
    private kpiService: KpiService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarTodo();
  }

  // ── Carga inicial ─────────────────────────────────────────────────────

  private cargarTodo(): void {
    this.loading = true;
    this.cdr.markForCheck();

    // Cargar KPIs catálogo
    this.kpiService.getCatalogo().subscribe({
      next: (kpis) => {
        this.kpis = kpis;
        this.cdr.markForCheck();
      },
    });

    // Cargar prácticas para el selector
    this.practicasService.getPracticas('ACTIVA').subscribe({
      next: (practicas) => {
        this.practicas = practicas;
        this.cdr.markForCheck();
      },
    });

    // Cargar reglas
    this.cargarReglas();
  }

  categoriaDesdeKpi(codigoKpi: string): string {
    const kpi = this.kpis.find((k) => k.codigo === codigoKpi);
    return kpi?.categoria ?? '';
  }

  setFiltroCatPractica(valor: string): void {
    this.filtroCatPractica = valor;
    this.cdr.markForCheck();
  }

  cargarReglas(): void {
    this.loading = true;
    this.reglasService
      .getReglas(this.filtroEstado || undefined, this.filtroEscala || undefined)
      .subscribe({
        next: (reglas) => {
          this.reglas = reglas;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al cargar las reglas.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Filtros ───────────────────────────────────────────────────────────

  onFiltroEstadoChange(v: string): void {
    this.filtroEstado = v;
    this.cargarReglas();
  }

  onFiltroEscalaChange(v: string): void {
    this.filtroEscala = v;
    this.cargarReglas();
  }

  onFiltroBusquedaChange(v: string): void {
    this.filtroBusqueda = v.toLowerCase().trim();
    this.cdr.markForCheck();
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroEscala = '';
    this.filtroBusqueda = '';
    this.cargarReglas();
  }

  // ── Lista filtrada ────────────────────────────────────────────────────

  get reglasFiltradas(): ReglaAdminDTO[] {
    if (!this.filtroBusqueda) return this.reglas;
    return this.reglas.filter(
      (r) =>
        (r.nombreKpi ?? '').toLowerCase().includes(this.filtroBusqueda) ||
        (r.codigoKpi ?? '').toLowerCase().includes(this.filtroBusqueda) ||
        (r.mensaje ?? '').toLowerCase().includes(this.filtroBusqueda) ||
        r.escalaAplicable.toLowerCase().includes(this.filtroBusqueda),
    );
  }

  // ── Formulario ────────────────────────────────────────────────────────

  abrirCrear(): void {
    this.modoForm = 'crear';
    this.reglaSeleccionada = null;
    this.resetForm();
    this.tabActivo = 'form';
    this.cdr.markForCheck();
  }

  abrirEditar(regla: ReglaAdminDTO): void {
    this.modoForm = 'editar';
    this.reglaSeleccionada = regla;

    this.form = {
      idKpi: regla.idKpi,
      operador: regla.operador,
      umbral1: regla.umbral1,
      umbral2: regla.umbral2,
      umbralTipo: regla.umbralTipo,
      estadoKpiObjetivo: regla.estadoKpiObjetivo,
      escalaAplicable: regla.escalaAplicable,
      mensaje: regla.mensaje ?? '',
      prioridad: regla.prioridad,
      estado: regla.estado,
    };

    // Cargar prácticas vinculadas
    this.practicasSeleccionadas = regla.practicas.map((p) => ({
      idPractica: p.idPractica,
      orden: p.orden,
    }));

    this.tabActivo = 'form';
    this.cdr.markForCheck();
  }

  cancelarForm(): void {
    this.tabActivo = 'lista';
    this.resetForm();
    this.error = '';
    this.exito = '';
    this.cdr.markForCheck();
  }

  private resetForm(): void {
    this.form = {
      idKpi: null,
      operador: 'MENOR_QUE',
      umbral1: null,
      umbral2: null,
      umbralTipo: 'ABSOLUTO',
      estadoKpiObjetivo: 'CRITICO',
      escalaAplicable: 'TODAS',
      mensaje: '',
      prioridad: null,
      estado: 'ACTIVA',
    };
    this.practicasSeleccionadas = [];
    this.filtroCatPractica = '';
  }

  // ── Prácticas en el form ──────────────────────────────────────────────

  get practicasDisponibles(): PracticaAdminDTO[] {
    if (!this.filtroCatPractica) return this.practicas;
    return this.practicas.filter((p) => p.categoria === this.filtroCatPractica);
  }

  isPracticaSeleccionada(idPractica: number): boolean {
    return this.practicasSeleccionadas.some((p) => p.idPractica === idPractica);
  }

  togglePractica(idPractica: number): void {
    const idx = this.practicasSeleccionadas.findIndex((p) => p.idPractica === idPractica);

    if (idx !== -1) {
      this.practicasSeleccionadas.splice(idx, 1);
      this.practicasSeleccionadas = this.practicasSeleccionadas.map((p, i) => ({
        ...p,
        orden: (i + 1) as unknown as number,
      }));
    } else {
      const siguienteOrden = this.practicasSeleccionadas.length + 1;
      this.practicasSeleccionadas.push({
        idPractica,
        orden: siguienteOrden,
      });
    }
    this.cdr.markForCheck();
  }

  moverArriba(idx: number): void {
    if (idx === 0) return;
    const temp = this.practicasSeleccionadas[idx];
    this.practicasSeleccionadas[idx] = this.practicasSeleccionadas[idx - 1];
    this.practicasSeleccionadas[idx - 1] = temp;
    this.recalcularOrdenes();
    this.cdr.markForCheck();
  }

  moverAbajo(idx: number): void {
    if (idx === this.practicasSeleccionadas.length - 1) return;
    const temp = this.practicasSeleccionadas[idx];
    this.practicasSeleccionadas[idx] = this.practicasSeleccionadas[idx + 1];
    this.practicasSeleccionadas[idx + 1] = temp;
    this.recalcularOrdenes();
    this.cdr.markForCheck();
  }

  private recalcularOrdenes(): void {
    this.practicasSeleccionadas = this.practicasSeleccionadas.map((p, i) => ({
      ...p,
      orden: i + 1,
    }));
  }

  nombrePractica(idPractica: number): string {
    return this.practicas.find((p) => p.idPractica === idPractica)?.nombre ?? '—';
  }

  // ── Guardar ───────────────────────────────────────────────────────────

  guardar(): void {
    if (!this.form.idKpi) {
      this.error = 'Debes seleccionar un KPI.';
      this.cdr.markForCheck();
      return;
    }
    if (this.form.umbral1 === null) {
      this.error = 'Debes ingresar el umbral principal.';
      this.cdr.markForCheck();
      return;
    }

    this.guardando = true;
    this.error = '';
    this.exito = '';
    this.cdr.markForCheck();

    if (this.modoForm === 'crear') {
      this.reglasService
        .crearRegla({
          idKpi: this.form.idKpi,
          operador: this.form.operador,
          umbral1: this.form.umbral1,
          umbral2: this.form.umbral2,
          umbralTipo: this.form.umbralTipo,
          estadoKpiObjetivo: this.form.estadoKpiObjetivo,
          escalaAplicable: this.form.escalaAplicable,
          mensaje: this.form.mensaje || null,
          prioridad: this.form.prioridad,
          practicas: this.practicasSeleccionadas,
        })
        .subscribe({
          next: () => {
            this.exito = '✅ Regla creada correctamente.';
            this.guardando = false;
            this.cargarReglas();
            this.cancelarForm();
            this.cdr.markForCheck();
          },
          error: () => {
            this.error = 'Error al crear la regla.';
            this.guardando = false;
            this.cdr.markForCheck();
          },
        });
    } else {
      this.reglasService
        .editarRegla(this.reglaSeleccionada!.idRegla, {
          operador: this.form.operador,
          umbral1: this.form.umbral1,
          umbral2: this.form.umbral2,
          umbralTipo: this.form.umbralTipo,
          estadoKpiObjetivo: this.form.estadoKpiObjetivo,
          escalaAplicable: this.form.escalaAplicable,
          mensaje: this.form.mensaje || null,
          prioridad: this.form.prioridad,
          estado: this.form.estado,
          practicas: this.practicasSeleccionadas,
        })
        .subscribe({
          next: () => {
            this.exito = '✅ Regla actualizada correctamente.';
            this.guardando = false;
            this.cargarReglas();
            this.cancelarForm();
            this.cdr.markForCheck();
          },
          error: () => {
            this.error = 'Error al editar la regla.';
            this.guardando = false;
            this.cdr.markForCheck();
          },
        });
    }
  }

  // ── Desactivar ────────────────────────────────────────────────────────

  desactivar(regla: ReglaAdminDTO, event: Event): void {
    event.stopPropagation();
    if (!confirm(`¿Desactivar la regla "${regla.nombreKpi}"?`)) return;

    this.reglasService.desactivarRegla(regla.idRegla).subscribe({
      next: () => {
        this.exito = '✅ Regla desactivada.';
        this.cargarReglas();
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al desactivar la regla.';
        this.cdr.markForCheck();
      },
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  get necesitaUmbral2(): boolean {
    return this.form.operador === 'ENTRE';
  }

  get kpiSeleccionadoNombre(): string {
    return this.kpis.find((k) => k.idKpi === this.form.idKpi)?.nombre ?? '—';
  }

  get kpisPorCategoria(): Record<string, KpiCatalogo[]> {
    const grupos: Record<string, KpiCatalogo[]> = {};
    for (const kpi of this.kpis) {
      const cat = kpi.categoria ?? 'OTROS';
      if (!grupos[cat]) grupos[cat] = [];
      grupos[cat].push(kpi);
    }
    return grupos;
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

  labelCondicion(regla: ReglaAdminDTO): string {
    const op: Record<string, string> = {
      MENOR_QUE: '<',
      MAYOR_QUE: '>',
      ENTRE: 'entre',
      MENOR_PCT_PROMEDIO: '< % promedio',
      MAYOR_PCT_PROMEDIO: '> % promedio',
      MAYOR_PCT_TOP: '> % top',
    };
    const opLabel = op[regla.operador] ?? regla.operador;
    if (regla.operador === 'ENTRE') {
      return `${opLabel} ${regla.umbral1} y ${regla.umbral2}`;
    }
    return `${opLabel} ${regla.umbral1}`;
  }

  get hayFiltrosActivos(): boolean {
    return !!(this.filtroEstado || this.filtroEscala || this.filtroBusqueda);
  }

  onFormChange(campo: string, valor: any): void {
    (this.form as any)[campo] = valor;
    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
