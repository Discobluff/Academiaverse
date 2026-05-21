import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';

interface ChatMessage {
  id: number;
  message: string;
  sender: { id: number };
  buyer: { id: number };
  item: { id: number; title: string };
}

@Component({
  selector: 'app-chats-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chats-list.html',
  styleUrls: ['./chats-list.css']
})
export class ChatsListComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);

  uniqueItems: { id: number; title: string; buyerId: number }[] = [];

  ngOnInit() {
    this.loadChats();
  }

  loadChats() {
    this.http.get<ChatMessage[]>(`${environment.apiBaseUrl}/api/chat`)
      .subscribe({
        next: (chats) => {
          // Group by item and remember the first buyer (the original buyer)
          const seen = new Set<number>();
          this.uniqueItems = [];
          for (const chat of chats) {
            if (!seen.has(chat.item.id)) {
              seen.add(chat.item.id);
              this.uniqueItems.push({
                id: chat.item.id,
                title: chat.item.title,
                buyerId: chat.buyer.id   // keep the original buyer for this conversation
              });
            }
          }
        },
        error: (err) => console.error('Failed to load chats', err)
      });
  }

  openChat(itemId: number, buyerId: number) {
    this.router.navigate(['/chat', itemId, buyerId]);
  }
}