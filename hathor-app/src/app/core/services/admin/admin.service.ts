import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface HatoAdminDTO {
  idHato: string;
  nombreHato: string;
  departamento: string;
  ciudad: string;
  tropico: string | null;
  escala: string | null;
  tipoHato: string | null;
  areaHato: number | null;
  altitud: number | null;
  porcentajeCompletitud: number;
  nombreUsuario: string | null;
  apellidoUsuario: string | null;
  correoUsuario: string | null;
  latitud: number | null;
  longitud: number | null;
}

export interface HatoAdminDetalleDTO {
  idHato: string;
  nombreHato: string;
  departamento: string;
  ciudad: string;
  tropico: string | null;
  escala: string | null;
  tipoHato: string | null;
  areaHato: number | null;
  areaPastoreo: number | null;
  altitud: number | null;
  cantCorrales: number | null;
  cantSalasOrdenio: number | null;
  capacidadAlmacenarLeche: number | null;
  cantEmpleadosPermanentes: number | null;
  cantEmpleadosTemporales: number | null;
  gastoMensualNomina: number | null;
  gastoMensualAlimentacion: number | null;
  porcentajeCompletitud: number;
  latitud: number | null;
  longitud: number | null;
  idUsuario: string | null;
  nombreUsuario: string | null;
  apellidoUsuario: string | null;
  correoUsuario: string | null;
  celularUsuario: string | null;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly API_URL = `${environment.apiUrl}/Admin`;

  constructor(private http: HttpClient) {}

  getHatosFiltrados(
    departamento?: string,
    region?: string,
    tropico?: string,
    escala?: string,
    tipoHato?: string,
  ): Observable<HatoAdminDTO[]> {
    const params: any = { _t: Date.now().toString() };
    if (departamento) params['departamento'] = departamento;
    if (region) params['region'] = region;
    if (tropico) params['tropico'] = tropico;
    if (escala) params['escala'] = escala;
    if (tipoHato) params['tipoHato'] = tipoHato;
    return this.http.get<HatoAdminDTO[]>(`${this.API_URL}/Hatos`, { params });
  }

  getDetalleHato(idHato: string): Observable<HatoAdminDetalleDTO> {
    return this.http.get<HatoAdminDetalleDTO>(`${this.API_URL}/Hatos/${idHato}`);
  }

  getDepartamentos(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/Hatos/filtros/departamentos`);
  }

  getRegiones(departamento: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/Hatos/filtros/regiones`, {
      params: { departamento },
    });
  }

  getTropicos(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/Hatos/filtros/tropicos`);
  }

  getEscalas(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/Hatos/filtros/escalas`);
  }
}
