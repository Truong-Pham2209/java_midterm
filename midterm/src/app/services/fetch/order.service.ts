import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { Observable } from 'rxjs';
import { ConfirmedOrderRequest, OrderResponse } from '../../models/order';
import { Paging } from '../../models/paging';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private readonly baseService: BaseService) { }

  getAllInCart(): Observable<OrderResponse[]> {
    return this.baseService.get<OrderResponse[]>('/api/orders/cart');
  }

  getHistory(page: number): Observable<Paging<OrderResponse>> {
    return this.baseService.get<Paging<OrderResponse>>(`/api/orders/history`, { page });
  }

  addToCart(productId: number): Observable<any> {
    return this.baseService.post<OrderResponse>('/api/orders/cart', { productId });
  }

  confirmOrder(order: ConfirmedOrderRequest): Observable<any> {
    return this.baseService.post<OrderResponse>('/api/orders/', order);
  }

  removeFromCart(productId: number): Observable<any> {
    return this.baseService.delete<OrderResponse>(`/api/orders/${productId}/`);
  }
}
