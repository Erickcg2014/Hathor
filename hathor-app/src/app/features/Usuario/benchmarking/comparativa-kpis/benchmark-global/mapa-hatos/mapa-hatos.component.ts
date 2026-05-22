import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  Input,
  OnChanges,
  OnDestroy,
  PLATFORM_ID,
  SimpleChanges,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

import { HatoAnonimizadoDTO } from '../../../../../../core/services/benchmarking/benchmarking.service';

@Component({
  selector: 'app-mapa-hatos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mapa-hatos.component.html',
  styleUrl: './mapa-hatos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapaHatosComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() modoGeneral = false;
  @Input() hatos: HatoAnonimizadoDTO[] = [];

  private map: any = null;
  private cluster: any = null;
  private L: any = null;

  private mapaListo = false;
  private hatosPendientes: HatoAnonimizadoDTO[] | null = null;

  readonly mapId = `mapa-hatos-${Math.random().toString(36).slice(2)}`;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      setTimeout(() => this.inicializarMapa(), 100);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['hatos'] && !changes['hatos'].firstChange) {
      if (this.mapaListo) {
        this.actualizarMarcadores();
      } else {
        this.hatosPendientes = this.hatos;
      }
    }
  }

  private async inicializarMapa(): Promise<void> {
    const contenedor = document.getElementById(this.mapId);
    if (!contenedor) return;

    const L = await import('leaflet');
    this.L = L;

    (window as any).L = L;

    await import('leaflet.markercluster');

    const LG = (window as any).L;

    const iconDefault = L.icon({
      iconUrl: 'leaflet/marker-icon.png',
      iconRetinaUrl: 'leaflet/marker-icon-2x.png',
      shadowUrl: 'leaflet/marker-shadow.png',
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

    if (this.hatosPendientes) {
      this.hatos = this.hatosPendientes;
      this.hatosPendientes = null;
    }

    this.actualizarMarcadores();
  }

  private actualizarMarcadores(): void {
    if (!this.map || !this.L) return;

    this.map.eachLayer((layer: any) => {
      if (layer instanceof this.L.Marker || layer instanceof this.L.LayerGroup) {
        if (!layer._url) this.map.removeLayer(layer);
      }
    });

    if (this.cluster) {
      try {
        this.map.removeLayer(this.cluster);
      } catch (e) {
        console.warn('Error eliminando cluster anterior:', e);
      }
      this.cluster = null;
    }

    const hatosAnonimos = this.hatos.filter((h) => !h.esMiHato);
    const miHato = this.hatos.find((h) => h.esMiHato);
    const usarCluster = this.modoGeneral ? hatosAnonimos.length > 10 : hatosAnonimos.length > 3;

    if (usarCluster) {
      this.cluster = this.L.markerClusterGroup({
        showCoverageOnHover: false,
        maxClusterRadius: 60,
        disableClusteringAtZoom: 12,
        spiderfyOnMaxZoom: true,
        iconCreateFunction: (c: any) => {
          const count = c.getChildCount();
          return this.L.divIcon({
            html: `<div class="cluster-icon">${count}</div>`,
            className: 'cluster-wrapper',
            iconSize: [36, 36],
          });
        },
      });
    }

    const bounds: any[] = [];
    let marcadoresCargados = 0;

    // ── Hatos anónimos ─────────────────────────────────────
    for (const hato of hatosAnonimos) {
      const lat = hato.latitudDifuminada;
      const lng = hato.longitudDifuminada;

      if (lat == null || lng == null) {
        console.warn('Hato sin coordenadas — omitido:', hato.alias);
        continue;
      }

      const icono = this.crearIconoHatoAnonimo(this.modoGeneral ? null : hato.interpretacion);
      const marker = this.L.marker([lat, lng], { icon: icono });

      marker.bindPopup(
        `<div class="popup-anonimo">
          <strong>${hato.alias}</strong><br/>
          ${
            !this.modoGeneral
              ? `Trópico: ${hato.tropico ?? '—'}<br/>
          Escala: ${hato.escala ?? '—'}<br/>`
              : ''
          }
          Región: ${hato.departamento ?? '—'}
        </div>`,
        { maxWidth: 200, className: 'hathor-popup' },
      );

      if (usarCluster) {
        this.cluster.addLayer(marker);
      } else {
        marker.addTo(this.map);
      }

      bounds.push([lat, lng]);
      marcadoresCargados++;
    }

    if (usarCluster) {
      this.map.addLayer(this.cluster);
    }

    // ── Mi hato ────────────────────────────────────────────
    if (miHato?.latitudDifuminada != null && miHato?.longitudDifuminada != null) {
      const lat = miHato.latitudDifuminada;
      const lng = miHato.longitudDifuminada;

      const marcador = this.L.marker([lat, lng], {
        icon: this.crearIconoMiHato(),
        zIndexOffset: 1000,
      });

      marcador.bindPopup(
        `<div class="popup-mihato">
        <strong>📍 Tu hato</strong><br/>
        ${miHato.tropico ?? '—'} · ${miHato.escala ?? '—'}<br/>
        ${miHato.departamento ?? '—'}
      </div>`,
        { maxWidth: 200, className: 'hathor-popup' },
      );

      marcador.addTo(this.map);
      bounds.push([lat, lng]);
      marcadoresCargados++;
    } else {
      console.warn('Mi hato sin coordenadas o no encontrado — no se agrega al mapa');
    }

    // ── Ajustar vista ──────────────────────────────────────
    if (bounds.length > 0) {
      this.map.fitBounds(this.L.latLngBounds(bounds), { padding: [50, 50] });
    } else {
      console.warn('No hay bounds — vista no ajustada');
    }
  }

  private crearIconoMiHato(): any {
    return this.L.divIcon({
      html: `<div class="marcador-mihato">🏠</div>`,
      className: '',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -42],
    });
  }

  private crearIconoHatoAnonimo(interpretacion: string | null): any {
    if (this.modoGeneral) {
      return this.L.icon({
        iconUrl: 'assets/leaflet/marker-icon.png',
        iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
        shadowUrl: 'assets/leaflet/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
      });
    }

    const colorMap: Record<string, string> = {
      OPTIMO: '#15c944',
      BUENO: '#3b82f6',
      ACEPTABLE: '#f59e0b',
      CRITICO: '#ef4444',
    };
    const color = colorMap[interpretacion ?? ''] ?? '#9ca3af';

    return this.L.divIcon({
      html: `<div class="marcador-anonimo" style="background:${color}">
             <div class="marcador-anonimo-inner"></div>
           </div>`,
      className: '',
      iconSize: [16, 16],
      iconAnchor: [8, 8],
    });
  }

  ngOnDestroy(): void {
    this.mapaListo = false;
    this.hatosPendientes = null;
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
    this.L = null;
  }
}
