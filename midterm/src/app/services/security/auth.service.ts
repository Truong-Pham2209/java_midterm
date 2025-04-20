import { Injectable } from '@angular/core';
import { TokenStorageService } from './token-storage.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import { backendUrl } from '../../../environments/environment';
import { JwtResponse } from '../../models/auth/token';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private readonly router: Router,
    private readonly http: HttpClient,
    private storageService: TokenStorageService
  ) { }

  isAuthenticated(): boolean {
    const accessToken = this.storageService.getToken();
    const refreshToken = this.storageService.getRefreshToken();

    if (accessToken && refreshToken) {
      try {
        const decoded = jwtDecode<any>(accessToken);
        console.log("Token decoded:", decoded);
        return true;
      } catch (err) {
        console.error("Invalid token format or fake token detected:", err);
        this.storageService.redirectTo401();
      }
    }

    this.storageService.clearTokens();
    return false;
  }

  hasAnyRoles(roles: string[]): boolean {
    const accessToken = this.storageService.getToken();
    const refreshToken = this.storageService.getRefreshToken();

    if (accessToken && refreshToken) {
      try {
        const decoded = jwtDecode<any>(accessToken);
        return roles.includes(decoded.role);
      } catch (err) {
        console.error("Invalid token format or fake token detected:", err);
        this.storageService.clearTokens();
      }
    }

    return false;
  }

  logout(): void {
    this.storageService.clearTokens();
    console.log("Logout successful, redirecting to login page...");
    this.router.navigate(['/login']);
  }

  loginWithUsernameAndPassword(body: { username: string, password: string }): void {
    this.http.post<JwtResponse>(`${backendUrl}/api/auth/login`, body).subscribe({
      next: (res: JwtResponse) => {
        this.onLoginSuccess(res);
      },
      error: (err) => {
        alert("Mật khẩu hoặc tên đăng nhập không đúng!");
        this.onLoginError(err);
        console.error("Login error:", err);
      }
    });
  }

  private onLoginSuccess(jwt: JwtResponse) {
    this.storageService.setTokens(jwt);
    const decoded = jwtDecode<any>(jwt.accessToken);
    if (decoded.role === 'ADMIN') {
      this.router.navigate(['/admin/home']);
      return;
    }

    this.router.navigate(['/home']);
  }

  private onLoginError(err: any) {
    this.storageService.clearTokens();
    this.router.navigate(['/login']);
    console.error("Login error:", err);
  }
}
