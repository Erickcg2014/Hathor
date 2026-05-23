import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnDestroy, OnInit, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, takeUntil, firstValueFrom } from 'rxjs';
import { Hato, HatoService } from '../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import {
  PerfilProductivo,
  PerfilProductivoService,
} from '../../../../core/services/produccion/perfil-productivo.service';
import {
  ProduccionLeche,
  ProduccionLecheService,
  RegistroProduccionLecheDTO,
} from '../../../../core/services/produccion/produccion-leche.service';
import {
  VentaLeche,
  VentaLecheService,
} from '../../../../core/services/finanzas/venta-leche.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import { KpiService } from '../../../../core/services/kpi/kpi.service';
import { ActualizarPerfilRapidoDTO } from '../../../../core/services/produccion/perfil-productivo.service';

@Component({
  selector: 'app-info-produccion',
  standalone: true,
  imports: [CommonModule, RouterLink, SpinnerComponent, FormsModule],
  templateUrl: './info-produccion.component.html',
  styleUrl: './info-produccion.component.css',
})
export class InfoProduccionComponent implements OnInit, OnDestroy {
  loading = true;
  error = '';
  hatoActivo: Hato | null = null;
  perfil: PerfilProductivo | null = null;
  registrosProduccion: ProduccionLeche[] = [];
  ventasLeche: VentaLeche[] = [];
  @Input() modoLectura = false;

  mostrarFormRegistro = false;
  guardandoRegistro = false;
  errorRegistro = '';
  nuevoRegistro: RegistroProduccionLecheDTO = {
    idHato: '',
    fecha: new Date().toISOString().split('T')[0],
    litrosProducidos: 0,
    vacasOrdenadas: undefined,
  };

  mostrarEdicionPerfil = false;
  guardandoPerfil = false;
  errorPerfil = '';
  editPerfil: ActualizarPerfilRapidoDTO = {};

  private destroy$ = new Subject<void>();

