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
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';
import { UserService } from '../../../core/services/usuario/user.service';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, SpinnerComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error = '';
  success = '';
  showPassword = false;
  showConfirmPassword = false;
  correoExistente = false;
  correoIntentado = '';

  registroExitoso = false;
  credenciales: { correo: string; password: string } | null = null;
  showCredentialPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.form = this.fb.group(
      {
        nombre: ['', [Validators.required, Validators.minLength(2)]],
        apellido: ['', [Validators.required, Validators.minLength(2)]],
        email: ['', [Validators.required, Validators.email]],
        celular: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
        password: ['', [Validators.required, this.passwordValidator]],
        confirmPassword: ['', [Validators.required]],
      },
      {
        validators: this.passwordMatchValidator,
      },
    );
  }

  passwordValidator(control: AbstractControl): { [key: string]: any } | null {
    const value = control.value;
    if (!value) {
      return null;
    }

    // Mínimo 10 caracteres
    const hasMinLength = value.length >= 10;
    // Al menos una minúscula
    const hasLowercase = /[a-z]/.test(value);
    // Al menos una mayúscula
    const hasUppercase = /[A-Z]/.test(value);
    // Al menos un número
    const hasNumber = /\d/.test(value);
    // Al menos un símbolo
    const hasSymbol = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value);

    const isValid = hasMinLength && hasLowercase && hasUppercase && hasNumber && hasSymbol;

    if (!isValid) {
      return {
        passwordStrength: {
          hasMinLength,
          hasLowercase,
          hasUppercase,
          hasNumber,
          hasSymbol,
        },
      };
    }

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

  async onSubmit() {
    if (this.form.invalid || this.loading) return;

    this.loading = true;
    this.error = '';

    try {
      const { nombre, apellido, email, celular, password } = this.form.value;

      // 1. Registrar en Supabase Auth
      const authResponse = await this.authService.signUp(email, password);
      const userId = authResponse.user?.id;

      if (!userId) throw new Error('No se pudo obtener el ID del usuario de Supabase.');

      // 2. Registrar perfil en el backend
      await firstValueFrom(
        this.userService.crearUsuario({ idAuth: userId, nombre, apellido, correo: email, celular }),
      );

      // 3. Éxito
      this.credenciales = { correo: email, password };
      this.registroExitoso = true;
      this.cdr.markForCheck();
    } catch (err: any) {
      console.error('Error en registro:', err);

      const msg = err.message || '';
      const status = err.status;

      if (
        msg.includes('User already registered') ||
        msg.includes('already registered') ||
        status === 409
      ) {
        // Correo duplicado → mostrar overlay
        this.correoExistente = true;
        this.correoIntentado = this.form.value.email;
      } else if (msg.includes('429') || msg.includes('rate_limit')) {
        this.error = 'Demasiados intentos. Por favor espera unos minutos e intenta de nuevo.';
      } else if (msg.includes('Invalid email')) {
        this.error = 'Correo electrónico inválido.';
      } else {
        this.error = msg || 'Error al registrarse. Inténtalo más tarde.';
      }
      this.cdr.markForCheck();
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  irARecuperarPassword() {
    this.router.navigate(['/login'], {
      queryParams: { recovery: 'true', email: this.correoIntentado },
    });
  }

  cerrarOverlayCorreo() {
    this.correoExistente = false;
  }

  irAlLogin() {
    this.router.navigate(['/login'], {
      queryParams: { confirmed: 'pending', email: this.credenciales?.correo },
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
  toggleCredentialPassword() {
    this.showCredentialPassword = !this.showCredentialPassword;
  }

  get nombre() {
    return this.form.get('nombre');
  }

  get apellido() {
    return this.form.get('apellido');
  }

  get email() {
    return this.form.get('email');
  }

  get celular() {
    return this.form.get('celular');
  }

  get password() {
    return this.form.get('password');
  }

  get confirmPassword() {
    return this.form.get('confirmPassword');
  }
}
