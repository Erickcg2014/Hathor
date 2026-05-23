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
  GestionPracticasService,
  PracticaAdminDTO,
  CrearPracticaDTO,
  EditarPracticaDTO,
} from '../../../core/services/admin/gestion-practicas.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

type TabActivo = 'lista' | 'form';
type ModoForm = 'crear' | 'editar';

@Component({
  selector: 'app-gestion-practicas',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './gestion-practicas.component.html',
  styleUrl: './gestion-practicas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GestionPracticasComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  tabActivo: TabActivo = 'lista';
  modoForm: ModoForm = 'crear';
  loading = false;
  guardando = false;
  error = '';
  exito = '';

  // ── Datos ─────────────────────────────────────────────────────────────
  practicas: PracticaAdminDTO[] = [];
  practicaSeleccionada: PracticaAdminDTO | null = null;

  // ── Filtros ───────────────────────────────────────────────────────────
  filtroEstado: string = '';
  filtroCategoria: string = '';
  filtroEscala: string = '';
  filtroDificultad: string = '';
  filtroBusqueda: string = '';

  // ── Formulario ────────────────────────────────────────────────────────
  form: CrearPracticaDTO & { estado?: string } = {
    nombre: '',
    descripcion: null,
    objetivo: null,
    categoria: null,
    impactoEsperado: null,
    pasos: null,
    kpiImpactado: null,
    dificultad: 'MEDIA',
    duracionDias: null,
    escala: 'TODAS',
    tropicaAplicable: 'TODOS',
    estado: 'ACTIVA',
  };

  // ── Opciones ──────────────────────────────────────────────────────────
  readonly categorias = [
    { valor: 'PRODUCTIVIDAD', label: '🥛 Productividad' },
    { valor: 'HATO', label: '🐄 Hato' },
    { valor: 'FINANCIERO', label: '💰 Financiero' },
    { valor: 'EFICIENCIA', label: '⚙️ Eficiencia' },
  ];

  readonly dificultades = [
    { valor: 'BAJA', label: '🟢 Baja' },
    { valor: 'MEDIA', label: '🟡 Media' },
    { valor: 'ALTA', label: '🔴 Alta' },
  ];

  readonly escalas = ['TODAS', 'PEQUEÑA', 'MEDIANA', 'GRANDE', 'EMPRESARIAL'];

  readonly tropicos = ['TODOS', 'FRIO', 'TEMPLADO', 'CALIDO'];

  pasosArray: string[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private practicasService: GestionPracticasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarPracticas();
  }

  // ── Carga ─────────────────────────────────────────────────────────────

  cargarPracticas(): void {
    this.loading = true;
    this.cdr.markForCheck();

    this.practicasService
      .getPracticas(
        this.filtroEstado || undefined,
        this.filtroCategoria || undefined,
        this.filtroEscala || undefined,
        this.filtroDificultad || undefined,
      )
      .subscribe({
        next: (data) => {
          this.practicas = data;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al cargar las prácticas.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Filtros ───────────────────────────────────────────────────────────

  onFiltroChange(campo: string, valor: string): void {
    (this as any)[campo] = valor;
    this.cargarPracticas();
  }

  onBusquedaChange(valor: string): void {
    this.filtroBusqueda = valor.toLowerCase().trim();
    this.cdr.markForCheck();
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroCategoria = '';
    this.filtroEscala = '';
    this.filtroDificultad = '';
    this.filtroBusqueda = '';
    this.cargarPracticas();
  }

  get practicasFiltradas(): PracticaAdminDTO[] {
    if (!this.filtroBusqueda) return this.practicas;
    return this.practicas.filter(
      (p) =>
        p.nombre.toLowerCase().includes(this.filtroBusqueda) ||
        (p.descripcion ?? '').toLowerCase().includes(this.filtroBusqueda) ||
        (p.kpiImpactado ?? '').toLowerCase().includes(this.filtroBusqueda),
    );
  }

  get hayFiltrosActivos(): boolean {
    return !!(
      this.filtroEstado ||
      this.filtroCategoria ||
      this.filtroEscala ||
      this.filtroDificultad ||
      this.filtroBusqueda
    );
  }

  // ── Formulario ────────────────────────────────────────────────────────

  abrirCrear(): void {
    this.modoForm = 'crear';
    this.practicaSeleccionada = null;
    this.resetForm();
    this.tabActivo = 'form';
    this.cdr.markForCheck();
  }

  abrirEditar(practica: PracticaAdminDTO): void {
    this.modoForm = 'editar';
    this.practicaSeleccionada = practica;

    this.form = {
      nombre: practica.nombre,
      descripcion: practica.descripcion,
      objetivo: practica.objetivo,
      categoria: practica.categoria,
      impactoEsperado: practica.impactoEsperado,
      pasos: practica.pasos,
      kpiImpactado: practica.kpiImpactado,
      dificultad: practica.dificultad,
      duracionDias: practica.duracionDias,
      escala: practica.escala,
      tropicaAplicable: practica.tropicaAplicable,
      estado: practica.estado,
    };

    // Parsear pasos JSON a array
    this.parsearPasos(practica.pasos);

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
      nombre: '',
      descripcion: null,
      objetivo: null,
      categoria: null,
      impactoEsperado: null,
      pasos: null,
      kpiImpactado: null,
      dificultad: 'MEDIA',
      duracionDias: null,
      escala: 'TODAS',
      tropicaAplicable: 'TODOS',
      estado: 'ACTIVA',
    };
    this.pasosArray = [''];
  }

  // ── Editor de pasos ───────────────────────────────────────────────────

  private parsearPasos(pasosJson: string | null): void {
    if (!pasosJson) {
      this.pasosArray = [''];
      return;
    }
    try {
      const parsed = JSON.parse(pasosJson);
      this.pasosArray = Array.isArray(parsed) && parsed.length > 0 ? parsed : [''];
    } catch {
      this.pasosArray = [''];
    }
  }

  agregarPaso(): void {
    this.pasosArray = [...this.pasosArray, ''];
    this.cdr.markForCheck();
  }

  eliminarPaso(idx: number): void {
    if (this.pasosArray.length <= 1) return;
    this.pasosArray = this.pasosArray.filter((_, i) => i !== idx);
    this.sincronizarPasos();
    this.cdr.markForCheck();
  }

  onPasoChange(idx: number, valor: string): void {
    this.pasosArray[idx] = valor;
    this.sincronizarPasos();
  }

  private sincronizarPasos(): void {
    const pasosFiltrados = this.pasosArray.filter((p) => p.trim());
    this.form.pasos = pasosFiltrados.length > 0 ? JSON.stringify(pasosFiltrados) : null;
  }

  // ── Cambios en el form ────────────────────────────────────────────────

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
    if (!this.form.nombre?.trim()) {
      this.error = 'El nombre de la práctica es obligatorio.';
      this.cdr.markForCheck();
      return;
    }

    this.sincronizarPasos();
    this.guardando = true;
    this.error = '';
    this.exito = '';
    this.cdr.markForCheck();

    if (this.modoForm === 'crear') {
      this.practicasService.crearPractica(this.form).subscribe({
        next: () => {
          this.exito = '✅ Práctica creada correctamente.';
          this.guardando = false;
          this.cargarPracticas();
          this.cancelarForm();
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al crear la práctica.';
          this.guardando = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      const dto: EditarPracticaDTO = {
        nombre: this.form.nombre,
        descripcion: this.form.descripcion,
        objetivo: this.form.objetivo,
        categoria: this.form.categoria ?? undefined,
        impactoEsperado: this.form.impactoEsperado,
        pasos: this.form.pasos,
        kpiImpactado: this.form.kpiImpactado,
        dificultad: this.form.dificultad,
        duracionDias: this.form.duracionDias,
        escala: this.form.escala,
        tropicaAplicable: this.form.tropicaAplicable ?? undefined,
        estado: this.form.estado,
      };

      this.practicasService.editarPractica(this.practicaSeleccionada!.idPractica, dto).subscribe({
        next: () => {
          this.exito = '✅ Práctica actualizada correctamente.';
          this.guardando = false;
          this.cargarPracticas();
          this.cancelarForm();
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al editar la práctica.';
          this.guardando = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  desactivar(practica: PracticaAdminDTO, event: Event): void {
    event.stopPropagation();
    if (!confirm(`¿Desactivar "${practica.nombre}"?`)) return;

    this.practicasService.desactivarPractica(practica.idPractica).subscribe({
      next: () => {
        this.exito = '✅ Práctica desactivada.';
        this.cargarPracticas();
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al desactivar la práctica.';
        this.cdr.markForCheck();
      },
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  iconoCategoria(cat: string | null): string {
    const map: Record<string, string> = {
      PRODUCTIVIDAD: '🥛',
      HATO: '🐄',
      FINANCIERO: '💰',
      EFICIENCIA: '⚙️',
    };
    return map[cat ?? ''] ?? '📊';
  }

  colorDificultad(dif: string): string {
    const map: Record<string, string> = {
      BAJA: 'dif-baja',
      MEDIA: 'dif-media',
      ALTA: 'dif-alta',
    };
    return map[dif] ?? '';
  }

  get practicasActivasCount(): number {
    return this.practicas.filter((p) => p.estado === 'ACTIVA').length;
  }

  get practicasInactivasCount(): number {
    return this.practicas.filter((p) => p.estado === 'INACTIVA').length;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
