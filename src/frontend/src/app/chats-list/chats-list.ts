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
  createdAt: string;
}

interface Conversation {
  itemId: number;
  itemTitle: string;
  buyerId: number;
  buyerName: string;
  lastMessageTime: string;
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
          const conversationMap = new Map<string, Conversation>();
          for (const chat of chats) {
            const key = `${chat.item.id}-${chat.buyer.id}`;
            const existing = conversationMap.get(key);
            if (!existing || chat.createdAt > existing.lastMessageTime) {
              const buyerName = chat.buyer.firstname
                ? `${chat.buyer.firstname} ${chat.buyer.lastname || ''}`
                : `Buyer #${chat.buyer.id}`;
              conversationMap.set(key, {
                itemId: chat.item.id,
                itemTitle: chat.item.title,
                buyerId: chat.buyer.id,
                buyerName,
                lastMessageTime: chat.createdAt
              });
            }
          }
          // Sort by most recent message first
          const sorted = Array.from(conversationMap.values())
            .sort((a, b) => b.lastMessageTime.localeCompare(a.lastMessageTime));
          this.conversations = sorted.map(c => ({
            itemId: c.itemId,
            itemTitle: c.itemTitle,
            buyerId: c.buyerId,
            buyerName: c.buyerName
          }));
        },
        error: (err) => console.error('Failed to load chats', err)
      });
  }

  openChat(itemId: number, buyerId: number) {
    this.router.navigate(['/chat', itemId, buyerId]);
  }
}