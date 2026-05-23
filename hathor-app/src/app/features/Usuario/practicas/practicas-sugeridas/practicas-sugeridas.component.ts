import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import {
  EstadoPractica,
  HatoPracticaDTO,
  PracticasService,
} from '../../../../core/services/practicas/practicas.service';
import { KpiService, KpiResultado } from '../../../../core/services/kpi/kpi.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { Router } from '@angular/router';

// Avance pasos
import { AvancePasosComponent } from '../../../../shared/avance-pasos/avance-pasos.component';
import { HatoPracticaPasoResponseDTO } from '../../../../core/services/practicas/practicas.service';

// AI ASISTENTE
import {
  AsistenteService,
  PracticaGeneradaDTO,
  PracticaIAResponseDTO,
} from '../../../../core/services/asistente/asistente.service';

@Component({
  selector: 'app-practicas-sugeridas',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent, AvancePasosComponent],
  templateUrl: './practicas-sugeridas.component.html',
  styleUrl: './practicas-sugeridas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PracticasSugeridasComponent implements OnInit, OnDestroy {
  @Input() modoLectura = false;

  // ── Estado ────────────────────────────────────────────────────────────
  hatoActivo: Hato | null = null;
  practicas: HatoPracticaDTO[] = [];
  loading = false;
  loadingKpis = false;
  error = '';

  // ── Filtros ───────────────────────────────────────────────────────────
  filtroEstado: 'TODOS' | EstadoPractica = 'TODOS';
  searchQuery = '';

  // ── Avance inline ─────────────────────────────────────────────────────
  // idHatoPractica de la práctica cuyo slider está abierto
  practicaAvanceAbierta: string | null = null;
  avanceEditando = 0;
  guardandoAvance = false;

  // ── Acción de estado ──────────────────────────────────────────────────
  avanzandoEstado: string | null = null;

  // Propiedades - avance pasos
  overlayAvanceVisible = false;
  practicaAvanceActual: HatoPracticaDTO | null = null;
  pasosTextoActual: string[] = [];
  cargandoPasos = false;
  private destroy$ = new Subject<void>();

  // ── Generación IA ─────────────────────────────────────────────────────
  modalIAVisible = false;
  kpisCriticos: KpiResultado[] = [];
  kpiSeleccionado: string | null = null;
  generando = false;
  confirmando = false;
  practicaPreview: PracticaGeneradaDTO | null = null;
  errorIA = '';

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private practicasService: PracticasService,
    private router: Router,
    private kpiService: KpiService,
    private asistenteService: AsistenteService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarTodo(hato.idHato);
      } else {
        this.practicas = [];
      }
      this.cdr.markForCheck();
    });

    // Cargar hato inicial si no hay uno activo
    if (!this.hatoState.getHatoActivo()) {
      this.hatoService.getMisHatos().subscribe({
        next: (hatos) => {
          if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
          this.cdr.markForCheck();
        },
      });
    }
  }

  // ── Carga ─────────────────────────────────────────────────────────────

  private cargarTodo(idHato: string): void {
    this.loading = true;
    this.loadingKpis = true;
    this.error = '';
    this.cdr.markForCheck();

    // Cargar prácticas
    this.practicasService
      .getPracticasByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.practicas = data;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.practicas = [];
          this.loading = false;
          this.error = 'Error al cargar las prácticas.';
          this.cdr.markForCheck();
        },
      });

    // Cargar KPIs para el caché — permite mostrar
    // el valor actual del KPI impactado en cada práctica
    this.kpiService.getKpis(idHato).subscribe({
      next: () => {
        this.loadingKpis = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingKpis = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Acción de estado ──────────────────────────────────────────────────

  avanzarEstado(practica: HatoPracticaDTO): void {
    const siguiente = this.getSiguienteEstado(practica.estado);
    if (!siguiente || this.modoLectura) return;

    this.avanzandoEstado = practica.idHatoPractica;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarEstado(practica.idHatoPractica, siguiente)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          this.practicas = this.practicas.map((p) =>
            p.idHatoPractica === actualizada.idHatoPractica ? actualizada : p,
          );
          this.avanzandoEstado = null;
          // Cerrar slider si estaba abierto
          if (this.practicaAvanceAbierta === practica.idHatoPractica) {
            this.practicaAvanceAbierta = null;
          }
          this.cdr.markForCheck();
        },
        error: () => {
          this.avanzandoEstado = null;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Avance inline ─────────────────────────────────────────────────────

  abrirSliderAvance(practica: HatoPracticaDTO): void {
    if (this.modoLectura) return;
    this.practicaAvanceAbierta = practica.idHatoPractica;
    this.avanceEditando = practica.porcentajeAvance ?? 0;
    this.cdr.markForCheck();
  }

  cerrarSliderAvance(): void {
    this.practicaAvanceAbierta = null;
    this.cdr.markForCheck();
  }

  guardarAvance(practica: HatoPracticaDTO): void {
    if (this.modoLectura) return;
    this.guardandoAvance = true;
    this.cdr.markForCheck();

    this.practicasService
      .actualizarAvance(practica.idHatoPractica, this.avanceEditando)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizada) => {
          this.practicas = this.practicas.map((p) =>
            p.idHatoPractica === actualizada.idHatoPractica ? actualizada : p,
          );
          this.practicaAvanceAbierta = null;
          this.guardandoAvance = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.guardandoAvance = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── KPI helpers ───────────────────────────────────────────────────────

  getKpiDePractica(codigoKpi: string): KpiResultado | null {
    if (!codigoKpi) return null;
    return this.kpiService.getKpiDesdeCache(codigoKpi);
  }

  colorEstadoKpi(estado: string | undefined): string {
    if (!estado) return 'kpi-sin-datos';
    const map: Record<string, string> = {
      OPTIMO: 'kpi-optimo',
      ACEPTABLE: 'kpi-aceptable',
      CRITICO: 'kpi-critico',
      SIN_DATOS: 'kpi-sin-datos',
    };
    return map[estado] ?? 'kpi-sin-datos';
  }

  iconoEstadoKpi(estado: string | undefined): string {
    const map: Record<string, string> = {
      OPTIMO: '✅',
      ACEPTABLE: '🟡',
      CRITICO: '🔴',
      SIN_DATOS: '—',
    };
    return map[estado ?? ''] ?? '—';
  }

  // ── Filtros ───────────────────────────────────────────────────────────

  get practicasFiltradas(): HatoPracticaDTO[] {
    let lista = [...this.practicas];

    if (this.filtroEstado !== 'TODOS') {
      lista = lista.filter((p) => p.estado === this.filtroEstado);
    }

    const q = this.searchQuery.trim().toLowerCase();
    if (q) {
      lista = lista.filter(
        (p) =>
          p.nombrePractica.toLowerCase().includes(q) ||
          p.categoria.toLowerCase().includes(q) ||
          p.kpiImpactado.toLowerCase().includes(q),
      );
    }

    return lista;
  }

  setFiltroEstado(estado: 'TODOS' | EstadoPractica): void {
    this.filtroEstado = estado;
    this.cdr.markForCheck();
  }

  onBusquedaChange(): void {
    this.cdr.markForCheck();
  }

  // ── Getters stats ─────────────────────────────────────────────────────

  get totalPendientes(): number {
    return this.practicas.filter((p) => p.estado === 'PENDIENTE').length;
  }

  get totalEnCurso(): number {
    return this.practicas.filter((p) => p.estado === 'EN_CURSO').length;
  }

  get totalCompletadas(): number {
    return this.practicas.filter((p) => p.estado === 'COMPLETADA').length;
  }

  get promedioAvance(): number {
    if (!this.practicas.length) return 0;
    const suma = this.practicas.reduce((acc, p) => acc + (p.porcentajeAvance ?? 0), 0);
    return Math.round(suma / this.practicas.length);
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  verDetalle(practica: HatoPracticaDTO): void {
    this.router.navigate(['/practicas/detalle', practica.idHatoPractica], { state: { practica } });
  }

  estadoLabel(estado: EstadoPractica): string {
    const map: Record<EstadoPractica, string> = {
      PENDIENTE: 'Pendiente',
      EN_CURSO: 'En curso',
      COMPLETADA: 'Completada',
    };
    return map[estado];
  }

  claseEstado(estado: EstadoPractica): string {
    const map: Record<EstadoPractica, string> = {
      PENDIENTE: 'estado-pendiente',
      EN_CURSO: 'estado-curso',
      COMPLETADA: 'estado-completada',
    };
    return map[estado];
  }

  iconoCategoria(categoria: string): string {
    const map: Record<string, string> = {
      PRODUCTIVIDAD: '🥛',
      HATO: '🐄',
      FINANCIERO: '💰',
      EFICIENCIA: '⚙️',
    };
    return map[categoria] ?? '🌱';
  }

  colorDificultad(dificultad: string): string {
    const map: Record<string, string> = {
      BAJA: 'dif-baja',
      MEDIA: 'dif-media',
      ALTA: 'dif-alta',
    };
    return map[dificultad] ?? '';
  }

  botonEstadoLabel(estado: EstadoPractica): string {
    if (estado === 'PENDIENTE') return '▶ Iniciar';
    if (estado === 'EN_CURSO') return '✓ Completar';
    return 'Finalizada';
  }

  private getSiguienteEstado(estado: EstadoPractica): EstadoPractica | null {
    if (estado === 'PENDIENTE') return 'EN_CURSO';
    if (estado === 'EN_CURSO') return 'COMPLETADA';
    return null;
  }

  // Métodos - avance pasos
  abrirOverlayAvance(practica: HatoPracticaDTO): void {
    this.practicaAvanceActual = practica;
    this.cargandoPasos = true;
    this.cdr.markForCheck();

    this.practicasService.getDetalle(practica.idPractica).subscribe({
      next: (detalle) => {
        this.pasosTextoActual = detalle.pasos ?? [];
        this.overlayAvanceVisible = true;
        this.cargandoPasos = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.pasosTextoActual = [];
        this.overlayAvanceVisible = true;
        this.cargandoPasos = false;
        this.cdr.markForCheck();
      },
    });
  }

  cerrarOverlayAvance(): void {
    this.overlayAvanceVisible = false;
    this.practicaAvanceActual = null;
    this.pasosTextoActual = [];
    this.cdr.markForCheck();
  }

  onAvanceActualizado(response: HatoPracticaPasoResponseDTO): void {
    if (!this.practicaAvanceActual) return;
    const id = this.practicaAvanceActual.idHatoPractica;
    this.practicas = this.practicas.map((p) =>
      p.idHatoPractica === id
        ? { ...p, porcentajeAvance: response.porcentajeAvance, estado: response.estado as any }
        : p,
    );
    this.cdr.markForCheck();
  }

  // ── Generación IA ─────────────────────────────────────────────────────

  abrirModalIA(): void {
    // Obtener KPIs críticos del caché
    this.kpisCriticos = this.kpiService
      .getTodosDesdeCache()
      .filter((k) => k.estado === 'CRITICO' || k.estado === 'ACEPTABLE');

    this.kpiSeleccionado = this.kpisCriticos.length > 0 ? this.kpisCriticos[0].codigo : null;

    this.practicaPreview = null;
    this.errorIA = '';
    this.modalIAVisible = true;
    this.cdr.markForCheck();
  }

  cerrarModalIA(): void {
    this.modalIAVisible = false;
    this.practicaPreview = null;
    this.kpiSeleccionado = null;
    this.errorIA = '';
    this.cdr.markForCheck();
  }

  generarPrevisualizacion(): void {
    if (!this.hatoActivo?.idHato || !this.kpiSeleccionado) return;

    this.generando = true;
    this.practicaPreview = null;
    this.errorIA = '';
    this.cdr.markForCheck();

    this.asistenteService
      .generarPractica(
        this.hatoActivo.idHato as string,
        this.kpiSeleccionado,
        false, // solo preview
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: PracticaIAResponseDTO) => {
          this.practicaPreview = res.practicaGenerada;
          this.generando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorIA =
            err.status === 409
              ? 'Esta práctica ya está asignada a tu hato.'
              : 'Error generando la práctica. Intenta de nuevo.';
          this.generando = false;
          this.cdr.markForCheck();
        },
      });
  }

  confirmarPractica(): void {
    if (!this.hatoActivo?.idHato || !this.kpiSeleccionado) return;

    this.confirmando = true;
    this.errorIA = '';
    this.cdr.markForCheck();

    this.asistenteService
      .generarPractica(
        this.hatoActivo.idHato as string,
        this.kpiSeleccionado,
        true, // guardar en BD
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.confirmando = false;
          this.cerrarModalIA();
          // Recargar prácticas para mostrar la nueva
          this.cargarTodo(this.hatoActivo!.idHato as string);
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorIA =
            err.status === 409
              ? 'Esta práctica ya está asignada a tu hato.'
              : 'Error al guardar la práctica. Intenta de nuevo.';
          this.confirmando = false;
          this.cdr.markForCheck();
        },
      });
  }

  labelKpi(codigo: string): string {
    const kpi = this.kpiService.getKpiDesdeCache(codigo);
    return kpi ? kpi.nombre : codigo;
  }

  iconoEstadoIA(estado: string): string {
    const map: Record<string, string> = {
      CRITICO: '🔴',
      ACEPTABLE: '🟡',
    };
    return map[estado] ?? '📊';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
