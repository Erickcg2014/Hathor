import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HatoAnonimizadoDTO } from '../../../../core/services/benchmarking/benchmarking.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { MapaHatosComponent } from '../../benchmarking/comparativa-kpis/benchmark-global/mapa-hatos/mapa-hatos.component';

@Component({
  selector: 'app-home-mapa',
  standalone: true,
  imports: [CommonModule, SpinnerComponent, MapaHatosComponent],
  templateUrl: './home-mapa.component.html',
  styleUrl: './home-mapa.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeMapaComponent {
  @Input() hatosPlataforma: HatoAnonimizadoDTO[] = [];
  @Input() loadingMapa = true;

  get totalHatos(): number {
    return this.hatosPlataforma.length;
  }
}
