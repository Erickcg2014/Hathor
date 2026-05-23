import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CategoriaInventario {
  idCategoriaInventario: number;
  nombre: string;
  descripcion?: string;
  tipo?: string; // PADRE, HIJA
  esPredefinida?: boolean;
  unidadMedida?: string;
  orden?: number;
  activa?: boolean;
  categoriaPadre?: CategoriaInventario | null;
}

@Injectable({ providedIn: 'root' })
export class CategoriaInventarioService {
  private readonly API_URL = `${environment.apiUrl}/CategoriaInventario`;

  constructor(private http: HttpClient) {}

  getCategorias(): Observable<CategoriaInventario[]> {
    return this.http.get<CategoriaInventario[]>(this.API_URL);
  }

  getCategoriasParent(): Observable<CategoriaInventario[]> {
    return this.http.get<CategoriaInventario[]>(`${this.API_URL}/padres`);
  }

  getSubcategorias(idPadre: number): Observable<CategoriaInventario[]> {
    return this.http.get<CategoriaInventario[]>(`${this.API_URL}/${idPadre}/subcategorias`);
  }
}
