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
  bothConfirmed = false;

  ngOnInit() {
    this.currentUserId = this.authService.getUserId();
    this.route.params.subscribe(params => {
      this.itemId = +params['itemId'];
      this.buyerId = +params['buyerId'];
      this.loadItemDetails();
      this.loadBuyerName();
      this.loadMessages();
      this.polling = setInterval(() => this.loadMessages(), 3000);
      this.tryRequestConfirmation();
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

  tryRequestConfirmation(): void {
    if (!this.itemId) return;

    const path = `${environment.apiBaseUrl}/api/sale-confirmation/${this.itemId}/${this.currentUserId === this.buyerId ? 'buyer-popup' : 'owner-popup'}`;
    this.http.get<string>(path, { responseType: 'text' as 'json' })
    .subscribe({
      next: (status) => {
        if (status === 'PENDING_YOU') {
          this.triggerPopup();
        } else if (status === 'BOTH_CONFIRMED') {
          this.bothConfirmed = true;
          alert('Both parties have confirmed the transaction. It is complete!');
        }
        // 'NONE' → do nothing
      },
      error: (err) => {
        console.error(this.currentUserId, this.buyerId, this.itemId);
        console.error('Failed to load sale confirmation popup state', err);
      }
    });
  }

  triggerPopup() {
    const message = "The other party has confirmed the transaction. Would you also like to confirm the transaction?";
    this.showConfirmDialog(message, () => {
      this.confirmTransaction();
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

  confirmTransaction() {
    const buyerText = "Did you receive the item and pay the agreed upon amount? And do you agree to the terms agreed upon in the chat & item description?";
    const sellerText = "Did you receive the payment and give the item to the buyer? And do you agree to the terms agreed upon in the chat & item description?";
    const message = (this.currentUserId === this.buyerId ? buyerText : sellerText) + "<br/><br/><b> This action cannot be undone.</b>";

    const path = this.currentUserId === this.buyerId
      ? `${environment.apiBaseUrl}/api/sale-confirmation/${this.itemId}/buyer-confirmed`
      : `${environment.apiBaseUrl}/api/sale-confirmation/${this.itemId}/owner-confirmed`;

    this.showConfirmDialog(message, () => {
      this.http.put(path, {}).subscribe({
        next: () => {
          alert('Transaction confirmed. Thank you for using Academiaverse!');
        },
        error: () => alert('Failed to confirm transaction. Please try again.')
      });
    });
  }

  isMine(msg: ChatMessage): boolean {
    return msg.sender.id === this.currentUserId;
  }

  goBack() {
    window.history.back();
  }

  showConfirmDialog(message: string, onConfirm: () => void) {
    const overlay = document.createElement("div");
    overlay.style.cssText = `
      position: fixed; inset: 0; background: rgba(23,32,24,0.4);
      display: flex; align-items: center; justify-content: center; z-index: 1000;
    `;

    overlay.innerHTML = `
      <div style="background: #fff; border-radius: 16px; border: 1px solid #D9E2D6;
                  padding: 24px; max-width: 400px; width: 90%; box-shadow: 0 2px 8px rgba(23,32,24,0.08);">
        <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
          <div style="width: 36px; height: 36px; border-radius: 12px; background: #DDEFE3;
                      display: flex; align-items: center; justify-content: center; flex-shrink: 0;
                      color: #245F3B; font-size: 18px;">
            &#x1f6e1;&#xfe0f;
          </div>
          <h2 style="font-size: 16px; font-weight: 600; margin: 0; color: #172018;">Confirm transaction</h2>
        </div>
        <p style="font-size: 14px; color: #5D6B61; line-height: 1.65; margin: 0 0 24px;">${message}</p>
        <div style="display: flex; gap: 8px; justify-content: flex-end;">
          <button id="dialog-no" style="padding: 8px 20px; font-size: 14px; font-weight: 500; cursor: pointer;
                  border-radius: 12px; border: 1px solid #D9E2D6; background: #fff; color: #172018;">No</button>
          <button id="dialog-yes" style="padding: 8px 20px; font-size: 14px; font-weight: 500; cursor: pointer;
                  border-radius: 12px; border: 1px solid #2F7D4F; background: #2F7D4F; color: #fff;">Yes</button>
        </div>
      </div>
    `;

    document.body.appendChild(overlay);

    const yesButton = overlay.querySelector<HTMLButtonElement>("#dialog-yes");
    const noButton = overlay.querySelector<HTMLButtonElement>("#dialog-no");

    yesButton?.addEventListener("click", () => {
      document.body.removeChild(overlay);
      onConfirm();
    });

    noButton?.addEventListener("click", () => {
      document.body.removeChild(overlay);
      alert("Transaction not confirmed");
    });
  }
}