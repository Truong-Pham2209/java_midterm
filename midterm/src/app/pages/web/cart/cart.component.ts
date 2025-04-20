import { ConfirmedOrderRequest, OrderResponse } from './../../../models/order';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OrderService } from '../../../services/fetch/order.service';

@Component({
  selector: 'app-orders',
  imports: [CommonModule, FontAwesomeModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  address: string = '';
  orders: OrderResponse[] = [];

  constructor(
    private readonly orderService: OrderService
  ) { }

  ngOnInit(): void {
    this.orderService.getAllInCart().subscribe({
      next: (res) => {
        this.orders = res.map((item: any) => ({
          ...item,
          quantity: 1
        }));
      },
      error: (err) => {
        console.error('Error fetching orders items:', err);
      }
    });
  }

  increase(item: any): void {
    if (item.quantity < 99) item.quantity++;
  }

  decrease(item: any): void {
    if (item.quantity > 1) item.quantity--;
  }

  remove(item: any): void {
    this.orderService.removeFromCart(item.id).subscribe({
      next: () => {
        this.orders = this.orders.filter(i => i !== item);
      },
      error: (err) => {
        console.error('Xóa sản phẩm thất bại:', err);
      }
    });
  }

  getTotal(): number {
    return this.orders.reduce((total, item) => total + this.getDiscountedPrice(item) * item.quantity, 0);
  }

  getDiscountedPrice(item: OrderResponse): number {
    const rawPrice = item.price * (1 - item.discount / 100);
    return Math.round(rawPrice / 1000) * 1000;
  }


  checkout(): void {
    console.log('Thanh toán:', {
      orders: this.orders,
      address: this.address,
      total: this.getTotal(),
    });

    if (!this.canCheckout()) {
      alert('Vui lòng nhập địa chỉ giao hàng và chọn ít nhất một sản phẩm!');
      return;
    }

    const orderData: ConfirmedOrderRequest = {
      items: this.orders.map(item => ({
        id: item.id,
        quantity: item.quantity,
      })),
      address: this.address,
    };

    this.orderService.confirmOrder(orderData).subscribe({
      next: (res) => {
        console.log('Đặt hàng thành công:', res);
        alert('Đặt hàng thành công!');
        this.orders = [];
        this.address = '';
      },
      error: (err) => {
        console.error('Đặt hàng thất bại:', err);
        alert('Đặt hàng thất bại! Vui lòng thử lại sau.');
      }
    });
  }

  canCheckout(): boolean {
    return this.orders.length > 0 && this.address.trim() !== '';
  }
}
