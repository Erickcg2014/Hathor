import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';

import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { NavbarAdminComponent } from '../../shared/navbar-admin/navbar-admin.component';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { SidebarStateService } from '../../core/services/ui-state/sidebar-state.service';
import { HatoService } from '../../core/services/hato/hato.service';
import { HatoStateService } from '../../core/services/ui-state/hato-state.service';
import { UserService } from '../../core/services/usuario/user.service';
import { BitacoraOrdenioComponent } from '../../shared/bitacora-ordenio/bitacora-ordenio.component';
import { AsistenteChatComponent } from '../../shared/asistente-chat/asistente-chat.component';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    NavbarComponent,
    NavbarAdminComponent,
    SidebarComponent,
    BitacoraOrdenioComponent,
    AsistenteChatComponent,
  ],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardLayoutComponent implements OnInit, OnDestroy {
  sidebarCollapsed = false;
  esAdmin = false;

  private destroy$ = new Subject<void>();

  constructor(
    private sidebarState: SidebarStateService,
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private userService: UserService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.sidebarState.collapsed$.pipe(takeUntil(this.destroy$)).subscribe((val) => {
      this.sidebarCollapsed = val;
      this.cdr.markForCheck();
    });

    this.userService
      .getUsuarioLogueado()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (usuario) => {
          this.esAdmin = usuario?.rol === 'ADMIN';
          this.cdr.markForCheck();
          if (!this.esAdmin) {
            this.verificarHatos();
          }
        },
        error: () => {
          this.esAdmin = false;
          this.cdr.markForCheck();
        },
      });
  }

  private verificarHatos(): void {
    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        if (hatos.length === 0) {
          this.router.navigate(['/hato']);
        } else {
          if (!this.hatoState.getHatoActivo()) {
            this.hatoState.setHatoActivo(hatos[0]);
          }
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.cdr.markForCheck();
      },
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
