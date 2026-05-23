import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit{

  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);

  email = "";
  password = "";

  errorMessage = "";

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }
  }

  login() {
    this.authService.login({ username: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/home']),
      error: (error) => {
        if (error.status === 401) {
          this.errorMessage = "Wrong password.";
        } 
        if (error.status === 404) {
          this.errorMessage = "User not found.";
        }
      },
    });
  }

}
