import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CategoryResponse } from '../../../models/category';
import { FileService } from '../../../services/file.service';
import { CategoryService } from '../../../services/fetch/category.service';
import { backendUrl } from '../../../../environments/environment';

@Component({
  selector: 'app-category',
  imports: [CommonModule, FontAwesomeModule, FormsModule],
  templateUrl: './category.component.html',
  styleUrl: './category.component.scss'
})
export class CategoryComponent implements OnInit {
  categories: CategoryResponse[] = [];

  category = { id: 0, name: '', image: '' };
  editingCategory: boolean = false;

  selectedFile: File | null = null;

  constructor(
    private readonly categoryService: CategoryService,
    private readonly fileService: FileService
  ) { }

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error fetching categories:', error);
      }
    });
  }

  onSubmit() {
    if (!this.validateForm()) return;
    const formData = this.buildFormData();

    const callback = this.editingCategory
      ? this.categoryService.update(this.category.id, formData)
      : this.categoryService.create(formData);

    callback.subscribe({
      next: (category) => {
        if (this.editingCategory) {
          const index = this.categories.findIndex(b => b.id === category.id);
          if (index !== -1) this.categories[index] = category;
        } else {
          this.categories.push(category);
        }

        this.resetForm();
      },
      error: (error) => {
        console.error('Lỗi xử lý thương hiệu:', error);
        alert(error.message || 'Có lỗi xảy ra!');
      }
    });
  }

  async editCategory(category: CategoryResponse) {
    this.category = { ...category };
    this.editingCategory = true;

    if (!category.image) {
      this.selectedFile = null;
      return;
    }

    const file = await this.fileService.getFile(category.image);
    this.selectedFile = file;
    console.log("Selected file:", this.selectedFile);
  }

  deleteCategory(category: CategoryResponse) {
    if (!confirm(`Bạn có chắc chắn muốn xóa danh mục ${category.name}?`)) {
      return;
    }

    this.categoryService.delete(category.id).subscribe({
      next: () => {
        this.categories = this.categories.filter(c => c.id !== category.id);
        this.resetForm();
      },
      error: (error) => {
        console.error('Error deleting brand:', error);
        alert(error.message || 'Có lỗi xảy ra khi xóa danh mục!');
      }
    });
  }

  resetForm() {
    this.category = { id: 0, name: '', image: '' };
    this.editingCategory = false;
    this.selectedFile = null;
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  getImageUrl(image: string): string {
    if (image.startsWith('http')) {
      return image;
    }

    return `${backendUrl}/api/files/?id=${image}`;
  }

  async editBrand(category: CategoryResponse) {
    this.category = { ...category };
    this.editingCategory = true;

    if (!category.image) {
      this.selectedFile = null;
      return;
    }

    const file = await this.fileService.getFile(category.image);
    this.selectedFile = file;
    console.log("Selected file:", this.selectedFile);
  }

  private validateForm(): boolean {
    if (!this.category.name.trim()) {
      alert('Hãy nhập tên thương hiệu!');
      return false;
    }

    if (!this.editingCategory && !this.selectedFile) {
      alert('Hãy chọn ảnh cho thương hiệu!');
      return false;
    }

    return true;
  }

  private buildFormData(): FormData {
    const formData = new FormData();
    formData.append('category', new Blob([JSON.stringify(this.category)], { type: 'application/json' }));
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }
    return formData;
  }
}
