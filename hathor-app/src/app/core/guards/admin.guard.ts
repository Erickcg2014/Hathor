import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable, map, take } from 'rxjs';
import { UserService } from '../../core/services/usuario/user.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(
    private userService: UserService,
    private router: Router,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.userService.getUsuarioLogueado().pipe(
      take(1),
      map((usuario) => {
        if (usuario?.rol === 'ADMIN') {
          return true;
        }
        // Redirigir a home si no es admin
        return this.router.createUrlTree(['/home']);
      }),
    );
  }
}
