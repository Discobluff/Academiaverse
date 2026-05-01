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
export class AddItem {
  item = {
    title: '',
    description: '',
    price: null as number | null,
    type: 'sale',
    images: [] as string[],
    paymentMethod: "points" as "cash" | "points"
  };

  isDigital = false;
  selectedFile: File | null = null;

  private http = inject(HttpClient);
  private router = inject(Router);

  // New validation method
  isFormValid(): boolean {
    return this.item.title.trim().length > 0 && this.item.price !== null && this.item.price > 0;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onDigitalChange() {
    if (this.isDigital) {
      this.item.type = 'sale';
    }
  }

  onSubmit() {
    if (!this.isFormValid()) {
      return;
    }

    if (this.isDigital && this.selectedFile) {
      const formData = new FormData();
      formData.append('title', this.item.title);
      formData.append('description', this.item.description);
      formData.append('price', String(this.item.price));
      formData.append('type', this.item.type);
      formData.append('file', this.selectedFile);
      formData.append('paymentMethod', this.item.paymentMethod);

      this.http.post(`${environment.apiBaseUrl}/api/items/upload`, formData).subscribe({
        next: () => {
          alert('Digital item created successfully!');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error(err);
          alert('Failed to create digital item.');
        }
      });
    } else {
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
}
