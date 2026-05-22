import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface BenchmarkRow {
  indicator: string;
  yourFarm: string;
  yourNote: string;
  yourNoteClass: 'up' | 'down' | 'neutral';
  average: string;
  leader: string;
}

@Component({
  selector: 'app-benchmarking-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './benchmarking-section.component.html',
  styleUrl: './benchmarking-section.component.css'
})
export class BenchmarkingSectionComponent {
  rows: BenchmarkRow[] = [
    { indicator: 'Litros / vaca / día', yourFarm: '14.2 L', yourNote: '↗ Por encima', yourNoteClass: 'up', average: '13.8 L', leader: '22.0 L' },
    { indicator: 'Costo por litro', yourFarm: '$1,450', yourNote: '↘ Por debajo', yourNoteClass: 'down', average: '$1,320', leader: '$980' },
    { indicator: 'Margen bruto', yourFarm: '18%', yourNote: '↘ Por debajo', yourNoteClass: 'down', average: '22%', leader: '41%' },
    { indicator: 'Eficiencia alimenticia', yourFarm: '1.4 kg/L', yourNote: '— Promedio', yourNoteClass: 'neutral', average: '1.5 kg/L', leader: '1.1 kg/L' }
  ];
}
