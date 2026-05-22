import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import {
  RecomendacionDTO,
  RecomendacionesPracticasService,
} from '../../../../core/services/practicas/recomendaciones-practicas.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-recomendaciones',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './recomendaciones.component.html',
  styleUrl: './recomendaciones.component.css',
})
export class RecomendacionesComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  recomendaciones: RecomendacionDTO[] = [];
  loading = false;
  noLeidas = 0;

  paginaActual = 0;
  totalPaginas = 0;
  pageSize = 8;

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private recomendacionesService: RecomendacionesPracticasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      this.paginaActual = 0;

      if (hato?.idHato) {
        this.cargarRecomendaciones(hato.idHato, this.paginaActual);
        this.cargarNoLeidas(hato.idHato);
      } else {
        this.recomendaciones = [];
        this.totalPaginas = 0;
      }

      this.cdr.markForCheck();
    });
  }

  cargarRecomendaciones(idHato: string, pagina: number): void {
    this.loading = true;
    this.recomendacionesService
      .getActivasByHato(idHato, pagina, this.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.recomendaciones = page.content ?? [];
          this.paginaActual = page.number ?? 0;
          this.totalPaginas = page.totalPages ?? 0;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.recomendaciones = [];
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  cargarNoLeidas(idHato: string): void {
    this.recomendacionesService
      .countNoLeidas(idHato)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (count) => {
          this.noLeidas = count;
          this.cdr.markForCheck();
        },
      });
  }

  marcarLeida(idRecomendacion: number): void {
    this.recomendacionesService
      .marcarComoLeida(idRecomendacion)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.recomendaciones = this.recomendaciones.map((r) =>
            r.idRecomendacion === idRecomendacion ? { ...r, leida: true } : r,
          );

          if (this.hatoActivo?.idHato) {
            this.cargarNoLeidas(this.hatoActivo.idHato);
          }
        },
      });
  }

  descartar(idRecomendacion: number): void {
    this.recomendacionesService
      .descartar(idRecomendacion)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.recomendaciones = this.recomendaciones.filter(
            (r) => r.idRecomendacion !== idRecomendacion,
          );

          if (this.hatoActivo?.idHato) {
            this.cargarNoLeidas(this.hatoActivo.idHato);
          }
          this.cdr.markForCheck();
        },
      });
  }

  cambiarPagina(delta: number): void {
    if (!this.hatoActivo?.idHato) return;
    const siguiente = this.paginaActual + delta;
    if (siguiente < 0 || siguiente >= this.totalPaginas) return;

    this.cargarRecomendaciones(this.hatoActivo.idHato, siguiente);
  }

  prioridadLabel(prioridad: string): string {
    const map: Record<string, string> = {
      ALTA: 'Alta',
      MEDIA: 'Media',
      BAJA: 'Baja',
    };
    return map[prioridad] ?? prioridad;
  }

  iconoPrioridad(prioridad: string): string {
    const map: Record<string, string> = {
      ALTA: '🔴',
      MEDIA: '🟡',
      BAJA: '🟢',
    };
    return map[prioridad] ?? '📋';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
