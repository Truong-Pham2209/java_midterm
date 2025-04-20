import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { Observable } from 'rxjs';
import { BrandResponse } from '../../models/brand';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  constructor(private readonly baseService: BaseService) { }

  getAll(): Observable<BrandResponse[]> {
    return this.baseService.get<BrandResponse[]>('/api/brands/');
  }

  create(formData: FormData): Observable<BrandResponse> {
    return this.baseService.post<BrandResponse>('/api/brands/', formData, true);
  }

  update(id: number, formData: FormData): Observable<BrandResponse> {
    return this.baseService.put<BrandResponse>(`/api/brands/${id}/`, formData, true);
  }

  delete(id: number): Observable<any> {
    return this.baseService.delete<any>(`/api/brands/${id}/`);
  }
}
