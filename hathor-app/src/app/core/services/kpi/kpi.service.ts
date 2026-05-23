import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface DetalleCalculoItem {
  variable: string;
  valor: number | null;
  unidad: string | null;
  tipo: 'INPUT' | 'INTERMEDIO' | 'RESULTADO';
}

export interface KpiCatalogo {
  idKpi: number;
  codigo: string;
  nombre: string;
  unidad: string | null;
  categoria: string | null;
  formula: string | null;
}

export interface KpiResultado {
  idKpi: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  formula: string;
  unidad: string;
  categoria: string;
  valor: number | null;
  estado: 'OPTIMO' | 'ACEPTABLE' | 'CRITICO' | 'SIN_DATOS';
  periodo: string;
  fechaCalculo: string;
  benchmarkPromedio: number | null;
  benchmarkTop: number | null;
  diferenciaPct: number | null;
  razonSinDatos: string | null;
  detalleCalculo: DetalleCalculoItem[];
}

export interface KpiHistorico {
  periodo: string;
  fechaCalculo: string;
  valor: number;
  estado: string;
}
@Injectable({ providedIn: 'root' })
export class KpiService {
  private readonly API_URL = `${environment.apiUrl}/Kpi`;

  // Caché en memoria — persiste en el frontend
  private kpisCache: KpiResultado[] = [];
  private hatoIdCache: string | null = null;

  constructor(private http: HttpClient) {}

  calcularKpis(idHato: string): Observable<KpiResultado[]> {
    return this.http.post<KpiResultado[]>(`${this.API_URL}/calcular/${idHato}`, {}).pipe(
      tap((kpis) => {
        this.kpisCache = kpis;
        this.hatoIdCache = idHato;
      }),
    );
  }

  getKpis(idHato: string): Observable<KpiResultado[]> {
    // Si ya tenemos los KPIs del mismo hato en caché, los retorna sin llamar al backend
    if (this.hatoIdCache === idHato && this.kpisCache.length > 0) {
      return of(this.kpisCache);
    }
    return this.http.get<KpiResultado[]>(`${this.API_URL}/hato/${idHato}`).pipe(
      tap((kpis) => {
        this.kpisCache = kpis;
        this.hatoIdCache = idHato;
      }),
    );
  }

  getHistorico(idHato: string, codigo: string): Observable<KpiHistorico[]> {
    return this.http.get<KpiHistorico[]>(`${this.API_URL}/hato/${idHato}/historico/${codigo}`);
  }

  // Obtener un KPI específico desde caché
  getKpiDesdeCache(codigo: string): KpiResultado | null {
    return this.kpisCache.find((k) => k.codigo === codigo) ?? null;
  }
  getTodosDesdeCache(): KpiResultado[] {
    return this.kpisCache;
  }
  getCatalogo(): Observable<KpiCatalogo[]> {
    return this.http.get<KpiCatalogo[]>(`${this.API_URL}/catalogo`);
  }

  invalidarCache(): void {
    this.kpisCache = [];
    this.hatoIdCache = null;
  }
}
