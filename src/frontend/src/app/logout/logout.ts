import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-logout',
  imports: [],
  templateUrl: './logout.html',
})
export class Logout implements OnInit {
  
  authService: AuthService = inject(AuthService);

  ngOnInit() {
    this.logout();
  }

    logout(): void {
        this.authService.logout();
    }
}
