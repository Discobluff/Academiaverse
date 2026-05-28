import { Injectable } from '@angular/core';

type AlertType = 'success' | 'error' | 'info' | 'warning';

const ICONS: Record<AlertType, string> = {
  success: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`,
  error:   `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>`,
  warning: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`,
  info:    `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`,
};

const COLORS: Record<AlertType, { icon: string; iconBg: string; btn: string; btnBorder: string; btnHover: string }> = {
  success: { icon: '#245F3B', iconBg: '#DDEFE3', btn: '#2F7D4F', btnBorder: '#2F7D4F', btnHover: '#245F3B' },
  error:   { icon: '#881b14', iconBg: '#FFF0EF', btn: '#B42318', btnBorder: '#B42318', btnHover: '#961e14' },
  warning: { icon: '#7a4f0e', iconBg: '#FEF9EC', btn: '#B7791F', btnBorder: '#B7791F', btnHover: '#9a6519' },
  info:    { icon: '#1a4caa', iconBg: '#EEF4FF', btn: '#2F7D4F', btnBorder: '#2F7D4F', btnHover: '#245F3B' },
};

function titleFor(type: AlertType): string {
  switch (type) {
    case 'success': return $localize`:@@dialog_title_success:Success`;
    case 'error':   return $localize`:@@dialog_title_error:Error`;
    case 'warning': return $localize`:@@dialog_title_warning:Warning`;
    case 'info':    return $localize`:@@dialog_title_info:Notice`;
  }
}

@Injectable({ providedIn: 'root' })
export class DialogService {

  alert(message: string, type: AlertType = 'info'): Promise<void> {
    return new Promise(resolve => {
      const { overlay, card } = this.createShell();
      const c = COLORS[type];

      card.innerHTML = `
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:16px;">
          <div style="width:36px;height:36px;border-radius:12px;background:${c.iconBg};
                      display:flex;align-items:center;justify-content:center;flex-shrink:0;color:${c.icon};">
            ${ICONS[type]}
          </div>
          <h2 style="font-size:16px;font-weight:600;margin:0;color:#172018;">${this.escape(titleFor(type))}</h2>
        </div>
        <p style="font-size:14px;color:#5D6B61;line-height:1.65;margin:0 0 24px;">${this.escape(message)}</p>
        <div style="display:flex;justify-content:flex-end;">
          <button id="dlg-ok" style="padding:8px 24px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid ${c.btnBorder};background:${c.btn};color:#fff;">${this.escape($localize`:@@dialog_btn_ok:OK`)}</button>
        </div>
      `;

      document.body.appendChild(overlay);
      const okBtn = card.querySelector<HTMLButtonElement>('#dlg-ok')!;
      okBtn.focus();
      okBtn.addEventListener('click', () => { document.body.removeChild(overlay); resolve(); });
      overlay.addEventListener('click', (e) => { if (e.target === overlay) { document.body.removeChild(overlay); resolve(); } });
    });
  }

  confirm(message: string, title?: string): Promise<boolean> {
    return new Promise(resolve => {
      const { overlay, card } = this.createShell();
      const c = COLORS['info'];
      const heading = title ?? $localize`:@@dialog_title_confirm:Confirm`;

      card.innerHTML = `
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:16px;">
          <div style="width:36px;height:36px;border-radius:12px;background:#DDEFE3;
                      display:flex;align-items:center;justify-content:center;flex-shrink:0;color:#245F3B;">
            &#x1f6e1;&#xfe0f;
          </div>
          <h2 style="font-size:16px;font-weight:600;margin:0;color:#172018;">${this.escape(heading)}</h2>
        </div>
        <p style="font-size:14px;color:#5D6B61;line-height:1.65;margin:0 0 24px;">${this.escape(message)}</p>
        <div style="display:flex;gap:8px;justify-content:flex-end;">
          <button id="dlg-no" style="padding:8px 20px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid #D9E2D6;background:#fff;color:#172018;">${this.escape($localize`:@@dialog_btn_no:No`)}</button>
          <button id="dlg-yes" style="padding:8px 20px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid ${c.btn};background:${c.btn};color:#fff;">${this.escape($localize`:@@dialog_btn_yes:Yes`)}</button>
        </div>
      `;

      document.body.appendChild(overlay);
      const close = (result: boolean) => { document.body.removeChild(overlay); resolve(result); };
      card.querySelector<HTMLButtonElement>('#dlg-yes')!.addEventListener('click', () => close(true));
      card.querySelector<HTMLButtonElement>('#dlg-no')!.addEventListener('click', () => close(false));
      overlay.addEventListener('click', (e) => { if (e.target === overlay) close(false); });
    });
  }

