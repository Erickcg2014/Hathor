import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// ===== INTERFACES =====

export interface CategoriaFinanciera {
  idCategoriaFinanciera: string;
  nombre: string;
  tipo: string;
  esPredefinida: boolean;
  unidadProduccion?: string;
  categoriaPadre?: {
    idCategoriaFinanciera: string;
    nombre: string;
    tipo: string;
  } | null;
}
export interface CrearCategoriaFinancieraDTO {
  nombre: string;
  tipo: string;
  esPredefinida: boolean;
  unidadProduccion?: string;
  categoriaPadre?: { idCategoriaFinanciera: string } | null;
}

export interface CrearCategoriaPersonalizadaDTO {
  nombre: string;
  tipo: string;
  idCategoriaPadre?: string;
}

export interface CategoriaAgrupada {
  idCategoria: string;
  nombre: string;
  descripcion: string | null;
  tipo: string;
  seleccionada?: boolean;
}

export interface CategoriasAgrupadas {
  ingresos: CategoriaAgrupada[];
  gastos: CategoriaAgrupada[];
  inversiones: CategoriaAgrupada[];
}

@Injectable({ providedIn: 'root' })
export class CategoriaFinancieraService {
  private readonly API_URL = `${environment.apiUrl}/CategoriaFinanciera`;

  constructor(private http: HttpClient) {}

  getCategorias(): Observable<CategoriaFinanciera[]> {
    return this.http.get<CategoriaFinanciera[]>(this.API_URL);
  }

  getCategoriasPorTipo(tipo: string): Observable<CategoriaFinanciera[]> {
    return this.http.get<CategoriaFinanciera[]>(`${this.API_URL}?tipo=${tipo}`);
  }

  crearCategoria(dto: CrearCategoriaFinancieraDTO): Observable<CategoriaFinanciera> {
    return this.http.post<CategoriaFinanciera>(this.API_URL, dto);
  }

  crearCategoriaPersonalizada(dto: CrearCategoriaPersonalizadaDTO): Observable<CategoriaAgrupada> {
    return this.http.post<CategoriaAgrupada>(`${this.API_URL}/personalizada`, dto);
  }

  getCategoriasAgrupadas(): Observable<CategoriasAgrupadas> {
    return this.http.get<CategoriasAgrupadas>(`${this.API_URL}/agrupadas`);
  }

  getCategoriasMe(): Observable<CategoriaFinanciera[]> {
    return this.http.get<CategoriaFinanciera[]>(`${this.API_URL}/me`);
  }
}
