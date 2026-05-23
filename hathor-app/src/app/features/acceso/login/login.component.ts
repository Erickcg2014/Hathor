import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, SpinnerComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error = '';
  showPassword = false;
  confirmationMessage = '';
  userEmail = '';

  showForgotOverlay = false;
  forgotEmail = '';
  forgotLoading = false;
  forgotExitoso = false;
  forgotError = '';

  correoNoConfirmado = false;
  reenvioLoading = false;
  reenvioExitoso = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });

    this.route.queryParams.subscribe((params) => {
      if (params['confirmed'] === 'pending') {
        this.userEmail = params['email'] || '';
        this.confirmationMessage =
          `Te hemos enviado un correo de confirmación a ${this.userEmail}.\n` +
          'Por favor revisa tu bandeja de entrada y haz clic en el enlace para activar tu cuenta.';
        if (this.userEmail) {
          this.form.patchValue({ email: this.userEmail });
        }
      }
    });
  }
  async onSubmit() {
    if (this.form.invalid) return;

    this.loading = true;
    this.error = '';
    this.correoNoConfirmado = false;
    this.reenvioExitoso = false;

    try {
      const { email, password } = this.form.value;
      await this.authService.signIn(email, password);
      this.router.navigate(['/home']);
    } catch (err: any) {
      const msg = err.message || '';

      if (msg.toLowerCase().includes('email not confirmed')) {
        this.correoNoConfirmado = true;
        this.userEmail = this.form.value.email;
      } else if (msg.toLowerCase().includes('invalid login credentials')) {
        this.error = 'Correo o contraseña incorrectos.';
      } else if (
        msg.toLowerCase().includes('user not found') ||
        msg.toLowerCase().includes('no user found') ||
        msg.toLowerCase().includes('invalid email')
      ) {
        this.error = 'No se encontró ninguna cuenta asociada a ese correo.';
      } else {
        this.error = msg || 'Error al iniciar sesión.';
      }
      this.cdr.markForCheck();
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  async reenviarConfirmacion() {
    this.reenvioLoading = true;
    this.reenvioExitoso = false;

    try {
      await this.authService.resendConfirmation(this.userEmail);
      this.reenvioExitoso = true;
    } catch (err: any) {
      this.error = 'No se pudo reenviar el correo. Intenta más tarde.';
    } finally {
      this.reenvioLoading = false;
      this.cdr.markForCheck();
    }
  }
  abrirForgotOverlay() {
    this.showForgotOverlay = true;
    this.forgotEmail = this.form.value.email ?? '';
    this.forgotError = '';
    this.forgotExitoso = false;
    this.cdr.markForCheck();
  }

  cerrarForgotOverlay() {
    this.showForgotOverlay = false;
    this.forgotExitoso = false;
    this.forgotError = '';
    this.cdr.markForCheck();
  }

  async enviarResetPassword() {
    if (!this.forgotEmail) {
      this.forgotError = 'Ingresa tu correo electrónico.';
      this.cdr.markForCheck();
      return;
    }

    this.forgotLoading = true;
    this.forgotError = '';

    try {
      await this.authService.sendPasswordResetEmail(this.forgotEmail);
      this.forgotExitoso = true;
    } catch (err: any) {
      this.forgotError = err.message || 'No se pudo enviar el correo. Intenta más tarde.';
    } finally {
      this.forgotLoading = false;
      this.cdr.markForCheck();
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  get email() {
    return this.form.get('email');
  }

  get password() {
    return this.form.get('password');
  }
}
