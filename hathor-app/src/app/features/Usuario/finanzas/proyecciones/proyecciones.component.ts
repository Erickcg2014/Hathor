import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  ProyeccionesService,
  ProyeccionesResponseDTO,
  ProyeccionMensualDTO,
} from '../../../../core/services/finanzas/proyecciones.service';
import {
  InversionService,
  InversionPlaneadaDTO,
  CrearInversionDTO,
} from '../../../../core/services/finanzas/inversion.service';
import {
  CategoriaFinancieraService,
  CategoriaFinanciera,
} from '../../../../core/services/finanzas/categoria-financiera.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-proyecciones',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './proyecciones.component.html',
  styleUrl: './proyecciones.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProyeccionesComponent implements OnInit, OnDestroy {
  // ── Estado general ────────────────────────────────────────────────
  loading = true;
  error = '';
  hatoActivo: Hato | null = null;

  // ── Proyecciones ──────────────────────────────────────────────────
  respuesta: ProyeccionesResponseDTO | null = null;
  mesesSeleccionados: 3 | 6 = 6;

  // ── Inversiones planeadas ─────────────────────────────────────────
  inversiones: InversionPlaneadaDTO[] = [];
  loadingInversiones = false;

  // ── Modal agregar inversión ───────────────────────────────────────
  modalVisible = false;
  guardandoInversion = false;
  categoriasInversion: CategoriaFinanciera[] = [];
  loadingCategorias = false;

  // Formulario nueva inversión
  form: CrearInversionDTO = {
    descripcion: '',
    monto: 0,
    mesEjecucion: '',
    retornoEsperadoPct: null,
    mesesRetorno: null,
    idCategoria: null,
  };

  // Meses disponibles para el selector del formulario
  mesesDisponibles: { valor: string; label: string }[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private proyeccionesService: ProyeccionesService,
    private inversionService: InversionService,
    private categoriaService: CategoriaFinancieraService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.generarMesesDisponibles();

    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarTodo(hato.idHato as string);
      } else {
        this.loading = false;
      }
      this.cdr.markForCheck();
    });

    if (!this.hatoState.getHatoActivo()) {
      this.hatoService.getMisHatos().subscribe({
        next: (hatos) => {
          if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
          else this.loading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  // ── Carga ─────────────────────────────────────────────────────────

  private cargarTodo(idHato: string): void {
    this.cargarProyecciones(idHato);
    this.cargarInversiones(idHato);
  }

  private cargarProyecciones(idHato: string): void {
    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.proyeccionesService
      .getProyecciones(idHato, this.mesesSeleccionados)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (respuesta) => {
          this.respuesta = respuesta;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al cargar las proyecciones.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  private cargarInversiones(idHato: string): void {
    this.loadingInversiones = true;
    this.cdr.markForCheck();

    this.inversionService
      .getByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (inv) => {
          this.inversiones = inv;
          this.loadingInversiones = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loadingInversiones = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Selector de meses ─────────────────────────────────────────────

  cambiarMeses(meses: 3 | 6): void {
    this.mesesSeleccionados = meses;
    if (this.hatoActivo?.idHato) {
      this.cargarProyecciones(this.hatoActivo.idHato as string);
    }
  }

  // ── Modal inversión ───────────────────────────────────────────────

  abrirModal(): void {
    this.resetForm();
    this.modalVisible = true;
    this.cargarCategoriasInversion();
    this.cdr.markForCheck();
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.cdr.markForCheck();
  }

  private cargarCategoriasInversion(): void {
    this.loadingCategorias = true;
    this.cdr.markForCheck();

    this.categoriaService
      .getCategoriasPorTipo('INVERSION')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cats) => {
          this.categoriasInversion = cats;
          this.loadingCategorias = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loadingCategorias = false;
          this.cdr.markForCheck();
        },
      });
  }

  guardarInversion(): void {
    if (!this.hatoActivo?.idHato) return;
    if (!this.form.descripcion || !this.form.monto || !this.form.mesEjecucion) return;

    this.guardandoInversion = true;
    this.cdr.markForCheck();

    this.inversionService
      .crear(this.hatoActivo.idHato as string, this.form)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.guardandoInversion = false;
          this.modalVisible = false;
          // Recargar proyecciones e inversiones
          this.cargarTodo(this.hatoActivo!.idHato as string);
          this.cdr.markForCheck();
        },
        error: () => {
          this.guardandoInversion = false;
          this.cdr.markForCheck();
        },
      });
  }

  cancelarInversion(idInversion: number): void {
    this.inversionService
      .cancelar(idInversion)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.cargarTodo(this.hatoActivo!.idHato as string);
          this.cdr.markForCheck();
        },
      });
  }
  ejecutarInversion(inv: InversionPlaneadaDTO): void {
    if (
      !confirm(`¿Confirmas que ejecutaste la inversión
    "${inv.descripcion}" por ${this.formatCOP(inv.monto)}?`)
    )
      return;

    this.inversionService
      .actualizar(inv.idInversion, {
        estado: 'EJECUTADA',
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.cargarTodo(this.hatoActivo!.idHato as string);
          this.cdr.markForCheck();
        },
      });
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private resetForm(): void {
    this.form = {
      descripcion: '',
      monto: 0,
      mesEjecucion: this.mesesDisponibles[0]?.valor ?? '',
      retornoEsperadoPct: null,
      mesesRetorno: null,
      idCategoria: null,
    };
  }

  private generarMesesDisponibles(): void {
    const meses: { valor: string; label: string }[] = [];
    const base = new Date();
    const fmt = new Intl.DateTimeFormat('es-CO', {
      month: 'long',
      year: 'numeric',
    });
    for (let i = 1; i <= 12; i++) {
      const d = new Date(base.getFullYear(), base.getMonth() + i, 1);
      const valor = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
      meses.push({
        valor,
        label: fmt.format(d).replace(/^(.)/, (c) => c.toUpperCase()),
      });
    }
    this.mesesDisponibles = meses;
  }

  claseMargen(estado: string): string {
    const map: Record<string, string> = {
      POSITIVO: 'margen-positivo',
      NEUTRO: 'margen-neutro',
      NEGATIVO: 'margen-negativo',
    };
    return map[estado] ?? '';
  }

  iconoMargen(estado: string): string {
    const map: Record<string, string> = {
      POSITIVO: '🟢',
      NEUTRO: '🟡',
      NEGATIVO: '🔴',
    };
    return map[estado] ?? '—';
  }

  labelModelo(modelo: string): string {
    const map: Record<string, string> = {
      PROMEDIO_MOVIL: 'Promedio móvil',
      TENDENCIA_LINEAL: 'Tendencia lineal',
      ESTACIONALIDAD: 'Estacionalidad',
    };
    return map[modelo] ?? modelo;
  }

  formatCOP(valor: number | null): string {
    if (valor == null) return '—';
    return '$ ' + new Intl.NumberFormat('es-CO').format(Math.round(valor));
  }

  get tendenciaIngresosLabel(): string {
    const t = this.respuesta?.tendenciaIngresos;
    if (t == null) return '—';
    if (t > 0) return `↗ +${t.toFixed(1)}% mensual`;
    if (t < 0) return `↘ ${t.toFixed(1)}% mensual`;
    return '→ Estable';
  }

  get tendenciaEgresosLabel(): string {
    const t = this.respuesta?.tendenciaEgresos;
    if (t == null) return '—';
    if (t > 0) return `↗ +${t.toFixed(1)}% mensual`;
    if (t < 0) return `↘ ${t.toFixed(1)}% mensual`;
    return '→ Estable';
  }

  get formValido(): boolean {
    return !!(this.form.descripcion?.trim() && this.form.monto > 0 && this.form.mesEjecucion);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
