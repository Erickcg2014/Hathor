import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecomendacionGeneralDTO } from '../../../../core/services/practicas/recomendacion-general.service';
import { Hato } from '../../../../core/services/hato/hato.service';

export interface ClimaPronosticoDia {
  fecha: string;
  tempMax: number;
  tempMin: number;
  precipitacion: number;
  condicion: number;
  icono: string;
}

export interface ClimaData {
  temperatura: number;
  sensacionTermica: number;
  precipitacion: number;
  viento: number;
  condicion: number;
  condicionLabel: string;
  icono: string;
  esNeutral: boolean;
  pronostico: ClimaPronosticoDia[];
}

@Component({
  selector: 'app-home-clima',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home-clima.component.html',
  styleUrl: './home-clima.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeClimaComponent {
  @Input() climaData: ClimaData | null = null;
  @Input() recomendacionesClima: RecomendacionGeneralDTO[] = [];
  @Input() loadingClima = false;
  @Input() hatoActivo: Hato | null = null;

  @Output() navegarA = new EventEmitter<string>();

  onIrA(url: string): void {
    this.navegarA.emit(url);
  }

  formatFechaPronostico(fecha: string): string {
    const d = new Date(fecha + 'T12:00:00');
    return d.toLocaleDateString('es-CO', {
      weekday: 'short',
      day: 'numeric',
    });
  }
}
