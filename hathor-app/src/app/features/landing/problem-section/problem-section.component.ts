import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface ProblemItem {
  title: string;
  description: string;
  icon: 'excel' | 'warning' | 'chart' | 'margin';
}

@Component({
  selector: 'app-problem-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './problem-section.component.html',
  styleUrl: './problem-section.component.css'
})
export class ProblemSectionComponent {
  problems: ProblemItem[] = [
    {
      title: 'Excels desconectados',
      description: 'Manejas múltiples hojas de cálculo sin integración, perdiendo horas consolidando datos que deberían estar automáticos.',
      icon: 'excel'
    },
    {
      title: 'Sin visibilidad financiera',
      description: 'No sabes si tu hato es rentable hasta que ya es tarde. Los registros en papel o Excel no te alertan a tiempo.',
      icon: 'warning'
    },
    {
      title: 'Sin comparación con el sector',
      description: '¿Cómo saber si tus 14 litros/vaca/día son buenos o malos? Sin referentes del sector, operas a ciegas.',
      icon: 'chart'
    },
    {
      title: 'Márgenes en riesgo',
      description: 'Con precios de leche volátiles y costos crecientes, no tener datos claros puede significar pérdidas invisibles.',
      icon: 'margin'
    }
  ];
}
