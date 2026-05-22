import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── DTOs de configuración ──────────────────────────────────────────────────

export interface ReporteConfigDTO {
  incluirResumenEjecutivo: boolean;
  incluirKpis: boolean;
  incluirBenchmarking: boolean;
  incluirRanking: boolean;
  incluirFinanzas: boolean;
  incluirProduccion: boolean;
  incluirPracticas: boolean;
  nivelBenchmark: 'NACIONAL' | 'TROPICO' | 'TROPICO_ESCALA';
  periodoDesde: string | null;
  periodoHasta: string | null;
  tituloPersonalizado: string | null;
}

export interface ReporteHistorialDTO {
  idReporte: number;
  tipo: string;
  nombre: string;
  fechaGeneracion: string | null;
  periodoDesde: string | null;
  periodoHasta: string | null;
  urlArchivo: string | null;
  tamanioBytes: number | null;
  estado: string;
  tamanioFormateado: string | null;
  configuracion: ReporteConfigDTO | null;
}

export const REPORTE_CONFIG_DEFAULT: ReporteConfigDTO = {
  incluirResumenEjecutivo: true,
  incluirKpis: true,
  incluirBenchmarking: true,
  incluirRanking: true,
  incluirFinanzas: true,
  incluirProduccion: true,
  incluirPracticas: true,
  nivelBenchmark: 'NACIONAL',
  periodoDesde: null,
  periodoHasta: null,
  tituloPersonalizado: null,
};

// ── Service ───────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly API_URL = `${environment.apiUrl}/Reporte`;

  constructor(private http: HttpClient) {}

  generarReporte(idHato: string, config: ReporteConfigDTO): Observable<Blob> {
    return this.http.post(`${this.API_URL}/${idHato}/generar`, config, { responseType: 'blob' });
  }

  getHistorial(idHato: string): Observable<ReporteHistorialDTO[]> {
    return this.http.get<ReporteHistorialDTO[]>(`${this.API_URL}/${idHato}/historial`, {
      params: { _t: Date.now().toString() },
    });
  }

  regenerarReporte(idReporte: number): Observable<Blob> {
    return this.http.post(`${this.API_URL}/${idReporte}/regenerar`, {}, { responseType: 'blob' });
  }

  descargarPdf(blob: Blob, nombreHato: string): void {
    const fecha = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    const nombre = nombreHato
      .toLowerCase()
      .replace(/\s+/g, '-')
      .replace(/[^a-z0-9-]/g, '');
    const filename = `reporte-${nombre}-${fecha}.pdf`;

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();

    setTimeout(() => URL.revokeObjectURL(url), 100);
  }
}
