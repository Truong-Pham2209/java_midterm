import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { BrandResponse } from '../../../models/brand';
import { FormsModule } from '@angular/forms';
import { BrandService } from '../../../services/fetch/brand.service';
import { backendUrl } from '../../../../environments/environment';
import { FileService } from '../../../services/file.service';

@Component({
  selector: 'app-brand',
  imports: [CommonModule, FontAwesomeModule, FormsModule],
  templateUrl: './brand.component.html',
  styleUrl: './brand.component.scss'
})
export class BrandComponent implements OnInit {
  brands: BrandResponse[] = [];

  brand = { id: 0, name: '', image: '' };
  editingBrand: boolean = false;

  selectedFile: File | null = null;

  constructor(
    private readonly brandService: BrandService,
    private readonly fileService: FileService
  ) { }

  ngOnInit(): void {
    this.brandService.getAll().subscribe({
      next: (brands) => {
        this.brands = brands;
      },
      error: (error) => {
        console.error('Error fetching brands:', error);
      }
    });
  }

  onSubmit() {
    if (!this.validateForm()) return;
    const formData = this.buildFormData();

    const callback = this.editingBrand
      ? this.brandService.update(this.brand.id, formData)
      : this.brandService.create(formData);

    callback.subscribe({
      next: (brand) => {
        if (this.editingBrand) {
          const index = this.brands.findIndex(b => b.id === brand.id);
          if (index !== -1) this.brands[index] = brand;
        } else {
          this.brands.push(brand);
        }

        this.resetForm();
      },
      error: (error) => {
        console.error('Lỗi xử lý thương hiệu:', error);
        alert(error.message || 'Có lỗi xảy ra!');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.brand.name.trim()) {
      alert('Hãy nhập tên thương hiệu!');
      return false;
    }

    if (!this.editingBrand && !this.selectedFile) {
      alert('Hãy chọn ảnh cho thương hiệu!');
      return false;
    }

    return true;
  }

  private buildFormData(): FormData {
    const formData = new FormData();
    formData.append('brand', new Blob([JSON.stringify(this.brand)], { type: 'application/json' }));
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }
    return formData;
  }


  getImageUrl(image: string): string {
    if (image.startsWith('http')) {
      return image;
    }

    return `${backendUrl}/api/files/?id=${image}`;
  }

  async editBrand(brand: BrandResponse) {
    this.brand = { ...brand };
    this.editingBrand = true;

    if (!brand.image) {
      this.selectedFile = null;
      return;
    }

    const file = await this.fileService.getFile(brand.image);
    this.selectedFile = file;
    console.log("Selected file:", this.selectedFile);
  }


  deleteBrand(brand: BrandResponse) {
    if (!confirm(`Bạn có chắc chắn muốn xóa thương hiệu ${brand.name}?`)) {
      return;
    }

    this.brandService.delete(brand.id).subscribe({
      next: () => {
        this.brands = this.brands.filter(b => b.id !== brand.id);
        this.resetForm();
      },
      error: (error) => {
        console.error('Error deleting brand:', error);
        alert(error.message || 'Có lỗi xảy ra khi xóa thương hiệu!');
      }
    });
  }

  resetForm() {
    this.brand = { id: 0, name: '', image: '' };
    this.editingBrand = false;
    this.selectedFile = null;
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }
}
