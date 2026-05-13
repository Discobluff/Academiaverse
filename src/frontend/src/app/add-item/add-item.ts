import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { environment } from '../../environments/environments';

interface CourseOption {
  id: number;
  name: string;
}

type ItemType = 'sale' | 'rent';
type PaymentMethod = 'cash' | 'points';

interface AddItemForm {
  title: string;
  description: string;
  notes: string;
  price: number | null;
  type: ItemType;
  images: string[];
  paymentMethod: PaymentMethod;
}

interface CreateItemPayload extends AddItemForm {
  course?: { id: number };
}

@Component({
  selector: 'app-add-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-item.html',
  styleUrls: ['./add-item.css']
})
export class AddItem implements OnInit {
  item: AddItemForm = {
    title: '',
    description: '',
    notes: '',
    price: null,
    type: 'sale',
    images: [],
    paymentMethod: 'points'
  };

  isDigital = false;
  selectedFile: File | null = null;
  courses: CourseOption[] = [];
  selectedCourseId: number | null = null;

  private http = inject(HttpClient);
  private router = inject(Router);

  ngOnInit() {
    this.loadCourses();
  }

  loadCourses() {
    this.http.get<CourseOption[]>(`${environment.apiBaseUrl}/api/courses/search?q=`)
      .subscribe({
        next: (data) => this.courses = data,
        error: (err) => console.error('Failed to load courses', err)
      });
  }

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

    const body: CreateItemPayload = { ...this.item };
    if (this.selectedCourseId) {
      body.course = { id: this.selectedCourseId };
    }

    if (this.isDigital && this.selectedFile) {
      const formData = new FormData();
      formData.append('title', this.item.title);
      formData.append('description', this.item.description);
      formData.append('notes', this.item.notes);
      formData.append('price', String(this.item.price));
      formData.append('type', this.item.type);
      formData.append('file', this.selectedFile);
      formData.append('paymentMethod', this.item.paymentMethod);
      if (this.selectedCourseId) {
        formData.append('courseId', String(this.selectedCourseId));
      }

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
      this.http.post(`${environment.apiBaseUrl}/api/items`, body).subscribe({
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