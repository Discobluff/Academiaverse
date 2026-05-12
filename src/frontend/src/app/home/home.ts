import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
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
export class Home implements OnInit {

  private http: HttpClient = inject(HttpClient);
  private router: Router = inject(Router);
  public authService: AuthService = inject(AuthService);

  email = "";
  admin = "";

  ngOnInit(){
    this.getTest();
    this.isAdmin();
  }

  hasAdminRole() {
    return this.authService.hasRole('ADMIN');
  }

  getTest() {
    console.log(this.authService.getUserId());
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
