import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';

interface ChatItem {
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
  private authService = inject(AuthService);

  uniqueItems: { id: number; title: string }[] = [];

  ngOnInit() {
    this.loadChats();
  }

  loadChats() {
    this.http.get<ChatItem[]>(`${environment.apiBaseUrl}/api/chat`)
      .subscribe({
        next: (chats) => {
          // Extract unique items from the chat messages
          const seen = new Set<number>();
          this.uniqueItems = [];
          for (const chat of chats) {
            if (!seen.has(chat.item.id)) {
              seen.add(chat.item.id);
              this.uniqueItems.push({ id: chat.item.id, title: chat.item.title });
            }
          }
        },
        error: (err) => console.error('Failed to load chats', err)
      });
  }

  openChat(itemId: number) {
    const buyerId = this.authService.getUserId();
    if (!buyerId) {
      alert('You must be logged in to view chats.');
      return;
    }
    this.router.navigate(['/chat', itemId, buyerId]);
  }
}