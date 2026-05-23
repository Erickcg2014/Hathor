import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, firstValueFrom, distinctUntilChanged } from 'rxjs';

import { HatoService, Hato } from '../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../core/services/ui-state/hato-state.service';
import { UserService, Usuario } from '../../../core/services/usuario/user.service';
import { KpiService, KpiResultado } from '../../../core/services/kpi/kpi.service';
import {
  BenchmarkingService,
  BenchmarkHatoResultado,
  HatoAnonimizadoDTO,
} from '../../../core/services/benchmarking/benchmarking.service';
import {
  RankingService,
  RankingResumenDTO,
} from '../../../core/services/benchmarking/ranking.service';
import {
  PerfilProductivoService,
  PerfilProductivo,
} from '../../../core/services/produccion/perfil-productivo.service';
import {
  ProduccionLecheService,
  ProduccionLeche,
} from '../../../core/services/produccion/produccion-leche.service';
import { InventarioGeneralService } from '../../../core/services/inventario/inventario-general.service';
import { InventarioGanadoService } from '../../../core/services/inventario/inventario-ganado.service';
import { RegistroFinancieroService } from '../../../core/services/finanzas/registro-financiero.service';
import {
  RecomendacionGeneralDTO,
  RecomendacionGeneralService,
} from '../../../core/services/practicas/recomendacion-general.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

