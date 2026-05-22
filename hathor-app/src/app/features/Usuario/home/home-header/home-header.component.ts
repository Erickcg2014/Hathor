import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Usuario } from '../../../../core/services/usuario/user.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import { RankingResumenDTO } from '../../../../core/services/benchmarking/ranking.service';

export type TipoSaludo = 'manana' | 'tarde' | 'noche';

export interface FraseMotivacional {
  mensaje: string;
  autor: string;
}

@Component({
  selector: 'app-home-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home-header.component.html',
  styleUrl: './home-header.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeHeaderComponent {
  @Input() usuario: Usuario | null = null;
  @Input() hatoActivo: Hato | null = null;
  @Input() tipoSaludo: TipoSaludo = 'manana';
  @Input() saludoTexto = '';
  @Input() frase: FraseMotivacional | null = null;
  @Input() rankingResumen: RankingResumenDTO | null = null;
  @Input() loadingRanking = true;

  get iconoRanking(): string {
    const pos = this.rankingResumen?.posicionNacional ?? 999;
    if (pos === 1) return '🥇';
    if (pos === 2) return '🥈';
    if (pos === 3) return '🥉';
    return '🏅';
  }
}
