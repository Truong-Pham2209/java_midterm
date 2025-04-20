import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faHome, faShoppingBasket, faSignOut, faTag, faTrademark, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'admin-header',
  imports: [CommonModule, RouterModule, FontAwesomeModule],
  templateUrl: './header.component.html',
  styleUrl: '../../../styles/header.scss',
})
export class HeaderComponent implements OnInit {
  currentUrl: string = '';
  isCustomer: boolean = false;
  headerItems: HeaderItem[] = [
    { text: 'Trang chủ', redirectTo: '/home', icon: faHome },
    { text: 'Nhãn hàng', redirectTo: '/admin/brand', icon: faTrademark },
    { text: 'Danh mục', redirectTo: '/admin/category', icon: faTag },
    { text: 'Sản phẩm', redirectTo: '/admin/product', icon: faShoppingBasket },
    { text: 'Đăng xuất', redirectTo: '/logout', icon: faSignOut },
  ];


  constructor(
    private readonly router: Router
  ) {
  }

  ngOnInit(): void {
    this.currentUrl = this.router.url;
    console.log(this.currentUrl);
  }
}

interface HeaderItem {
  text: string;
  redirectTo: string;
  icon: IconDefinition;
}