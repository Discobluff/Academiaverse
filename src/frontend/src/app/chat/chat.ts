import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';

interface ChatMessage {
  id: number;
  message: string;
  sender: { id: number; firstname: string; lastname: string };
  buyer: { id: number; firstname?: string; lastname?: string };
  item: { id: number; title: string };
  createdAt: string;
}

interface ItemDetail {
  id: number;
  title: string;
  owner: { id: number; firstname: string; lastname: string };
  course?: { id: number; name: string };
}

interface UserDetail {
  id: number;
  firstname: string;
  lastname: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
  styleUrls: ['./chat.css']
})
export class ChatComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);

  itemId: number | null = null;
  buyerId: number | null = null;
  messages: ChatMessage[] = [];
  newMessage = '';
  currentUserId: number | null = null;
  private polling: ReturnType<typeof setInterval> | null = null;

  itemTitle = 'Loading...';
  sellerName = '';
  buyerName = '';
  courseName = 'Not assigned';

  ngOnInit() {
    this.currentUserId = this.authService.getUserId();
    this.route.params.subscribe(params => {
      this.itemId = +params['itemId'];
      this.buyerId = +params['buyerId'];
      this.loadItemDetails();
      this.loadBuyerName();
      this.loadMessages();
      this.polling = setInterval(() => this.loadMessages(), 3000);
    });
  }

  ngOnDestroy() {
    if (this.polling) clearInterval(this.polling);
  }

  loadItemDetails() {
    this.http.get<ItemDetail>(`${environment.apiBaseUrl}/api/items/${this.itemId}`)
    .subscribe({
      next: (item) => {
        this.itemTitle = item.title;
        this.sellerName = item.owner ? `${item.owner.firstname} ${item.owner.lastname}` : 'Unknown seller';
        this.courseName = item.course?.name || 'Not assigned';
      },
      error: () => {
        this.itemTitle = 'Unknown item';
        this.sellerName = 'Unknown seller';
      }
    });
  }

  loadBuyerName() {
    this.http.get<UserDetail>(`${environment.apiBaseUrl}/v1/users/${this.buyerId}`)
    .subscribe({
      next: (user) => this.buyerName = `${user.firstname} ${user.lastname}`,
      error: () => this.buyerName = 'Unknown buyer'
    });
  }

  scrollToBottom() {
    setTimeout(() => {
      const panel = document.querySelector('.messages-panel');
      if (panel) {
        panel.scrollTop = panel.scrollHeight;
      }
    }, 100);
  }

  loadMessages() {
    this.http.get<ChatMessage[]>(`${environment.apiBaseUrl}/api/chat`)
    .subscribe({
      next: (data) => {
        this.messages = data.filter((m) => m.item.id === this.itemId && m.buyer.id === this.buyerId);
      },
      error: (err) => {
        console.error('Failed to load messages', err);
        // Keep old messages visible even if the request fails
      }
    });
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;
    const body = { message: this.newMessage, itemId: this.itemId, buyerId: this.buyerId };
    this.http.post(`${environment.apiBaseUrl}/api/chat`, body)
    .subscribe({
      next: () => {
        this.newMessage = '';
        this.loadMessages();
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Failed to send message', err);
        alert('Could not send message. Please try again.');
      }
    });
  }

  isMine(msg: ChatMessage): boolean {
    return msg.sender.id === this.currentUserId;
  }

  goBack() {
    window.history.back();
  }
}
