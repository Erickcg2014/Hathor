import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface ValorReferenciaGanado {
  idValorReferencia: number;
  raza: {
    idRaza: number;
    tipoRaza: string;
    nombre: string;
  };
  categoriaGanado: {
    idCategoria: number;
    nombreCategoria: string;
  };
  valorPromedio: number;
  region: string;
  anio: number;
}

@Injectable({ providedIn: 'root' })
export class ValorReferenciaGanadoService {
  private readonly API_URL = `${environment.apiUrl}/ValorReferenciaGanado`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ValorReferenciaGanado[]> {
    return this.http.get<ValorReferenciaGanado[]>(this.API_URL);
  }

  getByRazaYCategoria(idRaza: number, idCategoria: number): Observable<ValorReferenciaGanado> {
    return this.http.get<ValorReferenciaGanado>(
      `${this.API_URL}/raza/${idRaza}/categoria/${idCategoria}`,
    );
  }
}
