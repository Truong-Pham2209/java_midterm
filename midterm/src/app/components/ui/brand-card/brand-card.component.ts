import { Component, Input, OnInit } from '@angular/core';
import { BrandResponse } from '../../../models/brand';
import { backendUrl } from '../../../../environments/environment';

@Component({
  selector: 'brand-card',
  imports: [],
  templateUrl: './brand-card.component.html',
  styleUrl: './brand-card.component.scss'
})
export class BrandCardComponent implements OnInit {
  @Input() brand!: BrandResponse;

  constructor(

  ) { }

  ngOnInit(): void {

  }

  getImageUrl(image: string): string {
    if (image.startsWith('http')) {
      return image;
    }

    return `${backendUrl}/api/files/?id=${image}`;
  }
}
