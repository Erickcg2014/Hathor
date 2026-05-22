import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RankingResumenDTO } from '../../../../../core/services/benchmarking/ranking.service';

@Component({
  selector: 'app-ranking-resumen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ranking-resumen.component.html',
  styleUrl: './ranking-resumen.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RankingResumenComponent {
  @Input() resumen: RankingResumenDTO | null = null;
  @Input() nombreHato = '';

  get scoreColor(): string {
    const s = this.resumen?.scoreCompuesto ?? 0;
    if (s >= 75) return 'score-optimo';
    if (s >= 50) return 'score-bueno';
    if (s >= 25) return 'score-aceptable';
    return 'score-critico';
  }

  get medallaIcon(): string {
    const pos = this.resumen?.posicionNacional ?? 999;
    if (pos === 1) return '🥇';
    if (pos === 2) return '🥈';
    if (pos === 3) return '🥉';
    return '🏅';
  }

  formatScore(score: number | null): string {
    if (score === null) return '—';
    return score.toFixed(1);
  }

  formatPosicion(pos: number | null, total: number): string {
    if (pos === null) return '—';
    return `#${pos} de ${total}`;
  }
}
