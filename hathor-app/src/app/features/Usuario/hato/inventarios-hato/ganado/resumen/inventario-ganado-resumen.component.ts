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
  InventarioGanado,
  InventarioGanadoService,
  ActualizarInventarioGanadoDTO,
} from '../../../../../../core/services/inventario/inventario-ganado.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

interface TotalPorTipo {
  tipo: string;
  cantidad: number;
  total: number;
}

interface FilaEdicion {
  cantidad: number;
  edadPromedioMeses: number;
  valorUnitario: number;
}

@Component({
  selector: 'app-inventario-ganado-resumen',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './inventario-ganado-resumen.component.html',
  styleUrl: './inventario-ganado-resumen.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventarioGanadoResumenComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  loading = false;
  error = '';
  registros: InventarioGanado[] = [];

  // ── Edición inline ────────────────────────────────────────
  filaEditando: string | null = null;
  datosEdicion: FilaEdicion = {
    cantidad: 0,
    edadPromedioMeses: 0,
    valorUnitario: 0,
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
    private inventarioService: InventarioGanadoService,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarDatos(hato.idHato);
      } else {
        this.registros = [];
        this.cdr.markForCheck();
      }
    });
  }

  // ── Getters ───────────────────────────────────────────────

  get cabezasTotales(): number {
    return this.registros.reduce((acc, r) => acc + r.cantidad, 0);
  }

  get valorTotal(): number {
    return this.registros.reduce((acc, r) => acc + r.valorTotal, 0);
  }

  get edadPromedioPonderada(): number {
    const totalCabezas = this.cabezasTotales;
    if (!totalCabezas) return 0;
    const suma = this.registros.reduce(
      (acc, r) => acc + this.calcularEdadActual(r.edadPromedioMeses, r.fechaRegistro) * r.cantidad,
      0,
    );
    return suma / totalCabezas;
  }

  get resumenPorTipo(): TotalPorTipo[] {
    const map = new Map<string, TotalPorTipo>();
    this.registros.forEach((r) => {
      const key = r.raza.tipoRaza;
      const actual = map.get(key) ?? { tipo: key, cantidad: 0, total: 0 };
      actual.cantidad += r.cantidad;
      actual.total += r.valorTotal;
      map.set(key, actual);
    });
    return Array.from(map.values()).sort((a, b) => b.total - a.total);
  }

  // ── Edad dinámica ─────────────────────────────────────────

  calcularEdadActual(edadPromedioMeses: number, fechaRegistro: string): number {
    if (!fechaRegistro) return edadPromedioMeses;
    const registro = new Date(fechaRegistro);
    const hoy = new Date();
    const mesesTranscurridos =
      (hoy.getFullYear() - registro.getFullYear()) * 12 + (hoy.getMonth() - registro.getMonth());
    return edadPromedioMeses + Math.max(0, mesesTranscurridos);
  }

  // ── Edición inline ────────────────────────────────────────

  abrirEdicion(registro: InventarioGanado): void {
    this.filaEditando = registro.idInventario;
    this.errorEdicion = '';
    // Mostrar la edad actual calculada como valor inicial
    this.datosEdicion = {
      cantidad: registro.cantidad,
      edadPromedioMeses: this.calcularEdadActual(
        registro.edadPromedioMeses,
        registro.fechaRegistro,
      ),
      valorUnitario: registro.valorUnitario,
    };
    this.cdr.markForCheck();
  }

  cancelarEdicion(): void {
    this.filaEditando = null;
    this.errorEdicion = '';
    this.cdr.markForCheck();
  }

  guardarEdicion(registro: InventarioGanado): void {
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

    const dto: ActualizarInventarioGanadoDTO = {
      cantidad: this.datosEdicion.cantidad,
      edadPromedioMeses: this.datosEdicion.edadPromedioMeses,
      valorUnitario: this.datosEdicion.valorUnitario,
    };

    this.inventarioService
      .actualizar(registro.idInventario, dto)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (actualizado) => {
          this.registros = this.registros.map((r) =>
            r.idInventario === actualizado.idInventario ? actualizado : r,
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

  solicitarConfirmacion(idInventario: string): void {
    this.confirmandoEliminar = idInventario;
    this.cdr.markForCheck();
  }

  cancelarEliminar(): void {
    this.confirmandoEliminar = null;
    this.cdr.markForCheck();
  }

  confirmarEliminar(idInventario: string): void {
    this.filaEliminando = idInventario;
    this.confirmandoEliminar = null;
    this.cdr.markForCheck();

    this.inventarioService
      .eliminar(idInventario)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.registros = this.registros.filter((r) => r.idInventario !== idInventario);
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
        next: (registros) => {
          this.registros = registros;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'No fue posible cargar el inventario de ganado.';
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
