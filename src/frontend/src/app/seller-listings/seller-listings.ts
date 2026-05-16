import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environments';

interface item { id: number; title: string; description: string; price: number; type: string; notes: string; paymentMethod: string; status: string };

@Component({
  selector: 'app-seller-listings',
  imports: [],
  templateUrl: './seller-listings.html',
  styleUrl: './seller-listings.css',
})
export class SellerListings implements OnInit{

  http: HttpClient = inject(HttpClient);
  router: Router = inject(Router);

  items: item[] = [];

  ngOnInit() {
    this.getListings();
  }

  getListings() {
    this.http.get<item[]>(`${environment.apiBaseUrl}/api/items/seller_listings`)
    .subscribe(data => this.items = data);
  }

  viewItem(id: number) {
    this.router.navigate(['/item', id]);
  }

  editItem(id: number) {
    this.router.navigate(['/edit-item', id]);
  }

  deleteItem(id: number) {
    if (confirm('Are you sure you want to delete this item?')) {
      this.http.delete(`${environment.apiBaseUrl}/api/items/${id}`)
      .subscribe({
        next: () => {
          this.items = this.items.filter(i => i.id !== id);
        },
        error: (err) => {
          console.error('Delete failed', err);
          alert('Could not delete item.');
        }
      });
    }
  }
}