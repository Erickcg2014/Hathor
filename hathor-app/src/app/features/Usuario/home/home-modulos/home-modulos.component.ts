import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PerfilProductivo } from '../../../../core/services/produccion/perfil-productivo.service';
import { ProduccionLeche } from '../../../../core/services/produccion/produccion-leche.service';
import { KpiResultado } from '../../../../core/services/kpi/kpi.service';
import { BenchmarkHatoResultado } from '../../../../core/services/benchmarking/benchmarking.service';
import { RankingResumenDTO } from '../../../../core/services/benchmarking/ranking.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

import { ModProduccionComponent } from './mod-produccion/mod-produccion.component';
import { ModFinanzasComponent, FinanzaMes } from './mod-finanzas/mod-finanzas.component';
import { ModGanadoComponent, DistribucionGanado } from './mod-ganado/mod-ganado.component';
import { ModKpisComponent } from './mod-kpis/mod-kpis.component';
import { Hato } from '../../../../core/services/hato/hato.service';

@Component({
  selector: 'app-home-modulos',
  standalone: true,
  imports: [
    CommonModule,
    SpinnerComponent,
    ModProduccionComponent,
    ModFinanzasComponent,
    ModGanadoComponent,
    ModKpisComponent,
  ],
  templateUrl: './home-modulos.component.html',
  styleUrl: './home-modulos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeModulosComponent {
  // ── Hato ──────────────────────────────────────────────────────────────────
  @Input() hatoActivo: Hato | null = null;

  // ── Producción ────────────────────────────────────────────────────────────
  @Input() perfilProductivo: PerfilProductivo | null = null;
  @Input() produccionUltimos30: ProduccionLeche[] = [];
  @Input() loadingProduccion = true;

  // ── Finanzas ──────────────────────────────────────────────────────────────
  @Input() totalIngresos = 0;
  @Input() totalEgresos = 0;
  @Input() balanceNeto = 0;
  @Input() finanzasPorMes: FinanzaMes[] = [];
  @Input() loadingFinanzas = true;

  // ── Inventario ganado ─────────────────────────────────────────────────────
  @Input() totalAnimales = 0;
  @Input() distribucionGanado: DistribucionGanado[] = [];
  @Input() loadingInventario = true;

  // ── KPIs / Benchmarking ───────────────────────────────────────────────────
  @Input() kpis: KpiResultado[] = [];
  @Input() benchmarks: BenchmarkHatoResultado[] = [];
  @Input() rankingResumen: RankingResumenDTO | null = null;
  @Input() loadingKpis = true;

  // ── Navegación delegada al padre ──────────────────────────────────────────
  @Output() navegarA = new EventEmitter<string>();

  get iconoRanking(): string {
    const pos = this.rankingResumen?.posicionNacional ?? 999;
    if (pos === 1) return '🥇';
    if (pos === 2) return '🥈';
    if (pos === 3) return '🥉';
    return '🏅';
  }

  onNavegar(ruta: string): void {
    this.navegarA.emit(ruta);
  }
}
