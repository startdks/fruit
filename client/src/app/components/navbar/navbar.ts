import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthResponse } from '../../models/models';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './navbar.html',
    styleUrl: './navbar.scss',
})
export class NavbarComponent {
    @Input() currentUser: AuthResponse | null = null
    @Input() cartCount: number = 0;

    @Output() onSignInClick = new EventEmitter<void>();
    @Output() onLogout = new EventEmitter<void>();
    @Output() onCartClick = new EventEmitter<void>();
    @Output() onNavigate = new EventEmitter<string>();

    showMobileMenu = false;

    handleNavClick(target: string): void {
        this.onNavigate.emit(target);
        this.showMobileMenu = false;
    }

}
