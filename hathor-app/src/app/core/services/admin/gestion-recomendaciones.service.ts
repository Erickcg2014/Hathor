import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface RecomendacionAdminDTO {
  idRecomendacion: number;
  idHato: string | null;
  nombreHato: string | null;
  tipo: string | null;
  mensaje: string | null;
  indicador: string | null;
  valorActual: number | null;
  valorReferencia: number | null;
  leida: boolean | null;
  prioridad: string;
  tipoEstado: string;
  fechaCreacion: string | null;
  escalaHato: string | null;
  tropicoHato: string | null;
  regionHato: string | null;
  idRegla: number | null;
  codigoKpiRegla: string | null;
  nombreKpiRegla: string | null;
}

export interface CrearRecomendacionDTO {
  idHato: string;
  tipo: string | null;
  mensaje: string;
  indicador: string | null;
  valorActual: number | null;
  valorReferencia: number | null;
  prioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  idRegla: number | null;
}

export interface CambiarEstadoDTO {
  tipoEstado: 'ACTIVA' | 'DESCARTADA' | 'COMPLETADA';
}

@Injectable({ providedIn: 'root' })
export class GestionRecomendacionesService {
  private readonly API_URL = `${environment.apiUrl}/Admin/GestionRecomendaciones`;

  constructor(private http: HttpClient) {}

  getByHato(idHato: string): Observable<RecomendacionAdminDTO[]> {
    return this.http.get<RecomendacionAdminDTO[]>(`${this.API_URL}/${idHato}`, {
      params: { _t: Date.now().toString() },
    });
  }

  getFiltradas(
    idHato?: string,
    tipoEstado?: string,
    prioridad?: string,
  ): Observable<RecomendacionAdminDTO[]> {
    const params: any = { _t: Date.now().toString() };
    if (idHato) params['idHato'] = idHato;
    if (tipoEstado) params['tipoEstado'] = tipoEstado;
    if (prioridad) params['prioridad'] = prioridad;
    return this.http.get<RecomendacionAdminDTO[]>(this.API_URL, { params });
  }

  crearRecomendacion(dto: CrearRecomendacionDTO): Observable<RecomendacionAdminDTO> {
    return this.http.post<RecomendacionAdminDTO>(this.API_URL, dto);
  }

  cambiarEstado(idRecomendacion: number, dto: CambiarEstadoDTO): Observable<RecomendacionAdminDTO> {
    return this.http.put<RecomendacionAdminDTO>(`${this.API_URL}/${idRecomendacion}/estado`, dto);
  }

  eliminar(idRecomendacion: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${idRecomendacion}`);
  }
}
