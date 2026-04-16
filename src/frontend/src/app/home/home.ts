import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html'
})
export class Home {

  constructor(private http: HttpClient, private authService: AuthService) {}

  test: String = "";
  admin: String = "";

  ngOnInit(){
    this.getTest();
    this.isAdmin();
  }

  hasAdminRole() {
    return this.authService.hasRole('ADMIN');
  }

  getTest() {
    this.http.get(`${environment.apiUrl}/test`, { responseType: 'text' }).subscribe({
      next: (data) => {
        this.test = data;
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