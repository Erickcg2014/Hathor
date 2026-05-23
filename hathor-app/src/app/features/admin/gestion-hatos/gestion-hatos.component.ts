import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AdminService, HatoAdminDTO } from '../../../core/services/admin/admin.service';
import {
  AdminStateService,
  FiltroAdmin,
} from '../../../core/services/ui-state/admin-state.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-gestion-hatos',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './gestion-hatos.component.html',
  styleUrl: './gestion-hatos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GestionHatosComponent implements OnInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  loading = false;
  error = '';
  hatos: HatoAdminDTO[] = [];

  // ── Filtros adicionales locales ───────────────────────────────────────
  filtroTropico: string = '';
  filtroEscala: string = '';
  filtroTipoHato: string = '';
  filtroBusqueda: string = '';

  tropicosOpciones: string[] = [];
  escalasOpciones: string[] = [];

  // ── Paginación ────────────────────────────────────────────────────────
  readonly PAGE_SIZE = 15;
  paginaActual = 1;

  // ── Filtros del navbar admin ──────────────────────────────────────────
  filtrosActuales: FiltroAdmin = {
    departamento: null,
    region: null,
    idHato: null,
    nombreHato: null,
  };

  private destroy$ = new Subject<void>();

  constructor(
    private adminService: AdminService,
    private adminState: AdminStateService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Cargar opciones de filtros locales
    this.cargarOpciones();

    // Suscribirse a cambios de filtros del navbar admin
    this.adminState
      .getFiltros$()
      .pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged(
          (a, b) =>
            a.departamento === b.departamento && a.region === b.region && a.idHato === b.idHato,
        ),
      )
      .subscribe((filtros) => {
        this.filtrosActuales = filtros;
        this.paginaActual = 1;
        this.cargarHatos();
        this.cdr.markForCheck();
      });
  }

  // ── Carga de datos ────────────────────────────────────────────────────

  private cargarOpciones(): void {
    this.adminService.getTropicos().subscribe({
      next: (t) => {
        this.tropicosOpciones = t;
        this.cdr.markForCheck();
      },
    });
    this.adminService.getEscalas().subscribe({
      next: (e) => {
        this.escalasOpciones = e;
        this.cdr.markForCheck();
      },
    });
  }

  cargarHatos(): void {
    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.adminService
      .getHatosFiltrados(
        this.filtrosActuales.departamento ?? undefined,
        this.filtrosActuales.region ?? undefined,
        this.filtroTropico || undefined,
        this.filtroEscala || undefined,
        this.filtroTipoHato || undefined,
      )
      .subscribe({
        next: (hatos) => {
          this.hatos = hatos;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al cargar los hatos.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Filtros locales ───────────────────────────────────────────────────

  onTropicoChange(valor: string): void {
    this.filtroTropico = valor;
    this.paginaActual = 1;
    this.cargarHatos();
    this.cdr.markForCheck();
  }

  onEscalaChange(valor: string): void {
    this.filtroEscala = valor;
    this.paginaActual = 1;
    this.cargarHatos();
    this.cdr.markForCheck();
  }

  onTipoHatoChange(valor: string): void {
    this.filtroTipoHato = valor;
    this.paginaActual = 1;
    this.cargarHatos();
    this.cdr.markForCheck();
  }

  onBusquedaChange(valor: string): void {
    this.filtroBusqueda = valor.toLowerCase().trim();
    this.paginaActual = 1;
    this.cdr.markForCheck();
  }

  limpiarFiltros(): void {
    this.filtroTropico = '';
    this.filtroEscala = '';
    this.filtroTipoHato = '';
    this.filtroBusqueda = '';
    this.paginaActual = 1;
    this.cargarHatos();
    this.cdr.markForCheck();
  }

  // ── Paginación ────────────────────────────────────────────────────────

  get hatosFiltradosLocalmente(): HatoAdminDTO[] {
    if (!this.filtroBusqueda) return this.hatos;
    return this.hatos.filter(
      (h) =>
        h.nombreHato.toLowerCase().includes(this.filtroBusqueda) ||
        h.departamento.toLowerCase().includes(this.filtroBusqueda) ||
        h.ciudad.toLowerCase().includes(this.filtroBusqueda) ||
        (h.correoUsuario ?? '').toLowerCase().includes(this.filtroBusqueda),
    );
  }

  get hatosPaginados(): HatoAdminDTO[] {
    const inicio = (this.paginaActual - 1) * this.PAGE_SIZE;
    return this.hatosFiltradosLocalmente.slice(inicio, inicio + this.PAGE_SIZE);
  }

  get totalPaginas(): number {
    return Math.ceil(this.hatosFiltradosLocalmente.length / this.PAGE_SIZE);
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  irAPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
    this.cdr.markForCheck();
  }

  // ── Navegación al detalle ─────────────────────────────────────────────

  verDetalle(hato: HatoAdminDTO): void {
    this.router.navigate(['/admin/hatos', hato.idHato]);
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  colorCompletitud(pct: number): string {
    if (pct >= 100) return 'comp-completo';
    if (pct >= 75) return 'comp-alto';
    if (pct >= 50) return 'comp-medio';
    return 'comp-bajo';
  }

  iconoEscala(escala: string | null): string {
    const map: Record<string, string> = {
      PEQUEÑA: '🏠',
      MEDIANA: '🏡',
      GRANDE: '🏘️',
      EMPRESARIAL: '🏢',
    };
    return map[escala ?? ''] ?? '🐄';
  }

  get hayFiltrosActivos(): boolean {
    return !!(
      this.filtrosActuales.departamento ||
      this.filtrosActuales.region ||
      this.filtroTropico ||
      this.filtroEscala ||
      this.filtroTipoHato ||
      this.filtroBusqueda
    );
  }

  get labelContexto(): string {
    const partes: string[] = [];
    if (this.filtrosActuales.departamento) {
      partes.push(this.filtrosActuales.departamento);
    }
    if (this.filtrosActuales.region) {
      partes.push(this.filtrosActuales.region);
    }
    return partes.length > 0 ? partes.join(' › ') : 'Nacional — todos los departamentos';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