  constructor(
    private hatoService: HatoService,
    private hatoState: HatoStateService,
    private perfilProductivoService: PerfilProductivoService,
    private produccionLecheService: ProduccionLecheService,
    private kpiService: KpiService,
    private ventaLecheService: VentaLecheService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato?.idHato) {
        this.cargarTodo(hato.idHato);
        return;
      }
      this.perfil = null;
      this.loading = false;
      this.cdr.markForCheck();
    });
    this.cargarHatoInicial();
  }

  private cargarHatoInicial(): void {
    if (this.hatoState.getHatoActivo()) return;
    this.hatoService.getMisHatos().subscribe({
      next: (hatos) => {
        if (hatos.length > 0) {
          this.hatoState.setHatoActivo(hatos[0]);
          return;
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.error = 'No se pudieron cargar tus hatos en este momento.';
        this.cdr.markForCheck();
      },
    });
  }

  private async cargarTodo(idHato: string): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      const [perfil, produccion, ventas] = await Promise.allSettled([
        firstValueFrom(this.perfilProductivoService.getPerfilByHato(idHato)),
        firstValueFrom(this.produccionLecheService.getByHato(idHato)),
        firstValueFrom(this.ventaLecheService.getByHato(idHato)),
      ]);

      this.perfil = perfil.status === 'fulfilled' ? perfil.value : null;
      this.registrosProduccion = produccion.status === 'fulfilled' ? produccion.value : [];
      this.ventasLeche = ventas.status === 'fulfilled' ? ventas.value : [];
    } catch {
      this.error = 'Error cargando los datos de producción.';
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  // ===== GETTERS PERFIL =====

  get produccionMensualLitros(): number {
    return this.perfil ? this.perfil.produccionDiariaLitros * 30 : 0;
  }

  get ingresoMensualEstimado(): number {
    return this.perfil ? this.produccionMensualLitros * this.perfil.precioLitroPromedio : 0;
  }

  get litrosPorVacaPorDia(): number {
    if (!this.perfil?.vacasEnOrdenio || !this.perfil?.produccionDiariaLitros) return 0;
    return Math.round((this.perfil.produccionDiariaLitros / this.perfil.vacasEnOrdenio) * 10) / 10;
  }

  // ===== GETTERS PRODUCCIÓN REAL =====

  get produccionEsteMes(): number {
    const ahora = new Date();
    return this.registrosProduccion
      .filter((r) => {
        const f = new Date(r.fecha);
        return f.getMonth() === ahora.getMonth() && f.getFullYear() === ahora.getFullYear();
      })
      .reduce((acc, r) => acc + (r.litrosProducidos ?? 0), 0);
  }

  get promedioDiarioReal(): number {
    const ahora = new Date();
    const registrosMes = this.registrosProduccion.filter((r) => {
      const f = new Date(r.fecha);
      return f.getMonth() === ahora.getMonth() && f.getFullYear() === ahora.getFullYear();
    });
    if (registrosMes.length === 0) return 0;
    return Math.round((this.produccionEsteMes / registrosMes.length) * 10) / 10;
  }

  get ultimosRegistrosProduccion(): ProduccionLeche[] {
    return this.registrosProduccion.slice(0, 7);
  }

  // ===== GETTERS VENTAS =====

  get ventasEsteMes(): VentaLeche[] {
    const ahora = new Date();
    return this.ventasLeche.filter((v) => {
      const f = new Date(v.fecha);
      return f.getMonth() === ahora.getMonth() && f.getFullYear() === ahora.getFullYear();
    });
  }

  get litrosVendidosEsteMes(): number {
    return this.ventasEsteMes.reduce((acc, v) => acc + (v.litrosVendidos ?? 0), 0);
  }

  get ingresoRealEsteMes(): number {
    return this.ventasEsteMes.reduce(
      (acc, v) => acc + (v.litrosVendidos ?? 0) * (v.precioLitro ?? 0),
      0,
    );
  }

  get precioPromedioReal(): number {
    const ventas = this.ventasEsteMes.filter((v) => v.litrosVendidos && v.precioLitro);
    if (ventas.length === 0) return 0;
    const totalLitros = ventas.reduce((acc, v) => acc + v.litrosVendidos, 0);
    const totalIngreso = ventas.reduce((acc, v) => acc + v.litrosVendidos * v.precioLitro, 0);
    return Math.round(totalIngreso / totalLitros);
  }

  get porcentajeVendido(): number {
    if (!this.produccionEsteMes || !this.litrosVendidosEsteMes) return 0;
    return Math.round((this.litrosVendidosEsteMes / this.produccionEsteMes) * 100);
  }

  get ultimasVentas(): VentaLeche[] {
    return this.ventasLeche.slice(0, 5);
  }

  // ===== GETTERS ESTADO PRODUCCIÓN =====

  get estadoProduccionMes(): 'sin-datos' | 'critico' | 'bajo' | 'aceptable' | 'optimo' {
    const p = this.produccionEsteMes;
    if (p === 0) return 'sin-datos';
    if (p < 2000) return 'critico';
    if (p < 10000) return 'bajo';
    if (p < 11250) return 'aceptable';
    return 'optimo';
  }

  get mensajeEstadoProduccion(): string {
    const p = this.produccionEsteMes;
    const d = this.promedioDiarioReal;
    switch (this.estadoProduccionMes) {
      case 'sin-datos':
        return '📋 Sin registros este mes — ingresa tu primer registro de producción.';
      case 'critico':
        return `🔴 Producción crítica este mes (${p.toFixed(0)} L) — muy por debajo del mínimo recomendado de 2.000 L.`;
      case 'bajo':
        return `🟠 Producción baja este mes (${p.toFixed(0)} L) — el promedio diario de ${d} L/día está por debajo del objetivo de 375 L/día.`;
      case 'aceptable':
        return `🟡 Producción aceptable este mes (${p.toFixed(0)} L) — cerca del rango óptimo. Promedio diario: ${d} L/día.`;
      case 'optimo':
        return `✅ Producción óptima este mes (${p.toFixed(0)} L) — superando los 11.250 L recomendados. Promedio diario: ${d} L/día.`;
      default:
        return '';
    }
  }

  get produccionHoy(): number {
    const hoy = new Date().toISOString().split('T')[0];
    const reg = this.registrosProduccion.find((r) => r.fecha === hoy);
    return reg?.litrosProducidos ?? 0;
  }

  get mensajeProduccionHoy(): string {
    if (this.produccionHoy === 0) return '⚠️ Sin producción registrada hoy';
    if (this.produccionHoy < 100) return `🔴 Producción muy baja hoy: ${this.produccionHoy} L`;
    return `🥛 Producción registrada hoy: ${this.produccionHoy} L`;
  }

  abrirEdicionPerfil(): void {
    this.editPerfil = {
      vacasEnOrdenio: this.perfil?.vacasEnOrdenio,
      produccionDiariaLitros: this.perfil?.produccionDiariaLitros,
      precioLitroPromedio: this.perfil?.precioLitroPromedio,
      periodoLactanciaPromedio: this.perfil?.periodoLactanciaPromedio,
      frecuenciaOrdenio: this.perfil?.frecuenciaOrdenio,
    };
    this.errorPerfil = '';
    this.mostrarEdicionPerfil = true;
    this.cdr.markForCheck();
  }

  cerrarEdicionPerfil(): void {
    this.mostrarEdicionPerfil = false;
    this.cdr.markForCheck();
  }

  async guardarEdicionPerfil(): Promise<void> {
    if (!this.hatoActivo?.idHato) return;

    this.guardandoPerfil = true;
    this.errorPerfil = '';
    this.cdr.markForCheck();

    try {
      this.perfil = await firstValueFrom(
        this.perfilProductivoService.actualizarParcial(this.hatoActivo.idHato, this.editPerfil),
      );
      this.mostrarEdicionPerfil = false;
      this.kpiService.calcularKpis(this.hatoActivo.idHato).subscribe();
    } catch {
      this.errorPerfil = 'Error al guardar. Intenta de nuevo.';
    } finally {
      this.guardandoPerfil = false;
      this.cdr.markForCheck();
    }
  }

  // ===== FORMULARIO NUEVO REGISTRO =====

  abrirFormRegistro(): void {
    this.nuevoRegistro = {
      idHato: this.hatoActivo?.idHato ?? '',
      fecha: new Date().toISOString().split('T')[0],
      litrosProducidos: 0,
      vacasOrdenadas: undefined,
    };
    this.errorRegistro = '';
    this.mostrarFormRegistro = true;
    this.cdr.markForCheck();
  }

  cerrarFormRegistro(): void {
    this.mostrarFormRegistro = false;
    this.cdr.markForCheck();
  }

  async guardarRegistro(): Promise<void> {
    if (!this.nuevoRegistro.litrosProducidos || this.nuevoRegistro.litrosProducidos <= 0) {
      this.errorRegistro = 'Ingresa una cantidad válida de litros producidos.';
      this.cdr.markForCheck();
      return;
    }

    this.guardandoRegistro = true;
    this.errorRegistro = '';

    try {
      await firstValueFrom(this.produccionLecheService.crearRegistro(this.nuevoRegistro));
      this.mostrarFormRegistro = false;
      await this.cargarTodo(this.hatoActivo!.idHato!);

      // Recalcular KPIs en segundo plano
      if (this.hatoActivo?.idHato) {
        this.kpiService.calcularKpis(this.hatoActivo.idHato).subscribe();
      }
    } catch {
      this.errorRegistro = 'Error al guardar el registro. Intenta de nuevo.';
    } finally {
      this.guardandoRegistro = false;
      this.cdr.markForCheck();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
