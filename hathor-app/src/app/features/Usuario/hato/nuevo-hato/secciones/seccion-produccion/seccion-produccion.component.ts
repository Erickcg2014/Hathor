import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { Hato, HatoService } from '../../../../../../core/services/hato/hato.service';
import {
  PerfilProductivoService,
  RegistroPerfilProductivoDTO,
} from '../../../../../../core/services/produccion/perfil-productivo.service';
import { InventarioGanadoService } from '../../../../../../core/services/inventario/inventario-ganado.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-seccion-produccion',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: './seccion-produccion.component.html',
  styleUrl: './seccion-produccion.component.css',
})
export class SeccionProduccionComponent implements OnInit {
  @Input() hatoActual: Hato | null = null;
  @Input() modoReadonly = false;

  @Output() seccionCompletada = new EventEmitter<Hato>();
  @Output() irSiguiente = new EventEmitter<void>();
  @Output() irAnterior = new EventEmitter<void>();

  form!: FormGroup;
  loadingGuardar = false;
  loadingRazas = false;
  error = '';

  razasRegistradas: string[] = [];

  vacasPreLlenadas = false;

  sistemasOrdenio = ['MANUAL', 'MECÁNICO'];
  destinosLeche = [
    'Industria láctea (Alquería, Colanta, etc.)',
    'Quesería artesanal',
    'Venta directa al consumidor',
    'Cooperativa',
    'Intermediario local',
    'Autoconsumo',
  ];

  constructor(
    private fb: FormBuilder,
    private hatoService: HatoService,
    private perfilProductivoService: PerfilProductivoService,
    private inventarioGanadoService: InventarioGanadoService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.initForm();
    if (!this.modoReadonly) {
      this.cargarRazasRegistradas();
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      razaPredominante: ['', Validators.required],
      produccionDiariaLitros: [null, [Validators.required, Validators.min(1)]],
      precioLitroPromedio: [null, [Validators.required, Validators.min(1)]],
      vacasEnOrdenio: [null, [Validators.required, Validators.min(1)]],
      frecuenciaOrdenio: [null, [Validators.required, Validators.min(1), Validators.max(3)]],
      sistemaOrdenio: ['', Validators.required],
      destinoLeche: ['', Validators.required],
      periodoLactanciaPromedio: [null, [Validators.min(1)]],
    });
  }

  get escalaCalculada(): string {
    const vacas = this.form.get('vacasEnOrdenio')?.value;
    if (!vacas) return '';
    if (vacas < 25) return 'PEQUEÑA';
    if (vacas <= 200) return 'MEDIANA';
    if (vacas <= 500) return 'GRANDE';
    return 'EMPRESARIAL';
  }

  get iconoEscala(): string {
    const map: Record<string, string> = {
      PEQUEÑA: '🏡',
      MEDIANA: '🏘️',
      GRANDE: '🏭',
      EMPRESARIAL: '🏢',
    };
    return map[this.escalaCalculada] ?? '';
  }

  private readonly CATEGORIAS_PRODUCCION = [
    'producción',
    'produccion',
    'ordeño',
    'ordenio',
    'lactancia',
    'lactante',
    'vaca',
    'lechera',
  ];

  private async cargarRazasRegistradas(): Promise<void> {
    if (!this.hatoActual?.idHato) return;
    this.loadingRazas = true;
    try {
      const inventario = await firstValueFrom(
        this.inventarioGanadoService.getByHato(this.hatoActual.idHato),
      );

      const razasUnicas = [...new Set(inventario.map((i) => i.raza.nombre))];
      this.razasRegistradas = razasUnicas.length > 0 ? razasUnicas : this.razasFallback;

      const vacasActual = this.form.get('vacasEnOrdenio')?.value;
      if (!vacasActual) {
        const vacasEnProduccion = inventario
          .filter((item) => {
            const nombreCategoria = item.categoriaGanado?.nombreCategoria?.toLowerCase() ?? '';
            return this.CATEGORIAS_PRODUCCION.some((cat) => nombreCategoria.includes(cat));
          })
          .reduce((total, item) => total + (item.cantidad ?? 0), 0);

        if (vacasEnProduccion > 0) {
          this.form.patchValue({ vacasEnOrdenio: vacasEnProduccion });
          this.vacasPreLlenadas = true;
        }
      }
    } catch {
      this.razasRegistradas = this.razasFallback;
    } finally {
      this.loadingRazas = false;
      this.cdr.markForCheck();
    }
  }

  private readonly razasFallback = [
    'Holstein',
    'Jersey',
    'Pardo Suizo',
    'Normando',
    'Gyr Lechero',
    'Girolando',
    'Simmental',
    'Brahman',
    'Cebu',
    'Criolla',
    'Otra',
  ];

  // ===== GETTERS CALCULADOS =====

  get produccionMensualCalculada(): number {
    const diaria = this.form.get('produccionDiariaLitros')?.value;
    return diaria ? Math.round(diaria * 30) : 0;
  }

  get ingresoMensualEstimado(): number {
    const mensual = this.produccionMensualCalculada;
    const precio = this.form.get('precioLitroPromedio')?.value;
    return mensual && precio ? mensual * precio : 0;
  }

  get litrosPorVacaPorDia(): number {
    const litros = this.form.get('produccionDiariaLitros')?.value;
    const vacas = this.form.get('vacasEnOrdenio')?.value;
    if (!litros || !vacas || vacas === 0) return 0;
    return Math.round((litros / vacas) * 10) / 10;
  }

  // ===== GUARDAR =====

  async guardar(): Promise<void> {
    if (this.form.invalid || !this.hatoActual?.idHato) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }

    this.loadingGuardar = true;
    this.error = '';

    try {
      const v = this.form.value;
      const dto: RegistroPerfilProductivoDTO = {
        idHato: this.hatoActual.idHato!,
        razaPredominante: v.razaPredominante,
        produccionDiariaLitros: v.produccionDiariaLitros,
        precioLitroPromedio: v.precioLitroPromedio,
        vacasEnOrdenio: v.vacasEnOrdenio,
        frecuenciaOrdenio: v.frecuenciaOrdenio,
        sistemaOrdenio: v.sistemaOrdenio,
        destinoLeche: v.destinoLeche,
        periodoLactanciaPromedio: v.periodoLactanciaPromedio,
      };

      await firstValueFrom(this.perfilProductivoService.crearPerfil(dto));

      const hatoActualizado = await firstValueFrom(
        this.hatoService.actualizarCompletitud(this.hatoActual.idHato!, 75),
      );

      this.seccionCompletada.emit(hatoActualizado);
    } catch {
      this.error = 'Error al guardar. Intenta de nuevo.';
      this.cdr.markForCheck();
    } finally {
      this.loadingGuardar = false;
      this.cdr.markForCheck();
    }
  }
}
