import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom, Subject, takeUntil } from 'rxjs';

import { Hato } from '../../../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../../../core/services/ui-state/hato-state.service';
import {
  CategoriaGanado,
  CategoriaGanadoService,
} from '../../../../../../core/services/inventario/categoria-ganado.service';
import { Raza, RazaService } from '../../../../../../core/services/inventario/raza.service';
import {
  InventarioGanadoDTO,
  InventarioGanadoService,
} from '../../../../../../core/services/inventario/inventario-ganado.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-inventario-ganado-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: './inventario-ganado-registro.component.html',
  styleUrl: './inventario-ganado-registro.component.css',
})
export class InventarioGanadoRegistroComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  categorias: CategoriaGanado[] = [];
  tiposRaza: string[] = [];
  razasPorFila: Raza[][] = [];
  razasPorTipo = new Map<string, Raza[]>();
  loading = false;
  guardando = false;
  error = '';
  success = '';
  submitted = false;
  toastVisible = false;

  form!: FormGroup;

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private hatoState: HatoStateService,
    private categoriaService: CategoriaGanadoService,
    private razaService: RazaService,
    private inventarioService: InventarioGanadoService,
  ) {
    this.form = this.fb.group({
      registros: this.fb.array([]),
    });
  }

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      this.submitted = false;
      this.error = '';
      this.success = '';
      this.form.setControl('registros', this.fb.array([]));
      this.razasPorFila = [];
      this.agregarRegistro();
    });

    this.cargarCatalogos();
  }

  get registros(): FormArray {
    return this.form.get('registros') as FormArray;
  }

  get totalEstimado(): number {
    return this.registros.controls.reduce((acc, ctrl) => {
      const cantidad = Number(ctrl.get('cantidad')?.value ?? 0);
      const valor = Number(ctrl.get('valorUnitario')?.value ?? 0);
      return acc + cantidad * valor;
    }, 0);
  }

  private cargarCatalogos(): void {
    this.loading = true;

    Promise.all([
      firstValueFrom(this.categoriaService.getCategorias()),
      firstValueFrom(this.razaService.getTiposRaza()),
    ])
      .then(([categorias, tipos]) => {
        this.categorias = categorias;
        this.tiposRaza = tipos;
        this.loading = false;
      })
      .catch(() => {
        this.error = 'No se pudieron cargar categorías y razas.';
        this.loading = false;
      });
  }

  private crearRegistroForm(): FormGroup {
    return this.fb.group({
      tipoRaza: [null as string | null, [Validators.required]],
      idRaza: [null as number | null, [Validators.required]],
      idCategoria: [null as number | null, [Validators.required]],
      cantidad: [0, [Validators.required, Validators.min(1)]],
      edadPromedioMeses: [0, [Validators.required, Validators.min(0)]],
      valorUnitario: [0, [Validators.required, Validators.min(1)]],
    });
  }

  agregarRegistro(): void {
    this.registros.push(this.crearRegistroForm());
    this.razasPorFila.push([]);
  }

  eliminarRegistro(index: number): void {
    if (this.registros.length <= 1) return;
    this.registros.removeAt(index);
    this.razasPorFila.splice(index, 1);
  }

  async onTipoRazaChange(index: number): Promise<void> {
    const group = this.registros.at(index) as FormGroup;
    const tipo = group.get('tipoRaza')?.value as string | null;

    if (!tipo) {
      this.razasPorFila[index] = [];
      group.patchValue({ idRaza: null });
      return;
    }

    if (!this.razasPorTipo.has(tipo)) {
      const razas = await firstValueFrom(this.razaService.getRazasPorTipo(tipo));
      this.razasPorTipo.set(tipo, razas);
    }

    this.razasPorFila[index] = this.razasPorTipo.get(tipo) ?? [];
    group.patchValue({ idRaza: null });
  }

  seleccionarTipoRaza(index: number, tipo: string): void {
    const group = this.registros.at(index) as FormGroup;
    group.patchValue({ tipoRaza: tipo });
    void this.onTipoRazaChange(index);
  }

  subtotalFila(index: number): number {
    const group = this.registros.at(index) as FormGroup;
    const cantidad = Number(group.get('cantidad')?.value ?? 0);
    const valor = Number(group.get('valorUnitario')?.value ?? 0);
    return cantidad * valor;
  }

  tieneErrores(index: number): boolean {
    return (this.registros.at(index) as FormGroup).invalid;
  }

  controlInvalido(index: number, controlName: string): boolean {
    const control = this.registros.at(index)?.get(controlName);
    return !!control && this.submitted && control.invalid;
  }

  formatCOP(valor: number | null | undefined): string {
    if (valor === null || valor === undefined) return '$ 0';
    return '$ ' + new Intl.NumberFormat('es-CO').format(valor);
  }

  guardar(): void {
    this.submitted = true;
    this.error = '';
    this.success = '';

    if (!this.hatoActivo?.idHato) {
      this.error = 'Selecciona un hato activo para registrar ganado.';
      return;
    }

    if (!this.registros.length || this.form.invalid) {
      this.registros.markAllAsTouched();
      return;
    }

    const payload: InventarioGanadoDTO[] = this.registros.controls.map((ctrl) => ({
      idHato: this.hatoActivo!.idHato!,
      idRaza: ctrl.get('idRaza')?.value as number,
      idCategoria: ctrl.get('idCategoria')?.value as number,
      cantidad: Number(ctrl.get('cantidad')?.value ?? 0),
      edadPromedioMeses: Number(ctrl.get('edadPromedioMeses')?.value ?? 0),
      valorUnitario: Number(ctrl.get('valorUnitario')?.value ?? 0),
    }));

    this.guardando = true;
    Promise.all(payload.map((dto) => firstValueFrom(this.inventarioService.crearRegistro(dto))))
      .then(() => {
        this.guardando = false;
        this.success = 'Registros de inventario ganado guardados correctamente.';
        this.submitted = false;
        this.form.setControl('registros', this.fb.array([]));
        this.razasPorFila = [];
        this.agregarRegistro();
        this.mostrarToast();
      })
      .catch(() => {
        this.guardando = false;
        this.error = 'No fue posible guardar los registros de ganado.';
      });
  }

  private mostrarToast(): void {
    this.toastVisible = true;
    setTimeout(() => {
      this.toastVisible = false;
    }, 3500);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
