import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { UserService, Usuario, ActualizarUsuarioDTO } from '../../../core/services/usuario/user.service';
import { HatoService, Hato } from '../../../core/services/hato/hato.service';
import { AuthService } from '../../../core/services/auth/auth.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-usuario',
  standalone: true,
  templateUrl: './usuario.component.html',
  styleUrl: './usuario.component.css',
  imports: [CommonModule, ReactiveFormsModule, SpinnerComponent],
})
export class UsuarioComponent implements OnInit {
  usuario: Usuario | null = null;
  hatos: Hato[] = [];
  loading = true;
  error = '';

  modoEdicion = false;
  guardando = false;
  errorEdicion: string | null = null;
  editForm!: FormGroup;

  mostrarModalEliminar = false;
  eliminando = false;
  errorEliminar = '';

  constructor(
    private userService: UserService,
    private hatoService: HatoService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.error = '';

    forkJoin({
      usuario: this.userService.getUsuarioLogueado(),
      hatos: this.hatoService.getMisHatos(),
    }).subscribe({
      next: ({ usuario, hatos }) => {
        this.usuario = usuario;
        this.hatos = hatos;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error:', err);
        this.loading = false;
        if (err.status !== 401) {
          this.error = 'No se pudo cargar la informacion.';
        }
        this.cdr.markForCheck();
      },
    });
  }

  abrirEdicion(): void {
    if (!this.usuario) return;
    this.editForm = this.fb.group({
      nombre: [this.usuario.nombre, [Validators.required, Validators.minLength(2)]],
      apellido: [this.usuario.apellido, [Validators.required, Validators.minLength(2)]],
      celular: [this.usuario.celular, [Validators.required, Validators.minLength(7)]],
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
    if (!this.editForm.valid) return;
    this.guardando = true;
    this.errorEdicion = null;

    const dto: ActualizarUsuarioDTO = this.editForm.value;
    this.userService.actualizarUsuario(dto).subscribe({
      next: (usuarioActualizado) => {
        this.usuario = usuarioActualizado;
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

  abrirModalEliminar(): void {
    this.mostrarModalEliminar = true;
    this.errorEliminar = '';
    this.cdr.markForCheck();
  }

  cerrarModalEliminar(): void {
    if (this.eliminando) return;
    this.mostrarModalEliminar = false;
    this.errorEliminar = '';
    this.cdr.markForCheck();
  }

  async confirmarEliminarCuenta(): Promise<void> {
    this.eliminando = true;
    this.errorEliminar = '';
    this.cdr.markForCheck();

    try {
      await this.userService.eliminarMiCuenta().toPromise();
      await this.authService.signOut();
      this.router.navigate(['/']);
    } catch {
      this.errorEliminar = 'Ocurrio un error al eliminar la cuenta. Intenta de nuevo.';
      this.eliminando = false;
      this.cdr.markForCheck();
    }
  }
}