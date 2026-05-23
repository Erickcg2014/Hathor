import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { HatoService, Hato, RegistroHatoDTO } from '../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { UserService } from '../../../../core/services/usuario/user.service';

@Component({
  selector: 'app-info-hato',
  standalone: true,
  imports: [CommonModule, SpinnerComponent, ReactiveFormsModule],
  templateUrl: './info-hato.component.html',
  styleUrl: './info-hato.component.css',
})
export class InfoHatoComponent implements OnInit, OnDestroy {
  loading = true;
  tieneHatos = false;
  mostrarOverlay = false;
  hatoActivo: Hato | null = null;
  esAdmin = false;

  modoEdicion = false;
  guardando = false;
  errorEdicion: string | null = null;
  editForm!: FormGroup;

  tiposHato: string[] = [];
  tropicos: string[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      this.cdr.markForCheck();
    });

    this.hatoService.getOpciones().subscribe({
      next: (opciones) => {
        this.tiposHato = opciones.tiposHato;
        this.tropicos = opciones.tropicos;
        this.cdr.markForCheck();
      },
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

  abrirEdicion(): void {
    if (!this.hatoActivo) return;
    this.editForm = this.fb.group({
      nombreHato: [this.hatoActivo.nombreHato, [Validators.required, Validators.minLength(2)]],
      tipoHato: [this.hatoActivo.tipoHato, Validators.required],
      departamento: [this.hatoActivo.departamento, Validators.required],
      ciudad: [this.hatoActivo.ciudad, Validators.required],
      altitud: [this.hatoActivo.altitud, [Validators.required, Validators.min(0)]],
      tropico: [this.hatoActivo.tropico, Validators.required],
      areaHato: [this.hatoActivo.areaHato, [Validators.required, Validators.min(0)]],
      areaPastoreo: [this.hatoActivo.areaPastoreo, [Validators.required, Validators.min(0)]],
      cantCorrales: [this.hatoActivo.cantCorrales, [Validators.required, Validators.min(0)]],
      cantSalasOrdenio: [
        this.hatoActivo.cantSalasOrdenio,
        [Validators.required, Validators.min(0)],
      ],
      capacidadAlmacenarLeche: [
        this.hatoActivo.capacidadAlmacenarLeche,
        [Validators.required, Validators.min(0)],
      ],
      cantEmpleadosPermanentes: [
        this.hatoActivo.cantEmpleadosPermanentes,
        [Validators.required, Validators.min(0)],
      ],
      cantEmpleadosTemporales: [
        this.hatoActivo.cantEmpleadosTemporales,
        [Validators.required, Validators.min(0)],
      ],
    });
    this.errorEdicion = null;
    this.modoEdicion = true;
    this.cdr.markForCheck();
  }

  cerrarEdicion(): void {
    this.modoEdicion = false;
    this.errorEdicion = null;
    this.cdr.markForCheck();
  }

  guardarEdicion(): void {
    if (!this.editForm.valid || !this.hatoActivo?.idHato) return;
    this.guardando = true;
    this.errorEdicion = null;

    const dto: RegistroHatoDTO = this.editForm.value;
    this.hatoService.actualizarHato(this.hatoActivo.idHato, dto).subscribe({
      next: (hatoActualizado) => {
        this.hatoState.setHatoActivo(hatoActualizado);
        this.guardando = false;
        this.modoEdicion = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorEdicion = 'No se pudo guardar los cambios. Intenta nuevamente.';
        this.guardando = false;
        this.cdr.markForCheck();
      },
    });
  }

  get mostrarAccionesFormulario(): boolean {
    return !this.esAdmin;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
