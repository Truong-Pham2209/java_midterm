import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ProductFilter, ProductResponse, ProductSortOptions } from '../../../models/product';
import { faPencilSquare, faPlusCircle, faSearch, faSpinner, faTrash } from '@fortawesome/free-solid-svg-icons';
import { ProductService } from '../../../services/fetch/product.service';
import { BrandService } from '../../../services/fetch/brand.service';
import { CategoryService } from '../../../services/fetch/category.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BrandResponse } from '../../../models/brand';
import { CategoryResponse } from '../../../models/category';
import { backendUrl } from '../../../../environments/environment';

@Component({
  selector: 'app-product',
  imports: [CommonModule, FontAwesomeModule, FormsModule],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent implements OnInit {
  products: ProductResponse[] = [];
  brands: BrandResponse[] = [];
  categories: CategoryResponse[] = [];
  sortList = ProductSortOptions;

  loadingMore: boolean = false;
  totalPages: number = 0;

  faAdd = faPlusCircle;
  faLoading = faSpinner;
  faEdit = faPencilSquare;
  faDelete = faTrash;
  iconSearch = faSearch;

  filter: ProductFilter = {
    brandId: null,
    categoryId: null,
    keyword: '',
    sort: this.sortList[0].value,
    page: 0
  };

  constructor(
    private readonly brandService: BrandService,
    private readonly categoryService: CategoryService,
    private readonly productService: ProductService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.brandService.getAll().subscribe((res) => {
      this.brands = res;
    });

    this.categoryService.getAll().subscribe((res) => {
      this.categories = res;
    });

    this.onSearch();
  }

  onAddProduct() {
    this.router.navigate(['/admin/product/edit']);
  }

  onEditProduct(product: ProductResponse) {
    this.router.navigate(['/admin/product/edit'], { queryParams: { id: product.id } });
  }

  onDeleteProduct(product: ProductResponse) {
    if (!confirm(`Bạn có muốn xóa sản phẩm ${product.name}`)) return;

    this.productService.delete(product.id).subscribe(() => {
      alert('Xóa sản phẩm thành công');
      this.products = this.products.filter((p) => p.id !== product.id);
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

  onSearch() {
    this.loadingMore = false;
    this.products = [];
    this.filter.page = 0;
    this.productService.getAll(this.filter).subscribe((res) => {
      setTimeout(() => {
        this.loadingMore = false;
        this.products = res.contents;
        this.filter.page = res.currentPage;
        this.totalPages = res.totalPages;
        console.log(res);

      }, 1000);
    });
  }

  getImageUrl(image: string): string {
    if (!image) {
      return `/images/product.jpg`;
    }

    if (image.startsWith('http')) {
      return image;
    }

    return `${backendUrl}/api/files/?id=${image}`;
  }
}
