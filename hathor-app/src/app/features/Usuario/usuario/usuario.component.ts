import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { UserService, Usuario } from '../../../core/services/usuario/user.service';
import { HatoService, Hato } from '../../../core/services/hato/hato.service';
import { AuthService } from '../../../core/services/auth/auth.service';

@Component({
  selector: 'app-usuario',
  standalone: true,
  templateUrl: './usuario.component.html',
  styleUrl: './usuario.component.css',
  imports: [CommonModule],
})
export class UsuarioComponent implements OnInit {
  usuario: Usuario | null = null;
  hatos: Hato[] = [];
  loading = true;
  error = '';

  mostrarModalEliminar = false;
  eliminando = false;
  errorEliminar = '';

  constructor(
    private userService: UserService,
    private hatoService: HatoService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
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
          this.error = 'No se pudo cargar la información.';
        }
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
      this.errorEliminar = 'Ocurrió un error al eliminar la cuenta. Intenta de nuevo.';
      this.eliminando = false;
      this.cdr.markForCheck();
    }
  }
}
