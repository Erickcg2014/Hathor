import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom, Subject, takeUntil } from 'rxjs';

import { Hato } from '../../../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../../../core/services/ui-state/hato-state.service';
import {
  CategoriaInventario,
  CategoriaInventarioService,
} from '../../../../../../core/services/inventario/categoria-inventario.service';
import {
  InventarioGeneralDTO,
  InventarioGeneralService,
} from '../../../../../../core/services/inventario/inventario-general.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-inventario-general-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: './inventario-general-registro.component.html',
  styleUrl: './inventario-general-registro.component.css',
})
export class InventarioGeneralRegistroComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  categorias: CategoriaInventario[] = [];
  categoriasPadre: CategoriaInventario[] = [];
  subcategoriasPorFila: CategoriaInventario[][] = [];
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
    private categoriaService: CategoriaInventarioService,
    private inventarioService: InventarioGeneralService,
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
      this.subcategoriasPorFila = [];
      this.agregarRegistro();
    });

    this.cargarCategorias();
  }

  get registros(): FormArray {
    return this.form.get('registros') as FormArray;
  }

  get totalEstimado(): number {
    return this.registros.controls.reduce((acc, ctrl) => {
      const cantidad = Number(ctrl.get('cantidad')?.value ?? 0);
      const valorUnitario = Number(ctrl.get('valorUnitario')?.value ?? 0);
      return acc + cantidad * valorUnitario;
    }, 0);
  }

  private cargarCategorias(): void {
    this.loading = true;
    this.categoriaService
      .getCategorias()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cats) => {
          this.categorias = cats;
          this.categoriasPadre = cats.filter((c) => c.tipo === 'PADRE');
          this.loading = false;
        },
        error: () => {
          this.error = 'No se pudieron cargar las categorías de inventario.';
          this.loading = false;
        },
      });
  }

  private crearRegistroForm(): FormGroup {
    return this.fb.group({
      idCategoriaPadre: [null as number | null, [Validators.required]],
      idCategoriaInventario: [null as number | null, [Validators.required]],
      nombreItem: ['', [Validators.required, Validators.minLength(2)]],
      cantidad: [0, [Validators.required, Validators.min(1)]],
      valorUnitario: [0, [Validators.required, Validators.min(1)]],
      descripcion: [''],
    });
  }

  agregarRegistro(): void {
    this.registros.push(this.crearRegistroForm());
    this.subcategoriasPorFila.push([]);
  }

  eliminarRegistro(index: number): void {
    if (this.registros.length <= 1) return;
    this.registros.removeAt(index);
    this.subcategoriasPorFila.splice(index, 1);
  }

  onCategoriaPadreChange(index: number): void {
    const group = this.registros.at(index) as FormGroup;
    const idPadre = Number(group.get('idCategoriaPadre')?.value ?? 0);

    const subcategorias = this.categorias.filter(
      (c) => c.tipo === 'HIJA' && c.categoriaPadre?.idCategoriaInventario === idPadre,
    );

    this.subcategoriasPorFila[index] = subcategorias;
    group.patchValue({
      idCategoriaInventario: null,
      nombreItem: '',
    });
  }

  onSubcategoriaChange(index: number): void {
    const group = this.registros.at(index) as FormGroup;
    const idSubcategoria = Number(group.get('idCategoriaInventario')?.value ?? 0);
    const subcategoria = this.subcategoriasPorFila[index]?.find(
      (c) => c.idCategoriaInventario === idSubcategoria,
    );

    if (subcategoria) {
      group.patchValue({ nombreItem: subcategoria.nombre });
    }
  }

  subtotalFila(index: number): number {
    const group = this.registros.at(index) as FormGroup;
    const cantidad = Number(group.get('cantidad')?.value ?? 0);
    const valorUnitario = Number(group.get('valorUnitario')?.value ?? 0);
    return cantidad * valorUnitario;
  }

  tieneErrores(index: number): boolean {
    const group = this.registros.at(index) as FormGroup;
    return group.invalid;
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
      this.error = 'Selecciona un hato activo para registrar el item.';
      return;
    }

    if (!this.registros.length || this.form.invalid) {
      this.registros.markAllAsTouched();
      return;
    }

    const payload: InventarioGeneralDTO[] = this.registros.controls.map((ctrl) => ({
      idHato: this.hatoActivo!.idHato!,
      idCategoriaInventario: ctrl.get('idCategoriaInventario')?.value as number,
      nombreItem: ((ctrl.get('nombreItem')?.value as string | null) ?? '').trim(),
      cantidad: Number(ctrl.get('cantidad')?.value ?? 0),
      valorUnitario: Number(ctrl.get('valorUnitario')?.value ?? 0),
      descripcion: ((ctrl.get('descripcion')?.value as string | null) ?? '').trim() || undefined,
    }));

    this.guardando = true;
    Promise.all(payload.map((dto) => firstValueFrom(this.inventarioService.crearItem(dto))))
      .then(() => {
        this.guardando = false;
        this.success = 'Registros de inventario general guardados correctamente.';
        this.submitted = false;
        this.form.setControl('registros', this.fb.array([]));
        this.subcategoriasPorFila = [];
        this.agregarRegistro();
        this.mostrarToast();
      })
      .catch(() => {
        this.guardando = false;
        this.error = 'No fue posible guardar los registros. Intenta de nuevo.';
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
