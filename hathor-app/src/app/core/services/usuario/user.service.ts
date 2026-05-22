import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, from, switchMap } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { environment } from '../../../../environments/environment';

export interface Usuario {
  idUsuario?: string;
  idAuth: string;
  nombre: string;
  apellido: string;
  correo: string;
  celular: string;
  estado?: boolean;
  fecha_creacion?: string;
  rol: 'USER' | 'ADMIN';
}

export interface CrearUsuario {
  idAuth: string;
  nombre: string;
  apellido: string;
  correo: string;
  celular: string;
}
@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly API_URL = `${environment.apiUrl}/Usuario`;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  private getAuthHeaders(): Observable<HttpHeaders> {
    return from(this.authService.getAccessToken()).pipe(
      switchMap((token) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
        });
        return [headers];
      }),
    );
  }

  crearUsuario(usuario: CrearUsuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.API_URL, usuario);
  }

  getUsuarioLogueado(): Observable<Usuario> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) => this.http.get<Usuario>(`${this.API_URL}/me`, { headers })),
    );
  }
  eliminarMiCuenta(): Observable<void> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) => this.http.delete<void>(`${this.API_URL}/me`, { headers })),
    );
  }
}
