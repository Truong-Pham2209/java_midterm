import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMoneyBillWave, faTruck, faUndoAlt } from '@fortawesome/free-solid-svg-icons';
import { ProductService } from '../../../services/fetch/product.service';
import { ProductResponse } from '../../../models/product';
import { backendUrl } from '../../../../environments/environment';
import { AuthService } from '../../../services/security/auth.service';
import { OrderService } from '../../../services/fetch/order.service';

@Component({
  selector: 'app-product-detail',
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {
  faMoney = faMoneyBillWave;
  faReturn = faUndoAlt;
  faShipping = faTruck;

  product!: ProductResponse;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly productService: ProductService,
    private readonly authService: AuthService,
    private readonly orderService: OrderService,
  ) { }

  ngOnInit(): void {
    const idStr = this.route.snapshot.queryParamMap.get('id');
    const id = idStr ? parseInt(idStr) : null;
    if (!id) {
      alert('Không tìm thấy sản phẩm!');
      this.router.navigate(['/product']);
      return;
    }

    this.productService.getById(id).subscribe({
      next: (res) => {
        this.product = res;
      },
      error: () => {
        alert('Không tìm thấy sản phẩm!');
        this.router.navigate(['/product']);
      }
    });
  }

  get discountedPrice(): number {
    return this.product.price * (100 - this.product.discount) / 100;
  }

  get image(): string {
    if (this.product.image.startsWith('http')) {
      return this.product.image;
    }

    return `${backendUrl}/api/files/?id=${this.product.image}`;
  }

  addToCart() {
    if (!this.authService.isAuthenticated()) {
      alert('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!');
      this.router.navigate(['/login']);
      return;
    }

    this.orderService.addToCart(this.product.id).subscribe({
      next: () => {
        alert('Thêm sản phẩm vào giỏ hàng thành công!');
      },
      error: (err) => {
        alert(err.message || 'Có lỗi xảy ra trong quá trình thêm sản phẩm vào giỏ hàng!');
      }
    });
  }
}
