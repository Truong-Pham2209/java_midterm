import { BrandService } from './../../../services/fetch/brand.service';
import { Component, OnInit } from '@angular/core';
import { BrandResponse } from '../../../models/brand';
import { CommonModule } from '@angular/common';
import { BrandCardComponent } from '../../../components/ui/brand-card/brand-card.component';
import { ProductCardComponent } from '../../../components/ui/product-card/product-card.component';
import { CategoryCardComponent } from '../../../components/ui/category-card/category-card.component';
import { CategoryResponse } from '../../../models/category';
import { ProductFilter, ProductResponse, ProductSortOptions } from '../../../models/product';
import { CategoryService } from '../../../services/fetch/category.service';
import { ProductService } from '../../../services/fetch/product.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, BrandCardComponent, ProductCardComponent, CategoryCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  brands: BrandResponse[] = [];
  categories: CategoryResponse[] = [];
  products: ProductResponse[] = [];

  filter: ProductFilter = {
    page: 0,
    sort: ProductSortOptions[0].value,
    keyword: '',
    categoryId: null,
    brandId: null
  }

  constructor(
    private readonly brandService: BrandService,
    private readonly categoryService: CategoryService,
    private readonly productService: ProductService
  ) { }

  ngOnInit(): void {
    this.brandService.getAll().subscribe((response) => {
      this.brands = response;
    });

    this.categoryService.getAll().subscribe((response) => {
      this.categories = response;
    });

    this.productService.getAll(this.filter).subscribe((response) => {
      this.products = response.contents;
    });
  }
}
