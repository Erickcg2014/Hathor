import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-benchmarking',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './benchmarking.component.html',
  styleUrl: './benchmarking.component.css',
})
export class BenchmarkingComponent {}
