import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterModule, Router, NavigationEnd } from '@angular/router';
import { Location, CommonModule } from '@angular/common';
import { Navbar } from './navbar/navbar';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterModule, Navbar, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private router = inject(Router);
  private location = inject(Location);

  showBack = false;

  private topLevel = new Set([
    '/home', '/', '/login', '/register', '/logout',
    '/search', '/add-item', '/seller-listings', '/buyer-interests', '/chats', '/terms'
  ]);

  constructor() {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => {
        const path = e.urlAfterRedirects || e.url;
        this.showBack = !this.topLevel.has(path);
      });
  }

  goBack() {
    this.location.back();
  }
}
