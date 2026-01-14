import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { NotificationService } from '../../../services/notification.service';
import { LoginRequest } from '../../../models';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent {
    loginForm: LoginRequest = { email: '', password: '' };

    constructor(
        private authService: AuthService,
        private cartService: CartService,
        private notificationService: NotificationService,
        private router: Router
    ) { }

    handleLogin(): void {
        this.authService.login(this.loginForm).subscribe({
            next: (response) => {
                if (response.userId) {
                    // Show welcome message
                    this.notificationService.show(`Welcome back! ${response.fullName}`);

                    // Transfer guest cart to logged-in user
                    this.cartService.transferGuestCart(response.userId).subscribe({
                        next: () => this.router.navigate(['/']),
                        error: () => this.router.navigate(['/'])
                    });
                } else {
                    alert(response.message || 'Login failed');
                }
            },
            error: (err) => alert('Login failed. Please try again.')
        });
    }
}
