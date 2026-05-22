import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface InversionPlaneadaDTO {
  idInversion: number;
  descripcion: string;
  monto: number;
  mesEjecucion: string;
  mesEjecucionLabel: string;
  retornoEsperadoPct: number | null;
  mesesRetorno: number | null;
  estado: string;
  fechaCreacion: string | null;
  idCategoria: string | null;
  nombreCategoria: string | null;
  tipoCategoria: string | null;
  retornoMensualEstimado: number | null;
}

export interface InversionResumenDTO {
  idInversion: number;
  descripcion: string;
  monto: number;
  nombreCategoria: string;
  icono: string;
}

export interface CrearInversionDTO {
  descripcion: string;
  monto: number;
  mesEjecucion: string;
  retornoEsperadoPct: number | null;
  mesesRetorno: number | null;
  idCategoria: string | null;
}

export interface ActualizarInversionDTO {
  descripcion?: string;
  monto?: number;
  mesEjecucion?: string;
  retornoEsperadoPct?: number | null;
  mesesRetorno?: number | null;
  idCategoria?: string | null;
  estado?: string;
}

@Injectable({ providedIn: 'root' })
export class InversionService {
  private readonly API_URL = `${environment.apiUrl}/inversiones`;

  constructor(private http: HttpClient) {}

  getByHato(idHato: string): Observable<InversionPlaneadaDTO[]> {
    return this.http.get<InversionPlaneadaDTO[]>(`${this.API_URL}/${idHato}`, {
      params: { _t: Date.now().toString() },
    });
  }

  crear(idHato: string, dto: CrearInversionDTO): Observable<InversionPlaneadaDTO> {
    return this.http.post<InversionPlaneadaDTO>(`${this.API_URL}/${idHato}`, dto);
  }

  actualizar(idInversion: number, dto: ActualizarInversionDTO): Observable<InversionPlaneadaDTO> {
    return this.http.put<InversionPlaneadaDTO>(`${this.API_URL}/${idInversion}`, dto);
  }

  cancelar(idInversion: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${idInversion}`);
  }
}
