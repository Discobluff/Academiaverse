import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';

interface ChatMessage {
  id: number;
  message: string;
  sender: { id: number };
  buyer: { id: number; firstname?: string; lastname?: string };
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

  conversations: { itemId: number; itemTitle: string; buyerId: number; buyerName: string }[] = [];

  ngOnInit() {
    this.loadChats();
  }

  loadChats() {
    this.http.get<ChatMessage[]>(`${environment.apiBaseUrl}/api/chat`)
      .subscribe({
        next: (chats) => {
          const seen = new Set<string>();
          this.conversations = [];
          for (const chat of chats) {
            const key = `${chat.item.id}-${chat.buyer.id}`;
            if (!seen.has(key)) {
              seen.add(key);
              const buyerName = chat.buyer.firstname
                ? `${chat.buyer.firstname} ${chat.buyer.lastname || ''}`
                : `Buyer #${chat.buyer.id}`;
              this.conversations.push({
                itemId: chat.item.id,
                itemTitle: chat.item.title,
                buyerId: chat.buyer.id,
                buyerName
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