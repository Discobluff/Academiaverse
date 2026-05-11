import { Component, inject } from '@angular/core';
import { LanguageService } from '../services/language.service';
import { HostListener } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-language-switcher',
  imports: [MatIconModule],
  templateUrl: './language-switcher.html',
  styleUrl: './language-switcher.css',
})


export class LanguageSwitcher {
  isOpen = false;

  langService = inject(LanguageService);
  currentLocale = this.langService.getCurrentLocale();
  

  switchLanguage(locale: string): void {
    this.isOpen = false;
    this.langService.switchTo(locale);
    this.currentLocale = this.langService.getCurrentLocale();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!(event.target as HTMLElement).closest('.dropdown')) {
      this.isOpen = false;
    }
  }

}
