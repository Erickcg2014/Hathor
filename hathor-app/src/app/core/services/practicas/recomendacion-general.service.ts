import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface RecomendacionGeneralDTO {
  idRecomendacion: number;
  tipo: string;
  subtipo: string | null;
  titulo: string;
  mensaje: string;
  prioridad: string;
  estado: string;
  leida: boolean;
  fechaCreacion: string | null;
  fechaExpiracion: string | null;
  icono: string | null;
  urlAccion: string | null;
  labelAccion: string | null;
  esGlobal: boolean;
  tiempoRelativo: string | null;
}

export interface RecomendacionesResumenDTO {
  totalNoLeidas: number;
  altas: RecomendacionGeneralDTO[];
  medias: RecomendacionGeneralDTO[];
  bajas: RecomendacionGeneralDTO[];
}

export interface CrearRecomendacionAdminDTO {
  titulo: string;
  mensaje: string;
  prioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  icono?: string;
  urlAccion?: string;
  labelAccion?: string;
  idHato?: string | null;
  fechaExpiracion?: string | null;
}

export interface CrearRecomendacionClimaDTO {
  subtipo: string;
  titulo: string;
  mensaje: string;
  icono: string;
}

@Injectable({ providedIn: 'root' })
export class RecomendacionGeneralService {
  private readonly API_URL = `${environment.apiUrl}/recomendaciones-generales`;

  constructor(private http: HttpClient) {}

  // ── Usuario ───────────────────────────────────────────────────────────

  getResumen(idHato: string): Observable<RecomendacionesResumenDTO> {
    return this.http.get<RecomendacionesResumenDTO>(`${this.API_URL}/${idHato}`, {
      params: { _t: Date.now().toString() },
    });
  }

  marcarLeida(idRecomendacion: number): Observable<any> {
    return this.http.patch(`${this.API_URL}/${idRecomendacion}/leida`, {});
  }

  marcarTodasLeidas(idHato: string): Observable<any> {
    return this.http.patch(`${this.API_URL}/${idHato}/leidas`, {});
  }

  // ── Clima ─────────────────────────────────────────────────────────────

  crearClimatica(idHato: string, subtipo: string): Observable<RecomendacionGeneralDTO[]> {
    return this.http.post<RecomendacionGeneralDTO[]>(`${this.API_URL}/${idHato}/clima`, {
      subtipo,
    });
  }

  limpiarClimaticas(idHato: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/${idHato}/clima`);
  }

  // ── Admin ─────────────────────────────────────────────────────────────

  crearAdmin(dto: CrearRecomendacionAdminDTO): Observable<RecomendacionGeneralDTO> {
    return this.http.post<RecomendacionGeneralDTO>(`${this.API_URL}/admin`, dto);
  }

  getAdmin(): Observable<RecomendacionGeneralDTO[]> {
    return this.http.get<RecomendacionGeneralDTO[]>(`${this.API_URL}/admin`, {
      params: { _t: Date.now().toString() },
    });
  }

  eliminar(idRecomendacion: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${idRecomendacion}`);
  }
}
