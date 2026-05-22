import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface InventarioGeneralDTO {
  idHato: string;
  idCategoriaInventario: number;
  nombreItem: string;
  cantidad: number;
  valorUnitario: number;
  descripcion?: string;
}

export interface ActualizarInventarioGeneralDTO {
  nombreItem?: string;
  cantidad?: number;
  valorUnitario?: number;
  descripcion?: string;
}

export interface CategoriaInventario {
  idCategoriaInventario: number;
  nombre: string;
  descripcion?: string;
  tipo: string;
  unidadMedida?: string;
  categoriaPadre?: {
    idCategoriaInventario: number;
    nombre: string;
    tipo: string;
  };
}

export interface InventarioGeneral {
  idInventarioGeneral: string;
  hato: { idHato: string };
  categoriaInventario: CategoriaInventario;
  nombreItem: string;
  cantidad: number;
  valorUnitario: number;
  valorTotal: number;
  fechaRegistro: string;
  descripcion?: string;
}

@Injectable({ providedIn: 'root' })
export class InventarioGeneralService {
  private readonly API_URL = `${environment.apiUrl}/InventarioGeneral`;

  constructor(private http: HttpClient) {}

  crearItem(dto: InventarioGeneralDTO): Observable<InventarioGeneral> {
    return this.http.post<InventarioGeneral>(this.API_URL, dto);
  }

  getByHato(idHato: string): Observable<InventarioGeneral[]> {
    return this.http.get<InventarioGeneral[]>(`${this.API_URL}/hato/${idHato}`);
  }

  actualizar(
    idInventarioGeneral: string,
    dto: ActualizarInventarioGeneralDTO,
  ): Observable<InventarioGeneral> {
    return this.http.patch<InventarioGeneral>(`${this.API_URL}/${idInventarioGeneral}`, dto);
  }

  eliminar(idInventarioGeneral: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${idInventarioGeneral}`);
  }
}
