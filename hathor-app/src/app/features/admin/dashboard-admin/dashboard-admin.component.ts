import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChildren,
  QueryList,
  ElementRef,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { Chart, registerables, TooltipItem } from 'chart.js';

import {
  AdminEstadisticasService,
  EstadisticasGlobalesDTO,
  EstadisticasDepartamentoDTO,
  EstadisticasEscalaDTO,
  EstadisticasTropicoDTO,
} from '../../../core/services/admin/admin-estadisticas.service';

import { AdminService, HatoAdminDTO } from '../../../core/services/admin/admin.service';
import { AdminStateService } from '../../../core/services/ui-state/admin-state.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './dashboard-admin.component.html',
  styleUrl: './dashboard-admin.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardAdminComponent implements OnInit, AfterViewInit, OnDestroy {
  // ── Estado general ────────────────────────────────────────────────────
  loadingGlobales = true;
  loadingDepartamentos = true;
  loadingEscalas = true;
  loadingTropicos = true;
  loadingMapa = true;
  error = '';

  // ── Datos ─────────────────────────────────────────────────────────────
  globales: EstadisticasGlobalesDTO | null = null;
  departamentos: EstadisticasDepartamentoDTO[] = [];
  escalas: EstadisticasEscalaDTO[] = [];
  tropicos: EstadisticasTropicoDTO[] = [];
  hatosConCoordenadas: HatoAdminDTO[] = [];

  // ── Mapa ──────────────────────────────────────────────────────────────
  mapaListo = false;
  private map: any = null;
  private L: any = null;

  // ── Gráficas ──────────────────────────────────────────────────────────
  @ViewChildren('chartEscala')
  chartEscalaRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  @ViewChildren('chartTropico')
  chartTropicoRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  @ViewChildren('chartDepartamento')
  chartDepartamentoRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  @ViewChildren('chartCompletitud')
  chartCompletitudRef!: QueryList<ElementRef<HTMLCanvasElement>>;

  private charts: Chart<any, any, any>[] = [];
  private viewReady = false;

  // ── Tab activo ────────────────────────────────────────────────────────
  tabActivo: 'resumen' | 'departamentos' | 'escalas' | 'tropicos' | 'mapa' = 'resumen';

  readonly tabs = [
    { id: 'resumen', label: 'Resumen', icono: '📊' },
    { id: 'departamentos', label: 'Departamentos', icono: '📍' },
    { id: 'escalas', label: 'Por escala', icono: '📐' },
    { id: 'tropicos', label: 'Por trópico', icono: '🌿' },
    { id: 'mapa', label: 'Mapa nacional', icono: '🗺️' },
  ] as const;

  private destroy$ = new Subject<void>();

  readonly mapId = `mapa-admin-${Math.random().toString(36).slice(2)}`;

  constructor(
    private estadisticasService: AdminEstadisticasService,
    private adminService: AdminService,
    private adminState: AdminStateService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarTodo();
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
  }

  // ── Carga de datos ────────────────────────────────────────────────────

  private cargarTodo(): void {
    this.cargarGlobales();
    this.cargarDepartamentos();
    this.cargarEscalas();
    this.cargarTropicos();
    this.cargarMapa();
  }

  private cargarGlobales(): void {
    this.loadingGlobales = true;
    this.estadisticasService.getGlobales().subscribe({
      next: (data) => {
        this.globales = data;
        this.loadingGlobales = false;
        this.cdr.markForCheck();
        this.intentarGrafica('completitud');
        this.intentarGrafica('escala');
        this.intentarGrafica('tropico');
      },
      error: () => {
        this.loadingGlobales = false;
        this.error = 'Error al cargar estadísticas globales.';
        this.cdr.markForCheck();
      },
    });
  }

  private cargarDepartamentos(): void {
    this.loadingDepartamentos = true;
    this.estadisticasService.getPorDepartamento().subscribe({
      next: (data) => {
        this.departamentos = data;
        this.loadingDepartamentos = false;
        this.cdr.markForCheck();
        this.intentarGrafica('departamento');
      },
      error: () => {
        this.loadingDepartamentos = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarEscalas(): void {
    this.loadingEscalas = true;
    this.estadisticasService.getPorEscala().subscribe({
      next: (data) => {
        this.escalas = data;
        this.loadingEscalas = false;
        this.cdr.markForCheck();
        this.intentarGrafica('escala');
      },
      error: () => {
        this.loadingEscalas = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarTropicos(): void {
    this.loadingTropicos = true;
    this.estadisticasService.getPorTropico().subscribe({
      next: (data) => {
        this.tropicos = data;
        this.loadingTropicos = false;
        this.cdr.markForCheck();
        this.intentarGrafica('tropico');
      },
      error: () => {
        this.loadingTropicos = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarMapa(): void {
    this.loadingMapa = true;
    this.adminService.getHatosFiltrados().subscribe({
      next: (hatos) => {
        this.hatosConCoordenadas = hatos.filter((h) => h.latitud != null && h.longitud != null);
        this.loadingMapa = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingMapa = false;
        this.cdr.markForCheck();
      },
    });
  }

  // ── Tabs ──────────────────────────────────────────────────────────────

  setTab(tab: typeof this.tabActivo): void {
    this.tabActivo = tab;
    this.cdr.markForCheck();

    if (tab === 'mapa' && !this.mapaListo) {
      setTimeout(() => this.inicializarMapa(), 100);
    }
    if (tab === 'resumen') {
      setTimeout(() => {
        this.intentarGrafica('escala');
        this.intentarGrafica('tropico');
        this.intentarGrafica('completitud');
      }, 50);
    }
    if (tab === 'departamentos') {
      setTimeout(() => this.intentarGrafica('departamento'), 50);
    }
  }

  // ── Gráficas ──────────────────────────────────────────────────────────

  private intentarGrafica(tipo: 'escala' | 'tropico' | 'departamento' | 'completitud'): void {
    if (!this.viewReady) {
      setTimeout(() => this.intentarGrafica(tipo), 100);
      return;
    }
    setTimeout(() => {
      switch (tipo) {
        case 'escala':
          this.buildGraficaEscala();
          break;
        case 'tropico':
          this.buildGraficaTropico();
          break;
        case 'departamento':
          this.buildGraficaDepartamento();
          break;
        case 'completitud':
          this.buildGraficaCompletitud();
          break;
      }
    }, 0);
  }

  private destruirGrafica(id: string): void {
    const idx = this.charts.findIndex((c) => (c as any)._adminId === id);
    if (idx !== -1) {
      this.charts[idx].destroy();
      this.charts.splice(idx, 1);
    }
  }

  private buildGraficaEscala(): void {
    const canvas = this.chartEscalaRef?.first?.nativeElement;
    if (!canvas || !this.escalas.length) return;
    this.destruirGrafica('escala');

    const colores = [
      'rgba(25,230,77,0.82)',
      'rgba(21,128,61,0.82)',
      'rgba(59,130,246,0.82)',
      'rgba(245,158,11,0.82)',
    ];

    const chart = new Chart<'doughnut', number[], string>(canvas, {
      type: 'doughnut',
      data: {
        labels: this.escalas.map((e) => e.escala),
        datasets: [
          {
            data: this.escalas.map((e) => e.totalHatos),
            backgroundColor: colores.slice(0, this.escalas.length),
            borderWidth: 2,
            borderColor: '#ffffff',
            hoverOffset: 6,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '60%',
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              color: '#374151',
              font: { size: 11 },
              boxWidth: 12,
              padding: 12,
            },
          },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'doughnut'>) =>
                ` ${ctx.label}: ${ctx.parsed} hatos (${this.escalas[
                  ctx.dataIndex
                ]?.porcentajeDelTotal?.toFixed(1)}%)`,
            },
          },
        },
      },
    });
    (chart as any)._adminId = 'escala';
    this.charts.push(chart);
  }

  private buildGraficaTropico(): void {
    const canvas = this.chartTropicoRef?.first?.nativeElement;
    if (!canvas || !this.tropicos.length) return;
    this.destruirGrafica('tropico');

    const colores = [
      'rgba(25,230,77,0.82)',
      'rgba(245,158,11,0.82)',
      'rgba(239,68,68,0.82)',
      'rgba(59,130,246,0.82)',
    ];

    const chart = new Chart<'bar', number[], string>(canvas, {
      type: 'bar',
      data: {
        labels: this.tropicos.map((t) => t.tropico),
        datasets: [
          {
            label: 'Hatos',
            data: this.tropicos.map((t) => t.totalHatos),
            backgroundColor: colores.slice(0, this.tropicos.length),
            borderRadius: 6,
            borderSkipped: false,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'bar'>) =>
                ` ${ctx.parsed.y} hatos (${this.tropicos[
                  ctx.dataIndex
                ]?.porcentajeDelTotal?.toFixed(1)}%)`,
            },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: '#374151', font: { size: 11 } },
          },
          y: {
            beginAtZero: true,
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: {
              color: '#9ca3af',
              font: { size: 10 },
              stepSize: 1,
            },
          },
        },
      },
    });
    (chart as any)._adminId = 'tropico';
    this.charts.push(chart);
  }

  private buildGraficaDepartamento(): void {
    const canvas = this.chartDepartamentoRef?.first?.nativeElement;
    if (!canvas || !this.departamentos.length) return;
    this.destruirGrafica('departamento');

    const top10 = [...this.departamentos].sort((a, b) => b.totalHatos - a.totalHatos).slice(0, 10);

    const chart = new Chart<'bar', number[], string>(canvas, {
      type: 'bar',
      data: {
        labels: top10.map((d) => d.departamento),
        datasets: [
          {
            label: 'Total hatos',
            data: top10.map((d) => d.totalHatos),
            backgroundColor: 'rgba(25,230,77,0.82)',
            borderColor: '#15c944',
            borderWidth: 1,
            borderRadius: 6,
          },
          {
            label: 'Con KPIs calculados',
            data: top10.map((d) => d.hatosConKpis),
            backgroundColor: 'rgba(21,128,61,0.82)',
            borderColor: '#15803d',
            borderWidth: 1,
            borderRadius: 6,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: {
              color: '#374151',
              font: { size: 11 },
              boxWidth: 14,
            },
          },
        },
        scales: {
          x: {
            beginAtZero: true,
            grid: { color: 'rgba(0,0,0,0.04)' },
            ticks: { color: '#9ca3af', font: { size: 10 } },
          },
          y: {
            grid: { display: false },
            ticks: { color: '#374151', font: { size: 11 } },
          },
        },
      },
    });
    (chart as any)._adminId = 'departamento';
    this.charts.push(chart);
    canvas.parentElement!.style.height = Math.max(280, top10.length * 38) + 'px';
  }

  private buildGraficaCompletitud(): void {
    const canvas = this.chartCompletitudRef?.first?.nativeElement;
    if (!canvas || !this.globales) return;
    this.destruirGrafica('completitud');

    const prom = this.globales.promedioCompletitud;

    const chart = new Chart<'doughnut', number[], string>(canvas, {
      type: 'doughnut',
      data: {
        labels: ['Completado', 'Pendiente'],
        datasets: [
          {
            data: [prom, 100 - prom],
            backgroundColor: ['rgba(25,230,77,0.9)', 'rgba(233,245,235,0.9)'],
            borderWidth: 0,
            hoverOffset: 4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '72%',
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx: TooltipItem<'doughnut'>) => ` ${Number(ctx.parsed).toFixed(1)}%`,
            },
          },
        },
      },
    });
    (chart as any)._adminId = 'completitud';
    this.charts.push(chart);
  }

  // ── Mapa Leaflet ──────────────────────────────────────────────────────

  private async inicializarMapa(): Promise<void> {
    const contenedor = document.getElementById(this.mapId);
    if (!contenedor || this.mapaListo) return;

    const L = await import('leaflet');
    this.L = L;
    (window as any).L = L;
    await import('leaflet.markercluster');
    const LG = (window as any).L;

    const iconDefault = L.icon({
      iconUrl: 'assets/leaflet/marker-icon.png',
      iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
      shadowUrl: 'assets/leaflet/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41],
    });
    L.Marker.prototype.options.icon = iconDefault;

    this.map = L.map(this.mapId, {
      center: [4.5709, -74.2973] as L.LatLngTuple,
      zoom: 6,
      zoomControl: true,
      scrollWheelZoom: true,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 18,
    }).addTo(this.map);

    this.L = LG;
    this.mapaListo = true;

    this.renderizarMarcadores();
  }

  private renderizarMarcadores(): void {
    if (!this.map || !this.L) return;

    const cluster = this.L.markerClusterGroup({
      showCoverageOnHover: false,
      maxClusterRadius: 60,
      disableClusteringAtZoom: 12,
      iconCreateFunction: (c: any) => {
        const count = c.getChildCount();
        return this.L.divIcon({
          html: `<div class="cluster-icon">${count}</div>`,
          className: 'cluster-wrapper',
          iconSize: [36, 36],
        });
      },
    });

    const bounds: any[] = [];

    for (const hato of this.hatosConCoordenadas) {
      if (hato.latitud == null || hato.longitud == null) continue;

      // Color por completitud
      const color =
        hato.porcentajeCompletitud >= 100
          ? '#19e64d'
          : hato.porcentajeCompletitud >= 75
            ? '#3b82f6'
            : hato.porcentajeCompletitud >= 50
              ? '#f59e0b'
              : '#ef4444';

      const icono = this.L.divIcon({
        html: `<div style="
          width:14px;height:14px;
          background:${color};
          border-radius:50%;
          border:2px solid #fff;
          box-shadow:0 2px 6px rgba(0,0,0,0.2)">
        </div>`,
        className: '',
        iconSize: [14, 14],
        iconAnchor: [7, 7],
      });

      const marker = this.L.marker([hato.latitud, hato.longitud], { icon: icono });

      marker.bindPopup(
        `
        <div style="font-size:12px;color:#163f2a;line-height:1.6">
          <strong>${hato.nombreHato}</strong><br/>
          ${hato.departamento} · ${hato.ciudad}<br/>
          Escala: ${hato.escala ?? '—'} · Trópico: ${hato.tropico ?? '—'}<br/>
          Completitud: <strong>${hato.porcentajeCompletitud}%</strong><br/>
          <span style="color:#6b7280;font-size:11px">
            ${hato.correoUsuario ?? '—'}
          </span>
        </div>`,
        { maxWidth: 220, className: 'hathor-popup' },
      );

      cluster.addLayer(marker);
      bounds.push([hato.latitud, hato.longitud]);
    }

    this.map.addLayer(cluster);

    if (bounds.length > 0) {
      this.map.fitBounds(this.L.latLngBounds(bounds), { padding: [40, 40] });
    }
  }

  // ── Navegación ────────────────────────────────────────────────────────

  irAHatos(): void {
    this.router.navigate(['/admin/hatos']);
  }

  irAReglas(): void {
    this.router.navigate(['/admin/practicas']);
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  get promedioCompletitudTexto(): string {
    if (!this.globales) return '—';
    return this.globales.promedioCompletitud.toFixed(1) + '%';
  }

  get porcentajeHatosConKpis(): string {
    if (!this.globales || this.globales.totalHatos === 0) return '—';
    return ((this.globales.hatosConKpis / this.globales.totalHatos) * 100).toFixed(1) + '%';
  }

  formatNum(n: number | null | undefined): string {
    if (n == null) return '—';
    return new Intl.NumberFormat('es-CO').format(Math.round(n));
  }

  formatDecimal(n: number | null | undefined): string {
    if (n == null) return '—';
    return n.toFixed(1);
  }

  ngOnDestroy(): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
    this.destroy$.next();
    this.destroy$.complete();
  }
}
