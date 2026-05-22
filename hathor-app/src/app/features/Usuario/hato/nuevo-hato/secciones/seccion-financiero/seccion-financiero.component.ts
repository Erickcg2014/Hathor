import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { Hato, HatoService } from '../../../../../../core/services/hato/hato.service';
import {
  PerfilFinancieroService,
  RegistroPerfilFinancieroDTO,
} from '../../../../../../core/services/finanzas/perfil-financiero.service';
import { FormsModule } from '@angular/forms';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';
import { FinanzasExcelComponent } from './finanzas-excel/finanzas-excel.component';
import { FinanzasManualComponent } from './finanzas-manual/finanzas-manual.component';
import { Router } from '@angular/router';

export type OpcionRegistro = 'EXCEL' | 'MANUAL' | 'OMITIDO' | null;
export type ModoManual = 'PROMEDIO' | 'MENSUAL' | 'ANUAL';

export interface RegistroManualItem {
  idCategoria: string;
  nombre: string;
  tipo: 'INGRESO' | 'GASTO' | 'COSTO' | 'INVERSION';
  periodos: { fecha: string; monto: number }[];
}

export interface DatosManual {
  modo: ModoManual;
  registros: RegistroManualItem[];
}

@Component({
  selector: 'app-seccion-financiero',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    SpinnerComponent,
    FinanzasExcelComponent,
    FinanzasManualComponent,
  ],
  templateUrl: './seccion-financiero.component.html',
  styleUrl: './seccion-financiero.component.css',
})
export class SeccionFinancieroComponent implements OnInit {
  @Input() hatoActual: Hato | null = null;
  @Input() modoReadonly = false;

  @Output() seccionCompletada = new EventEmitter<Hato>();
  @Output() irAnterior = new EventEmitter<void>();

  // ===== ESTADO PRINCIPAL =====
  opcionSeleccionada: OpcionRegistro = null;
  loadingGuardar = false;
  error = '';

  //====CAMPOS DE COSTOS FIJOS =====
  gastoMensualNomina: number | null = null;
  gastoMensualAlimentacion: number | null = null;

  // ===== ESTADO DE HIJOS =====
  excelListo = false;
  datosManual: DatosManual | null = null;
  mostrarOverlayOmitir = false;

  constructor(
    private hatoService: HatoService,
    private perfilFinancieroService: PerfilFinancieroService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (this.hatoActual) {
      this.gastoMensualNomina = this.hatoActual.gastoMensualNomina ?? null;
      this.gastoMensualAlimentacion = this.hatoActual.gastoMensualAlimentacion ?? null;
    }
  }

  // ===== SELECCIÓN DE OPCIÓN =====

  seleccionarOpcion(opcion: OpcionRegistro): void {
    this.opcionSeleccionada = opcion;
    this.excelListo = false;
    this.datosManual = null;
    this.error = '';
    this.cdr.markForCheck();
  }

  confirmarOmitir(): void {
    this.mostrarOverlayOmitir = true;
    this.cdr.markForCheck();
  }

  cancelarOmitir(): void {
    this.mostrarOverlayOmitir = false;
    this.cdr.markForCheck();
  }

  getNombreOpcion(opcion: OpcionRegistro): string {
    const nombres: Record<string, string> = {
      EXCEL: 'Cargar Excel',
      MANUAL: 'Registro manual',
      OMITIDO: 'Omitir por ahora',
    };
    return opcion ? nombres[opcion] : '';
  }

  // ===== CALLBACKS DE HIJOS =====

  onExcelCargaExitosa(): void {
    this.excelListo = true;
    this.cdr.markForCheck();
  }

  onManualDatosListos(datos: DatosManual): void {
    this.datosManual = datos;
    this.cdr.markForCheck();
  }

  formatCOP(valor: number | null): string {
    if (!valor) return '';
    return new Intl.NumberFormat('es-CO').format(valor);
  }

  // ===== VALIDACIÓN =====

  get puedeGuardar(): boolean {
    if (this.opcionSeleccionada === 'EXCEL') return this.excelListo;
    if (this.opcionSeleccionada === 'MANUAL')
      return this.datosManual !== null && this.datosManual.registros.length > 0;
    if (this.opcionSeleccionada === 'OMITIDO') return true;
    return false;
  }

  // ===== GUARDAR =====

  async guardar(): Promise<void> {
    if (!this.hatoActual?.idHato || !this.puedeGuardar) return;

    this.loadingGuardar = true;
    this.error = '';

    try {
      if (this.gastoMensualNomina !== null || this.gastoMensualAlimentacion !== null) {
        await firstValueFrom(
          this.hatoService.actualizarCostosFijos(
            this.hatoActual.idHato!,
            this.gastoMensualNomina ?? 0,
            this.gastoMensualAlimentacion ?? 0,
          ),
        );
      }

      const periodo = new Date().toISOString().substring(0, 7);

      const perfilDto: RegistroPerfilFinancieroDTO = {
        idHato: this.hatoActual.idHato!,
        metodoRegistro: this.opcionSeleccionada!,
        periodo,
        ...(this.opcionSeleccionada === 'MANUAL' && this.datosManual
          ? {
              detalles: this.datosManual.registros.flatMap((r) =>
                r.periodos.map((p) => ({
                  idCategoria: r.idCategoria,
                  tipo: r.tipo,
                  titulo: r.nombre,
                  fecha: p.fecha,
                  montoMensual: p.monto,
                })),
              ),
            }
          : {}),
      };

      await firstValueFrom(this.perfilFinancieroService.crearPerfil(perfilDto));

      const hatoActualizado = await firstValueFrom(
        this.hatoService.actualizarCompletitud(this.hatoActual.idHato!, 100),
      );

      this.seccionCompletada.emit(hatoActualizado);
      if (this.opcionSeleccionada === 'OMITIDO') {
        this.router.navigate(['/home']);
      }
    } catch {
      this.error = 'Error al guardar. Intenta de nuevo.';
      this.cdr.markForCheck();
    } finally {
      this.loadingGuardar = false;
      this.cdr.markForCheck();
    }
  }
}
