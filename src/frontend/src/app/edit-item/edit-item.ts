import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../environments/environments';
import { DialogService } from '../core/services/dialog.service';

interface EditItemForm {
  title: string;
  description: string;
  notes: string;
  price: number | null;
  type: string;
  paymentMethod: string;
  availability?: string;
  availableUntil?: string;
  isDigital?: boolean;
  digitalFileName?: string;
  digitalFilePath?: string;
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
  private dialog = inject(DialogService);

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
  availableUntilDate = '';
  minDate = '';
  isDigital = false;
  digitalFileName = '';
  selectedFile: File | null = null;

  ngOnInit() {
    const today = new Date();
    this.minDate = today.toISOString().substring(0, 10);
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
        this.isDigital = data.isDigital || false;
        this.digitalFileName = data.digitalFileName || '';
        if (data.availableUntil) {
          this.availableUntilDate = data.availableUntil.substring(0, 10);
        }
      },
      error: (err) => {
        console.error('Failed to load item', err);
        this.dialog.alert($localize`:@@msg_item_not_found:Item not found.`, 'error')
          .then(() => this.router.navigate(['/seller-listings']));
      }
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

  downloadFile() {
    if (!this.itemId) return;
    this.http.get(`${environment.apiBaseUrl}/api/items/${this.itemId}/download`, {
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.digitalFileName || 'download';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Download failed', err);
        this.dialog.alert($localize`:@@msg_download_failed:Download failed.`, 'error');
      }
    });
  }

  onSubmit() {
    if (!this.isFormValid()) return;

    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('title', this.item.title);
      formData.append('description', this.item.description);
      formData.append('notes', this.item.notes);
      formData.append('price', String(this.item.price));
      formData.append('type', this.item.type);
      formData.append('paymentMethod', this.item.paymentMethod);
      formData.append('file', this.selectedFile);
      if (this.item.availability) {
        formData.append('availability', this.item.availability);
      }
      if (this.availableUntilDate && this.item.availability === 'Unavailable') {
        formData.append('availableUntil', this.availableUntilDate + 'T00:00:00');
      }
      this.http.put(`${environment.apiBaseUrl}/api/items/${this.itemId}/upload`, formData)
        .subscribe({
          next: () => {
            this.dialog.alert($localize`:@@msg_item_updated:Item updated successfully!`, 'success')
              .then(() => this.router.navigate(['/seller-listings']));
          },
          error: (err) => {
            console.error('Failed to update item', err);
            this.dialog.alert($localize`:@@msg_update_item_failed:Failed to update item.`, 'error');
          }
        });
    } else {
      const body: Record<string, unknown> = { ...this.item };
      if (this.availableUntilDate && this.item.availability === 'Unavailable') {
        body['availableUntil'] = this.availableUntilDate + 'T00:00:00';
      } else {
        body['availableUntil'] = null;
      }
      this.http.put(`${environment.apiBaseUrl}/api/items/${this.itemId}`, body)
        .subscribe({
          next: () => {
            this.dialog.alert($localize`:@@msg_item_updated:Item updated successfully!`, 'success')
              .then(() => this.router.navigate(['/seller-listings']));
          },
          error: (err) => {
            console.error('Failed to update item', err);
            this.dialog.alert($localize`:@@msg_update_item_failed:Failed to update item.`, 'error');
          }
        });
    }
  }
}
