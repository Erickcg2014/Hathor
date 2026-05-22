import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, firstValueFrom, takeUntil } from 'rxjs';

import {
  BenchmarkHatoResultado,
  BenchmarkingService,
} from '../../../../core/services/benchmarking/benchmarking.service';
import { Hato, HatoService } from '../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { GraficasBenchmarkingComponent } from './graficas-benchmarking/graficas-benchmarking.component';
import { BenchmarkGlobalComponent } from './benchmark-global/benchmark-global.component';

@Component({
  selector: 'app-comparativa-kpis',
  imports: [
    CommonModule,
    SpinnerComponent,
    GraficasBenchmarkingComponent,
    BenchmarkGlobalComponent,
  ],
  templateUrl: './comparativa-kpis.component.html',
  styleUrl: './comparativa-kpis.component.css',
})
export class ComparativaKpisComponent implements OnInit, OnDestroy {
  loading = true;
  calculando = false;
  sinResultadosPlataforma = false;
  error = '';

  hatoActivo: Hato | null = null;
  resultados: BenchmarkHatoResultado[] = [];

  nivelActivo: 'NACIONAL' | 'TROPICO' | 'TROPICO_ESCALA' = 'NACIONAL';

  readonly niveles: {
    valor: 'NACIONAL' | 'TROPICO' | 'TROPICO_ESCALA';
    label: string;
    descripcion: string;
  }[] = [
    {
      valor: 'NACIONAL',
      label: 'Nacional',
      descripcion: 'Compara contra el promedio de todos los hatos lecheros de Colombia.',
    },
    {
      valor: 'TROPICO',
      label: 'Por trópico',
      descripcion: `Compara contra hatos en tu mismo trópico (${this.hatoActivo?.tropico ?? 'según tu hato'}).`,
    },
    {
      valor: 'TROPICO_ESCALA',
      label: 'Trópico + Escala',
      descripcion: 'Compara contra hatos de tu mismo trópico y tamaño de operación.',
    },
  ];

  modoActivo: 'REFERENCIA' | 'PLATAFORMA' = 'REFERENCIA';
  categoriaActiva: string | null = null;

  readonly modos: { valor: 'REFERENCIA' | 'PLATAFORMA'; label: string; descripcion: string }[] = [
    {
      valor: 'REFERENCIA',
      label: 'Benchmark estándar',
      descripcion:
        'Compara tus KPIs contra valores promedio e ideales del sector ganadero colombiano.',
    },
    {
      valor: 'PLATAFORMA',
      label: 'Benchmark global',
      descripcion:
        'Compara tus KPIs contra los valores reales de otros hatos registrados en el sistema',
    },
  ];

