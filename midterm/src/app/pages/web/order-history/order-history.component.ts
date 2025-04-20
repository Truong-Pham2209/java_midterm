import { Component, OnInit } from '@angular/core';
import { OrderResponse } from '../../../models/order';
import { OrderService } from '../../../services/fetch/order.service';
import { CommonModule } from '@angular/common';
import { OrderItemComponent } from '../../../components/ui/order-item/order-item.component';

@Component({
  selector: 'app-order-history',
  imports: [CommonModule, OrderItemComponent],
  templateUrl: './order-history.component.html',
  styleUrl: './order-history.component.scss'
})
export class OrderHistoryComponent implements OnInit {
  orders: OrderResponse[] = [];

  page: number = 0;
  totalPages: number = 0;
  loading = false;

  constructor(
    private readonly orderService: OrderService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.orderService.getHistory(this.page).subscribe({
      next: (response) => {
        setTimeout(() => {
          this.loading = false;
          this.orders = response.contents;
          this.totalPages = response.totalPages;
          this.page = response.currentPage;
        }, 1000);
      },
      error: (error) => {
        console.error('Error fetching order history:', error);
        this.loading = false;
      }
    });
  }

  loadMore() {
    if (this.loading || this.page >= this.totalPages - 1) return;

    this.loading = true;
    this.page++;
    this.orderService.getHistory(this.page).subscribe({
      next: (response) => {
        setTimeout(() => {
          this.loading = false;
          this.orders = [...this.orders, ...response.contents];
          this.totalPages = response.totalPages;
          this.page = response.currentPage;
        }, 1000);
      },
      error: (error) => {
        console.error('Error fetching more orders:', error);
        this.loading = false;
      }
    });
  }
}
