import { Component } from '@angular/core';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-logout',
  imports: [],
  templateUrl: './logout.html',
})
export class Logout {
  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.logout();
  }

    logout(): void {
        this.authService.logout();
    }
}
