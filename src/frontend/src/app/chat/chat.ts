import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../environments/environments';
import { AuthService } from '../core/services/auth.service';
import { DialogService } from '../core/services/dialog.service';

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
  private dialog = inject(DialogService);

  itemId: number | null = null;
  buyerId: number | null = null;
  messages: ChatMessage[] = [];
  newMessage = '';
  currentUserId: number | null = null;
  private polling: ReturnType<typeof setInterval> | null = null;

  itemTitle = $localize`:@@msg_loading:Loading...`;
  sellerName = '';
  buyerName = '';
  courseName = $localize`:@@msg_not_assigned:Not assigned`;
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
        this.sellerName = item.owner ? `${item.owner.firstname} ${item.owner.lastname}` : $localize`:@@msg_unknown_seller:Unknown seller`;
        this.courseName = item.course?.name || $localize`:@@msg_not_assigned:Not assigned`;
      },
      error: () => {
        this.itemTitle = $localize`:@@msg_unknown_item:Unknown item`;
        this.sellerName = $localize`:@@msg_unknown_seller:Unknown seller`;
      }
    });
  }

  loadBuyerName() {
    this.http.get<UserDetail>(`${environment.apiBaseUrl}/v1/users/${this.buyerId}`)
    .subscribe({
      next: (user) => this.buyerName = `${user.firstname} ${user.lastname}`,
      error: () => this.buyerName = $localize`:@@msg_unknown_buyer:Unknown buyer`
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
          this.dialog.alert($localize`:@@msg_both_confirmed_complete:Both parties have confirmed the transaction. It is complete!`, 'success');
        }
      },
      error: (err) => {
        console.error(this.currentUserId, this.buyerId, this.itemId);
        console.error('Failed to load sale confirmation popup state', err);
      }
    });
  }

  async triggerPopup() {
    const confirmed = await this.dialog.confirm(
      $localize`:@@msg_other_party_confirmed:The other party has confirmed the transaction. Would you also like to confirm the transaction?`,
      $localize`:@@msg_confirm_transaction_title:Confirm transaction`
    );
    if (confirmed) {
      this.confirmTransaction();
    }
  }

  scrollToBottom() {
    setTimeout(() => {
      const panel = document.querySelector('.chat__messages');
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
        this.dialog.alert($localize`:@@msg_send_failed:Could not send message. Please try again.`, 'error');
      }
    });
  }

  async confirmTransaction() {
    const buyerText = $localize`:@@msg_buyer_confirm_text:Did you receive the item and pay the agreed upon amount? And do you agree to the terms agreed upon in the chat & item description?`;
    const sellerText = $localize`:@@msg_seller_confirm_text:Did you receive the payment and give the item to the buyer? And do you agree to the terms agreed upon in the chat & item description?`;
    const undoneNote = $localize`:@@msg_action_cannot_be_undone:This action cannot be undone.`;
    const message = (this.currentUserId === this.buyerId ? buyerText : sellerText)
      + `<br/><br/><b>${undoneNote}</b>`;

    const path = this.currentUserId === this.buyerId
      ? `${environment.apiBaseUrl}/api/sale-confirmation/${this.itemId}/buyer-confirmed`
      : `${environment.apiBaseUrl}/api/sale-confirmation/${this.itemId}/owner-confirmed`;

    const confirmed = await this.dialog.confirmHtml(message, $localize`:@@msg_confirm_transaction_title:Confirm transaction`);
    if (!confirmed) {
      this.dialog.alert($localize`:@@msg_transaction_not_confirmed:Transaction not confirmed.`, 'info');
      return;
    }

    this.http.put(path, {}).subscribe({
      next: () => {
        this.dialog.alert($localize`:@@msg_transaction_confirmed:Transaction confirmed. Thank you for using Academiaverse!`, 'success');
      },
      error: () => this.dialog.alert($localize`:@@msg_transaction_confirm_failed:Failed to confirm transaction. Please try again.`, 'error')
    });
  }

  isMine(msg: ChatMessage): boolean {
    return msg.sender.id === this.currentUserId;
  }

  goBack() {
    window.history.back();
  }
}
