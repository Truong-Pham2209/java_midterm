import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-error',
  imports: [],
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss'
})
export class ErrorComponent implements OnInit {
  status: string | null = null;
  message = 'Đã có lỗi xảy ra.';
  buttonText = 'Về trang chủ';

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    const urlParams = new URLSearchParams(window.location.search);
    this.status = urlParams.get('status');
    if (this.status) {
      this.updateMessage(this.status);
    } else {
      this.router.navigate(['/home']);
    }
  }

  updateMessage(status: string | null) {
    switch (status) {
      case '404':
        this.message = 'Không tìm thấy trang.';
        break;
      case '403':
        this.message = 'Bạn không có quyền truy cập.';
        break;
      case '401':
        this.message = 'Phiên đăng nhập đã hết hạn.';
        this.buttonText = 'Đăng nhập lại';
        break;
      default:
        this.message = 'Đã có lỗi xảy ra.';
    }
  }

  goBack() {
    if (this.status === '401') {
      this.router.navigate(['/login']);
    } else {
      this.router.navigate(['/home']);
    }
  }
}
