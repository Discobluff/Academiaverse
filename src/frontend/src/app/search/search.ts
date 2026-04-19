import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search.html',
  styleUrls: ['./search.css']
})
export class SearchComponent implements OnInit {
  query = '';
  results: any[] = [];
  recentSearches: string[] = [];
  suggestions: string[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadRecentSearches();
  }

  search() {
    if (!this.query.trim()) return;
    this.http.get<any[]>(`/api/courses/search?q=${this.query}`)
      .subscribe(data => {
        this.results = data;
        this.saveRecentSearch(this.query);
      });
  }

  saveRecentSearch(query: string) {
    this.http.post(`/api/search/recent?query=${query}`, {}, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }).subscribe(() => this.loadRecentSearches());
  }

  loadRecentSearches() {
    this.http.get<string[]>('/api/search/recent')
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
    this.http.get<string[]>(`/api/courses/autocomplete?q=${this.query}`)
      .subscribe(data => this.suggestions = data);
  }
}
