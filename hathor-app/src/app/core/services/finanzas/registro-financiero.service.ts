import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface RegistroFinancieroDTO {
  id_hato: string;
  id_categoria: string;
  titulo: string;
  tipo_movimiento: 'INGRESO' | 'GASTO' | 'COSTO' | 'INVERSION';
  fecha: string;
  descripcion?: string;
  monto: number;
  litros_vendidos_leche?: number | null;
  precio_litro_leche?: number | null;
}

export interface ResumenPeriodoDTO {
  mes: string;
  mesLabel: string;
  ingresos: number;
  egresos: number;
  balance: number;
  esHistorico: boolean;
  esMixto: boolean;
}

export interface RegistroFinanciero {
  idRegistro: string;
  titulo: string | null;
  tipoMovimiento: string;
  fecha: string;
  descripcion: string | null;
  monto: number;
  categoriaFinanciera: { idCategoriaFinanciera: string; nombre: string; tipo: string } | null;
  esHistorico: boolean;
  precisionFecha: 'ANUAL' | 'MENSUAL' | 'EXACTA';
}

export interface ResultadoCargaMasiva {
  mensaje: string;
  registros: number;
  detalle: { fila: number; estado: 'OK' | 'ERROR'; titulo?: string; mensaje?: string }[];
}

@Injectable({ providedIn: 'root' })
export class RegistroFinancieroService {
  private readonly API_URL = `${environment.apiUrl}/Finanzas`;

  constructor(private http: HttpClient) {}

  crearRegistros(dtos: RegistroFinancieroDTO[]): Observable<RegistroFinanciero[]> {
    return this.http.post<RegistroFinanciero[]>(`${this.API_URL}/`, dtos);
  }

  getRegistrosByHato(idHato: string): Observable<RegistroFinanciero[]> {
    return this.http.get<RegistroFinanciero[]>(`${this.API_URL}/${idHato}`);
  }

  crearRegistro(dto: RegistroFinancieroDTO): Observable<RegistroFinanciero[]> {
    return this.crearRegistros([dto]);
  }

  eliminarRegistro(idRegistro: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${idRegistro}`);
  }

  cargaMasiva(idHato: string, archivo: File): Observable<ResultadoCargaMasiva> {
    const formData = new FormData();
    formData.append('archivo', archivo);
    formData.append('idHato', idHato);
    return this.http.post<ResultadoCargaMasiva>(`${this.API_URL}/carga-masiva`, formData);
  }

  getRegistrosReales(idHato: string): Observable<RegistroFinanciero[]> {
    return this.http.get<RegistroFinanciero[]>(`${this.API_URL}/hato/${idHato}/reales`);
  }

  getRegistrosHistoricos(idHato: string): Observable<RegistroFinanciero[]> {
    return this.http.get<RegistroFinanciero[]>(`${this.API_URL}/hato/${idHato}/historicos`);
  }

  getRegistrosPorPeriodo(
    idHato: string,
    mesDesde: string,
    mesHasta: string,
  ): Observable<RegistroFinanciero[]> {
    return this.http.get<RegistroFinanciero[]>(`${this.API_URL}/hato/${idHato}/periodo`, {
      params: { mesDesde, mesHasta },
    });
  }

  // TODO: PRUEBA — eliminar antes de producción
  limpiarDatosPrueba(idHato: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/prueba/limpiar/${idHato}`);
  }
}
