import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'home' },
    {
        path: 'home',
        loadComponent: () => import('./home/home').then(m => m.Home)
    },
    {
        path: 'login',
        loadComponent: () => import('./login/login').then(m => m.Login)
    },
    {
        path: 'register',
        loadComponent: () => import('./register/register').then(m => m.Register)
    },
    {
        path: 'logout',
        loadComponent: () => import('./logout/logout').then(m => m.Logout),
        canActivate: [AuthGuard]
    },
    {
        path: 'search',
        loadComponent: () => import('./search/search').then(m => m.SearchComponent),
        canActivate: [AuthGuard]   // <-- Add this line
    },
    {
        path: 'add-item',
        loadComponent: () => import('./add-item/add-item').then(m => m.AddItem),
        canActivate: [AuthGuard]
    },
    {
        path: 'seller-listings',
        loadComponent: () => import('./seller-listings/seller-listings').then(m => m.SellerListings),
        canActivate: [AuthGuard]
    },
    {
        path: 'terms',
        loadComponent: () => import('./terms/terms').then(m => m.Terms)
    },
    {
      path: 'chat/:itemId/:buyerId',
      loadComponent: () => import('./chat/chat').then(m => m.ChatComponent),
      canActivate: [AuthGuard]
    },
    {
      path: 'item/:id',
      loadComponent: () => import('./item-detail/item-detail').then(m => m.ItemDetailComponent),
      canActivate: [AuthGuard]
    },
    {
      path: 'edit-item/:id',
      loadComponent: () => import('./edit-item/edit-item').then(m => m.EditItemComponent),
      canActivate: [AuthGuard]
    }
];
