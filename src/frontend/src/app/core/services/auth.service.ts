import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environments';

@Injectable({ providedIn: 'root' })
export class AuthService {
    constructor(private http: HttpClient) { } login(credentials: { username: String; password: String }): Observable<any> {
        return this.http.post<{ token: string }>(`${environment.apiUrl}/auth/login`, credentials).pipe(
            tap(response => localStorage.setItem('token', response.token))
        );
    } logout(): void {
        localStorage.removeItem('token');
    } getToken(): string | null {
        return localStorage.getItem('token');
    } isAuthenticated(): boolean {
        return !!this.getToken();
    }
}