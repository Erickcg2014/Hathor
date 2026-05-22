import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface AlertaHatoDTO {
  idAlerta: number;
  tipo: string;
  severidad: string;
  titulo: string;
  mensaje: string;
  leida: boolean;
  fechaCreacion: string | null;
  fechaExpiracion: string | null;
  codigoKpi: string | null;
  valorReferencia: number | null;
  estado: string;
  tiempoRelativo: string | null;
}

export interface AlertasResumenDTO {
  totalNoLeidas: number;
  severidadMaxima: string; // CRITICA | PREVENTIVA | OPORTUNIDAD | NINGUNA
  criticas: AlertaHatoDTO[];
  preventivas: AlertaHatoDTO[];
  oportunidades: AlertaHatoDTO[];
}

export interface AlertasAdminResumenDTO {
  hatosCriticos: number;
  alertasCriticas: AlertaHatoDTO[];
}

@Injectable({ providedIn: 'root' })
export class AlertasService {
  private readonly API_URL = `${environment.apiUrl}/alertas`;
  private _refrescarBadge$ = new Subject<void>();

  readonly refrescarBadge$ = this._refrescarBadge$.asObservable();

  notificarCambio(): void {
    this._refrescarBadge$.next();
  }

  constructor(private http: HttpClient) {}

  getResumen(idHato: string): Observable<AlertasResumenDTO> {
    return this.http.get<AlertasResumenDTO>(`${this.API_URL}/${idHato}`, {
      params: { _t: Date.now().toString() },
    });
  }

  marcarLeida(idAlerta: number): Observable<any> {
    return this.http.patch(`${this.API_URL}/${idAlerta}/leida`, {});
  }

  marcarTodasLeidas(idHato: string): Observable<any> {
    return this.http.patch(`${this.API_URL}/${idHato}/leidas`, {});
  }

  evaluarAlertas(idHato: string): Observable<any> {
    return this.http.post(`${this.API_URL}/${idHato}/evaluar`, {});
  }

  getResumenAdmin(): Observable<AlertasAdminResumenDTO> {
    return this.http.get<AlertasAdminResumenDTO>(`${this.API_URL}/admin/resumen`, {
      params: { _t: Date.now().toString() },
    });
  }
}
