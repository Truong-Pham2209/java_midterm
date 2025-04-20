import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { BrandResponse } from '../../../models/brand';
import { CategoryResponse } from '../../../models/category';
import { BrandService } from '../../../services/fetch/brand.service';
import { CategoryService } from '../../../services/fetch/category.service';
import { ProductService } from '../../../services/fetch/product.service';
import { FileService } from '../../../services/file.service';

@Component({
  selector: 'app-edit-product',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, FontAwesomeModule],
  templateUrl: './edit-product.component.html',
  styleUrl: './edit-product.component.scss'
})
export class EditProductComponent implements OnInit {
  form!: FormGroup;
  isUpdate = false;
  productId?: number;

  file: File | null = null;
  categories: CategoryResponse[] = [];
  brands: BrandResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private fileService: FileService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, this.nonEmptyStringValidator()]],
      description: ['', [Validators.required, this.nonEmptyStringValidator()]],
      price: [1000, [Validators.required, Validators.min(1000), this.nonEmptyStringValidator()]],
      discount: [0, [Validators.min(0), Validators.max(99), this.nonEmptyStringValidator()]],
      stock: [0, [Validators.min(0), this.nonEmptyStringValidator()]],
      categoryId: [null, Validators.required],
      brandId: [null, Validators.required],
    });

    this.categoryService.getAll().subscribe((data) => {
      this.categories = data;
      this.form.patchValue({
        categoryId: data[0].id,
      });
    });

    this.brandService.getAll().subscribe((data) => {
      this.brands = data;
      this.form.patchValue({
        brandId: data[0].id,
      });
    });

    const idStr = this.route.snapshot.queryParamMap.get('id');
    if (idStr && !isNaN(Number(idStr))) {
      this.productId = Number(idStr);
      this.isUpdate = true;

      this.productService.getById(this.productId).subscribe({
        next: (data) => {
          this.form.patchValue({
            name: data.name,
            description: data.description,
            price: data.price,
            discount: data.discount,
            stock: data.stock,
            categoryId: data.category.id,
            brandId: data.brand.id,
          });
          this.fileService.getFile(data.image).then((file) => {
            this.file = file;
          });
        },
        error: (err) => {
          alert(err.message || 'Lỗi khi lấy thông tin sản phẩm');
          this.router.navigate(['/admin/product']);
        },
      });
    }
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.file = file;
    }
  }

  save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (!this.file) {
      alert('Vui lòng chọn ảnh sản phẩm');
      return;
    }

    const formData = new FormData();
    const value = this.form.value;
    formData.append('product', new Blob([JSON.stringify(value)], { type: 'application/json' }));
    formData.append('file', this.file);

    if (this.isUpdate) {
      this.productService.update(this.productId!, formData).subscribe({
        next: () => {
          alert('Cập nhật sản phẩm thành công!');
          this.router.navigate(['/admin/product']);
        },
        error: (err) => {
          alert(err.message || 'Lỗi khi cập nhật sản phẩm');
        },
      });
    }
    else {
      this.productService.create(formData).subscribe({
        next: () => {
          alert('Thêm sản phẩm thành công!');
          this.router.navigate(['/admin/product']);
        },
        error: (err) => {
          alert(err.message || 'Lỗi khi thêm sản phẩm');
        },
      });
    }
  }

  get f() {
    return this.form.controls;
  }

  private nonEmptyStringValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (typeof value === 'string' && value.trim() === '') {
        return { nonEmpty: true };
      }
      return null;
    };
  }
}