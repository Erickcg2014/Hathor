// shared/avance-pasos/avance-pasos.component.ts

import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';

import {
  PracticasService,
  PasoDTO,
  HatoPracticaPasoResponseDTO,
} from '../../core/services/practicas/practicas.service';
import { SpinnerComponent } from '../spinner/spinner.component';

@Component({
  selector: 'app-avance-pasos',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './avance-pasos.component.html',
  styleUrl: './avance-pasos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvancePasosComponent implements OnChanges {
  // ── Inputs ────────────────────────────────────────────────────────────
  @Input() visible = false;
  @Input() idHatoPractica = '';
  @Input() nombrePractica = '';
  @Input() pasoTextos: string[] = []; // textos de Practica.pasos

  // ── Outputs ───────────────────────────────────────────────────────────
  @Output() cerrar = new EventEmitter<void>();
  @Output() avanceActualizado = new EventEmitter<HatoPracticaPasoResponseDTO>();

  // ── Estado ────────────────────────────────────────────────────────────
  loading = false;
  guardando = false;
  error = '';

  // ── Pasos ─────────────────────────────────────────────────────────────
  // Estado local de checkboxes — índice → completado
  estadoPasos: boolean[] = [];
  cargado = false;

  constructor(
    private practicasService: PracticasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    // Cuando se abre el overlay y hay idHatoPractica válido
    if (changes['visible']?.currentValue === true && this.idHatoPractica) {
      this.cargarPasos();
    }

    // Cuando cambia el idHatoPractica con overlay abierto
    if (changes['idHatoPractica']?.currentValue && this.visible) {
      this.cargarPasos();
    }

    // Inicializar estadoPasos cuando llegan los textos
    if (changes['pasoTextos']?.currentValue?.length > 0 && !this.cargado) {
      this.inicializarEstadoPasos();
    }
  }

  // ── Carga de pasos guardados ──────────────────────────────────────────

  private async cargarPasos(): Promise<void> {
    if (!this.idHatoPractica) return;
    this.loading = true;
    this.error = '';
    this.cargado = false;
    this.cdr.markForCheck();

    try {
      const response = await firstValueFrom(this.practicasService.getPasos(this.idHatoPractica));

      // Inicializar array con el tamaño de los textos
      this.estadoPasos = Array(this.pasoTextos.length).fill(false);

      // Aplicar pasos guardados
      for (const paso of response.pasos) {
        if (paso.indicePaso < this.estadoPasos.length) {
          this.estadoPasos[paso.indicePaso] = paso.completado;
        }
      }

      this.cargado = true;
    } catch {
      // Si no hay pasos guardados aún, inicializar vacío
      this.inicializarEstadoPasos();
      this.cargado = true;
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  private inicializarEstadoPasos(): void {
    this.estadoPasos = Array(this.pasoTextos.length).fill(false);
    this.cargado = true;
    this.cdr.markForCheck();
  }

  // ── Acciones ──────────────────────────────────────────────────────────

  togglePaso(indice: number): void {
    this.estadoPasos = this.estadoPasos.map((v, i) => (i === indice ? !v : v));
    this.cdr.markForCheck();
  }

  async guardar(): Promise<void> {
    if (!this.idHatoPractica) return;
    this.guardando = true;
    this.error = '';
    this.cdr.markForCheck();

    try {
      const pasos: PasoDTO[] = this.estadoPasos.map((completado, indicePaso) => ({
        indicePaso,
        completado,
      }));

      const response = await firstValueFrom(
        this.practicasService.actualizarPasos(this.idHatoPractica, pasos),
      );

      this.avanceActualizado.emit(response);
      this.onCerrar();
    } catch {
      this.error = 'Error al guardar el avance.';
    } finally {
      this.guardando = false;
      this.cdr.markForCheck();
    }
  }

  onCerrar(): void {
    this.cerrar.emit();
    this.cdr.markForCheck();
  }

  // ── Getters ───────────────────────────────────────────────────────────

  get totalPasos(): number {
    return this.pasoTextos.length;
  }

  get pasosCompletados(): number {
    return this.estadoPasos.filter(Boolean).length;
  }

  get porcentajeActual(): number {
    if (!this.totalPasos) return 0;
    return Math.round((this.pasosCompletados / this.totalPasos) * 100);
  }

  get todosCompletados(): boolean {
    return this.totalPasos > 0 && this.pasosCompletados === this.totalPasos;
  }
}
