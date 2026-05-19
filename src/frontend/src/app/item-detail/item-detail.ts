import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';

interface Item {
  id: number;
  title: string;
  description: string;
  notes: string;
  price: number;
  type: string;
  paymentMethod: string;
  isDigital: boolean;
  digitalFileName: string | null;
  digitalFilePath: string | null;
  images: string[];
  status: string;
  owner: { id: number };
}

interface InterestItem {
  id: number;
  item: { id: number };
}

@Component({
  selector: 'app-item-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item-detail.html',
  styleUrls: ['./item-detail.css']
})
export class ItemDetailComponent implements OnInit {
  http = inject(HttpClient);
  route = inject(ActivatedRoute);
  router = inject(Router);
  authService = inject(AuthService);

  item: Item | null = null;
  alreadyInterested = false;

  starsNumber = 5;
  starsSelected = 0;
  starsHovered = 0;
  hasBought = false;
  starsAverage = 0.0;

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.loadItem(id);
    });
  }

  getStarsAverage(itemId: number) {
    this.http.get<number>(`${environment.apiBaseUrl}/api/ratings`, {
      params: { itemId: itemId }
    }).subscribe({
      next: (average) => {
        this.starsAverage = average;
      },
      error: (err) => console.error(err)
    });
  }

  checkIfBought(itemId: number): void {
    this.http.get<boolean>(`${environment.apiBaseUrl}/api/items/bought`, {
      params: { itemId: itemId }
    }).subscribe({
      next: (hasBought) => {
        this.hasBought = hasBought;
      },
      error: (err) => console.error(err)
    });
  }

  loadItem(id?: number) {
    const itemId = id ?? this.item?.id;
    if (!itemId) return;
    this.http.get<Item>(`${environment.apiBaseUrl}/api/items/${itemId}`)
      .subscribe({
        next: (data) => {
          this.item = data;
          this.checkIfAlreadyInterested();
          this.checkIfBought(data.id);
          this.getStarsAverage(data.id);
          if (this.hasBought) {
            this.getItemRating(data.id);
          }
        },
        error: () => this.item = null
      });
  }

  range(n: number): number[] {
    return Array.from({ length: n }, (_, i) => i);
  }

  getItemRating(itemId: number) {
    this.http.get<{ rating: number }>(`${environment.apiBaseUrl}/api/ratings/user`, { params: { itemId: itemId } })
      .subscribe({
        next: (data) => this.starsSelected = data.rating,
        error: () => this.starsSelected = 0
      });
  }

  rateItem(stars: number) {
    if (!this.item) return;
    this.http.post(`${environment.apiBaseUrl}/api/ratings`, { itemId: this.item.id, ratingValue: stars })
      .subscribe({
        next: () => {
          console.log(stars, this.starsSelected);
          this.starsSelected = stars;
        },
        error: (err) => {
          console.error(err);
          alert('Could not submit rating.');
        }
      });
  }

  isOwner(): boolean {
    const userId = this.authService.getUserId();
    return this.item?.owner?.id === userId;
  }

  checkIfAlreadyInterested() {
    if (!this.item) return;
    this.http.get<InterestItem[]>(`${environment.apiBaseUrl}/api/interests`)
      .subscribe({
        next: (interests) => {
          this.alreadyInterested = interests.some(
            (interest) => interest.item?.id === this.item?.id
          );
        },
        error: () => this.alreadyInterested = false
      });
  }

  expressInterest() {
    if (!this.item || this.alreadyInterested) return;
    this.http.post(`${environment.apiBaseUrl}/api/interests`, { itemId: this.item.id })
      .subscribe({
        next: () => {
          alert('Interest expressed successfully!');
          this.alreadyInterested = true;
          this.loadItem();
        },
        error: (err) => {
          console.error(err);
          if (err.status === 409 || err.error?.message?.includes('already')) {
            alert('You have already expressed interest in this item.');
            this.alreadyInterested = true;
          } else {
            alert('Could not express interest.');
          }
        }
      });
  }

  startChat() {
    if (!this.item) return;
    const buyerId = this.authService.getUserId();
    if (!buyerId) {
      alert('You must be logged in to start a chat.');
      return;
    }
    this.router.navigate(['/chat', this.item.id, buyerId]);
  }

  buyWithPoints() {
    if (!this.item) return;
    this.http.post(`${environment.apiBaseUrl}/api/items/${this.item.id}/buy-with-points`, {})
      .subscribe({
        next: () => {
          alert('Item purchased with points!');
          this.loadItem(); // refresh status
        },
        error: (err) => {
          console.error(err);
          alert('Purchase failed. You may not have enough points.');
        }
      });
  }

  reportItem() {
    if (!this.item) return;
    const reason = prompt('Please describe the issue with this item:');
    if (!reason || !reason.trim()) return;

    this.http.post(`${environment.apiBaseUrl}/api/reports`, {
      itemId: this.item.id,
      reason: reason.trim()
    }).subscribe({
      next: () => alert('Report submitted. Thank you.'),
      error: (err) => {
        console.error('Report failed', err);
        alert('Could not submit report.');
      }
    });
  }
}