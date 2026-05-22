import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { HatoService, Hato } from '../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { UserService } from '../../../../core/services/usuario/user.service';

@Component({
  selector: 'app-info-hato',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  templateUrl: './info-hato.component.html',
  styleUrl: './info-hato.component.css',
})
export class InfoHatoComponent implements OnInit, OnDestroy {
  loading = true;
  tieneHatos = false;
  mostrarOverlay = false;
  hatoActivo: Hato | null = null;
  esAdmin = false;

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      this.cdr.markForCheck();
    });

    this.userService.getUsuarioLogueado().subscribe({
      next: (usuario) => {
        this.esAdmin = usuario?.rol === 'ADMIN';
        if (!this.esAdmin) {
          this.cargarHatos();
        } else {
          this.loading = false;
          this.mostrarOverlay = false;
          this.tieneHatos = true;
          this.cdr.markForCheck();
        }
      },
      error: () => {
        this.cargarHatos();
      },
    });
  }

  private cargarHatos(): void {
    this.loading = true;
    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        this.tieneHatos = hatos.length > 0;
        this.mostrarOverlay = !this.tieneHatos;

        if (hatos.length > 0 && !this.hatoState.getHatoActivo()) {
          this.hatoState.setHatoActivo(hatos[0]);
        }

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.mostrarOverlay = true;
        this.cdr.markForCheck();
      },
    });
  }

  irAFormulario(): void {
    this.router.navigate(['/hato/nuevo']);
  }

  get mostrarAccionesFormulario(): boolean {
    return !this.esAdmin;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
