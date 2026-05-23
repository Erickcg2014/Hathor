import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { UserService, Usuario } from '../../core/services/usuario/user.service';
import { SidebarStateService } from '../../core/services/ui-state/sidebar-state.service';
import { NavbarStateService } from '../../core/services/ui-state/navbar-state.service';
import { CommonModule } from '@angular/common';
import { SpinnerComponent } from '../spinner/spinner.component';
import { AlertasService, AlertasResumenDTO } from '../../core/services/alertas/alertas.service';
import { HatoStateService } from '../../core/services/ui-state/hato-state.service';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, SpinnerComponent],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent implements OnInit, OnDestroy {
  usuario: Usuario | null = null;
  loading = true;
  collapsed = false;

  totalNoLeidas: number = 0;
  severidadMaxima: string = 'NINGUNA';
  private fallbackRol = 'USER';
  private destroy$ = new Subject<void>();

  menuItems: MenuItem[] = [
    { label: 'Inicio', route: '/home', icon: '🏠', roles: ['USER', 'ADMIN'] },
    { label: 'Mi Progreso', route: '/progreso', icon: '📈', roles: ['USER', 'ADMIN'] },
    { label: 'Hato', route: '/hato', icon: '🐄', roles: ['USER', 'ADMIN'] },
    {
      label: 'Inventario general',
      route: '/inventarios/general/resumen',
      icon: '📦',
      roles: ['USER', 'ADMIN'],
    },

    {
      label: 'Inventario ganado',
      route: '/inventarios/ganado/resumen',
      icon: '🐮',
      roles: ['USER', 'ADMIN'],
    },
    { label: 'Producción', route: '/produccion', icon: '🥛', roles: ['USER', 'ADMIN'] },
    { label: 'Finanzas', route: '/finanzas', icon: '💰', roles: ['USER', 'ADMIN'] },
    { label: 'Benchmarking', route: '/benchmarking', icon: '📈', roles: ['USER', 'ADMIN'] },
    { label: 'Prácticas', route: '/practicas', icon: '🌱', roles: ['USER', 'ADMIN'] },
    { label: 'Reportes', route: '/reportes', icon: '📄', roles: ['USER', 'ADMIN'] },

    {
      label: 'Gestión Prácticas',
      route: '/admin/practicas',
      icon: '⚙️',
      roles: ['ADMIN'],
    },
    {
      label: 'Administración',
      route: '/admin',
      icon: '🛡️',
      roles: ['ADMIN'],
    },
  ];

  constructor(
    private router: Router,
    private userService: UserService,
    private sidebarState: SidebarStateService,
    private navbarState: NavbarStateService,
    private alertasService: AlertasService,
    private hatoState: HatoStateService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.sidebarState.collapsed$.pipe(takeUntil(this.destroy$)).subscribe((val) => {
      this.collapsed = val;
      this.cdr.markForCheck();
    });

    this.userService
      .getUsuarioLogueado()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.usuario = data;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.usuario = { rol: this.fallbackRol } as Usuario;
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      if (hato?.idHato) {
        this.cargarBadgeAlertas(hato.idHato as string);
      } else {
        this.totalNoLeidas = 0;
        this.severidadMaxima = 'NINGUNA';
        this.cdr.markForCheck();
      }
    });
    this.alertasService.refrescarBadge$.pipe(takeUntil(this.destroy$)).subscribe(() => {
      const hato = this.hatoState.getHatoActivo();
      if (hato?.idHato) {
        this.cargarBadgeAlertas(hato.idHato as string);
      }
    });
  }
  private cargarBadgeAlertas(idHato: string): void {
    this.alertasService.getResumen(idHato).subscribe({
      next: (resumen) => {
        console.log('🔔 Badge resumen:', resumen);
        this.totalNoLeidas = resumen.totalNoLeidas;
        this.severidadMaxima = resumen.severidadMaxima;
        this.cdr.markForCheck();
      },
      error: () => {
        this.totalNoLeidas = 0;
        this.severidadMaxima = 'NINGUNA';
        this.cdr.markForCheck();
      },
    });
  }

  get colorBadge(): string {
    const map: Record<string, string> = {
      CRITICA: '#ef4444',
      PREVENTIVA: '#f59e0b',
      OPORTUNIDAD: '#19e64d',
      NINGUNA: '#9ca3af',
    };
    return map[this.severidadMaxima] ?? '#9ca3af';
  }

  toggle(): void {
    this.sidebarState.toggle();
  }

  navigate(route: string): void {
    this.navbarState.setSeccionPorRuta(route);
    this.router.navigate([route]);
  }

  hasAccess(item: MenuItem): boolean {
    const rol = this.usuario?.rol ?? this.fallbackRol;
    return item.roles.includes(rol);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
