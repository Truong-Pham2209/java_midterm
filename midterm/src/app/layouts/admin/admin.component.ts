import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../components/admin/header/header.component';
import { FooterComponent } from '../../components/web/footer/footer.component';

@Component({
  selector: 'app-admin',
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {

}
