import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';
import { DialogService } from '../core/services/dialog.service';

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
  availability?: string;
  availableUntil?: string;
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
  private dialog = inject(DialogService);

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
        if (hasBought) {
          this.getItemRating(itemId);
        }
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
        },
        error: () => this.item = null
      });
  }

  range(n: number): number[] {
    return Array.from({ length: n }, (_, i) => i);
  }

  getItemRating(itemId: number) {
    this.http.get<{ ratingValue: number }>(`${environment.apiBaseUrl}/api/ratings/user`, { params: { itemId: itemId } })
      .subscribe({
        next: (data) => this.starsSelected = data.ratingValue,
        error: () => this.starsSelected = 0
      });
  }

  rateItem(stars: number) {
    if (!this.item) return;
    this.http.post(`${environment.apiBaseUrl}/api/ratings`, { itemId: this.item.id, ratingValue: stars })
      .subscribe({
        next: () => {
          this.starsSelected = stars;
          this.getStarsAverage(this.item!.id);
        },
        error: (err) => {
          console.error(err);
          this.dialog.alert($localize`:@@msg_rating_failed:Could not submit rating.`, 'error');
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
          this.dialog.alert($localize`:@@msg_interest_expressed:Interest expressed successfully!`, 'success');
          this.alreadyInterested = true;
          this.loadItem();
        },
        error: (err) => {
          console.error(err);
          if (err.status === 409 || err.error?.message?.includes('already')) {
            this.dialog.alert($localize`:@@msg_already_interested:You have already expressed interest in this item.`, 'info');
            this.alreadyInterested = true;
          } else {
            this.dialog.alert($localize`:@@msg_express_interest_failed:Could not express interest.`, 'error');
          }
        }
      });
  }

  startChat() {
    if (!this.item) return;
    const buyerId = this.authService.getUserId();
    if (!buyerId) {
      this.dialog.alert($localize`:@@msg_must_login_chat:You must be logged in to start a chat.`, 'warning');
      return;
    }
    this.router.navigate(['/chat', this.item.id, buyerId]);
  }

  buyWithPoints() {
    if (!this.item) return;
    this.http.post(`${environment.apiBaseUrl}/api/items/${this.item.id}/buy-with-points`, {})
      .subscribe({
        next: () => {
          this.dialog.alert($localize`:@@msg_item_purchased:Item purchased with points!`, 'success');
          this.loadItem();
        },
        error: (err) => {
          console.error(err);
          this.dialog.alert($localize`:@@msg_purchase_failed_points:Purchase failed. You may not have enough points.`, 'error');
        }
      });
  }

  async reportItem() {
    if (!this.item) return;
    const reason = await this.dialog.prompt($localize`:@@msg_report_prompt:Please describe the issue with this item:`, $localize`:@@msg_report_title:Report item`);
    if (!reason) return;

    this.http.post(`${environment.apiBaseUrl}/api/reports`, {
      itemId: this.item.id,
      reason: reason.trim()
    }).subscribe({
      next: () => this.dialog.alert($localize`:@@msg_report_submitted:Report submitted. Thank you.`, 'success'),
      error: (err) => {
        console.error('Report failed', err);
        this.dialog.alert($localize`:@@msg_report_failed:Could not submit report.`, 'error');
      }
    });
  }

  viewImage(path: string) {
    window.open(`${environment.apiBaseUrl}${path}`, '_blank');
  }

  downloadFile() {
    if (!this.item) return;
    this.http.get(`${environment.apiBaseUrl}/api/items/${this.item.id}/download`, {
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.item!.digitalFileName || 'download';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Download failed', err);
        this.dialog.alert($localize`:@@msg_download_failed_purchase:Download failed. Make sure you purchased this item and are logged in.`, 'error');
      }
    });
  }
}
