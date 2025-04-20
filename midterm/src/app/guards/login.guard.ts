import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/security/auth.service';
import { inject } from '@angular/core';

export const loginGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  if (authService.isAuthenticated()) {
    return inject(Router).createUrlTree(['/home']);
  }

  return true;
};
