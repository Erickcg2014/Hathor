import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface ProduccionLeche {
  idProduccion: string;
  fecha: string;
  litrosProducidos: number;
  vacasOrdenadas?: number | null;
  hato: { idHato: string };
}

export interface RegistroProduccionLecheDTO {
  idHato: string;
  fecha: string;
  litrosProducidos: number;
  vacasOrdenadas?: number;
}

@Injectable({ providedIn: 'root' })
export class ProduccionLecheService {
  private readonly API_URL = `${environment.apiUrl}/ProduccionLeche`;

  constructor(private http: HttpClient) {}

  crearRegistro(dto: RegistroProduccionLecheDTO): Observable<ProduccionLeche> {
    return this.http.post<ProduccionLeche>(this.API_URL, dto);
  }

  getByHato(idHato: string): Observable<ProduccionLeche[]> {
    return this.http.get<ProduccionLeche[]>(`${this.API_URL}/hato/${idHato}`);
  }
}
