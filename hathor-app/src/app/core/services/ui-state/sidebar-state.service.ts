import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SidebarStateService {
  private collapsed = new BehaviorSubject<boolean>(false);
  public collapsed$ = this.collapsed.asObservable();

  toggle() {
    this.collapsed.next(!this.collapsed.value);
  }

  isCollapsed(): boolean {
    return this.collapsed.value;
  }
}
