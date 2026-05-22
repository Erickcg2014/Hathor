import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-finanzas',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './finanzas.component.html',
  styleUrl: './finanzas.component.css',
})
export class FinanzasComponent {}