  prompt(message: string, title?: string): Promise<string | null> {
    return new Promise(resolve => {
      const { overlay, card } = this.createShell();
      const heading = title ?? $localize`:@@dialog_title_input_required:Input required`;
      const placeholder = $localize`:@@dialog_input_placeholder:Type here...`;

      card.innerHTML = `
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:16px;">
          <div style="width:36px;height:36px;border-radius:12px;background:#EEF4FF;
                      display:flex;align-items:center;justify-content:center;flex-shrink:0;color:#1a4caa;">
            ${ICONS['info']}
          </div>
          <h2 style="font-size:16px;font-weight:600;margin:0;color:#172018;">${this.escape(heading)}</h2>
        </div>
        <p style="font-size:14px;color:#5D6B61;line-height:1.65;margin:0 0 12px;">${this.escape(message)}</p>
        <textarea id="dlg-input" rows="3"
          style="width:100%;padding:8px 12px;font-size:14px;font-family:inherit;color:#172018;
                 background:#fff;border:1px solid #D9E2D6;border-radius:12px;resize:vertical;
                 margin-bottom:16px;box-sizing:border-box;min-height:44px;"
          placeholder="${this.escape(placeholder)}"></textarea>
        <div style="display:flex;gap:8px;justify-content:flex-end;">
          <button id="dlg-cancel" style="padding:8px 20px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid #D9E2D6;background:#fff;color:#172018;">${this.escape($localize`:@@dialog_btn_cancel:Cancel`)}</button>
          <button id="dlg-submit" style="padding:8px 20px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid #2F7D4F;background:#2F7D4F;color:#fff;">${this.escape($localize`:@@dialog_btn_submit:Submit`)}</button>
        </div>
      `;

      document.body.appendChild(overlay);
      const input = card.querySelector<HTMLTextAreaElement>('#dlg-input')!;
      input.focus();
      const close = (val: string | null) => { document.body.removeChild(overlay); resolve(val); };
      card.querySelector<HTMLButtonElement>('#dlg-submit')!.addEventListener('click', () => {
        const v = input.value.trim();
        close(v || null);
      });
      card.querySelector<HTMLButtonElement>('#dlg-cancel')!.addEventListener('click', () => close(null));
      overlay.addEventListener('click', (e) => { if (e.target === overlay) close(null); });
    });
  }

  confirmHtml(html: string, title?: string): Promise<boolean> {
    return new Promise(resolve => {
      const { overlay, card } = this.createShell();
      const heading = title ?? $localize`:@@dialog_title_confirm:Confirm`;

      card.innerHTML = `
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:16px;">
          <div style="width:36px;height:36px;border-radius:12px;background:#DDEFE3;
                      display:flex;align-items:center;justify-content:center;flex-shrink:0;color:#245F3B;">
            &#x1f6e1;&#xfe0f;
          </div>
          <h2 style="font-size:16px;font-weight:600;margin:0;color:#172018;">${this.escape(heading)}</h2>
        </div>
        <div style="font-size:14px;color:#5D6B61;line-height:1.65;margin:0 0 24px;">${html}</div>
        <div style="display:flex;gap:8px;justify-content:flex-end;">
          <button id="dlg-no" style="padding:8px 20px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid #D9E2D6;background:#fff;color:#172018;">${this.escape($localize`:@@dialog_btn_no:No`)}</button>
          <button id="dlg-yes" style="padding:8px 20px;font-size:14px;font-weight:500;cursor:pointer;
                  border-radius:12px;border:1px solid #2F7D4F;background:#2F7D4F;color:#fff;">${this.escape($localize`:@@dialog_btn_yes:Yes`)}</button>
        </div>
      `;

      document.body.appendChild(overlay);
      const close = (result: boolean) => { document.body.removeChild(overlay); resolve(result); };
      card.querySelector<HTMLButtonElement>('#dlg-yes')!.addEventListener('click', () => close(true));
      card.querySelector<HTMLButtonElement>('#dlg-no')!.addEventListener('click', () => close(false));
      overlay.addEventListener('click', (e) => { if (e.target === overlay) close(false); });
    });
  }

  private createShell(): { overlay: HTMLDivElement; card: HTMLDivElement } {
    const overlay = document.createElement('div');
    overlay.style.cssText = `
      position:fixed;inset:0;background:rgba(23,32,24,0.4);
      display:flex;align-items:center;justify-content:center;z-index:1000;
    `;
    const card = document.createElement('div');
    card.style.cssText = `
      background:#fff;border-radius:16px;border:1px solid #D9E2D6;
      padding:24px;max-width:400px;width:90%;box-shadow:0 2px 8px rgba(23,32,24,0.08);
    `;
    overlay.appendChild(card);
    return { overlay, card };
  }

  private escape(s: string): string {
    const d = document.createElement('div');
    d.textContent = s;
    return d.innerHTML;
  }
}
