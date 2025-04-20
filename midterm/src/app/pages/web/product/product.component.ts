import { CategoryResponse } from './../../../models/category';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faSearch, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { ProductCardComponent } from '../../../components/ui/product-card/product-card.component';
import { ProductFilter, ProductResponse, ProductSortOptions } from '../../../models/product';
import { CategoryService } from '../../../services/fetch/category.service';
import { BrandService } from '../../../services/fetch/brand.service';
import { ProductService } from '../../../services/fetch/product.service';
import { BrandResponse } from '../../../models/brand';

@Component({
  selector: 'app-product',
  imports: [CommonModule, FormsModule, FontAwesomeModule, ProductCardComponent],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent implements OnInit {
  iconSearch = faSearch;
  faSpinner = faSpinner;

  brands: BrandResponse[] = [];
  categories: CategoryResponse[] = [];
  sortList = ProductSortOptions;

  products: ProductResponse[] = [];
  loadingMore: boolean = false;
  totalPages: number = 0;

  filter: ProductFilter = {
    brandId: null,
    categoryId: null,
    keyword: '',
    sort: this.sortList[0].value,
    page: 0
  };

  constructor(
    private productService: ProductService,
    private brandService: BrandService,
    private categoryService: CategoryService
  ) {

  }

  ngOnInit(): void {
    this.brandService.getAll().subscribe((res) => {
      this.brands = res;

    });

    this.categoryService.getAll().subscribe((res) => {
      this.categories = res;
    });

    this.searchProducts();
  }

  searchProducts() {
    this.loadingMore = true;
    this.products = [];
    this.filter.page = 0;
    this.productService.getAll(this.filter).subscribe((res) => {
      setTimeout(() => {
        this.loadingMore = false;
        this.products = res.contents;
        this.filter.page = res.currentPage;
        this.totalPages = res.totalPages;
      }, 1000);
    });
  }

  loadMore() {
    if (this.loadingMore || this.totalPages - 1 <= this.filter.page) return;

    this.loadingMore = true;
    this.filter.page += 1;
    this.productService.getAll(this.filter).subscribe((res) => {
      setTimeout(() => {
        this.loadingMore = false;
        this.products = [...this.products, ...res.contents];
        this.filter.page = res.currentPage;
        this.totalPages = res.totalPages;
      }, 1000);
    });
  }
}
