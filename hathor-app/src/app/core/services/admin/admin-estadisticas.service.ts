import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface EntradaEstadisticaDTO {
  clave: string;
  cantidad: number;
  porcentaje: number | null;
}

export interface EstadisticasGlobalesDTO {
  totalHatos: number;
  hatosConKpis: number;
  promedioCompletitud: number;
  totalDepartamentos: number;
  hatosPorEscala: EntradaEstadisticaDTO[];
  hatosPorTropico: EntradaEstadisticaDTO[];
  hatosPorDepartamento: EntradaEstadisticaDTO[];
}

export interface EstadisticasDepartamentoDTO {
  departamento: string;
  totalHatos: number;
  promedioCompletitud: number;
  hatosConKpis: number;
  kpiDestacado: string | null;
  promedioKpiDestacado: number | null;
}

export interface EstadisticasEscalaDTO {
  escala: string;
  totalHatos: number;
  porcentajeDelTotal: number;
  promedioLitrosVacaDia: number | null;
  promedioMargenNeto: number | null;
}

export interface EstadisticasTropicoDTO {
  tropico: string;
  totalHatos: number;
  porcentajeDelTotal: number;
  promedioLitrosVacaDia: number | null;
  promedioCargarAnimal: number | null;
}

@Injectable({ providedIn: 'root' })
export class AdminEstadisticasService {
  private readonly API_URL = `${environment.apiUrl}/Admin/Estadisticas`;

  constructor(private http: HttpClient) {}

  getGlobales(): Observable<EstadisticasGlobalesDTO> {
    return this.http.get<EstadisticasGlobalesDTO>(`${this.API_URL}/globales`, {
      params: { _t: Date.now().toString() },
    });
  }

  getPorDepartamento(): Observable<EstadisticasDepartamentoDTO[]> {
    return this.http.get<EstadisticasDepartamentoDTO[]>(`${this.API_URL}/por-departamento`, {
      params: { _t: Date.now().toString() },
    });
  }

  getPorEscala(): Observable<EstadisticasEscalaDTO[]> {
    return this.http.get<EstadisticasEscalaDTO[]>(`${this.API_URL}/por-escala`, {
      params: { _t: Date.now().toString() },
    });
  }

  getPorTropico(): Observable<EstadisticasTropicoDTO[]> {
    return this.http.get<EstadisticasTropicoDTO[]>(`${this.API_URL}/por-tropico`, {
      params: { _t: Date.now().toString() },
    });
  }
}
