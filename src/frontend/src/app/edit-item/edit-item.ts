import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../environments/environments';

interface EditItemForm {
  title: string;
  description: string;
  notes: string;
  price: number | null;
  type: string;
  paymentMethod: string;
  availability?: string;
  availableUntil?: string;
}

@Component({
  selector: 'app-edit-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-item.html',
  styleUrls: ['./edit-item.css']
})
export class EditItemComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  itemId: number | null = null;
  item: EditItemForm = {
    title: '',
    description: '',
    notes: '',
    price: null,
    type: 'sale',
    paymentMethod: 'cash',
    availability: 'Available',
    availableUntil: ''
  };
  availableUntilDate: string = '';

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.itemId = +params['id'];
      if (this.itemId) {
        this.loadItem();
      }
    });
  }

  loadItem() {
    this.http.get<EditItemForm>(`${environment.apiBaseUrl}/api/items/${this.itemId}`)
    .subscribe({
      next: (data) => {
        this.item = data;
        this.item.availability = data.availability || 'Available';
        if (data.availableUntil) {
          this.availableUntilDate = data.availableUntil.substring(0, 10);
        }
      },
      error: (err) => {
        console.error('Failed to load item', err);
        alert('Item not found.');
        this.router.navigate(['/seller-listings']);
      }
    });
  }

  isFormValid(): boolean {
    return this.item.title.trim().length > 0 && this.item.price !== null && this.item.price > 0;
  }

  onSubmit() {
    if (!this.isFormValid()) return;
    const body: any = { ...this.item };
    if (this.availableUntilDate && this.item.availability === 'Unavailable') {
      body.availableUntil = this.availableUntilDate + 'T00:00:00';
    } else {
      body.availableUntil = null;
    }
    this.http.put(`${environment.apiBaseUrl}/api/items/${this.itemId}`, body)
    .subscribe({
      next: () => {
        alert('Item updated successfully!');
        this.router.navigate(['/seller-listings']);
      },
      error: (err) => {
        console.error('Failed to update item', err);
        alert('Failed to update item.');
      }
    });
  }
}