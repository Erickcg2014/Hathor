import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface VentaLeche {
  idVentaleche: string;
  fecha: string;
  precioLitro: number;
  litrosVendidos: number;
  registroFinanciero?: { idRegistro: string };
}

export interface RegistroVentaLecheDTO {
  idHato: string;
  fecha: string;
  precioLitro: number;
  litrosVendidos: number;
}

export interface RespuestaVentaLeche {
  ventaRegistrada: VentaLeche;
  alerta: boolean;
  litrosFaltantes: number;
  mensaje: string | null;
}

@Injectable({ providedIn: 'root' })
export class VentaLecheService {
  private readonly API_URL = `${environment.apiUrl}/VentaLeche`;

  constructor(private http: HttpClient) {}

  getByHato(idHato: string): Observable<VentaLeche[]> {
    return this.http.get<VentaLeche[]>(`${this.API_URL}/hato/${idHato}`);
  }

  crearVenta(dto: RegistroVentaLecheDTO): Observable<RespuestaVentaLeche> {
    return this.http.post<RespuestaVentaLeche>(`${this.API_URL}/registrar`, dto);
  }
}
