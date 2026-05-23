import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── DTOs ─────────────────────────────────────────────────────────────────────

export interface RankingResumenDTO {
  posicionNacional: number | null;
  posicionRegional: number | null;
  totalHatosNacional: number;
  totalHatosRegional: number;
  scoreCompuesto: number | null;
  kpisCriticos: number;
  kpisAceptables: number;
  kpisBuenos: number;
  kpisOptimos: number;
  fechaUltimoCalculo: string | null;
}

export interface HatoRankingItem {
  posicion: number;
  alias: string;
  valor: number | null;
  esMiHato: boolean;
}

export interface RankingCompuestoDTO {
  posicionMiHato: number;
  totalHatos: number;
  scoreMiHato: number | null;
  scorePromedio: number | null;
  scoreTop: number | null;
  regionFiltrada: string | null;
  ranking: HatoRankingItem[];
}

export interface RankingPorKpiDTO {
  codigoKpi: string;
  nombreKpi: string;
  unidadKpi: string;
  categoria: string;
  valorMiHato: number | null;
  posicionMiHato: number;
  totalHatos: number;
  regionFiltrada: string | null;
  ranking: HatoRankingItem[];
}

export interface PuntoEvolucion {
  fecha: string;
  posicion: number;
  totalHatos: number;
  valorHato: number | null;
  percentilEnFecha: number | null;
}

export interface EvolucionPosicionDTO {
  codigoKpi: string;
  nombreKpi: string;
  unidadKpi: string;
  mesesConsultados: number;
  puntos: PuntoEvolucion[];
  datosInsuficientes: boolean;
}

@Injectable({ providedIn: 'root' })
export class RankingService {
  private readonly API_URL = `${environment.apiUrl}/Benchmarking`;

  constructor(private http: HttpClient) {}

  getResumenRanking(idHato: string): Observable<RankingResumenDTO> {
    return this.http.get<RankingResumenDTO>(`${this.API_URL}/${idHato}/ranking/resumen`);
  }

  getRankingCompuesto(idHato: string, region?: string): Observable<RankingCompuestoDTO> {
    const params: any = {};
    if (region) params['region'] = region;
    return this.http.get<RankingCompuestoDTO>(`${this.API_URL}/${idHato}/ranking/compuesto`, {
      params,
    });
  }

  getRankingPorKpi(
    idHato: string,
    codigoKpi: string,
    region?: string,
  ): Observable<RankingPorKpiDTO> {
    const params: any = {};
    if (region) params['region'] = region;
    return this.http.get<RankingPorKpiDTO>(`${this.API_URL}/${idHato}/ranking/kpi/${codigoKpi}`, {
      params,
    });
  }

  getEvolucionPosicion(
    idHato: string,
    codigoKpi: string,
    meses: 3 | 6 | 12 = 6,
  ): Observable<EvolucionPosicionDTO> {
    return this.http.get<EvolucionPosicionDTO>(`${this.API_URL}/${idHato}/ranking/evolucion`, {
      params: { codigoKpi, meses: meses.toString() },
    });
  }
}
