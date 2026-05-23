import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface BenefitItem {
  metric: string;
  title: string;
  description: string;
  icon: 'clock' | 'growth' | 'target' | 'leaders';
}

@Component({
  selector: 'app-benefits-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './benefits-section.component.html',
  styleUrl: './benefits-section.component.css'
})
export class BenefitsSectionComponent {
  benefits: BenefitItem[] = [
    {
      metric: '-80%',
      title: 'Tiempo en consolidación',
      description: 'Deja de pasar horas cruzando datos entre Excels. Hathor lo hace automático.',
      icon: 'clock'
    },
    {
      metric: '+15%',
      title: 'Rentabilidad identificada',
      description: 'Los hatos que miden sus KPIs identifican oportunidades de mejora que antes eran invisibles.',
      icon: 'growth'
    },
    {
      metric: '6',
      title: 'KPIs clave del sector',
      description: 'Litros/vaca/día, costo por litro, margen bruto y más — calculados automáticamente.',
      icon: 'target'
    },
    {
      metric: 'Top 25%',
      title: 'Compárate con líderes',
      description: 'Benchmarking contra los mejores hatos del sector para saber exactamente dónde mejorar.',
      icon: 'leaders'
    }
  ];
}
