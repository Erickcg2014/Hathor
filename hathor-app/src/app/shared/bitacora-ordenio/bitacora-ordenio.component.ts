import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil, firstValueFrom } from 'rxjs';

import { HatoStateService } from '../../core/services/ui-state/hato-state.service';
import { UserService } from '../../core/services/usuario/user.service';
import {
  ProduccionLecheService,
  RegistroProduccionLecheDTO,
} from '../../core/services/produccion/produccion-leche.service';
import {
  VentaLecheService,
  RespuestaVentaLeche,
} from '../../core/services/finanzas/venta-leche.service';
import { PerfilProductivoService } from '../../core/services/produccion/perfil-productivo.service';
import { KpiService } from '../../core/services/kpi/kpi.service';
import { Hato } from '../../core/services/hato/hato.service';

type Paso = 'produccion' | 'venta' | 'resumen';

@Component({
  selector: 'app-bitacora-ordenio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bitacora-ordenio.component.html',
  styleUrl: './bitacora-ordenio.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BitacoraOrdenioComponent implements OnInit, OnDestroy {
  // ── Visibilidad ───────────────────────────────────────────────────────
  visible = false;
  esAdmin = false;

  // ── Estado ────────────────────────────────────────────────────────────
  paso: Paso = 'produccion';
  guardando = false;
  error = '';

  // ── Hato ──────────────────────────────────────────────────────────────
  hatoActivo: Hato | null = null;

  // ── Formulario producción ─────────────────────────────────────────────
  registroProduccion = {
    litrosProducidos: null as number | null,
    vacasOrdenadas: null as number | null,
    fecha: new Date().toISOString().split('T')[0],
    omitir: false,
  };

  // ── Alerta de producción ──────────────────────────────────────────────
  alertaProduccion = false;
  litrosFaltantes = 0;
  mensajeAlerta = '';
  agregandoProduccionSugerida = false;

  // ── Formulario venta ──────────────────────────────────────────────────
  registroVenta = {
    litrosVendidos: null as number | null,
    precioLitro: null as number | null,
    registrar: false,
  };

  // ── Resumen guardado ──────────────────────────────────────────────────
  produccionGuardada = false;
  ventaGuardada = false;

  private destroy$ = new Subject<void>();

  constructor(
    private hatoState: HatoStateService,
    private userService: UserService,
    private produccionService: ProduccionLecheService,
    private ventaService: VentaLecheService,
    private perfilProductivoService: PerfilProductivoService,
    private kpiService: KpiService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Verificar rol — admin nunca ve la bitácora
    this.userService
      .getUsuarioLogueado()
      .pipe(takeUntil(this.destroy$))
      .subscribe((usuario) => {
        this.esAdmin = usuario?.rol === 'ADMIN';
        this.cdr.markForCheck();
      });

    // Suscribirse al hato activo
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato && !this.esAdmin) {
        this.verificarMostrar(hato.idHato as string);
      }
      this.cdr.markForCheck();
    });
  }

  // ── Lógica de aparición ───────────────────────────────────────────────

  private verificarMostrar(idHato: string): void {
    if (this.esAdmin) return;

    const key = `bitacora_ultima_fecha_${idHato}`;
    const hoy = new Date().toISOString().split('T')[0];
    const ultimaVez = localStorage.getItem(key);

    // Solo mostrar si no ha aparecido hoy
    if (ultimaVez === hoy) return;

    // Pequeño delay para no interrumpir la carga inicial
    setTimeout(() => {
      this.visible = true;
      this.paso = 'produccion';
      this.resetForms();
      this.cdr.markForCheck();
    }, 1500);
  }

  private marcarMostrado(): void {
    if (!this.hatoActivo?.idHato) return;
    const key = `bitacora_ultima_fecha_${this.hatoActivo.idHato}`;
    const hoy = new Date().toISOString().split('T')[0];
    localStorage.setItem(key, hoy);
  }

  // ── Navegación entre pasos ────────────────────────────────────────────

  irAPaso(paso: Paso): void {
    this.paso = paso;
    this.error = '';
    this.cdr.markForCheck();
  }

  // ── Guardar producción ────────────────────────────────────────────────

  async guardarProduccion(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;

    if (this.registroProduccion.omitir) {
      this.produccionGuardada = false;
      this.irAPaso('venta');
      return;
    }

    if (
      !this.registroProduccion.litrosProducidos ||
      this.registroProduccion.litrosProducidos <= 0
    ) {
      this.error = 'Ingresa una cantidad válida de litros.';
      this.cdr.markForCheck();
      return;
    }

    this.guardando = true;
    this.error = '';
    this.cdr.markForCheck();

    try {
      const dto: RegistroProduccionLecheDTO = {
        idHato: this.hatoActivo.idHato as any,
        fecha: this.registroProduccion.fecha as any,
        litrosProducidos: this.registroProduccion.litrosProducidos,
        vacasOrdenadas: this.registroProduccion.vacasOrdenadas ?? undefined,
      };

      await firstValueFrom(this.produccionService.crearRegistro(dto));
      this.produccionGuardada = true;

      // Actualizar frecuencia de ordeño en perfil productivo
      await this.actualizarFrecuenciaOrdenio();

      // Recalcular KPIs en segundo plano
      this.kpiService.calcularKpis(this.hatoActivo.idHato as string).subscribe();

      this.irAPaso('venta');
    } catch {
      this.error = 'Error al guardar la producción.';
    } finally {
      this.guardando = false;
      this.cdr.markForCheck();
    }
  }

  // ── Actualizar frecuencia de ordeño ───────────────────────────────────

  private async actualizarFrecuenciaOrdenio(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;
    try {
      const perfil = await firstValueFrom(
        this.perfilProductivoService.getPerfilByHato(this.hatoActivo.idHato as string),
      );
      if (!perfil) return;

      // Calcular nueva frecuencia promediando con la actual
      const frecuenciaActual = perfil.frecuenciaOrdenio ?? 1;
      // Si hoy se registró producción se asume al menos 1 ordeño
      const nuevaFrecuencia = Math.round((frecuenciaActual + 1) / 2);

      if (nuevaFrecuencia !== frecuenciaActual) {
        await firstValueFrom(
          this.perfilProductivoService.actualizarPerfil(this.hatoActivo.idHato as string, {
            ...perfil,
            idHato: this.hatoActivo.idHato as string,
            frecuenciaOrdenio: nuevaFrecuencia,
          }),
        );
      }
    } catch {
      // No fallar la bitácora por error en el perfil
    }
  }

  // ── Guardar venta ─────────────────────────────────────────────────────

  async guardarVenta(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;

    if (!this.registroVenta.registrar) {
      this.ventaGuardada = false;
      this.irAPaso('resumen');
      return;
    }

    if (!this.registroVenta.litrosVendidos || this.registroVenta.litrosVendidos <= 0) {
      this.error = 'Ingresa una cantidad válida de litros vendidos.';
      this.cdr.markForCheck();
      return;
    }

    if (!this.registroVenta.precioLitro || this.registroVenta.precioLitro <= 0) {
      this.error = 'Ingresa un precio válido por litro.';
      this.cdr.markForCheck();
      return;
    }

    this.guardando = true;
    this.error = '';
    this.cdr.markForCheck();

    try {
      const respuesta: RespuestaVentaLeche = await firstValueFrom(
        this.ventaService.crearVenta({
          idHato: this.hatoActivo.idHato as string,
          fecha: this.registroProduccion.fecha,
          litrosVendidos: this.registroVenta.litrosVendidos,
          precioLitro: this.registroVenta.precioLitro,
        }),
      );

      this.ventaGuardada = true;

      // ── alerta de producción ──
      if (respuesta.alerta && respuesta.litrosFaltantes > 0) {
        this.alertaProduccion = true;
        this.litrosFaltantes = respuesta.litrosFaltantes;
        this.mensajeAlerta = respuesta.mensaje ?? '';
        this.cdr.markForCheck();
      } else {
        this.irAPaso('resumen');
      }
    } catch {
      this.error = 'Error al guardar la venta.';
    } finally {
      this.guardando = false;
      this.cdr.markForCheck();
    }
  }

  async aceptarProduccionSugerida(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;

    this.agregandoProduccionSugerida = true;
    this.cdr.markForCheck();

    try {
      const dto: RegistroProduccionLecheDTO = {
        idHato: this.hatoActivo.idHato as any,
        fecha: this.registroProduccion.fecha as any,
        litrosProducidos: this.litrosFaltantes,
        vacasOrdenadas: undefined,
      };
      await firstValueFrom(this.produccionService.crearRegistro(dto));
      this.kpiService.calcularKpis(this.hatoActivo.idHato as string).subscribe();
    } catch {
    } finally {
      this.alertaProduccion = false;
      this.agregandoProduccionSugerida = false;
      this.irAPaso('resumen');
      this.cdr.markForCheck();
    }
  }

  rechazarProduccionSugerida(): void {
    this.alertaProduccion = false;
    this.litrosFaltantes = 0;
    this.mensajeAlerta = '';
    this.irAPaso('resumen');
    this.cdr.markForCheck();
  }
  // ── Cerrar ────────────────────────────────────────────────────────────

  cerrar(): void {
    this.marcarMostrado();
    this.visible = false;
    this.cdr.markForCheck();
  }

  irAInventarios(): void {
    this.cerrar();
    this.router.navigate(['/inventarios']);
  }

  // ── Reset ─────────────────────────────────────────────────────────────

  private resetForms(): void {
    this.registroProduccion = {
      litrosProducidos: null,
      vacasOrdenadas: null,
      fecha: new Date().toISOString().split('T')[0],
      omitir: false,
    };
    this.registroVenta = {
      litrosVendidos: null,
      precioLitro: null,
      registrar: false,
    };
    this.produccionGuardada = false;
    this.ventaGuardada = false;
    this.error = '';
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  get saludoHora(): string {
    const h = new Date().getHours();
    if (h < 12) return '🌅 Buenos días';
    if (h < 18) return '☀️ Buenas tardes';
    return '🌙 Buenas noches';
  }

  get fechaHoy(): string {
    return new Date().toLocaleDateString('es-CO', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
    });
  }

  get totalVentaHoy(): number {
    if (!this.registroVenta.litrosVendidos || !this.registroVenta.precioLitro) return 0;
    return this.registroVenta.litrosVendidos * this.registroVenta.precioLitro;
  }

  formatCOP(valor: number): string {
    return '$ ' + new Intl.NumberFormat('es-CO').format(valor);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
