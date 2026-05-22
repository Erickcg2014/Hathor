import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface BenchmarkKpi {
  idKpi: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  formula: string;
  unidad: string;
  categoria: string;
}

export interface ComparativaHatosDTO {
  codigoKpi: string;
  nombreKpi: string;
  unidadKpi: string;
  valorHatoActual: number;
  nombreHatoActual: string;
  comparables: {
    posicion: number;
    valor: number;
    etiqueta: string;
  }[];
}

export interface BenchmarkReferencia {
  idBenchmark: number;
  region: string | null;
  valorPromedio: number | null;
  valorTop: number | null;
  anio: number | null;
  tropico: string | null;
  sistemaOrdenio: string | null;
}

export interface BenchmarkHatoResultado {
  idBenchmarkHato: number;
  kpi: BenchmarkKpi;
  benchReferencia: BenchmarkReferencia | null;
  percentil: number | null;
  interpretacion: string | null;
  valorHato: number | null;
  fechaCalculo: string;
}

export interface HatoValorDTO {
  alias: string;
  valor: number;
  posicion: number;
  esMiHato: boolean;
}

export interface KpiResumenGlobalDTO {
  codigoKpi: string;
  nombreKpi: string;
  unidadKpi: string;
  categoria: string;
  valorHatoActual: number | null;
  promedioGrupo: number | null;
  topGrupo: number | null;
  percentilEnGrupo: number | null;
  totalHatosGrupo: number;
  interpretacion: string | null;
  rankingHatos: HatoValorDTO[];
}

export interface HatoAnonimizadoDTO {
  alias: string;
  latitudDifuminada: number | null;
  longitudDifuminada: number | null;
  tropico: string | null;
  escala: string | null;
  departamento: string | null;
  valorKpiPrincipal: number | null;
  interpretacion: string | null;
  esMiHato: boolean;
}

export interface FiltrosAplicadosDTO {
  tropico: string | null;
  escala: string | null;
  region: string | null;
  cantidad: number;
  filtroTropicoActivo: boolean;
  filtroEscalaActivo: boolean;
  filtroRegionActivo: boolean;
}

export interface BenchmarkGlobalDTO {
  kpisResumen: KpiResumenGlobalDTO[];
  hatosEnMapa: HatoAnonimizadoDTO[];
  filtrosAplicados: FiltrosAplicadosDTO;
  totalHatosEncontrados: number;
  datosInsuficientes: boolean;
  mensajeFaltante: string | null;
}

@Injectable({ providedIn: 'root' })
export class BenchmarkingService {
  private readonly API_URL = `${environment.apiUrl}/Benchmarking`;

  constructor(private http: HttpClient) {}

  getBenchmarking(
    idHato: string,
    modo: 'REFERENCIA' | 'PLATAFORMA' = 'REFERENCIA',
    nivel: 'NACIONAL' | 'TROPICO' | 'TROPICO_ESCALA' = 'NACIONAL',
    categoria?: string,
  ): Observable<BenchmarkHatoResultado[]> {
    const params: any = { modo, nivel };
    if (categoria) params['categoria'] = categoria;
    return this.http.get<BenchmarkHatoResultado[]>(`${this.API_URL}/${idHato}`, { params });
  }

  getBenchmarkGlobal(
    idHato: string,
    filtroTropico: boolean = true,
    filtroEscala: boolean = false,
    filtroRegion: boolean = false,
    cantidad: 5 | 10 | 15 = 10,
  ): Observable<BenchmarkGlobalDTO> {
    return this.http.get<BenchmarkGlobalDTO>(`${this.API_URL}/${idHato}/global`, {
      params: {
        filtroTropico: filtroTropico.toString(),
        filtroEscala: filtroEscala.toString(),
        filtroRegion: filtroRegion.toString(),
        cantidad: cantidad.toString(),
      },
    });
  }

  getComparativaHatos(
    idHato: string,
    codigoKpi: string,
    top: 5 | 10 | 15 = 10,
  ): Observable<ComparativaHatosDTO> {
    return this.http.get<ComparativaHatosDTO>(`${this.API_URL}/${idHato}/comparativa-hatos`, {
      params: { codigoKpi, top: top.toString() },
    });
  }

  calcularBenchmarking(idHato: string): Observable<string> {
    return this.http.post(
      `${this.API_URL}/calcularBenchmarking/${idHato}`,
      {},
      {
        responseType: 'text',
      },
    );
  }
}
