import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html'
})
export class Home {

  constructor(private router: Router, private http: HttpClient, public authService: AuthService) {}

  email: String = "";
  admin: String = "";

  ngOnInit(){
    this.getTest();
    this.isAdmin();
  }

  clickLogin() {
    this.router.navigate(['/login']);
  }

  clickRegister() {
    this.router.navigate(['/register']);
  }

  clickLogout() {
    this.router.navigate(['/logout']);
  }

  clickSearch() {
    this.router.navigateByUrl('/search');
  }

  hasAdminRole() {
    return this.authService.hasRole('ADMIN');
  }

  getTest() {
    this.http.get(`${environment.apiUrl}/test`, { responseType: 'text' }).subscribe({
      next: (data) => {
        this.email = data;
      },
      error: (error) => {
        console.error('Error fetching test:', error);
      }
    });
  }

  isAdmin() {
    this.http.get(`${environment.apiUrl}/admin`, { responseType: 'text' }).subscribe({
      next: (data) => {
        this.admin = data;
      },
      error: (error) => {
        console.error('Error fetching admin:', error);
      }
    });
  }
}
