import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface RecomendacionPracticaResumenDTO {
  idHatoPractica: number | null;
  nombre: string;
  dificultad: string;
  estado: string;
  porcentajeAvance: number;
}

export interface RecomendacionDTO {
  idRecomendacion: number;
  mensaje: string;
  prioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  indicador: string;
  valorActual: number;
  valorReferencia: number;
  fechaCreacion: string;
  tipoEstado: 'ACTIVA' | 'DESCARTADA' | 'COMPLETADA';
  leida: boolean;
  escalaHato: string;
  tropicoHato: string;
  practicas: RecomendacionPracticaResumenDTO[];
}

export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

@Injectable({ providedIn: 'root' })
export class RecomendacionesPracticasService {
  private readonly API_URL = `${environment.apiUrl}/recomendaciones`;

  constructor(private http: HttpClient) {}

  getActivasByHato(
    idHato: string,
    page: number = 0,
    size: number = 10,
  ): Observable<PageResponse<RecomendacionDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<RecomendacionDTO>>(`${this.API_URL}/${idHato}`, { params });
  }

  countNoLeidas(idHato: string): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/${idHato}/contador`);
  }

  marcarComoLeida(idRecomendacion: number): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${idRecomendacion}/leer`, {});
  }

  descartar(idRecomendacion: number): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${idRecomendacion}/descartar`, {});
  }
}
