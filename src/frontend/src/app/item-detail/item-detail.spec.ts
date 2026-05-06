import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ItemDetailComponent } from './item-detail';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { vi, describe, it, expect, beforeEach } from 'vitest';

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
    images: ['img1.jpg']
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemDetailComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { params: of({ id: 1 }) }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ItemDetailComponent);
    component = fixture.componentInstance;
    // Simulate the HTTP call using Vitest spy
    vi.spyOn(component['http'], 'get').mockReturnValue(of(mockItem));
    fixture.detectChanges();
  });

  it('should display item details', () => {
    const el = fixture.nativeElement;
    expect(el.querySelector('.item-title').textContent).toContain('Test Book');
    expect(el.querySelector('.item-description').textContent).toContain('A great book');
    expect(el.querySelector('.item-notes').textContent).toContain('Some notes');
    expect(el.querySelector('.item-price').textContent).toContain('15');
    expect(el.querySelector('.item-type').textContent).toContain('sale');
    expect(el.querySelector('.item-payment').textContent).toContain('cash');
    expect(el.querySelector('.item-status').textContent).toContain('Available');
  });

  it('should not show digital file info for non-digital item', () => {
    const el = fixture.nativeElement;
    expect(el.querySelector('.digital-info')).toBeNull();
  });

  it('should show digital file name and no download link for digital item', async () => {
    const digitalItem = { ...mockItem, isDigital: true, digitalFileName: 'notes.pdf' };
    // Override the spy
    vi.spyOn(component['http'], 'get').mockReturnValue(of(digitalItem));
    component.loadItem();
    fixture.detectChanges();

    const el = fixture.nativeElement;
    expect(el.querySelector('.digital-info').textContent).toContain('notes.pdf');
    expect(el.querySelector('.digital-info').textContent).toContain('Available after purchase');
    expect(el.querySelector('a[download]')).toBeNull();
  });
});
