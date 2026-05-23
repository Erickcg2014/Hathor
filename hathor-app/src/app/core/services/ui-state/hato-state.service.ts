import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Hato } from '../hato/hato.service';

@Injectable({ providedIn: 'root' })
export class HatoStateService {
  private hatoActivo = new BehaviorSubject<Hato | null>(null);
  public hatoActivo$ = this.hatoActivo.asObservable();

  setHatoActivo(hato: Hato): void {
    this.hatoActivo.next(hato);
  }

  limpiarHato(): void {
    this.hatoActivo.next(null);
  }

  getHatoActivo(): Hato | null {
    return this.hatoActivo.value;
  }
}
