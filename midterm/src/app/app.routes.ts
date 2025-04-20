import { Routes } from '@angular/router';
import * as Layout from './layouts';
import * as WebPage from './pages/web';
import * as AdminPage from './pages/admin';
import * as FormPage from './pages/form';
import { webGuard } from './guards/web.guard';
import { adminGuard } from './guards/admin.guard';
import { loginGuard } from './guards/login.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },

    {
        path: 'admin',
        component: Layout.Admin,
        canActivate: [adminGuard],
        children: [
            { path: 'brand', component: AdminPage.Brand, title: "Manage Brand" },
            { path: 'category', component: AdminPage.Category, title: "Manage Category" },
            { path: 'product', component: AdminPage.Product, title: "Manage Product" },
            { path: 'product/edit', component: AdminPage.EditProduct, title: "Edit Product" },
        ]
    },

    {
        path: '',
        component: Layout.Web,
        children: [
            { path: 'home', component: WebPage.Home, title: "Home" },
            { path: 'cart', component: WebPage.Cart, canActivate: [webGuard], title: "Cart" },
            { path: 'order-history', component: WebPage.OrderHistory, canActivate: [webGuard], title: "Cart" },
            { path: 'product', component: WebPage.Product, title: "Cart" },
            { path: 'product/detail', component: WebPage.ProductDetail, title: "Cart" },
            { path: '', redirectTo: 'home', pathMatch: 'full' }
        ]
    },

    {
        path: '',
        children: [
            { path: 'login', component: FormPage.Login, canActivate: [loginGuard], title: "Login" },
            { path: 'logout', component: FormPage.Logout },
            { path: 'error', component: WebPage.Error, title: "Error" }
        ]
    },

    {
        path: '**', redirectTo: 'error?status=404', pathMatch: 'full'
    }
];
