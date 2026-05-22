import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Raza {
  idRaza: number;
  tipoRaza: string;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class RazaService {
  private readonly API_URL = `${environment.apiUrl}/Raza`;

  constructor(private http: HttpClient) {}

  getRazas(): Observable<Raza[]> {
    return this.http.get<Raza[]>(this.API_URL);
  }

  getTiposRaza(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/tipos`);
  }

  getRazasPorTipo(tipoRaza: string): Observable<Raza[]> {
    return this.http.get<Raza[]>(`${this.API_URL}/por-tipo/${tipoRaza}`);
  }
}
