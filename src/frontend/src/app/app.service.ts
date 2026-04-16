import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpXsrfTokenExtractor } from '@angular/common/http';
import { environment } from '../environments/environments';

@Injectable({ providedIn: 'root' })
export class AppService {
  authenticated = false;
  private http = inject(HttpClient);
  private tokenExtractor = inject(HttpXsrfTokenExtractor);

  authenticate(credentials: { email: string; password: string } | undefined, callback?: () => void): void {
    if (!credentials) {
      // Check if already authenticated (on app load)
      this.http.get<{ name?: string }>(`${environment.apiUrl}/user`, {
        withCredentials: true
      }).subscribe({
        next: (response) => {
          this.authenticated = !!response?.name;
          if (callback) callback();
        },
        error: () => {
          this.authenticated = false;
          if (callback) callback();
        }
      });
      return;
    }

    // Send form data for login (not JSON!)
    const formData = new URLSearchParams();
    formData.set('username', credentials.email); // Spring Security expects 'username'
    formData.set('password', credentials.password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      'X-Requested-With': 'XMLHttpRequest'
    });

    this.http.post(`${environment.apiUrl}/login`, formData.toString(), {
      headers,
      withCredentials: true // Send cookies
    }).subscribe({
      next: () => {
        // After login, check /user to confirm authentication
        this.http.get<{ name?: string }>(`${environment.apiUrl}/user`, {
          withCredentials: true
        }).subscribe({
          next: (response) => {
            this.authenticated = !!response?.name;
            if (callback) callback();
          },
          error: () => {
            this.authenticated = false;
            if (callback) callback();
          }
        });
      },
      error: (err) => {
        console.error('Login failed:', err);
        this.authenticated = false;
        if (callback) callback();
      }
    });
  }

  logout(callback?: () => void): void {
    this.http.post(`${environment.apiUrl}/logout`, {}, {
      withCredentials: true,
      headers: { 'X-XSRF-TOKEN': this.tokenExtractor.getToken() as string }
    }).subscribe({
      next: () => {
        this.authenticated = false;
        if (callback) callback();
      },
      error: (err) => {
        console.error('Logout failed:', err);
        this.authenticated = false;
        if (callback) callback();
      }
    });
  }
}