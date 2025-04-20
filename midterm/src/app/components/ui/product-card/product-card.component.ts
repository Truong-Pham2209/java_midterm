import { Component, Input, OnInit } from '@angular/core';
import { ProductResponse } from '../../../models/product';
import { backendUrl } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'product-card',
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent implements OnInit {
  @Input() product!: ProductResponse;

  constructor(
    private readonly router: Router,
  ) {
  }

  ngOnInit(): void {
  }

  onClick() {
    this.router.navigate(['/product/detail'], { queryParams: { id: this.product.id } });
  }

  getDiscountedPrice(): number {
    if (this.product.discount) {
      const discount = Math.round(this.product.price * this.product.discount / 100);
      return Math.round(this.product.price - discount);
    }
    return this.product.price;
  }

  getImageUrl(): string {
    if (this.product.image.startsWith('http')) {
      return this.product.image;
    }

    return `${backendUrl}/api/files/?id=${this.product.image}`;
  }
}
