import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface StepItem {
  number: string;
  title: string;
  description: string;
  icon: 'upload' | 'process' | 'chart' | 'idea';
}

@Component({
  selector: 'app-how-it-works-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './how-it-works-section.component.html',
  styleUrl: './how-it-works-section.component.css'
})
export class HowItWorksSectionComponent {
  steps: StepItem[] = [
    {
      number: '01',
      title: 'Ingresa tus datos',
      description: 'Carga tu información financiera vía formularios o importando archivos Excel de forma sencilla.',
      icon: 'upload'
    },
    {
      number: '02',
      title: 'Procesamiento automático',
      description: 'El sistema calcula tus KPIs, indicadores y genera reportes financieros de manera automática.',
      icon: 'process'
    },
    {
      number: '03',
      title: 'Visualiza y compara',
      description: 'Accede a dashboards interactivos y compara tus métricas con estándares del sector lechero.',
      icon: 'chart'
    },
    {
      number: '04',
      title: 'Mejora tu rentabilidad',
      description: 'Recibe recomendaciones basadas en benchmarking para optimizar la operación de tu hato.',
      icon: 'idea'
    }
  ];
}
