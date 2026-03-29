import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environments';

@Component({
  selector: 'app-test',
  imports: [CommonModule],
  templateUrl: './test.html',
  styleUrl: './test.css',
})
export class Test {
  constructor(private http: HttpClient) {}
  test: string = "hello";

  ngOnInit() {
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
