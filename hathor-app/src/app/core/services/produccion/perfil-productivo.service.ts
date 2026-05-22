import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface PerfilProductivo {
  idPerfil?: string;
  razaPredominante: string;
  produccionDiariaLitros: number;
  precioLitroPromedio: number;
  vacasEnOrdenio?: number;
  frecuenciaOrdenio?: number;
  sistemaOrdenio?: string;
  destinoLeche?: string;
  periodoLactanciaPromedio?: number;
  fechaRegistro?: string;
  fechaActualizacion?: string;
}

export interface ActualizarPerfilRapidoDTO {
  vacasEnOrdenio?: number;
  produccionDiariaLitros?: number;
  precioLitroPromedio?: number;
  periodoLactanciaPromedio?: number;
  frecuenciaOrdenio?: number;
}

export interface RegistroPerfilProductivoDTO {
  idHato: string;
  razaPredominante: string;
  produccionDiariaLitros: number;
  precioLitroPromedio: number;
  vacasEnOrdenio?: number;
  frecuenciaOrdenio?: number;
  sistemaOrdenio?: string;
  destinoLeche?: string;
  periodoLactanciaPromedio?: number;
}

@Injectable({ providedIn: 'root' })
export class PerfilProductivoService {
  private readonly API_URL = `${environment.apiUrl}/PerfilProductivo`;

  constructor(private http: HttpClient) {}

  crearPerfil(dto: RegistroPerfilProductivoDTO): Observable<PerfilProductivo> {
    return this.http.post<PerfilProductivo>(this.API_URL, dto);
  }

  actualizarPerfil(idHato: string, dto: RegistroPerfilProductivoDTO): Observable<PerfilProductivo> {
    return this.http.put<PerfilProductivo>(`${this.API_URL}/${idHato}`, dto);
  }

  getPerfilByHato(idHato: string): Observable<PerfilProductivo> {
    return this.http.get<PerfilProductivo>(`${this.API_URL}/${idHato}`);
  }

  actualizarParcial(idHato: string, dto: ActualizarPerfilRapidoDTO): Observable<PerfilProductivo> {
    return this.http.patch<PerfilProductivo>(`${this.API_URL}/${idHato}`, dto);
  }
}
