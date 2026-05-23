import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface ReglaResumenDTO {
  idRegla: number;
  codigoKpi: string | null;
  nombreKpi: string | null;
  operador: string;
  escalaAplicable: string;
  estado: string;
  orden: number;
}

export interface PracticaAdminDTO {
  idPractica: number;
  nombre: string;
  descripcion: string | null;
  objetivo: string | null;
  categoria: string | null;
  impactoEsperado: string | null;
  estado: string;
  pasos: string | null;
  kpiImpactado: string | null;
  dificultad: string;
  duracionDias: number | null;
  escala: string;
  tropicaAplicable: string;
  reglasVinculadas: ReglaResumenDTO[];
}

export interface CrearPracticaDTO {
  nombre: string;
  descripcion: string | null;
  objetivo: string | null;
  categoria: string | null;
  impactoEsperado: string | null;
  pasos: string | null;
  kpiImpactado: string | null;
  dificultad: string;
  duracionDias: number | null;
  escala: string;
  tropicaAplicable: string;
}

export interface EditarPracticaDTO {
  nombre?: string;
  descripcion?: string | null;
  objetivo?: string | null;
  categoria?: string | null;
  impactoEsperado?: string | null;
  pasos?: string | null;
  kpiImpactado?: string | null;
  dificultad?: string;
  duracionDias?: number | null;
  escala?: string;
  tropicaAplicable?: string;
  estado?: string;
}

@Injectable({ providedIn: 'root' })
export class GestionPracticasService {
  private readonly API_URL = `${environment.apiUrl}/Admin/GestionPracticas`;

  constructor(private http: HttpClient) {}

  getPracticas(
    estado?: string,
    categoria?: string,
    escala?: string,
    dificultad?: string,
  ): Observable<PracticaAdminDTO[]> {
    const params: any = { _t: Date.now().toString() };
    if (estado) params['estado'] = estado;
    if (categoria) params['categoria'] = categoria;
    if (escala) params['escala'] = escala;
    if (dificultad) params['dificultad'] = dificultad;
    return this.http.get<PracticaAdminDTO[]>(this.API_URL, { params });
  }

  getPracticaById(idPractica: number): Observable<PracticaAdminDTO> {
    return this.http.get<PracticaAdminDTO>(`${this.API_URL}/${idPractica}`);
  }

  crearPractica(dto: CrearPracticaDTO): Observable<PracticaAdminDTO> {
    return this.http.post<PracticaAdminDTO>(this.API_URL, dto);
  }

  editarPractica(idPractica: number, dto: EditarPracticaDTO): Observable<PracticaAdminDTO> {
    return this.http.put<PracticaAdminDTO>(`${this.API_URL}/${idPractica}`, dto);
  }

  desactivarPractica(idPractica: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${idPractica}`);
  }
}
