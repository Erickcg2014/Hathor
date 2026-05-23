import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface DetallePerfilDTO {
  idCategoria: string;
  tipo: 'INGRESO' | 'GASTO' | 'COSTO' | 'INVERSION';
  titulo: string;
  montoMensual: number;
}

export interface RegistroPerfilFinancieroDTO {
  idHato: string;
  metodoRegistro: 'EXCEL' | 'MANUAL' | 'OMITIDO';
  periodo?: string;
  descripcion?: string;
  detalles?: DetallePerfilDTO[];
}

export interface PerfilFinancieroDetalle {
  idDetalle?: string;
  tipo: string; // INGRESO | GASTO | COSTO | INVERSION
  titulo: string;
  montoMensual: number | null;
  fechaRegistro?: string;
  categoriaFinanciera?: {
    idCategoriaFinanciera: string;
    nombre: string;
    tipo: string;
  };
}

export interface PerfilFinanciero {
  idPerfilFinanciero?: string;
  metodoRegistro: 'EXCEL' | 'MANUAL' | 'OMITIDO';
  periodo?: string;
  descripcion?: string;
  fechaRegistro?: string;
  detalles?: PerfilFinancieroDetalle[];
}

@Injectable({ providedIn: 'root' })
export class PerfilFinancieroService {
  private readonly API_URL = `${environment.apiUrl}/PerfilFinanciero`;

  constructor(private http: HttpClient) {}

  crearPerfil(dto: RegistroPerfilFinancieroDTO): Observable<PerfilFinanciero> {
    return this.http.post<PerfilFinanciero>(this.API_URL, dto);
  }

  getPerfilesByHato(idHato: string): Observable<PerfilFinanciero[]> {
    return this.http.get<PerfilFinanciero[]>(`${this.API_URL}/hato/${idHato}`);
  }
}
