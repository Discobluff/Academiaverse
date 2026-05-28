import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {

  private http: HttpClient = inject(HttpClient);
  private router: Router = inject(Router);
  public authService: AuthService = inject(AuthService);

  email = "";
  admin = "";
  userPoints: number | null = null;

  ngOnInit(){
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
    }
    this.getTest();
    this.isAdmin();
    this.loadPoints();
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

  loadPoints() {
    const userId = this.authService.getUserId();
    if (!userId) return;
    this.http.get<{ points: number }>(`${environment.apiUrl}/users/${userId}`)
      .subscribe({
        next: (user) => this.userPoints = user.points,
        error: () => this.userPoints = null
      });
  }
}
