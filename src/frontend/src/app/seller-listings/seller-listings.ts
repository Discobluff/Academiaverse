import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environments';

interface item { id: number; title: string; description: string; price: number; type: string; notes: string; paymentMethod: string };

@Component({
  selector: 'app-seller-listings',
  imports: [],
  templateUrl: './seller-listings.html',
  styleUrl: './seller-listings.css',
})
export class SellerListings implements OnInit{

  http: HttpClient = inject(HttpClient);
  router: Router = inject(Router);   // new

  items: item[] = [];

  ngOnInit() {
    this.getListings();
  }

  getListings() {
    this.http.get<item[]>(`${environment.apiBaseUrl}/api/items/seller_listings`)
    .subscribe(data => this.items = data);
  }

  viewItem(id: number) {              // new
    this.router.navigate(['/item', id]);
  }

}
