import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface ReglaPracticaDTO {
  id: number;
  idPractica: number;
  nombrePractica: string;
  categoriaPractica: string | null;
  dificultadPractica: string | null;
  orden: number;
}

export interface ReglaAdminDTO {
  idRegla: number;
  idKpi: number | null;
  codigoKpi: string | null;
  nombreKpi: string | null;
  operador: string;
  umbral1: number | null;
  umbral2: number | null;
  umbralTipo: string;
  estadoKpiObjetivo: string;
  escalaAplicable: string;
  estado: string;
  mensaje: string | null;
  prioridad: number | null;
  practicas: ReglaPracticaDTO[];
}

export interface VincularPracticaDTO {
  idPractica: number;
  orden: number;
}

export interface CrearReglaDTO {
  idKpi: number;
  operador: string;
  umbral1: number | null;
  umbral2: number | null;
  umbralTipo: string;
  estadoKpiObjetivo: string;
  escalaAplicable: string;
  mensaje: string | null;
  prioridad: number | null;
  practicas: VincularPracticaDTO[];
}

export interface EditarReglaDTO {
  operador?: string;
  umbral1?: number | null;
  umbral2?: number | null;
  umbralTipo?: string;
  estadoKpiObjetivo?: string;
  escalaAplicable?: string;
  mensaje?: string | null;
  prioridad?: number | null;
  estado?: string;
  practicas?: VincularPracticaDTO[];
}

@Injectable({ providedIn: 'root' })
export class GestionReglasService {
  private readonly API_URL = `${environment.apiUrl}/Admin/GestionReglas`;

  constructor(private http: HttpClient) {}

  getReglas(estado?: string, escala?: string, idKpi?: number): Observable<ReglaAdminDTO[]> {
    const params: any = { _t: Date.now().toString() };
    if (estado) params['estado'] = estado;
    if (escala) params['escala'] = escala;
    if (idKpi) params['idKpi'] = idKpi.toString();
    return this.http.get<ReglaAdminDTO[]>(this.API_URL, { params });
  }

  getReglaById(idRegla: number): Observable<ReglaAdminDTO> {
    return this.http.get<ReglaAdminDTO>(`${this.API_URL}/${idRegla}`);
  }

  crearRegla(dto: CrearReglaDTO): Observable<ReglaAdminDTO> {
    return this.http.post<ReglaAdminDTO>(this.API_URL, dto);
  }

  editarRegla(idRegla: number, dto: EditarReglaDTO): Observable<ReglaAdminDTO> {
    return this.http.put<ReglaAdminDTO>(`${this.API_URL}/${idRegla}`, dto);
  }

  desactivarRegla(idRegla: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${idRegla}`);
  }

  vincularPractica(idRegla: number, dto: VincularPracticaDTO): Observable<ReglaAdminDTO> {
    return this.http.post<ReglaAdminDTO>(`${this.API_URL}/${idRegla}/practicas`, dto);
  }

  desvincularPractica(idRegla: number, idPractica: number): Observable<ReglaAdminDTO> {
    return this.http.delete<ReglaAdminDTO>(`${this.API_URL}/${idRegla}/practicas/${idPractica}`);
  }

  reordenarPracticas(idRegla: number, practicas: VincularPracticaDTO[]): Observable<ReglaAdminDTO> {
    return this.http.put<ReglaAdminDTO>(
      `${this.API_URL}/${idRegla}/practicas/reordenar`,
      practicas,
    );
  }
}
