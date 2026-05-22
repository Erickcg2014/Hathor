import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CategoriaGanado {
  idCategoria: number;
  nombreCategoria: string;
  descripcion?: string;
}

@Injectable({ providedIn: 'root' })
export class CategoriaGanadoService {
  private readonly API_URL = `${environment.apiUrl}/CategoriaGanado`;

  constructor(private http: HttpClient) {}

  getCategorias(): Observable<CategoriaGanado[]> {
    return this.http.get<CategoriaGanado[]>(this.API_URL);
  }
}
