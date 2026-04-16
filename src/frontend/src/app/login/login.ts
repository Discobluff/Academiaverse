import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  constructor(private authService: AuthService, private router: Router) {}
  email: String = "";
  password: String = "";

  errorMessage: String = "";

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }
  }


  login() {
    this.authService.login({ username: this.email, password: this.password }).subscribe({
      next: (response) =>       this.router.navigate(['/home']),
      error: (err) => this.errorMessage = "Login failed.",
    });
  }

}
