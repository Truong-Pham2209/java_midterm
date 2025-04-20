import { ProductFilter } from './../../models/product';
import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { Observable } from 'rxjs';
import { Paging } from '../../models/paging';
import { ProductResponse } from '../../models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private readonly baseService: BaseService) { }

  getAll(filter: ProductFilter): Observable<Paging<ProductResponse>> {
    return this.baseService.post<Paging<ProductResponse>>('/api/products/filter/', filter);
  }

  getById(id: number): Observable<ProductResponse> {
    return this.baseService.get<ProductResponse>(`/api/products/${id}/`);
  }

  create(formData: FormData): Observable<ProductResponse> {
    return this.baseService.post<ProductResponse>('/api/products/', formData, true);
  }

  update(id: number, formData: FormData): Observable<any> {
    return this.baseService.put<ProductResponse>(`/api/products/${id}/`, formData, true);
  }

  delete(id: number): Observable<any> {
    return this.baseService.delete<any>(`/api/products/${id}/`);
  }
}
