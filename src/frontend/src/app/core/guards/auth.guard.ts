import { inject, Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);

  canActivate(): boolean | UrlTree {
    if (this.authService.isAuthenticated()) {
      return true;
    } else {
      // Return a UrlTree to redirect without navigation side effects
      return this.router.createUrlTree(['/login']);
    }
  }
}
