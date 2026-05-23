import {
  Component,
  Input,
  Output,
  EventEmitter,
  ChangeDetectionStrategy,
  computed,
  input,
} from '@angular/core';
import { CommonModule } from '@angular/common';

import { Hato } from '../../../../core/services/hato/hato.service';

@Component({
  selector: 'app-home-estado',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home-estado.component.html',
  styleUrl: './home-estado.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeEstadoComponent {
  @Input() hatoActivo: Hato | null = null;
  @Input() esAdmin = false;
  @Input() completitud = 0;

  @Output() continuarFormulario = new EventEmitter<void>();
  @Output() registrarHato = new EventEmitter<void>();

  // ── Getters de estado derivado ────────────────────────────────────────────

  get registroCompleto(): boolean {
    return this.completitud >= 100;
  }

  get mostrarSelectorAdmin(): boolean {
    return this.esAdmin && !this.hatoActivo;
  }

  get mostrarSinHato(): boolean {
    return !this.hatoActivo && !this.esAdmin;
  }

  get mostrarBannerCompletitud(): boolean {
    return !this.registroCompleto && !!this.hatoActivo;
  }

  get pasoActualFormulario(): string {
    if (this.completitud <= 25) return 'Inventario general';
    if (this.completitud <= 50) return 'Producción lechera';
    if (this.completitud <= 75) return 'Registros financieros';
    return 'Completado';
  }

  get pasos(): number[] {
    return [25, 50, 75, 100];
  }

  onContinuarFormulario(): void {
    this.continuarFormulario.emit();
  }

  onRegistrarHato(): void {
    this.registrarHato.emit();
  }
}
