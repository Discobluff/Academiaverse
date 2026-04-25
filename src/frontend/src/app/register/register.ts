import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { environment } from '../../environments/environments';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register implements OnInit {
  
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);
  http: HttpClient = inject(HttpClient);

  firstname = "";
  lastname = "";
  email = "";
  password = "";
  passwordConfirmation = "";
  acceptedTerms = false;
  errorMessage = "";
  
  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }
  }

  register() {
    if (this.password != this.passwordConfirmation) {
      this.errorMessage = "The passwords are differents."
      return;
    }else if (!this.acceptedTerms) {
      this.errorMessage = "You must accept the terms of service to register."
      return;
    }
    const body = {
      firstname: this.firstname,
      lastname: this.lastname,
      email: this.email,
      password: this.password,
    };

  this.http.post(`${environment.apiUrl}/auth/register`, body, { responseType: 'text' }).subscribe({
    next: () => {
      this.router.navigate(['/login']);
    },
    error: () => {
      this.errorMessage = "Error with the registration.";
    }
  });
  }
}
