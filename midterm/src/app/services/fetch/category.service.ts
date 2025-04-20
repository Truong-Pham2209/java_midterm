import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { Observable } from 'rxjs';
import { CategoryResponse } from '../../models/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  constructor(private readonly baseService: BaseService) { }

  getAll(): Observable<CategoryResponse[]> {
    return this.baseService.get<CategoryResponse[]>('/api/categories/');
  }

  create(formData: FormData): Observable<CategoryResponse> {
    return this.baseService.post<CategoryResponse>('/api/categories/', formData, true);
  }

  update(id: number, formData: FormData): Observable<CategoryResponse> {
    return this.baseService.put<CategoryResponse>(`/api/categories/${id}/`, formData, true);
  }

  delete(id: number): Observable<any> {
    return this.baseService.delete<any>(`/api/categories/${id}/`);
  }
}
