import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';

interface InterestItem {
  id: number;
  item: {
    id: number;
    title: string;
    description: string;
    price: number;
    type: string;
    paymentMethod: string;
    isDigital: boolean;
  };
  createdAt: string;
}

interface PurchaseItem {
  id: number;
  title: string;
  description: string;
  price: number;
  type: string;
  paymentMethod: string;
  isDigital: boolean;
}

@Component({
  selector: 'app-buyer-interests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './buyer-interests.html',
  styleUrls: ['./buyer-interests.css']
})
export class BuyerInterestsComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private authService = inject(AuthService);

  interests: InterestItem[] = [];
  purchases: PurchaseItem[] = [];
  activeTab: 'interests' | 'purchases' = 'interests';

  ngOnInit() {
    this.loadInterests();
  }

  loadInterests() {
    this.http.get<InterestItem[]>(`${environment.apiBaseUrl}/api/interests`)
      .subscribe({
        next: (data) => this.interests = data,
        error: (err) => console.error('Failed to load interests', err)
      });
  }

  loadPurchases() {
    this.http.get<PurchaseItem[]>(`${environment.apiBaseUrl}/api/items/purchases`)
      .subscribe({
        next: (data) => this.purchases = data,
        error: (err) => console.error('Failed to load purchases', err)
      });
  }

  setTab(tab: 'interests' | 'purchases') {
    this.activeTab = tab;
    if (tab === 'purchases') {
      this.loadPurchases();
    }
  }

  viewItem(id: number) {
    this.router.navigate(['/item', id]);
  }

  removeInterest(itemId: number, event: MouseEvent) {
    event.stopPropagation();
    if (confirm('Remove this interest?')) {
      this.http.delete(`${environment.apiBaseUrl}/api/interests/${itemId}`)
        .subscribe({
          next: () => {
            this.interests = this.interests.filter(i => i.item.id !== itemId);
          },
          error: (err) => {
            console.error('Failed to remove interest', err);
            alert('Could not remove interest.');
          }
        });
    }
  }

  openChat(itemId: number, event: MouseEvent) {
    event.stopPropagation();
    const buyerId = this.authService.getUserId();
    if (!buyerId) {
      alert('You must be logged in to start a chat.');
      return;
    }
    this.router.navigate(['/chat', itemId, buyerId]);
  }
}