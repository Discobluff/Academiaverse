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
    }
];
