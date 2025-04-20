import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCartShopping, faClock, faGauge, faHome, faMobile, faMobileAlt, faSignIn, faSignOut, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../services/security/auth.service';

@Component({
  selector: 'web-header',
  imports: [CommonModule, RouterModule, FontAwesomeModule],
  templateUrl: './header.component.html',
  styleUrl: '../../../styles/header.scss',
})
export class HeaderComponent implements OnInit {
  currentUrl: string = '';
  isCustomer: boolean = false;
  headerItems: HeaderItem[] = [
    { text: 'Trang chủ', redirectTo: '/home', icon: faHome },
    { text: 'Sản phẩm', redirectTo: '/product', icon: faMobileAlt }
  ];


  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.currentUrl = this.router.url;
    console.log(this.currentUrl);

    if (!this.authService.isAuthenticated()) {
      this.headerItems.push({ text: 'Đăng nhập', redirectTo: '/login', icon: faSignIn });
      return;
    }

    if (this.authService.hasAnyRoles(['ADMIN'])) {
      this.headerItems.push({ text: 'Quản lý', redirectTo: '/admin/brand', icon: faGauge });
      this.headerItems.push({ text: 'Đăng xuất', redirectTo: '/logout', icon: faSignOut });
      return;
    }

    this.headerItems.push({ text: 'Giỏ hàng', redirectTo: '/cart', icon: faCartShopping });
    this.headerItems.push({ text: 'Lịch sử mua hàng', redirectTo: '/order-history', icon: faClock });
    this.headerItems.push({ text: 'Đăng xuất', redirectTo: '/logout', icon: faSignOut });
  }
}

interface HeaderItem {
  text: string;
  redirectTo: string;
  icon: IconDefinition;
}