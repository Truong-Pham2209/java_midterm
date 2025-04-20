import { Component, Input } from '@angular/core';
import { OrderResponse } from '../../../models/order';
import { CommonModule } from '@angular/common';
import { faMapMarkerAlt } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { backendUrl } from '../../../../environments/environment';

@Component({
  selector: 'app-order-item',
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './order-item.component.html',
  styleUrl: './order-item.component.scss'
})
export class OrderItemComponent {
  @Input() order!: OrderResponse;
  faAddress = faMapMarkerAlt;

  get image(): string {
    if (this.order.image.startsWith('http')) {
      return this.order.image;
    }

    return `${backendUrl}/api/files/?id=${this.order.image}`;
  }

  getTotalPrice(order: OrderResponse): number {
    return order.price * order.quantity;
  }

}
