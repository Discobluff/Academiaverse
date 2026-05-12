import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { LanguageSwitcher } from '../language-switcher/language-switcher';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, LanguageSwitcher],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  public authService: AuthService = inject(AuthService);
}
