import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { createClient } from '@supabase/supabase-js';
import { SUPABASE_CONFIG } from '../../../core/config/supabase.config';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error = '';
  showPassword = false;
  showConfirmPassword = false;
  resetExitoso = false;
  tokenValido = false;
  verificandoToken = true;

  private supabase = createClient(SUPABASE_CONFIG.url, SUPABASE_CONFIG.key);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  async ngOnInit() {
    this.form = this.fb.group(
      {
        password: ['', [Validators.required, this.passwordValidator]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator },
    );

    await this.procesarTokenDelHash();
  }

  private async procesarTokenDelHash(): Promise<void> {
    try {
      if (typeof window === 'undefined') {
        this.verificandoToken = false;
        this.cdr.markForCheck();
        return;
      }

      const hash = window.location.hash;

      if (!hash) {
        this.verificandoToken = false;
        this.tokenValido = false;
        this.cdr.markForCheck();
        return;
      }

      const params = new URLSearchParams(hash.substring(1));
      const accessToken = params.get('access_token');
      const refreshToken = params.get('refresh_token');
      const type = params.get('type');

      if (accessToken && refreshToken && type === 'recovery') {
        const { error } = await this.supabase.auth.setSession({
          access_token: accessToken,
          refresh_token: refreshToken,
        });

        if (error) {
          this.error = 'El enlace de recuperación es inválido o ha expirado.';
          this.tokenValido = false;
        } else {
          this.tokenValido = true;
          window.history.replaceState(null, '', window.location.pathname);
        }
      } else {
        this.tokenValido = false;
        this.error = 'Enlace de recuperación inválido.';
      }
    } catch {
      this.tokenValido = false;
      this.error = 'Error procesando el enlace de recuperación.';
    } finally {
      this.verificandoToken = false;
      this.cdr.markForCheck();
    }
  }

  async onSubmit() {
    if (this.form.invalid || this.loading) return;

    this.loading = true;
    this.error = '';

    try {
      await this.authService.updatePassword(this.form.value.password);
      this.resetExitoso = true;
    } catch (err: any) {
      this.error = err.message || 'No se pudo actualizar la contraseña. Intenta de nuevo.';
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  irAlLogin() {
    this.router.navigate(['/login']);
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  get password() {
    return this.form.get('password');
  }

  get confirmPassword() {
    return this.form.get('confirmPassword');
  }

  passwordValidator(control: AbstractControl): { [key: string]: any } | null {
    const value = control.value;
    if (!value) return null;
    const hasMinLength = value.length >= 10;
    const hasLowercase = /[a-z]/.test(value);
    const hasUppercase = /[A-Z]/.test(value);
    const hasNumber = /\d/.test(value);
    const hasSymbol = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value);
    const isValid = hasMinLength && hasLowercase && hasUppercase && hasNumber && hasSymbol;
    if (!isValid)
      return {
        passwordStrength: { hasMinLength, hasLowercase, hasUppercase, hasNumber, hasSymbol },
      };
    return null;
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      group.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }
}
