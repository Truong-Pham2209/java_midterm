import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../../components/web/footer/footer.component';
import { HeaderComponent } from '../../components/web/header/header.component';

@Component({
  selector: 'app-web',
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './web.component.html',
  styleUrl: './web.component.scss'
})
export class WebComponent {

}
