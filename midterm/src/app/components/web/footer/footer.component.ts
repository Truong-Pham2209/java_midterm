import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { faCaretRight, faEnvelopeOpen, faMapMarker, faMapMarkerAlt, faPhoneSquare, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'web-footer',
  imports: [CommonModule, RouterModule, FontAwesomeModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {
  faCaretRight = faCaretRight;

  links: LinkItem[] = [
    { text: 'Trang chủ', redirectTo: '/home' },
    { text: 'Sản phẩm', redirectTo: '/product' }
  ];

  contacts: ContactItem[] = [
    { icon: faMapMarkerAlt, text: 'Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM' },
    { icon: faEnvelopeOpen, text: 'phamtruong04112004@gmail.com' },
    { icon: faPhoneSquare, text: '0123456789' }
  ];
}

interface LinkItem {
  text: string;
  redirectTo: string;
}

interface ContactItem {
  icon: IconDefinition;
  text: string;
}
