import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  getCurrentLocale(): string {
    const path = window.location.href;
    // if (path.startsWith('/ar')) return 'ar';
    if (path.includes(':4201')) return 'ar';
    return 'en-US';
  }

//   switchTo(locale: string): void {
//     const current = this.getCurrentLocale();
//     if (current === locale) return;

//     const path = window.location.pathname;
//     let newPath: string;

//     if (locale === 'ar') {
//       newPath = path.replace(/^\/(en-US)?/, '/ar').replace(':4200', ':4201');
//     } else {
//       newPath = path.replace(/^\/ar/, '/en-US').replace(':4201', ':4200');
//     }

//     window.location.href = newPath || '/';
//   }

    switchTo(locale: string): void {
    if (locale === 'ar') {
        window.location.href = window.location.href.replace(':4200', ':4201');
    } else {
        window.location.href = window.location.href.replace(':4201', ':4200');
    }
    }
}