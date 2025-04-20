import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/security/auth.service';

@Component({
  selector: 'app-logout',
  imports: [],
  template: `<div>Đang chuyển tới trang login ...</div>`
})
export class LogoutComponent implements OnInit {
  constructor(private readonly authService: AuthService) { }

  ngOnInit(): void {
    console.log('LogoutComponent ngOnInit');
    this.authService.logout();
  }
}

