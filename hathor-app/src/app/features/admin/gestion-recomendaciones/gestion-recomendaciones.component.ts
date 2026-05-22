import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil, distinctUntilChanged } from 'rxjs';

import {
  GestionRecomendacionesService,
  RecomendacionAdminDTO,
  CrearRecomendacionDTO,
  CambiarEstadoDTO,
} from '../../../core/services/admin/gestion-recomendaciones.service';
import {
  AdminStateService,
  HatoAdminSeleccionado,
} from '../../../core/services/ui-state/admin-state.service';
import { AdminService } from '../../../core/services/admin/admin.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

type TabActivo = 'lista' | 'form';

@Component({
  selector: 'app-gestion-recomendaciones',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './gestion-recomendaciones.component.html',
  styleUrl: './gestion-recomendaciones.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GestionRecomendacionesComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  tabActivo: TabActivo = 'lista';
  loading = false;
  guardando = false;
  error = '';
  exito = '';

  hatoActivo: HatoAdminSeleccionado | null = null;
  recomendaciones: RecomendacionAdminDTO[] = [];

  // ── Filtros ───────────────────────────────────────────────────────────
  filtroEstado: string = '';
  filtroPrioridad: string = '';
  filtroBusqueda: string = '';

  // ── Formulario ────────────────────────────────────────────────────────
  form: CrearRecomendacionDTO = {
    idHato: '',
    tipo: null,
    mensaje: '',
    indicador: null,
    valorActual: null,
    valorReferencia: null,
    prioridad: 'MEDIA',
    idRegla: null,
  };

  readonly prioridades = [
    { valor: 'ALTA', label: '🔴 Alta', clase: 'prio-alta' },
    { valor: 'MEDIA', label: '🟡 Media', clase: 'prio-media' },
    { valor: 'BAJA', label: '🟢 Baja', clase: 'prio-baja' },
  ];

  readonly estados = [
    { valor: 'ACTIVA', label: '✅ Activa' },
    { valor: 'DESCARTADA', label: '🚫 Descartada' },
    { valor: 'COMPLETADA', label: '🏆 Completada' },
  ];

  readonly tipos = [
    'MEJORA_KPI',
    'ALERTA_CRITICA',
    'BUENAS_PRACTICAS',
    'EFICIENCIA',
    'FINANCIERO',
    'MANUAL',
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private recomendacionesService: GestionRecomendacionesService,
    private adminState: AdminStateService,
    private adminService: AdminService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Suscribirse al hato seleccionado del navbar admin
    this.adminState
      .getHatoSeleccionado$()
      .pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged((a, b) => a?.idHato === b?.idHato),
      )
      .subscribe((hato) => {
        this.hatoActivo = hato;
        if (hato) {
          this.form.idHato = hato.idHato;
          this.cargarRecomendaciones();
        } else {
          this.recomendaciones = [];
        }
        this.cdr.markForCheck();
      });
  }

  // ── Carga ─────────────────────────────────────────────────────────────

  cargarRecomendaciones(): void {
    if (!this.hatoActivo?.idHato) return;

    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.recomendacionesService
      .getFiltradas(
        this.hatoActivo.idHato,
        this.filtroEstado || undefined,
        this.filtroPrioridad || undefined,
      )
      .subscribe({
        next: (data) => {
          this.recomendaciones = data;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al cargar las recomendaciones.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Filtros ───────────────────────────────────────────────────────────

  onFiltroEstadoChange(v: string): void {
    this.filtroEstado = v;
    this.cargarRecomendaciones();
  }

  onFiltroPrioridadChange(v: string): void {
    this.filtroPrioridad = v;
    this.cargarRecomendaciones();
  }

  onBusquedaChange(v: string): void {
    this.filtroBusqueda = v.toLowerCase().trim();
    this.cdr.markForCheck();
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroPrioridad = '';
    this.filtroBusqueda = '';
    this.cargarRecomendaciones();
  }

  get recomendacionesFiltradas(): RecomendacionAdminDTO[] {
    if (!this.filtroBusqueda) return this.recomendaciones;
    return this.recomendaciones.filter(
      (r) =>
        (r.mensaje ?? '').toLowerCase().includes(this.filtroBusqueda) ||
        (r.indicador ?? '').toLowerCase().includes(this.filtroBusqueda) ||
        (r.tipo ?? '').toLowerCase().includes(this.filtroBusqueda),
    );
  }

  get hayFiltrosActivos(): boolean {
    return !!(this.filtroEstado || this.filtroPrioridad || this.filtroBusqueda);
  }

  // ── Contadores por estado ─────────────────────────────────────────────

  get countActivas(): number {
    return this.recomendaciones.filter((r) => r.tipoEstado === 'ACTIVA').length;
  }

  get countDescartadas(): number {
    return this.recomendaciones.filter((r) => r.tipoEstado === 'DESCARTADA').length;
  }

  get countCompletadas(): number {
    return this.recomendaciones.filter((r) => r.tipoEstado === 'COMPLETADA').length;
  }

  // ── Formulario ────────────────────────────────────────────────────────

  abrirForm(): void {
    this.resetForm();
    this.tabActivo = 'form';
    this.error = '';
    this.exito = '';
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
      idHato: this.hatoActivo?.idHato ?? '',
      tipo: null,
      mensaje: '',
      indicador: null,
      valorActual: null,
      valorReferencia: null,
      prioridad: 'MEDIA',
      idRegla: null,
    };
  }

  onFormChange(campo: string, valor: any): void {
    (this.form as any)[campo] = valor || null;
    this.cdr.markForCheck();
  }

  onFormChangeStr(campo: string, valor: string): void {
    (this.form as any)[campo] = valor;
    this.cdr.markForCheck();
  }

  // ── Guardar ───────────────────────────────────────────────────────────

  guardar(): void {
    if (!this.form.mensaje?.trim()) {
      this.error = 'El mensaje de la recomendación es obligatorio.';
      this.cdr.markForCheck();
      return;
    }
    if (!this.form.idHato) {
      this.error = 'Selecciona un hato desde el navbar superior.';
      this.cdr.markForCheck();
      return;
    }

    this.guardando = true;
    this.error = '';
    this.cdr.markForCheck();

    this.recomendacionesService.crearRecomendacion(this.form).subscribe({
      next: () => {
        this.exito = '✅ Recomendación creada correctamente.';
        this.guardando = false;
        this.cargarRecomendaciones();
        this.cancelarForm();
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al crear la recomendación.';
        this.guardando = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Cambiar estado ────────────────────────────────────────────────────

  cambiarEstado(
    rec: RecomendacionAdminDTO,
    nuevoEstado: 'ACTIVA' | 'DESCARTADA' | 'COMPLETADA',
    event: Event,
  ): void {
    event.stopPropagation();

    const dto: CambiarEstadoDTO = { tipoEstado: nuevoEstado };

    this.recomendacionesService.cambiarEstado(rec.idRecomendacion, dto).subscribe({
      next: () => {
        this.exito = `✅ Estado cambiado a ${nuevoEstado}.`;
        this.cargarRecomendaciones();
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al cambiar el estado.';
        this.cdr.markForCheck();
      },
    });
  }

  // ── Eliminar ──────────────────────────────────────────────────────────

  eliminar(rec: RecomendacionAdminDTO, event: Event): void {
    event.stopPropagation();
    if (!confirm('¿Eliminar esta recomendación permanentemente?')) return;

    this.recomendacionesService.eliminar(rec.idRecomendacion).subscribe({
      next: () => {
        this.exito = '✅ Recomendación eliminada.';
        this.cargarRecomendaciones();
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al eliminar la recomendación.';
        this.cdr.markForCheck();
      },
    });
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  clasePrioridad(prioridad: string): string {
    const map: Record<string, string> = {
      ALTA: 'prio-alta',
      MEDIA: 'prio-media',
      BAJA: 'prio-baja',
    };
    return map[prioridad] ?? '';
  }

  claseEstado(estado: string): string {
    const map: Record<string, string> = {
      ACTIVA: 'estado-activa',
      DESCARTADA: 'estado-descartada',
      COMPLETADA: 'estado-completada',
    };
    return map[estado] ?? '';
  }

  labelEstado(estado: string): string {
    const map: Record<string, string> = {
      ACTIVA: '✅ Activa',
      DESCARTADA: '🚫 Descartada',
      COMPLETADA: '🏆 Completada',
    };
    return map[estado] ?? estado;
  }

  iconoPrioridad(prioridad: string): string {
    const map: Record<string, string> = {
      ALTA: '🔴',
      MEDIA: '🟡',
      BAJA: '🟢',
    };
    return map[prioridad] ?? '⚪';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
