import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export type EstadoPractica = 'PENDIENTE' | 'EN_CURSO' | 'COMPLETADA';

export interface HatoPracticaDTO {
  idHatoPractica: string;
  estado: EstadoPractica;
  porcentajeAvance: number;
  fechaInicio: string | null;
  fechaFin: string | null;
  idPractica: number;
  nombrePractica: string;
  categoria: string;
  dificultad: string;
  kpiImpactado: string;
  duracionDias: number;
  idRecomendacion: number | null;
}

export interface PracticaDetalleDTO {
  idPractica: number;
  nombre: string;
  descripcion: string;
  objetivo: string;
  categoria: string;
  impactoEsperado: string;
  pasos: string[];
  kpiImpactado: string;
  dificultad: string;
  duracionDias: number;
  escala: string;
  tropicaAplicable: string;
}

export interface PasoDTO {
  indicePaso: number;
  completado: boolean;
}

export interface HatoPracticaPasoResponseDTO {
  pasos: PasoDTO[];
  porcentajeAvance: number;
  estado: string;
}

@Injectable({ providedIn: 'root' })
export class PracticasService {
  private readonly API_URL = `${environment.apiUrl}/practicas`;

  constructor(private http: HttpClient) {}

  getPracticasByHato(idHato: string): Observable<HatoPracticaDTO[]> {
    return this.http.get<HatoPracticaDTO[]>(`${this.API_URL}/${idHato}`);
  }

  getDetalle(idPractica: number): Observable<PracticaDetalleDTO> {
    return this.http.get<PracticaDetalleDTO>(`${this.API_URL}/detalle/${idPractica}`);
  }

  actualizarEstado(idHatoPractica: string, estado: EstadoPractica): Observable<HatoPracticaDTO> {
    return this.http.patch<HatoPracticaDTO>(`${this.API_URL}/hato/${idHatoPractica}/estado`, {
      estado,
    });
  }

  actualizarAvance(idHatoPractica: string, porcentaje: number): Observable<HatoPracticaDTO> {
    return this.http.patch<HatoPracticaDTO>(`${this.API_URL}/hato/${idHatoPractica}/avance`, {
      porcentaje,
    });
  }
  getPasos(idHatoPractica: string): Observable<HatoPracticaPasoResponseDTO> {
    return this.http.get<HatoPracticaPasoResponseDTO>(
      `${this.API_URL}/hato/${idHatoPractica}/pasos`,
    );
  }

  actualizarPasos(
    idHatoPractica: string,
    pasos: PasoDTO[],
  ): Observable<HatoPracticaPasoResponseDTO> {
    return this.http.put<HatoPracticaPasoResponseDTO>(
      `${this.API_URL}/hato/${idHatoPractica}/pasos`,
      { pasos },
    );
  }
}
