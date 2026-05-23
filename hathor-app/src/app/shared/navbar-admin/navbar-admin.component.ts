import {
  Component,
  OnInit,
  OnDestroy,
  HostListener,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { Subject, takeUntil, filter, firstValueFrom } from 'rxjs';

import { AuthService } from '../../core/services/auth/auth.service';
import { AdminService, HatoAdminDTO } from '../../core/services/admin/admin.service';
import {
  AdminStateService,
  HatoAdminSeleccionado,
} from '../../core/services/ui-state/admin-state.service';
import {
  NavbarStateService,
  NavSubItem,
  NavbarSection,
} from '../../core/services/ui-state/navbar-state.service';
import { SpinnerComponent } from '../spinner/spinner.component';
import { HatoStateService } from '../../core/services/ui-state/hato-state.service';

@Component({
  selector: 'app-navbar-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, SpinnerComponent],
  templateUrl: './navbar-admin.component.html',
  styleUrl: './navbar-admin.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarAdminComponent implements OnInit, OnDestroy {
  logoutLoading = false;

  readonly accesosRapidos = [
    { label: 'Panel', route: '/admin' },
    { label: 'Hatos', route: '/admin/hatos' },
    { label: 'Reglas', route: '/admin/reglas' },
    { label: 'Practicas', route: '/admin/practicas' },
    { label: 'Recomendaciones', route: '/admin/recomendaciones' },
  ];

  departamentos: string[] = [];
  regiones: string[] = [];
  hatos: HatoAdminDTO[] = [];

  departamentoSeleccionado: string | null = null;
  regionSeleccionada: string | null = null;
  hatoSeleccionado: HatoAdminDTO | null = null;

  loadingDepartamentos = false;
  loadingRegiones = false;
  loadingHatos = false;

  dropdownDeptoAbierto = false;
  dropdownRegionAbierto = false;
  dropdownHatoAbierto = false;

  seccionActiva: NavbarSection | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private adminService: AdminService,
    private adminState: AdminStateService,
    private navbarState: NavbarStateService,
    private hatoState: HatoStateService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarDepartamentos();

    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe((e: any) => {
        this.navbarState.setSeccionPorRuta(e.urlAfterRedirects);
        this.cdr.markForCheck();
      });

    this.navbarState.seccionActiva$.pipe(takeUntil(this.destroy$)).subscribe((seccion) => {
      this.seccionActiva = seccion;
      this.cdr.markForCheck();
    });

    const filtros = this.adminState.getFiltros();
    if (filtros.departamento) {
      this.departamentoSeleccionado = filtros.departamento;
      this.cargarRegiones(filtros.departamento);
    }
    if (filtros.region) {
      this.regionSeleccionada = filtros.region;
      this.cargarHatos(filtros.departamento!, filtros.region);
    }
  }

  // ── Carga en cascada ──────────────────────────────────────────────────

  private cargarDepartamentos(): void {
    this.loadingDepartamentos = true;
    this.adminService.getDepartamentos().subscribe({
      next: (deptos) => {
        this.departamentos = deptos;
        this.loadingDepartamentos = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingDepartamentos = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarRegiones(departamento: string): void {
    this.loadingRegiones = true;
    this.regiones = [];
    this.adminService.getRegiones(departamento).subscribe({
      next: (regiones) => {
        this.regiones = regiones;
        this.loadingRegiones = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingRegiones = false;
        this.cdr.markForCheck();
      },
    });
  }

  private cargarHatos(departamento: string, region: string): void {
    this.loadingHatos = true;
    this.hatos = [];
    this.adminService.getHatosFiltrados(departamento, region).subscribe({
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

  // ── Selección en cascada ──────────────────────────────────────────────

  seleccionarDepartamento(depto: string): void {
    this.departamentoSeleccionado = depto;
    this.regionSeleccionada = null;
    this.hatoSeleccionado = null;
    this.regiones = [];
    this.hatos = [];
    this.dropdownDeptoAbierto = false;

    this.adminState.setDepartamento(depto);
    this.cargarRegiones(depto);
    this.cdr.markForCheck();
  }

  seleccionarRegion(region: string): void {
    this.regionSeleccionada = region;
    this.hatoSeleccionado = null;
    this.hatos = [];
    this.dropdownRegionAbierto = false;

    this.adminState.setRegion(region);
    this.cargarHatos(this.departamentoSeleccionado!, region);
    this.cdr.markForCheck();
  }

  seleccionarHato(hato: HatoAdminDTO): void {
    this.hatoSeleccionado = hato;
    this.dropdownHatoAbierto = false;

    const seleccionado: HatoAdminSeleccionado = {
      idHato: hato.idHato,
      nombreHato: hato.nombreHato,
      departamento: hato.departamento,
      ciudad: hato.ciudad,
      tropico: hato.tropico,
      escala: hato.escala,
      tipoHato: hato.tipoHato,
      porcentajeCompletitud: hato.porcentajeCompletitud,
      nombreUsuario: hato.nombreUsuario,
      apellidoUsuario: hato.apellidoUsuario,
      correoUsuario: hato.correoUsuario,
    };

    this.adminState.setHatoSeleccionado(seleccionado);

    this.hatoState.setHatoActivo({
      idHato: hato.idHato,
      nombreHato: hato.nombreHato,
      departamento: hato.departamento,
      ciudad: hato.ciudad,
      altitud: hato.altitud ?? 0,
      tropico: hato.tropico ?? '',
      areaHato: hato.areaHato ?? 0,
      areaPastoreo: 0,
      cantCorrales: 0,
      cantSalasOrdenio: 0,
      capacidadAlmacenarLeche: 0,
      cantEmpleadosPermanentes: 0,
      cantEmpleadosTemporales: 0,
      tipoHato: hato.tipoHato ?? '',
      porcentajeCompletitud: hato.porcentajeCompletitud,
      gastoMensualNomina: 0,
      gastoMensualAlimentacion: 0,
      escala:
        (hato.escala as 'PEQUEÑA' | 'MEDIANA' | 'GRANDE' | 'EMPRESARIAL' | undefined) ?? undefined,
      latitud: hato.latitud ?? undefined,
      longitud: hato.longitud ?? undefined,
    });

    this.cdr.markForCheck();
  }

  // ── Toggles dropdowns ─────────────────────────────────────────────────

  toggleDepto(): void {
    this.dropdownDeptoAbierto = !this.dropdownDeptoAbierto;
    this.dropdownRegionAbierto = false;
    this.dropdownHatoAbierto = false;
    this.cdr.markForCheck();
  }

  toggleRegion(): void {
    if (!this.departamentoSeleccionado) return;
    this.dropdownRegionAbierto = !this.dropdownRegionAbierto;
    this.dropdownDeptoAbierto = false;
    this.dropdownHatoAbierto = false;
    this.cdr.markForCheck();
  }

  toggleHato(): void {
    if (!this.regionSeleccionada) return;
    this.dropdownHatoAbierto = !this.dropdownHatoAbierto;
    this.dropdownDeptoAbierto = false;
    this.dropdownRegionAbierto = false;
    this.cdr.markForCheck();
  }

  // ── Subnav ────────────────────────────────────────────────────────────

  irASubitem(item: NavSubItem): void {
    this.navbarState.setSeccionPorRuta(item.route);
    this.router.navigateByUrl(item.route);
  }

  isSubitemActivo(item: NavSubItem): boolean {
    return this.router.url === item.route || this.router.url.startsWith(`${item.route}/`);
  }

  iconoSubitem(item: NavSubItem): string {
    const route = item.route.toLowerCase();

    if (route.includes('/admin')) return '🛠️';
    if (route.includes('/hato')) return '🐄';
    if (route.includes('/inventarios')) return '📦';
    if (route.includes('/finanzas')) return '💰';
    if (route.includes('/produccion')) return '🥛';
    if (route.includes('/benchmarking')) return '📊';
    if (route.includes('/practicas')) return '✅';
    if (route.includes('/reportes')) return '🧾';
    if (route.includes('/alertas')) return '🔔';
    if (route.includes('/progreso')) return '📈';

    return '•';
  }

  irAccesoRapido(route: string): void {
    this.router.navigateByUrl(route);
  }

  isAccesoActivo(route: string): boolean {
    return route === '/admin' ? this.router.url === route : this.router.url.startsWith(route);
  }

  get estadoFiltros(): string {
    if (!this.departamentoSeleccionado) {
      return 'Selecciona departamento para empezar';
    }
    if (!this.regionSeleccionada) {
      return 'Ahora elige una region';
    }
    if (!this.hatoSeleccionado) {
      return `Listo: ${this.hatos.length} hato(s) disponible(s)`;
    }
    return `${this.hatoSeleccionado.nombreHato} (${this.hatoSeleccionado.porcentajeCompletitud}% completado)`;
  }

  get tituloSubnav(): string {
    const seccion = this.seccionActiva?.seccion;
    const titulos: Record<string, string> = {
      '/admin': 'Administracion',
      '/hato': 'Hato',
      '/inventarios': 'Inventarios',
      '/finanzas': 'Finanzas',
      '/produccion': 'Produccion',
      '/benchmarking': 'Benchmarking',
      '/practicas': 'Practicas',
      '/reportes': 'Reportes',
      '/alertas': 'Alertas',
      '/progreso': 'Progreso',
      '/home': 'Inicio',
    };

    return (seccion && titulos[seccion]) || 'Navegacion';
  }

  get descripcionSubnav(): string {
    const seccion = this.seccionActiva?.seccion;
    const descripciones: Record<string, string> = {
      '/admin': 'Control global de la plataforma',
      '/hato': 'Datos base y configuracion del hato',
      '/inventarios': 'Activos, ganado y existencias',
      '/finanzas': 'Ingresos, costos y proyecciones',
      '/produccion': 'Rendimiento y registro lechero',
      '/benchmarking': 'Comparativas y posicion KPI',
      '/practicas': 'Seguimiento de acciones sugeridas',
      '/reportes': 'Consultas y exportables del sistema',
      '/alertas': 'Novedades y eventos relevantes',
      '/progreso': 'Evolucion general del hato',
      '/home': 'Resumen ejecutivo inicial',
    };

    return (seccion && descripciones[seccion]) || 'Opciones de navegacion de la seccion actual';
  }

  irHome(): void {
    this.router.navigate(['/admin']);
  }

  irMiCuenta(): void {
    this.router.navigate(['/miCuenta']);
  }

  // ── Logout ────────────────────────────────────────────────────────────

  async logout(): Promise<void> {
    this.logoutLoading = true;
    this.cdr.markForCheck();
    try {
      await this.authService.signOut();
      this.adminState.limpiar();
      this.navbarState.limpiar();
      this.router.navigate(['/login'], { replaceUrl: true });
    } finally {
      this.logoutLoading = false;
      this.cdr.markForCheck();
    }
  }

  // ── Cerrar dropdowns al click fuera ───────────────────────────────────

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.selector-cascada')) {
      this.dropdownDeptoAbierto = false;
      this.dropdownRegionAbierto = false;
      this.dropdownHatoAbierto = false;
      this.cdr.markForCheck();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
