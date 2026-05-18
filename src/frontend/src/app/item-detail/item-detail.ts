import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
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
  authService = inject(AuthService);

  item: Item | null = null;
  alreadyInterested = false;

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.loadItem(id);
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
        },
        error: () => this.item = null
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