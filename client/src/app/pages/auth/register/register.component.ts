import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { RegisterRequest } from '../../../models';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
    registerForm: RegisterRequest = { fullName: '', email: '', password: '' };
    confirmPassword = '';

    constructor(
        private authService: AuthService,
        private cartService: CartService,
        private router: Router
    ) { }

    handleRegister(): void {
        if (this.registerForm.password !== this.confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        this.authService.register(this.registerForm).subscribe({
            next: (response) => {
                if (response.userId) {
                    // Transfer guest cart to new user
                    this.cartService.transferGuestCart(response.userId).subscribe({
                        next: () => this.router.navigate(['/']),
                        error: () => this.router.navigate(['/'])
                    });
                } else {
                    alert(response.message || 'Registration failed');
                }
            },
            error: (err) => alert(err.error?.message || 'Registration failed. Please try again.')
        });
    }
}
