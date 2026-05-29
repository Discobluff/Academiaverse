import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';
import { DialogService } from '../core/services/dialog.service';

interface InterestResponse {
  user: {
    firstname: string;
    lastname: string;
  };
}

interface Item {
  id: number;
  title: string;
  description: string;
  price: number;
  type: string;
  notes: string;
  paymentMethod: string;
  availability?: string;
  interestedUsers?: string[];
}

@Component({
  selector: 'app-seller-listings',
  imports: [CommonModule],
  templateUrl: './seller-listings.html',
  styleUrl: './seller-listings.css',
})
export class SellerListings implements OnInit {

  http: HttpClient = inject(HttpClient);
  router: Router = inject(Router);
  private dialog = inject(DialogService);

  items: Item[] = [];

  ngOnInit() {
    this.getListings();
  }

  getListings() {
    this.http.get<Item[]>(`${environment.apiBaseUrl}/api/items/seller_listings`)
      .subscribe(data => {
        this.items = data;
        this.items.forEach(item => this.loadInterestsForItem(item));
      });
  }

  loadInterestsForItem(item: Item) {
    this.http.get<InterestResponse[]>(`${environment.apiBaseUrl}/api/interests/by-item/${item.id}`)
      .subscribe(interests => {
        item.interestedUsers = interests.map(i => `${i.user.firstname} ${i.user.lastname || ''}`);
      });
  }

  viewItem(id: number) {
    this.router.navigate(['/item', id]);
  }

  editItem(id: number) {
    this.router.navigate(['/edit-item', id]);
  }

  async deleteItem(id: number) {
    const confirmed = await this.dialog.confirm($localize`:@@msg_delete_item_q:Are you sure you want to delete this item?`, $localize`:@@msg_delete_item_title:Delete item`);
    if (!confirmed) return;

    this.http.delete(`${environment.apiBaseUrl}/api/items/${id}`)
      .subscribe({
        next: () => {
          this.items = this.items.filter(i => i.id !== id);
        },
        error: (err) => {
          console.error('Delete failed', err);
          this.dialog.alert($localize`:@@msg_delete_item_failed:Could not delete item.`, 'error');
        }
      });
  }
}
