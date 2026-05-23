import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  ElementRef,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  AfterViewChecked,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, distinctUntilChanged } from 'rxjs';
import { Chart, registerables, TooltipItem } from 'chart.js';

import { HatoStateService } from '../../../core/services/ui-state/hato-state.service';
import { HatoService, Hato } from '../../../core/services/hato/hato.service';
import { UserService } from '../../../core/services/usuario/user.service';
import { KpiService, KpiResultado, KpiHistorico } from '../../../core/services/kpi/kpi.service';
import {
  PracticasService,
  HatoPracticaDTO,
} from '../../../core/services/practicas/practicas.service';
import { RankingService } from '../../../core/services/benchmarking/ranking.service';

Chart.register(...registerables);

// ── Interfaces ────────────────────────────────────────────────────────────

interface KpiEvolucion {
  kpi: KpiResultado;
  historico: KpiHistorico[];
  valorInicial: number | null;
  valorActual: number | null;
  deltaPct: number | null;
  mejoro: boolean;
}

interface ResumenPractica {
  practica: HatoPracticaDTO;
  kpiActual: KpiResultado | null;
  deltaPct: number | null;
}

@Component({
  selector: 'app-mi-progreso',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mi-progreso.component.html',
  styleUrl: './mi-progreso.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiProgresoComponent implements OnInit, AfterViewInit, OnDestroy {
  // ── Estado ────────────────────────────────────────────────────────────
  loading = true;
  loadingKpis = false;
  loadingRanking = false;
  error = '';
  esAdmin = false;

  // ── Datos ─────────────────────────────────────────────────────────────
  hatoActivo: Hato | null = null;
  kpis: KpiResultado[] = [];
  practicas: HatoPracticaDTO[] = [];
  kpisEvolucion: KpiEvolucion[] = [];
  practicasImpacto: ResumenPractica[] = [];

  // ── Ranking ───────────────────────────────────────────────────────────
  posicionActual: number | null = null;
  scoreActual: number | null = null;
  percentilActual: number | null = null;
  totalHatosNacional: number | null = null;

  // ── Chart ─────────────────────────────────────────────────────────────
  @ViewChild('chartScore', { static: false }) chartScoreRef?: ElementRef<HTMLCanvasElement>;
  private chartScore: Chart<any> | null = null;
  private viewReady = false;
  private graficaConstruida = false;

  // ── KPIs destacados ───────────────────────────────────────────────────
  // Máximo 6 KPIs con datos para mostrar evolución
  readonly MAX_KPIS_MOSTRAR = 6;

  // ── Tab activo ────────────────────────────────────────────────────────
  tabActivo: 'kpis' | 'practicas' | 'ranking' = 'kpis';

  private destroy$ = new Subject<void>();
  private pendientesIniciales = 0;
  private completadosIniciales = 0;

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private userService: UserService,
    private kpiService: KpiService,
    private practicasService: PracticasService,
    private rankingService: RankingService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Detectar rol
    this.userService
      .getUsuarioLogueado()
      .pipe(takeUntil(this.destroy$))
      .subscribe((usuario) => {
        this.esAdmin = usuario?.rol === 'ADMIN';
        this.cdr.markForCheck();
      });

    // Suscribirse al hato activo — funciona para USER y ADMIN
    this.hatoState.hatoActivo$
      .pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged((a, b) => a?.idHato === b?.idHato),
      )
      .subscribe((hato) => {
        this.hatoActivo = hato;
        if (hato?.idHato) {
          this.cargarTodo(hato.idHato as string);
        } else {
          this.loading = false;
        }
        this.cdr.markForCheck();
      });

    // Cargar hato inicial solo si es usuario normal
    if (!this.hatoState.getHatoActivo() && !this.esAdmin) {
      this.hatoService.getMisHatos().subscribe({
        next: (hatos) => {
          if (hatos.length > 0) this.hatoState.setHatoActivo(hatos[0]);
          else this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  ngAfterViewChecked(): void {
    if (!this.graficaConstruida && this.chartScoreRef?.nativeElement && this.scoreActual != null) {
      console.log('✅ AfterViewChecked — canvas disponible, construyendo gráfica');
      this.graficaConstruida = true;
      this.buildGraficaScore();
    }
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
  }

  // ── Carga de datos ────────────────────────────────────────────────────

  private cargarTodo(idHato: string): void {
    this.pendientesIniciales = 2;
    this.completadosIniciales = 0;
    this.graficaConstruida = false;
    this.loading = true;
    this.loadingKpis = true;
    this.loadingRanking = true;
    this.error = '';
    this.kpisEvolucion = [];
    this.practicasImpacto = [];
    this.cdr.markForCheck();

    // Cargar KPIs
    this.kpiService.getKpis(idHato).subscribe({
      next: (kpis) => {
        this.kpis = kpis;
        this.loadingKpis = false;
        this.construirEvolucionKpis(idHato, kpis);
        this.verificarCargaCompleta();
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingKpis = false;
        this.error = 'Error al cargar los KPIs.';
        this.verificarCargaCompleta();
        this.cdr.markForCheck();
      },
    });

    // Cargar prácticas
    this.practicasService.getPracticasByHato(idHato).subscribe({
      next: (practicas) => {
        this.practicas = practicas.filter(
          (p) => p.estado === 'EN_CURSO' || p.estado === 'COMPLETADA',
        );
        this.construirImpactoPracticas();
        this.cdr.markForCheck();
      },
    });

    // Cargar ranking
    this.rankingService.getResumenRanking(idHato).subscribe({
      next: (resumen) => {
        this.posicionActual = resumen.posicionNacional;
        this.scoreActual = resumen.scoreCompuesto;
        this.totalHatosNacional = resumen.totalHatosNacional;
        this.percentilActual =
          resumen.totalHatosNacional > 0 && resumen.posicionNacional != null
            ? Math.round((1 - resumen.posicionNacional / resumen.totalHatosNacional) * 100)
            : null;
        this.loadingRanking = false;
        this.verificarCargaCompleta();
        this.intentarGraficaScore();
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingRanking = false;
        this.verificarCargaCompleta();
        this.cdr.markForCheck();
      },
    });
  }

  private verificarCargaCompleta(): void {
    this.completadosIniciales++;
    if (this.completadosIniciales >= this.pendientesIniciales) {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  // ── Evolución de KPIs ─────────────────────────────────────────────────

  private construirEvolucionKpis(idHato: string, kpis: KpiResultado[]): void {
    // Filtrar KPIs con datos y ordenar por los más críticos primero
    const kpisConDatos = kpis
      .filter((k) => k.valor != null)
      .sort((a, b) => {
        const orden: Record<string, number> = {
          CRITICO: 0,
          ACEPTABLE: 1,
          OPTIMO: 2,
          SIN_DATOS: 3,
        };
        return (orden[a.estado] ?? 3) - (orden[b.estado] ?? 3);
      })
      .slice(0, this.MAX_KPIS_MOSTRAR);

    const evoluciones: KpiEvolucion[] = [];

    let pendientes = kpisConDatos.length;
    if (pendientes === 0) return;

    for (const kpi of kpisConDatos) {
      this.kpiService.getHistorico(idHato, kpi.codigo).subscribe({
        next: (historico) => {
          const ordenado = historico.sort(
            (a, b) => new Date(a.fechaCalculo).getTime() - new Date(b.fechaCalculo).getTime(),
          );

          const valorInicial = ordenado.length > 0 ? ordenado[0].valor : null;
          const valorActual = kpi.valor;

          let deltaPct: number | null = null;
          let mejoro = false;

          if (valorInicial != null && valorActual != null && valorInicial !== 0) {
            deltaPct = Math.round(((valorActual - valorInicial) / Math.abs(valorInicial)) * 100);
            mejoro = deltaPct > 0;
          }

          evoluciones.push({
            kpi,
            historico: ordenado.slice(-6),
            valorInicial,
            valorActual,
            deltaPct,
            mejoro,
          });

          pendientes--;
          if (pendientes === 0) {
            // Ordenar: mejorados primero, luego críticos
            this.kpisEvolucion = evoluciones.sort((a, b) => {
              if (a.mejoro && !b.mejoro) return -1;
              if (!a.mejoro && b.mejoro) return 1;
              return 0;
            });
            this.cdr.markForCheck();
          }
        },
        error: () => {
          pendientes--;
          if (pendientes === 0) {
            this.kpisEvolucion = evoluciones;
            this.cdr.markForCheck();
          }
        },
      });
    }
  }

  // ── Impacto de prácticas ──────────────────────────────────────────────

  private construirImpactoPracticas(): void {
    this.practicasImpacto = this.practicas.map((p) => {
      const kpiActual = p.kpiImpactado ? this.kpiService.getKpiDesdeCache(p.kpiImpactado) : null;

      // Buscar delta del KPI en evoluciones
      const evolucion = this.kpisEvolucion.find((ev) => ev.kpi.codigo === p.kpiImpactado);

      return {
        practica: p,
        kpiActual,
        deltaPct: evolucion?.deltaPct ?? null,
      };
    });
  }

  // ── Gráfica score ─────────────────────────────────────────────────────

  private intentarGraficaScore(): void {
    this.graficaConstruida = false;
    this.cdr.markForCheck();
  }

  private buildGraficaScore(): void {
    console.log('🔍 buildGraficaScore() llamado');
    console.log('🔍 chartScoreRef:', this.chartScoreRef);
    console.log('🔍 scoreActual:', this.scoreActual);

    const canvas = this.chartScoreRef?.nativeElement;
    if (!canvas) {
      console.error('❌ Canvas no encontrado');
      return;
    }
    if (this.scoreActual == null) {
      console.error('❌ scoreActual es null');
      return;
    }

    console.log('🔍 Canvas dimensiones:', canvas.width, 'x', canvas.height);
    console.log('🔍 Canvas en DOM:', document.contains(canvas));

    if (this.chartScore) {
      console.log('🔄 Destruyendo chart anterior');
      this.chartScore.destroy();
      this.chartScore = null;
    }

    const score = Math.min(100, Math.max(0, this.scoreActual));
    const restante = Math.max(0, 100 - score);

    console.log(`📊 Creando donut — score: ${score}, restante: ${restante}`);
    console.log(`🎨 Color arco principal: #19e64d`);
    console.log(`🎨 Color arco restante: #e9f5eb`);

    try {
      this.chartScore = new Chart<'doughnut'>(canvas, {
        type: 'doughnut',
        data: {
          datasets: [
            {
              data: [score, restante],
              backgroundColor: ['#19e64d', '#e9f5eb'],
              borderWidth: 0,
              hoverOffset: 0,
            },
          ],
        },
        options: {
          responsive: false,
          maintainAspectRatio: false,
          cutout: '78%',
          plugins: {
            legend: { display: false },
            tooltip: { enabled: false },
          },
          animation: {
            duration: 800,
            onComplete: () => {
              console.log('✅ Animación completada — arco renderizado correctamente');
            },
          },
        },
      });
      console.log('✅ Chart creado:', this.chartScore);
    } catch (e) {
      console.error('❌ Error creando chart:', e);
    }

    this.cdr.markForCheck();
  }

  // ── Tab ───────────────────────────────────────────────────────────────

  setTab(tab: typeof this.tabActivo): void {
    this.tabActivo = tab;
    if (tab === 'practicas') {
      this.construirImpactoPracticas();
    }
    this.cdr.markForCheck();
  }

  // ── Getters stats ─────────────────────────────────────────────────────

  get kpisMejorados(): number {
    return this.kpisEvolucion.filter((ev) => ev.mejoro).length;
  }

  get kpisCriticos(): number {
    return this.kpis.filter((k) => k.estado === 'CRITICO').length;
  }

  get kpisOptimos(): number {
    return this.kpis.filter((k) => k.estado === 'OPTIMO').length;
  }

  get kpisAceptables(): number {
    return this.kpis.filter((k) => k.estado === 'ACEPTABLE').length;
  }

  get kpisSinDatos(): number {
    return this.kpis.filter((k) => k.estado === 'SIN_DATOS').length;
  }

  get practicasCompletadas(): number {
    return this.practicas.filter((p) => p.estado === 'COMPLETADA').length;
  }

  get practicasEnCurso(): number {
    return this.practicas.filter((p) => p.estado === 'EN_CURSO').length;
  }

  get completitudPct(): number {
    return this.hatoActivo?.porcentajeCompletitud ?? 0;
  }

  // ── Helpers visuales ─────────────────────────────────────────────────

  colorEstadoKpi(estado: string): string {
    const map: Record<string, string> = {
      OPTIMO: 'kpi-optimo',
      ACEPTABLE: 'kpi-aceptable',
      CRITICO: 'kpi-critico',
      SIN_DATOS: 'kpi-sin-datos',
    };
    return map[estado] ?? 'kpi-sin-datos';
  }

  iconoEstadoKpi(estado: string): string {
    const map: Record<string, string> = {
      OPTIMO: '✅',
      ACEPTABLE: '🟡',
      CRITICO: '🔴',
      SIN_DATOS: '—',
    };
    return map[estado] ?? '—';
  }

  colorDelta(delta: number | null): string {
    if (delta == null) return 'delta-neutro';
    if (delta > 0) return 'delta-positivo';
    if (delta < 0) return 'delta-negativo';
    return 'delta-neutro';
  }

  iconoDelta(delta: number | null): string {
    if (delta == null) return '—';
    if (delta > 0) return '↗️';
    if (delta < 0) return '↘️';
    return '→';
  }

  calcularAlturaBar(valor: number, historico: KpiHistorico[]): number {
    if (!historico.length) return 0;
    const valores = historico.map((h) => h.valor);
    const max = Math.max(...valores);
    const min = Math.min(...valores);
    if (max === min) return 50;
    return Math.round(((valor - min) / (max - min)) * 80) + 10;
  }

  iconoCategoria(categoria: string): string {
    const map: Record<string, string> = {
      PRODUCTIVIDAD: '🥛',
      HATO: '🐄',
      FINANCIERO: '💰',
      EFICIENCIA: '⚙️',
    };
    return map[categoria] ?? '📊';
  }

  formatNum(n: number | null | undefined): string {
    if (n == null) return '—';
    return new Intl.NumberFormat('es-CO').format(Math.round(n));
  }

  irARanking(): void {
    this.router.navigate(['/benchmarking/ranking']);
  }

  irAPracticas(): void {
    this.router.navigate(['/practicas']);
  }

  ngOnDestroy(): void {
    this.chartScore?.destroy();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
