import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/security/auth.service';

export const webGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  if (!authService.isAuthenticated()) {
    return inject(Router).createUrlTree(['/login']);
  }

  return true;
};
