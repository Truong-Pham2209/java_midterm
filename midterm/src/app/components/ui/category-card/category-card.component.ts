import { Component, Input, OnInit } from '@angular/core';
import { CategoryResponse } from '../../../models/category';
import { backendUrl } from '../../../../environments/environment';

@Component({
  selector: 'category-card',
  imports: [],
  templateUrl: './category-card.component.html',
  styleUrl: './category-card.component.scss'
})
export class CategoryCardComponent implements OnInit {
  @Input() category!: CategoryResponse;

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