  readonly categorias: { valor: string | null; label: string; descripcion: string }[] = [
    { valor: null, label: 'Todas', descripcion: 'Ver todos los indicadores sin filtro.' },
    {
      valor: 'PRODUCTIVIDAD',
      label: '🥛 Productividad',
      descripcion: 'Indicadores de producción de leche por vaca, hectárea y ordeño.',
    },
    {
      valor: 'HATO',
      label: '🐄 Hato',
      descripcion: 'Indicadores de estructura y manejo del hato ganadero.',
    },
    {
      valor: 'FINANCIERO',
      label: '💰 Financiero',
      descripcion: 'Indicadores de rentabilidad, costos e ingresos del negocio.',
    },
    {
      valor: 'EFICIENCIA',
      label: '⚙️ Eficiencia',
      descripcion: 'Indicadores de productividad laboral y eficiencia operativa.',
    },
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private hatoService: HatoService,
    private benchmarkingService: BenchmarkingService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarBenchmarking(hato.idHato);
      } else {
        this.loading = false;
        this.resultados = [];
        this.cdr.markForCheck();
      }
    });

    this.cargarHatoInicial();
  }

  private cargarHatoInicial(): void {
    if (this.hatoState.getHatoActivo()) return;

    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        if (hatos.length > 0) {
          this.hatoState.setHatoActivo(hatos[0]);
        } else {
          this.loading = false;
          this.cdr.markForCheck();
        }
      },
      error: () => {
        this.loading = false;
        this.error = 'No se pudo cargar el hato activo.';
        this.cdr.markForCheck();
      },
    });
  }

  private cargarBenchmarking(idHato: string): void {
    this.loading = true;
    this.error = '';
    this.sinResultadosPlataforma = false;

    this.benchmarkingService
      .getBenchmarking(idHato, this.modoActivo, this.nivelActivo, this.categoriaActiva ?? undefined)
      .subscribe({
        next: (resultado) => {
          this.resultados = resultado;
          this.loading = false;
          if (resultado.length === 0 && this.modoActivo === 'PLATAFORMA') {
            this.sinResultadosPlataforma = true;
          }
          this.cdr.markForCheck();
        },
        error: () => {
          this.loading = false;
          this.resultados = [];
          this.error = 'No se pudo cargar el benchmarking.';
          this.cdr.markForCheck();
        },
      });
  }

  async calcularBenchmarking(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;

    this.calculando = true;
    this.error = '';
    this.cdr.markForCheck();

    try {
      await firstValueFrom(this.benchmarkingService.calcularBenchmarking(this.hatoActivo.idHato));
      this.cargarBenchmarking(this.hatoActivo.idHato);
    } catch {
      this.error = 'Error al calcular el benchmarking. Intenta de nuevo.';
    } finally {
      this.calculando = false;
      this.cdr.markForCheck();
    }
  }

  setModo(modo: 'REFERENCIA' | 'PLATAFORMA'): void {
    if (this.modoActivo === modo) return;
    this.modoActivo = modo;
    if (this.hatoActivo?.idHato) this.cargarBenchmarking(this.hatoActivo.idHato);
  }

  setCategoria(categoria: string | null): void {
    this.categoriaActiva = categoria;
    this.cdr.markForCheck();
  }

  setNivel(nivel: 'NACIONAL' | 'TROPICO' | 'TROPICO_ESCALA'): void {
    if (this.nivelActivo === nivel) return;
    this.nivelActivo = nivel;
    if (this.hatoActivo?.idHato) this.cargarBenchmarking(this.hatoActivo.idHato);
  }

  get totalComparadosReferencia(): number {
    return this.resultados.filter((r) => r.benchReferencia !== null).length;
  }

  get totalComparadosPlataforma(): number {
    return this.resultados.filter((r) => r.benchReferencia === null).length;
  }

  get fechaCalculo(): string {
    return this.resultados[0]?.fechaCalculo ?? '';
  }

  get tieneResultados(): boolean {
    return this.resultados.length > 0;
  }
  get filtrosAplicados(): { texto: string; tipo: 'modo' | 'nivel' | 'region' | 'categoria' }[] {
    const filtros: { texto: string; tipo: 'modo' | 'nivel' | 'region' | 'categoria' }[] = [];

    const modoLabel = this.modos.find((m) => m.valor === this.modoActivo)?.label ?? '';
    filtros.push({ texto: modoLabel, tipo: 'modo' });

    if (this.modoActivo === 'REFERENCIA') {
      const nivelLabel = this.niveles.find((n) => n.valor === this.nivelActivo)?.label ?? '';
      filtros.push({ texto: nivelLabel, tipo: 'nivel' });

      if (this.hatoActivo?.ciudad) filtros.push({ texto: this.hatoActivo.ciudad, tipo: 'region' });
      if (this.hatoActivo?.departamento)
        filtros.push({ texto: this.hatoActivo.departamento, tipo: 'region' });
      if (
        (this.nivelActivo === 'TROPICO' || this.nivelActivo === 'TROPICO_ESCALA') &&
        this.hatoActivo?.tropico
      )
        filtros.push({ texto: `Trópico ${this.hatoActivo.tropico}`, tipo: 'region' });
      if (this.nivelActivo === 'TROPICO_ESCALA' && this.hatoActivo?.escala)
        filtros.push({ texto: `Escala ${this.hatoActivo.escala}`, tipo: 'region' });
    }

    if (this.categoriaActiva) {
      const cat = this.categorias.find((c) => c.valor === this.categoriaActiva);
      if (cat) filtros.push({ texto: cat.label, tipo: 'categoria' });
    }

    return filtros;
  }

  get kpisOptimos(): number {
    return this.resultados.filter((r) => r.interpretacion === 'OPTIMO').length;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
