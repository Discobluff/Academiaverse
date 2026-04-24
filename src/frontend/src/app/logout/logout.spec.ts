import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Logout } from './logout';
import { AuthService } from '../core/services/auth.service';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('Logout', () => {
  let component: Logout;
  let fixture: ComponentFixture<Logout>;

  const mockAuthService = {
    isAuthenticated: () => false,
  };

beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Logout],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Logout);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
