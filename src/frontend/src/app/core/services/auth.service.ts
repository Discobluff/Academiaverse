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
            tap(response => localStorage.setItem('token', response.token))
        );
    } logout(): void {
        localStorage.removeItem('token');
        this.router.navigate(['/login']);
    } getToken(): string | null {
        return localStorage.getItem('token');
    } isAuthenticated(): boolean {
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
