import { backendUrl } from './../../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TokenStorageService } from '../security/token-storage.service';
import { catchError, Observable, switchMap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BaseService {
  constructor(
    private readonly http: HttpClient,
    private readonly tokenService: TokenStorageService
  ) { }

  get<T>(url: string, params?: Record<string, any>, addAuthorizationHeaders = true): Observable<T> {
    return this.requestWithRetry(() =>
      this.http.get<T>(this.getUrl(url), {
        headers: this.getHeaders(false, addAuthorizationHeaders),
        params: this.buildParams(params),
      })
    );
  }

  post<T>(url: string, body: any, isFormData = false, addAuthorizationHeaders = true): Observable<T> {
    return this.requestWithRetry(() =>
      this.http.post<T>(this.getUrl(url), body, {
        headers: this.getHeaders(isFormData, addAuthorizationHeaders),
      })
    );
  }

  put<T>(url: string, body: any, isFormData = false, addAuthorizationHeaders = true): Observable<T> {
    return this.requestWithRetry(() =>
      this.http.put<T>(this.getUrl(url), body, {
        headers: this.getHeaders(isFormData, addAuthorizationHeaders),
      })
    );
  }

  delete<T>(url: string, params?: Record<string, any>, addAuthorizationHeaders = true): Observable<T> {
    return this.requestWithRetry(() =>
      this.http.delete<T>(this.getUrl(url), {
        headers: this.getHeaders(false, addAuthorizationHeaders),
        params: this.buildParams(params),
      })
    );
  }

  private getHeaders(isFormData = false, addAuthorizationHeaders = true): HttpHeaders {
    let headers = new HttpHeaders();

    if (addAuthorizationHeaders) {
      const token = this.tokenService.getToken();
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    if (!isFormData) {
      headers = headers.set('Content-Type', 'application/json');
    }

    return headers;
  }

  private getUrl(url: string): string {
    if (url.startsWith('http')) return url;
    return url.startsWith('/') ? `${backendUrl}${url}` : `${backendUrl}/${url}`;
  }

  private buildParams(params?: Record<string, any>): HttpParams {
    let httpParams = new HttpParams();

    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (Array.isArray(value)) {
          value.forEach(v => {
            httpParams = httpParams.append(key, v);
          });
        } else if (value !== null && value !== undefined) {
          httpParams = httpParams.set(key, value);
        }
      });
    }

    return httpParams;
  }

  private requestWithRetry<T>(requestFn: () => Observable<T>): Observable<T> {
    return requestFn().pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return this.tokenService.refreshToken().pipe(
            switchMap(() => requestFn()),
            catchError(() => {
              this.tokenService.clearTokens();
              this.tokenService.redirectTo401();
              return throwError(() => ({
                status: 401,
                message: 'Refresh token thất bại',
                raw: error
              }));
            })
          );
        }

        const messages: Record<number, string> = {
          403: 'Forbidden',
          404: 'Not Found',
          500: 'Server Error',
        };

        console.warn(messages[error.status] || 'Unknown Error');

        return throwError(() => ({
          status: error.status,
          message: error.error?.message || 'Có lỗi xảy ra',
          raw: error
        }));
      })
    );
  }
}
