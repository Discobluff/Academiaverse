import { Component, inject, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search.html',
  styleUrls: ['./search.css']
})
export class SearchComponent implements OnInit {
  query = '';
  results: unknown[] = [];
  recentSearches: string[] = [];
  suggestions: string[] = [];

  private apiBaseUrl = environment.apiBaseUrl;

  http: HttpClient = inject(HttpClient);
  authService: AuthService = inject(AuthService);

  ngOnInit() {
    // Only load recent searches if authenticated
    if (this.authService.isAuthenticated()) {
      this.loadRecentSearches();
    }
  }

  search() {
    if (!this.query.trim()) return;
    this.http.get<unknown[]>(`${this.apiBaseUrl}/api/courses/search?q=${this.query}`)
      .subscribe(data => {
        this.results = data;
        // Only save search if authenticated
        if (this.authService.isAuthenticated()) {
          this.saveRecentSearch(this.query);
        }
      });
  }

  saveRecentSearch(query: string) {
    this.http.post(`${this.apiBaseUrl}/api/search/recent?query=${query}`, {}, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }).subscribe(() => this.loadRecentSearches());
  }

  loadRecentSearches() {
    this.http.get<string[]>(`${this.apiBaseUrl}/api/search/recent`)
      .subscribe(data => this.recentSearches = data);
  }

  useRecentSearch(query: string) {
    this.query = query;
    this.search();
  }

  onInput() {
    if (this.query.length < 2) {
      this.suggestions = [];
      return;
    }
    this.http.get<string[]>(`${this.apiBaseUrl}/api/courses/autocomplete?q=${this.query}`)
      .subscribe(data => this.suggestions = data);
  }
}
