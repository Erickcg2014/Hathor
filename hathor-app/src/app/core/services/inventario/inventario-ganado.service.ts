import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface InventarioGanadoDTO {
  idHato: string;
  idRaza: number;
  idCategoria: number;
  cantidad: number;
  edadPromedioMeses: number;
  valorUnitario: number;
}

export interface ActualizarInventarioGanadoDTO {
  cantidad?: number;
  edadPromedioMeses?: number;
  valorUnitario?: number;
}

export interface InventarioGanado {
  idInventario: string;
  hato: { idHato: string };
  raza: {
    idRaza: number;
    tipoRaza: string;
    nombre: string;
  };
  categoriaGanado: {
    idCategoria: number;
    nombreCategoria: string;
    descripcion?: string;
  };
  cantidad: number;
  edadPromedioMeses: number;
  fechaRegistro: string;
  valorUnitario: number;
  valorTotal: number;
}

@Injectable({ providedIn: 'root' })
export class InventarioGanadoService {
  private readonly API_URL = `${environment.apiUrl}/InventarioGanado`;

  constructor(private http: HttpClient) {}

  crearRegistro(dto: InventarioGanadoDTO): Observable<InventarioGanado> {
    return this.http.post<InventarioGanado>(this.API_URL, dto);
  }

  getByHato(idHato: string): Observable<InventarioGanado[]> {
    return this.http.get<InventarioGanado[]>(`${this.API_URL}/hato/${idHato}`);
  }

  actualizar(
    idInventario: string,
    dto: ActualizarInventarioGanadoDTO,
  ): Observable<InventarioGanado> {
    return this.http.patch<InventarioGanado>(`${this.API_URL}/${idInventario}`, dto);
  }

  eliminar(idInventario: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${idInventario}`);
  }
}
