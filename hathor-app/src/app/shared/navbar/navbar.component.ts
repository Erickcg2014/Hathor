import { Component, ChangeDetectorRef, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil, filter } from 'rxjs';
import { AuthService } from '../../core/services/auth/auth.service';
import { HatoService, Hato } from '../../core/services/hato/hato.service';
import { HatoStateService } from '../../core/services/ui-state/hato-state.service';
import { UserService } from '../../core/services/usuario/user.service';
import {
  NavbarStateService,
  NavbarSection,
  NavSubItem,
} from '../../core/services/ui-state/navbar-state.service';
import { SpinnerComponent } from '../spinner/spinner.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, SpinnerComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent implements OnInit, OnDestroy {
  logoutLoading = false;
  dropdownAbierto = false;
  loadingHatos = false;
  hatos: Hato[] = [];
  hatoActivo: Hato | null = null;
  esAdmin = false;

  seccionActiva: NavbarSection | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private navbarState: NavbarStateService,
    private userService: UserService,
    public router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarHatos();

    this.userService
      .getUsuarioLogueado()
      .pipe(takeUntil(this.destroy$))
      .subscribe((usuario) => {
        this.esAdmin = usuario?.rol === 'ADMIN';
        this.cdr.markForCheck();
      });

    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      this.cdr.markForCheck();
    });

    this.navbarState.seccionActiva$.pipe(takeUntil(this.destroy$)).subscribe((seccion) => {
      this.seccionActiva = seccion;
      this.cdr.markForCheck();
    });

    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe((e: any) => {
        this.navbarState.setSeccionPorRuta(e.urlAfterRedirects);
        this.cdr.markForCheck();
      });
  }

  cargarHatos(): void {
    this.loadingHatos = true;
    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        this.hatos = hatos;
        this.loadingHatos = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingHatos = false;
        this.cdr.markForCheck();
      },
    });
  }

  toggleDropdown(): void {
    if (!this.dropdownAbierto && this.hatos.length === 0 && !this.loadingHatos) {
      this.cargarHatos();
    }
    this.dropdownAbierto = !this.dropdownAbierto;
    this.cdr.markForCheck();
  }

  irAHato(hato: Hato): void {
    this.dropdownAbierto = false;
    this.hatoState.setHatoActivo(hato);
    this.navbarState.setSeccionPorRuta('/hato');
    this.router.navigate(['/hato']);
    this.cdr.markForCheck();
  }

  nuevoHato(): void {
    this.dropdownAbierto = false;
    this.router.navigate(['/hato/nuevo']);
    this.cdr.markForCheck();
  }

  irHome(): void {
    this.router.navigate(['/home']);
  }

  irMiCuenta(): void {
    this.router.navigate(['/miCuenta']);
  }

  irASubitem(item: NavSubItem): void {
    this.navbarState.setSeccionPorRuta(item.route);
    this.router.navigateByUrl(item.route);
  }

  isSubitemActivo(item: NavSubItem): boolean {
    return this.router.url === item.route;
  }

  get itemsVisibles(): NavSubItem[] {
    if (!this.seccionActiva) return [];
    if (this.esAdmin) {
      return this.seccionActiva.items.filter((item) => !item.soloUsuario);
    }
    return this.seccionActiva.items;
  }

  async logout(): Promise<void> {
    this.logoutLoading = true;
    this.cdr.markForCheck();
    try {
      await this.authService.signOut();
      this.hatoState.limpiarHato();
      this.navbarState.limpiar();
      this.router.navigate(['/login'], { replaceUrl: true });
    } finally {
      this.logoutLoading = false;
      this.cdr.markForCheck();
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.hatos-dropdown-wrapper')) {
      this.dropdownAbierto = false;
      this.cdr.markForCheck();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
