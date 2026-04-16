import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from '../../environments/environments';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html'
})
export class Home {

  constructor(private http: HttpClient) {}

  test: String = "";

  ngOnInit(){
    this.getTest();
  }

  getTest() {
    this.http.get(`${environment.apiUrl}/test`, { responseType: 'text' }).subscribe({
      next: (data) => {
        this.test = data;
      },
      error: (error) => {
        console.error('Error fetching test:', error);
      }
    });
  }
}