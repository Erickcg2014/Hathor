import {
  Component,
  OnDestroy,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { Hato } from '../../../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../../../core/services/ui-state/hato-state.service';
import {
  InventarioGeneral,
  InventarioGeneralService,
  ActualizarInventarioGeneralDTO,
} from '../../../../../../core/services/inventario/inventario-general.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

interface TotalPorCategoria {
  categoria: string;
  cantidad: number;
  total: number;
}

interface FilaEdicion {
  nombreItem: string;
  cantidad: number;
  valorUnitario: number;
  descripcion: string;
}

@Component({
  selector: 'app-inventario-general-resumen',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './inventario-general-resumen.component.html',
  styleUrl: './inventario-general-resumen.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventarioGeneralResumenComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  loading = false;
  error = '';
  items: InventarioGeneral[] = [];

  // ── Edición inline ────────────────────────────────────────
  filaEditando: string | null = null;
  datosEdicion: FilaEdicion = {
    nombreItem: '',
    cantidad: 0,
    valorUnitario: 0,
    descripcion: '',
  };
  guardandoEdicion = false;
  errorEdicion = '';

  // ── Eliminación ───────────────────────────────────────────
  filaEliminando: string | null = null;
  confirmandoEliminar: string | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private cdr: ChangeDetectorRef,
    private hatoState: HatoStateService,
    private inventarioService: InventarioGeneralService,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarDatos(hato.idHato);
      } else {
        this.items = [];
        this.cdr.markForCheck();
      }
    });
  }

  // ── Getters ───────────────────────────────────────────────

  get totalItems(): number {
    return this.items.reduce((acc, item) => acc + item.cantidad, 0);
  }

  get valorTotal(): number {
    return this.items.reduce((acc, item) => acc + item.valorTotal, 0);
  }

  get promedioValorUnitario(): number {
    return this.items.length
      ? this.items.reduce((acc, item) => acc + item.valorUnitario, 0) / this.items.length
      : 0;
  }

  get categoriasResumen(): TotalPorCategoria[] {
    const map = new Map<string, TotalPorCategoria>();
    this.items.forEach((item) => {
      // Usar categoría padre si existe, sino la propia
      const key =
        item.categoriaInventario.categoriaPadre?.nombre ?? item.categoriaInventario.nombre;
      const actual = map.get(key) ?? { categoria: key, cantidad: 0, total: 0 };
      actual.cantidad += item.cantidad;
      actual.total += item.valorTotal;
      map.set(key, actual);
    });
    return Array.from(map.values()).sort((a, b) => b.total - a.total);
  }

  // ── Helpers de categoría ──────────────────────────────────

  getNombreCategoriaPadre(item: InventarioGeneral): string {
    return item.categoriaInventario.categoriaPadre?.nombre ?? '—';
  }

  getNombreSubcategoria(item: InventarioGeneral): string {
    return item.categoriaInventario.categoriaPadre ? item.categoriaInventario.nombre : '—';
  }

  // ── Edición inline ────────────────────────────────────────

  abrirEdicion(item: InventarioGeneral): void {
    this.filaEditando = item.idInventarioGeneral;
    this.errorEdicion = '';
    this.datosEdicion = {
      nombreItem: item.nombreItem,
      cantidad: item.cantidad,
      valorUnitario: item.valorUnitario,
      descripcion: item.descripcion ?? '',
    };
    this.cdr.markForCheck();
  }

  cancelarEdicion(): void {
    this.filaEditando = null;
    this.errorEdicion = '';
    this.cdr.markForCheck();
  }

  guardarEdicion(item: InventarioGeneral): void {
    if (!this.datosEdicion.nombreItem?.trim()) {
      this.errorEdicion = 'El nombre del item es requerido.';
      this.cdr.markForCheck();
      return;
    }
    if (this.datosEdicion.cantidad < 1) {
      this.errorEdicion = 'La cantidad debe ser mayor a 0.';
      this.cdr.markForCheck();
      return;
    }
    if (this.datosEdicion.valorUnitario < 1) {
      this.errorEdicion = 'El valor unitario debe ser mayor a 0.';
      this.cdr.markForCheck();
      return;
    }

    this.guardandoEdicion = true;
    this.errorEdicion = '';
    this.cdr.markForCheck();

    const dto: ActualizarInventarioGeneralDTO = {
      nombreItem: this.datosEdicion.nombreItem,
      cantidad: this.datosEdicion.cantidad,
      valorUnitario: this.datosEdicion.valorUnitario,
      descripcion: this.datosEdicion.descripcion,
    };

    this.inventarioService
      .actualizar(item.idInventarioGeneral, dto)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizado) => {
          this.items = this.items.map((i) =>
            i.idInventarioGeneral === actualizado.idInventarioGeneral ? actualizado : i,
          );
          this.filaEditando = null;
          this.guardandoEdicion = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorEdicion = 'Error al guardar. Intenta de nuevo.';
          this.guardandoEdicion = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Eliminación ───────────────────────────────────────────

  solicitarConfirmacion(id: string): void {
    this.confirmandoEliminar = id;
    this.cdr.markForCheck();
  }

  cancelarEliminar(): void {
    this.confirmandoEliminar = null;
    this.cdr.markForCheck();
  }

  confirmarEliminar(id: string): void {
    this.filaEliminando = id;
    this.confirmandoEliminar = null;
    this.cdr.markForCheck();

    this.inventarioService
      .eliminar(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.items = this.items.filter((i) => i.idInventarioGeneral !== id);
          this.filaEliminando = null;
          this.cdr.markForCheck();
        },
        error: () => {
          this.filaEliminando = null;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Helpers ───────────────────────────────────────────────

  formatCOP(valor: number | null | undefined): string {
    if (valor === null || valor === undefined) return '$ 0';
    return '$ ' + new Intl.NumberFormat('es-CO').format(valor);
  }

  private cargarDatos(idHato: string): void {
    this.loading = true;
    this.error = '';
    this.inventarioService
      .getByHato(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (items) => {
          this.items = items;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'No fue posible cargar el inventario general.';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
