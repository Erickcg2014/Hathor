import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { filter, take, switchMap, map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AuthService } from '../../core/services/auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(): Observable<boolean> {
    return this.authService.initialized$.pipe(
      filter((init) => init === true),
      take(1),
      switchMap(() => this.authService.isAuthenticated()),
      map((isAuth) => {
        if (!isAuth) {
          this.router.navigate(['/login']);
          return false;
        }
        return true;
      }),
    );
  }
}
