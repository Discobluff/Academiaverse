import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
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
export class Register {
  constructor(private authService: AuthService, private router: Router, private http: HttpClient) {}

  firstname: String = "";
  lastname: String = "";
  email: String = "";
  password: String = "";
  passwordConfirmation = "";

  errorMessage: String = "";

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }
  }

  register() {
    if (this.password != this.passwordConfirmation) {
      this.errorMessage = "The passwords are differents."
      return;
    }
    const body = {
      firstname: this.firstname,
      lastname: this.lastname,
      email: this.email,
      password: this.password,
    };

  this.http.post(`${environment.apiUrl}/auth/register`, body, { responseType: 'text' }).subscribe({
    next: (data) => {
      this.router.navigate(['/login']);
    },
    error: (error) => {
      this.errorMessage = "Error with the registration.";
    }
  });
  }
}
