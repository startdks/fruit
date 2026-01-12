import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { FooterComponent } from './components/footer/footer';
import { NotificationComponent } from './components/notification/notification';
import { CartSidebarComponent } from './components/cart-sidebar/cart-sidebar';

@Component({
    selector: 'app-root',
    imports: [
        RouterOutlet,
        NavbarComponent,
        FooterComponent,
        NotificationComponent,
        CartSidebarComponent],
    templateUrl: './app.html',
    styleUrl: './app.scss'
})
export class App {
    protected readonly title = signal('client');
}
