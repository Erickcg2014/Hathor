import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

// ── Interfaces ────────────────────────────────────────────────────────────

export interface FiltroAdmin {
  departamento: string | null;
  region: string | null;
  idHato: string | null;
  nombreHato: string | null;
}

export interface HatoAdminSeleccionado {
  idHato: string;
  nombreHato: string;
  departamento: string;
  ciudad: string;
  tropico: string | null;
  escala: string | null;
  tipoHato: string | null;
  porcentajeCompletitud: number;
  // Info del dueño
  nombreUsuario: string | null;
  apellidoUsuario: string | null;
  correoUsuario: string | null;
}

const FILTRO_INICIAL: FiltroAdmin = {
  departamento: null,
  region: null,
  idHato: null,
  nombreHato: null,
};

@Injectable({ providedIn: 'root' })
export class AdminStateService {
  private hatoSeleccionado$ = new BehaviorSubject<HatoAdminSeleccionado | null>(null);

  private filtros$ = new BehaviorSubject<FiltroAdmin>({ ...FILTRO_INICIAL });

  // ── Observables públicos ──────────────────────────────────────────────

  getHatoSeleccionado$(): Observable<HatoAdminSeleccionado | null> {
    return this.hatoSeleccionado$.asObservable();
  }

  getFiltros$(): Observable<FiltroAdmin> {
    return this.filtros$.asObservable();
  }

  // ── Getters síncronos ─────────────────────────────────────────────────

  getHatoSeleccionado(): HatoAdminSeleccionado | null {
    return this.hatoSeleccionado$.getValue();
  }

  getFiltros(): FiltroAdmin {
    return this.filtros$.getValue();
  }

  getIdHatoSeleccionado(): string | null {
    return this.hatoSeleccionado$.getValue()?.idHato ?? null;
  }

  hayHatoSeleccionado(): boolean {
    return this.hatoSeleccionado$.getValue() !== null;
  }

  // ── Setters ───────────────────────────────────────────────────────────

  setHatoSeleccionado(hato: HatoAdminSeleccionado): void {
    this.hatoSeleccionado$.next(hato);
    // Sincronizar filtros con el hato seleccionado
    this.filtros$.next({
      departamento: hato.departamento,
      region: hato.ciudad,
      idHato: hato.idHato,
      nombreHato: hato.nombreHato,
    });
  }

  setDepartamento(departamento: string | null): void {
    const filtrosActuales = this.filtros$.getValue();
    // Al cambiar departamento resetear región y hato
    this.filtros$.next({
      ...filtrosActuales,
      departamento,
      region: null,
      idHato: null,
      nombreHato: null,
    });
    // Limpiar hato seleccionado
    this.hatoSeleccionado$.next(null);
  }

  setRegion(region: string | null): void {
    const filtrosActuales = this.filtros$.getValue();
    // Al cambiar región resetear hato
    this.filtros$.next({
      ...filtrosActuales,
      region,
      idHato: null,
      nombreHato: null,
    });
    this.hatoSeleccionado$.next(null);
  }

  limpiar(): void {
    this.hatoSeleccionado$.next(null);
    this.filtros$.next({ ...FILTRO_INICIAL });
  }
}