// ── Componentes hijos ─────────────────────────────────────────────────────
import {
  HomeHeaderComponent,
  TipoSaludo,
  FraseMotivacional,
} from './home-header/home-header.component';
import { HomeClimaComponent, ClimaData } from './home-clima/home-clima.component';
import { HomeEstadoComponent } from './home-estado/home-estado.component';
import { HomeMapaComponent } from './home-mapa/home-mapa.component';
import { HomeModulosComponent } from './home-modulos/home-modulos.component';
import { FinanzaMes } from './home-modulos/mod-finanzas/mod-finanzas.component';
import { DistribucionGanado } from './home-modulos/mod-ganado/mod-ganado.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    SpinnerComponent,
    HomeHeaderComponent,
    HomeClimaComponent,
    HomeEstadoComponent,
    HomeMapaComponent,
    HomeModulosComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit, OnDestroy {
  // ── Estado general ────────────────────────────────────────────────────────
  loading = true;
  hatoActivo: Hato | null = null;
  usuario: Usuario | null = null;
  completitud = 0;
  esAdmin = false;

  // ── Saludo ────────────────────────────────────────────────────────────────
  tipoSaludo: TipoSaludo = 'manana';
  saludoTexto = '';
  frase: FraseMotivacional | null = null;

  // ── Mapa ──────────────────────────────────────────────────────────────────
  loadingMapa = true;
  hatosPlataforma: HatoAnonimizadoDTO[] = [];

  // ── Clima ──────────────────────────────────────────────────────────────────
  loadingClima = false;
  climaData: ClimaData | null = null;
  recomendacionesClima: RecomendacionGeneralDTO[] = [];

  // ── Ranking ───────────────────────────────────────────────────────────────
  loadingRanking = true;
  rankingResumen: RankingResumenDTO | null = null;

  // ── KPIs ──────────────────────────────────────────────────────────────────
  loadingKpis = true;
  kpis: KpiResultado[] = [];
  benchmarks: BenchmarkHatoResultado[] = [];

  // ── Producción ────────────────────────────────────────────────────────────
  loadingProduccion = true;
  perfilProductivo: PerfilProductivo | null = null;
  produccionUltimos30: ProduccionLeche[] = [];

  // ── Finanzas ──────────────────────────────────────────────────────────────
  loadingFinanzas = true;
  totalIngresos = 0;
  totalEgresos = 0;
  balanceNeto = 0;
  finanzasPorMes: FinanzaMes[] = [];

  // ── Inventario ────────────────────────────────────────────────────────────
  loadingInventario = true;
  totalAnimales = 0;
  distribucionGanado: DistribucionGanado[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private userService: UserService,
    private kpiService: KpiService,
    private benchmarkingService: BenchmarkingService,
    private rankingService: RankingService,
    private perfilProductivoService: PerfilProductivoService,
    private produccionLecheService: ProduccionLecheService,
    private inventarioGeneralService: InventarioGeneralService,
    private inventarioGanadoService: InventarioGanadoService,
    private registroFinancieroService: RegistroFinancieroService,
    private recomendacionService: RecomendacionGeneralService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.inicializarSaludo();
    this.cargarFraseMotivacional();

    // Reaccionar a cambios del hato activo (admin selecciona desde navbar)
    this.hatoState.hatoActivo$
      .pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged((a, b) => a?.idHato === b?.idHato),
      )
      .subscribe((hato) => {
        if (hato && hato.idHato && hato.idHato !== this.hatoActivo?.idHato) {
          this.hatoActivo = hato;
          this.completitud = hato.porcentajeCompletitud ?? 25;
          this.cdr.markForCheck();

          const id = hato.idHato as string;
          this.cargarRanking(id);
          this.cargarKpis(id);
          this.cargarProduccion(id);
          this.cargarFinanzas(id);
          this.cargarInventario(id);
        }
      });

    this.cargarDatos();
  }

  // ── Saludo ────────────────────────────────────────────────────────────────

  private inicializarSaludo(): void {
    const hora = new Date().getHours();
    if (hora >= 5 && hora < 12) {
      this.tipoSaludo = 'manana';
      this.saludoTexto = 'Buenos días';
    } else if (hora >= 12 && hora < 18) {
      this.tipoSaludo = 'tarde';
      this.saludoTexto = 'Buenas tardes';
    } else {
      this.tipoSaludo = 'noche';
      this.saludoTexto = 'Buenas noches';
    }
  }

  private cargarFraseMotivacional(): void {
    fetch('/assets/motivacion.json')
      .then((r) => r.json())
      .then((frases: FraseMotivacional[]) => {
        this.frase = frases[Math.floor(Math.random() * frases.length)];
        this.cdr.markForCheck();
      })
      .catch(() => {});
  }

  // ── Carga principal ───────────────────────────────────────────────────────

  private async cargarDatos(): Promise<void> {
    this.loading = true;

    try {
      const [usuarioResult, hatosResult] = await Promise.allSettled([
        firstValueFrom(this.userService.getUsuarioLogueado()),
        firstValueFrom(this.hatoService.getMisHatos()),
      ]);

      if (usuarioResult.status === 'fulfilled') {
        this.usuario = usuarioResult.value;
        this.esAdmin = this.usuario?.rol === 'ADMIN';
      }

      if (hatosResult.status === 'fulfilled' && hatosResult.value.length > 0) {
        this.hatoActivo = hatosResult.value[0];
        this.completitud = this.hatoActivo.porcentajeCompletitud ?? 25;
        this.hatoState.setHatoActivo(this.hatoActivo);
      }
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }

    this.cargarMapa();

    if (this.hatoActivo?.idHato) {
      const id = this.hatoActivo.idHato as string;
      this.cargarRanking(id);
      this.cargarKpis(id);
      this.cargarProduccion(id);
      this.cargarFinanzas(id);
      this.cargarInventario(id);
    } else {
      this.loadingRanking = false;
      this.loadingKpis = false;
      this.loadingProduccion = false;
      this.loadingFinanzas = false;
      this.loadingInventario = false;
      this.cdr.markForCheck();
    }

    if (this.hatoActivo?.latitud && this.hatoActivo?.longitud && !this.esAdmin) {
      this.cargarClima(
        this.hatoActivo.latitud,
        this.hatoActivo.longitud,
        this.hatoActivo.idHato as string,
      );
    }
  }

  // ── Loaders independientes ────────────────────────────────────────────────

  private cargarMapa(): void {
    this.loadingMapa = true;
    this.hatoService.getMapaGeneral().subscribe({
      next: (hatos) => {
        const miHato = this.hatoActivo;
        if (miHato?.latitud && miHato?.longitud) {
          this.hatosPlataforma = [
            ...hatos,
            {
              alias: 'Tu hato',
              latitudDifuminada: miHato.latitud,
              longitudDifuminada: miHato.longitud,
              tropico: miHato.tropico,
              escala: miHato.escala ?? null,
              departamento: miHato.departamento,
              valorKpiPrincipal: null,
              interpretacion: null,
              esMiHato: true,
            },
          ];
        } else {
          this.hatosPlataforma = hatos;
        }
        this.loadingMapa = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingMapa = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarClima(lat: number, lng: number, idHato: string): void {
    this.loadingClima = true;
    this.cdr.markForCheck();

    // Caché 2h en localStorage
    const cacheKey = `clima_${idHato}`;
    const cacheRaw = localStorage.getItem(cacheKey);
    if (cacheRaw) {
      try {
        const cache = JSON.parse(cacheRaw);
        if (Date.now() - cache.timestamp < 2 * 60 * 60 * 1000) {
          this.climaData = cache.data;
          const subtipo = this.getSubtipoClima(
            cache.data.condicion,
            cache.data.precipitacion,
            cache.data.temperatura,
            cache.data.viento,
          );
          if (subtipo) this.crearRecomendacionesClima(idHato, subtipo);
          this.loadingClima = false;
          this.cdr.markForCheck();
          return;
        }
      } catch {
        localStorage.removeItem(cacheKey);
      }
    }

    const url =
      `https://api.open-meteo.com/v1/forecast` +
      `?latitude=${lat}&longitude=${lng}` +
      `&current=temperature_2m,apparent_temperature,precipitation,windspeed_10m,weathercode` +
      `&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode` +
      `&timezone=America%2FBogota&forecast_days=4`;

    fetch(url)
      .then((r) => r.json())
      .then((json) => {
        const c = json.current;
        const d = json.daily;

        const data: ClimaData = {
          temperatura: c.temperature_2m,
          sensacionTermica: c.apparent_temperature,
          precipitacion: c.precipitation,
          viento: c.windspeed_10m,
          condicion: c.weathercode,
          condicionLabel: this.labelCondicion(c.weathercode),
          icono: this.iconoCondicion(c.weathercode),
          esNeutral: this.esCondicionNeutral(
            c.weathercode,
            c.precipitation,
            c.temperature_2m,
            c.windspeed_10m,
          ),
          pronostico: d.time.slice(1, 4).map((fecha: string, i: number) => ({
            fecha,
            tempMax: d.temperature_2m_max[i + 1],
            tempMin: d.temperature_2m_min[i + 1],
            precipitacion: d.precipitation_sum[i + 1],
            condicion: d.weathercode[i + 1],
            icono: this.iconoCondicion(d.weathercode[i + 1]),
          })),
        };

        localStorage.setItem(cacheKey, JSON.stringify({ timestamp: Date.now(), data }));
        this.climaData = data;

        const subtipo = this.getSubtipoClima(
          data.condicion,
          data.precipitacion,
          data.temperatura,
          data.viento,
        );
        if (subtipo) {
          this.crearRecomendacionesClima(idHato, subtipo);
        } else {
          this.recomendacionesClima = [];
          this.recomendacionService.limpiarClimaticas(idHato).subscribe();
        }

        this.loadingClima = false;
        this.cdr.markForCheck();
      })
      .catch(() => {
        this.climaData = null;
        this.recomendacionesClima = [];
        this.loadingClima = false;
        this.cdr.markForCheck();
      });
  }

  private crearRecomendacionesClima(idHato: string, subtipo: string): void {
    this.recomendacionService.crearClimatica(idHato, subtipo).subscribe({
      next: (recs) => {
        this.recomendacionesClima = recs;
        this.cdr.markForCheck();
      },
      error: () => {
        this.recomendacionesClima = [];
        this.cdr.markForCheck();
      },
    });
  }

  private getSubtipoClima(
    code: number,
    precip: number,
    temp: number,
    viento: number,
  ): string | null {
    if (precip > 20 || (code >= 61 && code <= 82)) return 'LLUVIA_INTENSA';
    if (temp > 32) return 'CALOR_EXTREMO';
    if (temp < 4) return 'HELADA';
    if (viento > 50) return 'VIENTO_FUERTE';
    return null;
  }

  private esCondicionNeutral(code: number, precip: number, temp: number, viento: number): boolean {
    return precip <= 20 && temp > 4 && temp <= 32 && viento <= 50 && !(code >= 61 && code <= 82);
  }

  private labelCondicion(code: number): string {
    if (code === 0) return 'Despejado';
    if (code <= 3) return 'Parcialmente nublado';
    if (code <= 49) return 'Neblina';
    if (code <= 59) return 'Llovizna';
    if (code <= 69) return 'Lluvia';
    if (code <= 79) return 'Nevada';
    if (code <= 82) return 'Lluvia intensa';
    if (code <= 99) return 'Tormenta';
    return 'Variable';
  }

  private iconoCondicion(code: number): string {
    if (code === 0) return '☀️';
    if (code <= 3) return '⛅';
    if (code <= 49) return '🌫️';
    if (code <= 59) return '🌦️';
    if (code <= 69) return '🌧️';
    if (code <= 79) return '❄️';
    if (code <= 82) return '⛈️';
    if (code <= 99) return '🌩️';
    return '🌤️';
  }

  private cargarRanking(idHato: string): void {
    this.loadingRanking = true;
    this.rankingService.getResumenRanking(idHato).subscribe({
      next: (data) => {
        this.rankingResumen = data;
        this.loadingRanking = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingRanking = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarKpis(idHato: string): void {
    this.loadingKpis = true;
    this.kpiService.getKpis(idHato).subscribe({
      next: (kpis) => {
        this.kpis = kpis;
        this.loadingKpis = false;
        this.cdr.markForCheck();
        this.cargarBenchmarks(idHato);
      },
      error: () => {
        this.loadingKpis = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarBenchmarks(idHato: string): void {
    this.benchmarkingService.getBenchmarking(idHato, 'REFERENCIA', 'NACIONAL').subscribe({
      next: (data) => {
        this.benchmarks = data;
        this.cdr.markForCheck();
      },
      error: () => this.cdr.markForCheck(),
    });
  }

  private cargarProduccion(idHato: string): void {
    this.loadingProduccion = true;
    Promise.allSettled([
      firstValueFrom(this.perfilProductivoService.getPerfilByHato(idHato)),
      firstValueFrom(this.produccionLecheService.getByHato(idHato)),
    ]).then(([perfilResult, produccionResult]) => {
      if (perfilResult.status === 'fulfilled') {
        this.perfilProductivo = perfilResult.value;
      }
      if (produccionResult.status === 'fulfilled') {
        const hoy = new Date();
        const desde = new Date(hoy);
        desde.setDate(hoy.getDate() - 30);
        this.produccionUltimos30 = produccionResult.value
          .filter((p) => new Date(p.fecha) >= desde)
          .sort((a, b) => new Date(a.fecha).getTime() - new Date(b.fecha).getTime());
      }
      this.loadingProduccion = false;
      this.cdr.markForCheck();
    });
  }

  private cargarFinanzas(idHato: string): void {
    this.loadingFinanzas = true;
    this.registroFinancieroService.getRegistrosByHato(idHato).subscribe({
      next: (registros) => {
        this.totalIngresos = registros
          .filter((r) => r.tipoMovimiento === 'INGRESO')
          .reduce((s, r) => s + r.monto, 0);
        this.totalEgresos = registros
          .filter((r) => r.tipoMovimiento !== 'INGRESO')
          .reduce((s, r) => s + r.monto, 0);
        this.balanceNeto = this.totalIngresos - this.totalEgresos;
        this.finanzasPorMes = this.agruparFinanzasPorMes(registros);
        this.loadingFinanzas = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingFinanzas = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarInventario(idHato: string): void {
    this.loadingInventario = true;
    Promise.allSettled([
      firstValueFrom(this.inventarioGeneralService.getByHato(idHato)),
      firstValueFrom(this.inventarioGanadoService.getByHato(idHato)),
    ]).then(([generalResult, ganadoResult]) => {
      if (generalResult.status === 'fulfilled') {
        // totalItems disponible si se necesita en el futuro
      }
      if (ganadoResult.status === 'fulfilled') {
        const ganado = ganadoResult.value;
        this.totalAnimales = ganado.reduce((s, g) => s + (g.cantidad ?? 0), 0);
        const grupos: Record<string, number> = {};
        for (const g of ganado) {
          const cat = g.categoriaGanado?.nombreCategoria ?? 'Sin categoría';
          grupos[cat] = (grupos[cat] ?? 0) + (g.cantidad ?? 0);
        }
        this.distribucionGanado = Object.entries(grupos)
          .map(([categoria, cantidad]) => ({ categoria, cantidad }))
          .sort((a, b) => b.cantidad - a.cantidad);
      }
      this.loadingInventario = false;
      this.cdr.markForCheck();
    });
  }

  private agruparFinanzasPorMes(registros: any[]): FinanzaMes[] {
    const meses = [
      'Ene',
      'Feb',
      'Mar',
      'Abr',
      'May',
      'Jun',
      'Jul',
      'Ago',
      'Sep',
      'Oct',
      'Nov',
      'Dic',
    ];
    const hoy = new Date();
    return Array.from({ length: 6 }, (_, i) => {
      const fecha = new Date(hoy.getFullYear(), hoy.getMonth() - (5 - i), 1);
      const mm = fecha.getMonth();
      const yy = fecha.getFullYear();
      const delMes = registros.filter((r) => {
        const d = new Date(r.fecha);
        return d.getMonth() === mm && d.getFullYear() === yy;
      });
      return {
        mes: `${meses[mm]} ${yy}`,
        ingresos: delMes
          .filter((r) => r.tipoMovimiento === 'INGRESO')
          .reduce((s, r) => s + r.monto, 0),
        egresos: delMes
          .filter((r) => r.tipoMovimiento !== 'INGRESO')
          .reduce((s, r) => s + r.monto, 0),
      };
    });
  }

  // ── Navegación ────────────────────────────────────────────────────────────

  irA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  continuarFormulario(): void {
    this.router.navigate(['/hato/nuevo']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
