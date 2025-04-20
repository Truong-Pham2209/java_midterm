import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap, throwError } from 'rxjs';
import { backendUrl } from '../../../environments/environment';
import { JwtResponse } from '../../models/auth/token';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) { }

  getToken(): string {
    return localStorage.getItem('access_token') || '';
  }

  getRefreshToken(): string {
    return localStorage.getItem('refresh_token') || '';
  }

  setTokens(jwt: JwtResponse): void {
    localStorage.setItem('access_token', jwt.accessToken);
    localStorage.setItem('refresh_token', jwt.refreshToken);
  }

  clearTokens() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  }

  redirectTo401() {
    this.router.navigate(['/error'], { queryParams: { status: 401 } });
  }

  refreshToken(): Observable<JwtResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('Không tìm thấy refresh token'));
    }

    return this.http.post<JwtResponse>(`${backendUrl}/api/auth/refresh-token`, { refreshToken })
      .pipe(
        tap(res => this.setTokens(res))
      );
  }
}
