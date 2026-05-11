import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LanguageSwitcher } from '../language-switcher/language-switcher';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, LanguageSwitcher],
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

  clickAddItem() {
    this.router.navigate(['/add-item']);
  }

  clickSellerListings() {
    this.router.navigate(['/seller-listings']);
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
