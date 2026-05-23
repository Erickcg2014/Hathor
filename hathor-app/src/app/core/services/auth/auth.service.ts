import { Injectable } from '@angular/core';
import { createClient, SupabaseClient } from '@supabase/supabase-js';
import { BehaviorSubject, Observable } from 'rxjs';
import { SUPABASE_CONFIG } from '../../config/supabase.config';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private supabase: SupabaseClient;
  private currentUser = new BehaviorSubject<any>(null);
  private initialized = new BehaviorSubject<boolean>(false);
  public currentUser$ = this.currentUser.asObservable();
  public initialized$ = this.initialized.asObservable();

  constructor() {
    this.supabase = createClient(SUPABASE_CONFIG.url, SUPABASE_CONFIG.key);

    this.initUser().then(() => {
      this.supabase.auth.onAuthStateChange((event, session) => {
        this.currentUser.next(session?.user ?? null);
      });
    });
  }

  private async initUser() {
    const {
      data: { session },
    } = await this.supabase.auth.getSession();

    this.currentUser.next(session?.user ?? null);
    this.initialized.next(true);
  }

  async signUp(email: string, password: string): Promise<any> {
    const { data, error } = await this.supabase.auth.signUp({ email, password });

    if (error) {
      if (error.message.includes('rate_limit')) {
        throw new Error(
          'Demasiados intentos de registro. Por favor espera unos minutos e intenta de nuevo.',
        );
      } else if (error.message.includes('User already registered')) {
        throw new Error('Este correo ya está registrado. Intenta iniciar sesión.');
      } else if (error.message.includes('Invalid email')) {
        throw new Error('Correo electrónico inválido.');
      } else if (error.message.includes('Password should be')) {
        throw new Error('La contraseña debe tener al menos 6 caracteres.');
      } else {
        throw new Error(error.message);
      }
    }

    return data;
  }

  async signIn(email: string, password: string): Promise<any> {
    const { data, error } = await this.supabase.auth.signInWithPassword({ email, password });

    if (error) {
      throw new Error(error.message);
    }

    this.currentUser.next(data.user);
    return data;
  }

  async signOut(): Promise<void> {
    await this.supabase.auth.signOut();
    this.currentUser.next(null);
  }

  async getAccessToken(): Promise<string | null> {
    const {
      data: { session },
    } = await this.supabase.auth.getSession();
    return session?.access_token ?? null;
  }

  async resendConfirmation(email: string): Promise<void> {
    const { error } = await this.supabase.auth.resend({
      type: 'signup',
      email,
    });
    if (error) throw new Error(error.message);
  }

  // Envía el correo de recuperación de contraseña
  async sendPasswordResetEmail(email: string): Promise<void> {
    const { error } = await this.supabase.auth.resetPasswordForEmail(email, {
      redirectTo: `${environment.appUrl}/auth/reset-password`,
    });
    if (error) throw new Error(error.message);
  }

  // Actualiza la contraseña una vez el usuario llegó desde el enlace del correo
  async updatePassword(newPassword: string): Promise<void> {
    const { error } = await this.supabase.auth.updateUser({
      password: newPassword,
    });
    if (error) throw new Error(error.message);
  }

  getCurrentUser(): Observable<any> {
    return this.currentUser$;
  }

  isAuthenticated(): Observable<boolean> {
    return this.currentUser$.pipe(map((user) => !!user));
  }
}
