import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HatoAnonimizadoDTO } from '../benchmarking/benchmarking.service';
import { environment } from '../../../../environments/environment';

export interface Hato {
  idHato?: string;
  nombreHato: string;
  departamento: string;
  ciudad: string;
  altitud: number;
  tropico: string;
  areaHato: number;
  areaPastoreo: number;
  cantCorrales: number;
  cantSalasOrdenio: number;
  capacidadAlmacenarLeche: number;
  cantEmpleadosPermanentes: number;
  cantEmpleadosTemporales: number;
  tipoHato: string;
  porcentajeCompletitud?: number;
  gastoMensualNomina?: number;
  gastoMensualAlimentacion?: number;
  escala?: 'PEQUEÑA' | 'MEDIANA' | 'GRANDE' | 'EMPRESARIAL';
  latitud?: number;
  longitud?: number;
}

export interface CostosFijosDTO {
  gastoMensualNomina: number;
  gastoMensualAlimentacion: number;
}

export interface OpcionesHatoDTO {
  tiposHato: string[];
  tropicos: string[];
}

export interface RegistroHatoDTO {
  nombreHato: string;
  departamento: string;
  ciudad: string;
  altitud: number;
  tropico: string;
  areaHato: number;
  areaPastoreo: number;
  cantCorrales: number;
  cantSalasOrdenio: number;
  capacidadAlmacenarLeche: number;
  cantEmpleadosPermanentes: number;
  cantEmpleadosTemporales: number;
  tipoHato: string;
  latitud?: number;
  longitud?: number;
}

export interface InfraestructuraBasicaDTO {
  cantCorrales: number;
  cantSalasOrdenio: number;
  capacidadAlmacenarLeche: number;
  cantEmpleadosPermanentes: number;
  cantEmpleadosTemporales: number;
}

@Injectable({ providedIn: 'root' })
export class HatoService {
  private readonly API_URL = `${environment.apiUrl}/Hato`;

  constructor(private http: HttpClient) {}

  getMisHatos(): Observable<Hato[]> {
    return this.http.get<Hato[]>(`${this.API_URL}/me`);
  }

  crearHato(dto: RegistroHatoDTO): Observable<Hato> {
    return this.http.post<Hato>(this.API_URL, dto);
  }

  getHatoById(idHato: string): Observable<Hato> {
    return this.http.get<Hato>(`${this.API_URL}/${idHato}`);
  }

  getMapaGeneral(): Observable<HatoAnonimizadoDTO[]> {
    return this.http.get<HatoAnonimizadoDTO[]>(`${this.API_URL}/mapa-general`, {
      params: { _t: Date.now().toString() },
    });
  }

  actualizarCompletitud(idHato: string, porcentaje: number): Observable<Hato> {
    return this.http.put<Hato>(`${this.API_URL}/${idHato}/completitud`, { porcentaje });
  }

  actualizarInfraestructuraBasica(idHato: string, dto: InfraestructuraBasicaDTO): Observable<Hato> {
    return this.http.put<Hato>(`${this.API_URL}/${idHato}/infraestructura-basica`, dto);
  }
  actualizarCostosFijos(
    idHato: string,
    gastoMensualNomina: number,
    gastoMensualAlimentacion: number,
  ): Observable<Hato> {
    const dto: CostosFijosDTO = { gastoMensualNomina, gastoMensualAlimentacion };
    return this.http.put<Hato>(`${this.API_URL}/${idHato}/costos-fijos`, dto);
  }

  actualizarHato(idHato: string, dto: RegistroHatoDTO): Observable<Hato> {
    return this.http.put<Hato>(`${this.API_URL}/${idHato}`, dto);
  }

  getOpciones(): Observable<OpcionesHatoDTO> {
    return this.http.get<OpcionesHatoDTO>(`${this.API_URL}/opciones`);
  }
}
