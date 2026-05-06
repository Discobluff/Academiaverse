import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';

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

  item: Item | null = null;

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
      next: (data) => this.item = data,
               error: () => this.item = null
    });
  }
}
