import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { LanguageSwitcher } from '../language-switcher/language-switcher';
import { environment } from '../../environments/environments';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, LanguageSwitcher],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit, OnDestroy {
  public authService: AuthService = inject(AuthService);
  private http = inject(HttpClient);
  private router = inject(Router);

  userPoints: number | null = null;
  private pointsInterval: ReturnType<typeof setInterval> | null = null;

  ngOnInit() {
    this.loadPoints();
    // Refresh points every 5 seconds (so changes after a purchase are reflected)
    this.pointsInterval = setInterval(() => this.loadPoints(), 5000);
  }

  ngOnDestroy() {
    if (this.pointsInterval) clearInterval(this.pointsInterval);
  }

  isLogin(): boolean {
    return this.router.url === '/login';
  }

  isRegister(): boolean {
    return this.router.url === '/register';
  }

  loadPoints() {
    if (!this.authService.isAuthenticated()) {
      this.userPoints = null;
      return;
    }
    const userId = this.authService.getUserId();
    if (!userId) return;
    this.http.get<{ points: number }>(`${environment.apiUrl}/users/${userId}`)
      .subscribe({
        next: (user) => this.userPoints = user.points,
        error: () => this.userPoints = null
      });
  }
}