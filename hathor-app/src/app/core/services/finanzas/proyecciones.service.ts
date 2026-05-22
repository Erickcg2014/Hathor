import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InversionPlaneadaDTO } from './inversion.service';
import { environment } from '../../../../environments/environment';

export interface InversionResumenDTO {
  idInversion: number;
  descripcion: string;
  monto: number;
  nombreCategoria: string;
  icono: string;
}

export interface ProyeccionMensualDTO {
  periodo: string;
  periodoLabel: string;
  ingresoMin: number;
  ingresoMax: number;
  ingresoProyectado: number;
  egresoMin: number;
  egresoMax: number;
  egresoProyectado: number;
  margenMin: number;
  margenMax: number;
  margenProyectado: number;
  estadoMargen: string;
  alertas: string[];
  inversionesDelMes: InversionResumenDTO[];
  retornosDelMes: InversionResumenDTO[];
  tieneInversion: boolean;
  tieneRetorno: boolean;
}

export interface ProyeccionesResponseDTO {
  proyecciones: ProyeccionMensualDTO[];
  mesesHistorial: number;
  modelosAplicados: string[];
  datosInsuficientes: boolean;
  mensajeInsuficiente: string | null;
  promedioIngresosMensual: number | null;
  promedioEgresosMensual: number | null;
  tendenciaIngresos: number | null;
  tendenciaEgresos: number | null;
}

@Injectable({ providedIn: 'root' })
export class ProyeccionesService {
  private readonly API_URL = `${environment.apiUrl}/proyecciones`;

  constructor(private http: HttpClient) {}

  getProyecciones(idHato: string, meses: 3 | 6 = 6): Observable<ProyeccionesResponseDTO> {
    return this.http.get<ProyeccionesResponseDTO>(`${this.API_URL}/${idHato}`, {
      params: { meses: meses.toString(), _t: Date.now().toString() },
    });
  }
}
