import {
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  RankingCompuestoDTO,
  HatoRankingItem,
} from '../../../../../core/services/benchmarking/ranking.service';

@Component({
  selector: 'app-ranking-compuesto',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ranking-compuesto.component.html',
  styleUrl: './ranking-compuesto.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RankingCompuestoComponent implements OnChanges {
  @Input() datos: RankingCompuestoDTO | null = null;
  @Input() region: string | null = null;

  readonly PAGE_SIZE = 10;
  paginaActual = 1;
  itemsMostrados: HatoRankingItem[] = [];

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['datos']) {
      this.paginaActual = 1;
      this.actualizarPagina();
    }
  }

  private actualizarPagina(): void {
    if (!this.datos?.ranking) {
      this.itemsMostrados = [];
      return;
    }
    const fin = this.paginaActual * this.PAGE_SIZE;
    this.itemsMostrados = this.datos.ranking.slice(0, fin);
    this.cdr.markForCheck();
  }

  cargarMas(): void {
    this.paginaActual++;
    this.actualizarPagina();
  }

  get hayMas(): boolean {
    if (!this.datos?.ranking) return false;
    return this.itemsMostrados.length < this.datos.ranking.length;
  }

  get top3(): HatoRankingItem[] {
    if (!this.datos?.ranking) return [];
    return this.datos.ranking.slice(0, 3);
  }

  get posicionMiHato(): number {
    return this.datos?.posicionMiHato ?? 0;
  }

  get totalHatos(): number {
    return this.datos?.totalHatos ?? 0;
  }

  get percentilMiHato(): number {
    if (!this.datos || this.datos.totalHatos <= 1) return 0;
    return (
      ((this.datos.totalHatos - this.datos.posicionMiHato) / (this.datos.totalHatos - 1)) * 100
    );
  }

  medallaTop3(posicion: number): string {
    if (posicion === 1) return '🥇';
    if (posicion === 2) return '🥈';
    return '🥉';
  }

  colorBarra(item: HatoRankingItem): string {
    if (item.esMiHato) return '#19e64d';
    const score = item.valor ?? 0;
    if (score >= 75) return '#15803d';
    if (score >= 50) return '#3b82f6';
    if (score >= 25) return '#f59e0b';
    return '#ef4444';
  }

  anchoBarra(item: HatoRankingItem): number {
    if (!this.datos?.scoreTop || this.datos.scoreTop === 0) return 0;
    return Math.min(((item.valor ?? 0) / this.datos.scoreTop) * 100, 100);
  }

  formatScore(score: number | null): string {
    if (score === null) return '—';
    return score.toFixed(1);
  }

  labelRegion(): string {
    return this.region ? `Región: ${this.region}` : 'Nacional';
  }
}
