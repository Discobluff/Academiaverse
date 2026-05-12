import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environments';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  router: Router = inject(Router);
  http: HttpClient = inject(HttpClient);

  login(credentials: { username: string; password: string }): Observable<unknown> {
    return this.http.post<{ token: string }>(`${environment.apiUrl}/auth/login`, credentials).pipe(
      tap(response => this.setToken(response.token))
    );
  }

  logout(): void {
    document.cookie = 'token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; SameSite=Lax';
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    const match = document.cookie.match(/(?:^|;\s*)token=([^;]*)/);
    return match ? decodeURIComponent(match[1]) : null;
  }

  private setToken(token: string): void {
    document.cookie = `token=${encodeURIComponent(token)}; path=/; SameSite=Lax`;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const token = this.getToken();
    if (!token) return false;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.role == role;
  }

  getUserId(): number | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.userId ?? null;
    } catch {
      return null;
    }
  }
}