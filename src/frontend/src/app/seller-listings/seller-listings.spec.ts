import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellerListings } from './seller-listings';

describe('SellerListings', () => {
  let component: SellerListings;
  let fixture: ComponentFixture<SellerListings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerListings],
    }).compileComponents();

    fixture = TestBed.createComponent(SellerListings);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
