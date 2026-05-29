import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ItemDetailComponent } from './item-detail';
import { HttpClient } from '@angular/common/http';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { AuthService } from '../core/services/auth.service';

interface InterestItem {
  id: number;
  item: { id: number };
}

describe('ItemDetailComponent', () => {
  let component: ItemDetailComponent;
  let fixture: ComponentFixture<ItemDetailComponent>;

  const mockItem = {
    id: 1,
    title: 'Test Book',
    description: 'A great book',
    notes: 'Some notes',
    price: 15,
    type: 'sale',
    paymentMethod: 'cash',
    isDigital: false,
    digitalFileName: null,
    digitalFilePath: null,
    images: ['img1.jpg'],
    status: 'Available',
    owner: { id: 99 }
  };

  const mockEmptyInterests: InterestItem[] = [];

  beforeEach(async () => {
    const authServiceMock = {
      getUserId: vi.fn().mockReturnValue(100), // not owner by default
    };

    await TestBed.configureTestingModule({
      imports: [ItemDetailComponent],
      providers: [
        {
          provide: HttpClient,
          useValue: { get: vi.fn(), post: vi.fn() }
        },
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { params: of({ id: 1 }) }
        },
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ItemDetailComponent);
    component = fixture.componentInstance;

    // Spy on http.get to return different values per URL
    const http = TestBed.inject(HttpClient);
    vi.spyOn(http, 'get').mockImplementation((url: string) => {
      if (url.includes('/api/interests')) {
        return of(mockEmptyInterests);
      }
      if (url.includes('/api/items/bought')) {
        return of(false);   // not bought, so "Available after purchase" is shown
      }
      if (url.includes('/api/ratings')) {
        return of(0.0);
      }
      return of(mockItem);
    });

    fixture.detectChanges();
  });

  it('should display item details', () => {
    const el = fixture.nativeElement;
    expect(el.querySelector('.detail__title').textContent).toContain('Test Book');
    expect(el.querySelector('.detail__card').textContent).toContain('A great book');
    expect(el.querySelector('.detail__card').textContent).toContain('Some notes');
    expect(el.querySelector('.detail__price-tag').textContent).toContain('15');
    expect(el.querySelector('.detail__meta-grid').textContent).toContain('sale');
    expect(el.querySelector('.detail__meta-grid').textContent).toContain('cash');
    expect(el.querySelector('.detail__meta-grid').textContent).toContain('Available');
  });

  it('should not show digital file info for non-digital item', () => {
    const el = fixture.nativeElement;
    expect(el.querySelector('.detail__digital')).toBeNull();
  });

  it('should show digital file name and no download link for digital item when not bought', async () => {
    const digitalItem = { ...mockItem, isDigital: true, digitalFileName: 'notes.pdf' };
    const http = TestBed.inject(HttpClient);
    vi.spyOn(http, 'get').mockImplementation((url: string) => {
      if (url.includes('/api/interests')) {
        return of(mockEmptyInterests);
      }
      if (url.includes('/api/items/bought')) {
        return of(false);   // not bought
      }
      if (url.includes('/api/ratings')) {
        return of(0.0);
      }
      return of(digitalItem);
    });
    component.loadItem(1);
    fixture.detectChanges();

    const el = fixture.nativeElement;
    expect(el.querySelector('.detail__digital').textContent).toContain('notes.pdf');
    expect(el.querySelector('.detail__digital').textContent).toContain('Available after purchase');
    // No download button because not bought
    expect(el.querySelector('.detail__btn--info')).toBeNull();
  });

  it('should show Interested button when user is not owner', () => {
    const el = fixture.nativeElement;
    const btn = el.querySelector('.detail__actions .detail__btn--secondary');
    expect(btn).toBeTruthy();
    expect(btn.textContent).toContain('Interested');
  });

  it('should not show Interested button when user is owner', () => {
    const authService = TestBed.inject(AuthService);
    (authService.getUserId as ReturnType<typeof vi.fn>).mockReturnValue(99);
    component.loadItem(1);
    fixture.detectChanges();

    const el = fixture.nativeElement;
    const buttons = (el as HTMLElement).querySelectorAll('.detail__actions .detail__btn--secondary');
    let found = false;
    buttons.forEach((b: Element) => { if (b.textContent?.includes('Interested')) found = true; });
    expect(found).toBe(false);
  });
});
