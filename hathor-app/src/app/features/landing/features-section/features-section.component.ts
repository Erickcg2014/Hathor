import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface FeatureItem {
  title: string;
  description: string;
  icon: 'hatos' | 'finanzas' | 'kpi' | 'estadisticas' | 'reportes' | 'benchmarking';
}

@Component({
  selector: 'app-features-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './features-section.component.html',
  styleUrl: './features-section.component.css'
})
export class FeaturesSectionComponent {
  features: FeatureItem[] = [
    {
      title: 'Gestión de Hatos',
      description: 'Registra y administra tus hatos, animales y movimientos de forma centralizada y estructurada.',
      icon: 'hatos'
    },
    {
      title: 'Gestión Financiera',
      description: 'Controla ingresos por venta de leche y animales, y registra gastos en alimentación, veterinaria y mano de obra.',
      icon: 'finanzas'
    },
    {
      title: 'KPIs e Indicadores',
      description: 'Calcula automáticamente producción promedio por vaca, costo por litro, rentabilidad y margen bruto.',
      icon: 'kpi'
    },
    {
      title: 'Estadísticas Visuales',
      description: 'Visualiza tendencias y resultados financieros a través de gráficas interactivas y tableros intuitivos.',
      icon: 'estadisticas'
    },
    {
      title: 'Reportes Financieros',
      description: 'Genera estados de resultados, flujos de caja y exporta reportes en múltiples formatos.',
      icon: 'reportes'
    },
    {
      title: 'Benchmarking',
      description: 'Compara tus indicadores contra estándares de la industria e identifica oportunidades de mejora.',
      icon: 'benchmarking'
    }
  ];
}
