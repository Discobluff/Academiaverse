import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { environment } from '../../environments/environments';

@Component({
  selector: 'app-add-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-item.html',
  styleUrls: ['./add-item.css']
})
export class AddItemComponent {
  item = {
    title: '',
    description: '',
    price: null,
    type: 'sale',
    images: []
  };

  private http = inject(HttpClient);
  private router = inject(Router);

  onSubmit() {
    this.http.post(`${environment.apiBaseUrl}/api/items`, this.item).subscribe({
      next: () => {
        alert('Item created successfully!');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error(err);
        alert('Failed to create item. Make sure you are logged in.');
      }
    });
  }
}
