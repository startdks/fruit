import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router } from '@angular/router';
import { CartService } from './services/cart.service';
import { AuthService } from './services/auth.service';
import { OrderService } from './services/order.service';
import { NotificationService } from './services/notification.service';
import { Cart, CartItem, OrderRequest, OrderItemRequest, AuthResponse } from './models';

// Import shared components
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { NotificationComponent } from './components/notification/notification.component';
import { CartSidebarComponent } from './components/cart-sidebar/cart-sidebar.component';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [
        CommonModule,
        RouterOutlet,
        NavbarComponent,
        FooterComponent,
        NotificationComponent,
        CartSidebarComponent
    ],
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    // Signals for state management
    cart = signal<Cart>({ items: [], subtotal: 0, tax: 0, total: 0, itemCount: 0 });
    cartCount = signal<number>(0);
    currentUser = signal<AuthResponse | null>(null);

    // UI states
    showCartSidebar = false;

    // Notification
    notification = signal<string>('');

    constructor(
        private cartService: CartService,
        private authService: AuthService,
        private orderService: OrderService,
        private notificationService: NotificationService,
        private router: Router
    ) { }

    ngOnInit(): void {
        // Subscribe to auth state
        this.authService.currentUser$.subscribe(user => {
            this.currentUser.set(user);
            if (user) {
                this.loadCart();
            }
        });

        // Subscribe to cart count
        this.cartService.cartCount$.subscribe(count => {
            this.cartCount.set(count);
        });

        // Subscribe to notifications
        this.notificationService.message$.subscribe(message => {
            this.notification.set(message);
        });

        // Load cart summary
        this.loadCart();
    }

    loadCart(): void {
        const userId = this.currentUser()?.userId;
        this.cartService.getCart(userId).subscribe({
            next: (cart) => this.cart.set(cart),
            error: (err) => console.error('Error loading cart:', err)
        });
    }

    // Authentication
    handleSignInClick(): void {
        this.router.navigate(['/login']);
    }

    handleLogout(): void {
        this.authService.logout();
        this.showNotification('Logged out successfully');
        this.cart.set({ items: [], subtotal: 0, tax: 0, total: 0, itemCount: 0 });
        this.cartCount.set(0);
    }

    // Cart
    toggleCartSidebar(): void {
        this.showCartSidebar = !this.showCartSidebar;
        if (this.showCartSidebar) {
            this.notificationService.clear();
            this.loadCart();
        }
    }

    updateCartQuantity(event: { cartItemId: number; quantity: number }): void {
        this.cartService.updateQuantity(event.cartItemId, event.quantity).subscribe({
            next: () => this.loadCart(),
            error: (err) => console.error('Error updating quantity:', err)
        });
    }

    removeFromCart(cartItemId: number): void {
        this.cartService.removeFromCart(cartItemId).subscribe({
            next: () => {
                this.showNotification('Item removed from cart');
                this.loadCart();
            },
            error: (err) => console.error('Error removing item:', err)
        });
    }

    handleCheckout(): void {
        const userId = this.currentUser()?.userId;
        if (!userId) {
            this.showNotification('Please sign in to checkout');
            this.showCartSidebar = false;
            this.router.navigate(['/login']);
            return;
        }

        const items: OrderItemRequest[] = this.cart().items.map((item: CartItem) => ({
            productId: item.productId,
            quantity: item.quantity
        }));

        const orderRequest: OrderRequest = {
            userId,
            items,
            shippingAddress: '123 Fruit Street',
            shippingCity: 'Fresh City',
            shippingState: 'FC',
            shippingZip: '12345',
            shippingPhone: '(555) 123-4567'
        };

        this.orderService.createOrder(orderRequest).subscribe({
            next: (order) => {
                this.showNotification(`Order placed successfully! Total: $${this.cart().total.toFixed(2)}`);
                this.showCartSidebar = false;
                this.loadCart();
            },
            error: (err) => this.showNotification('Error placing order')
        });
    }

    // Navigation smooth scroll
    handleNavigate(target: string): void {
        // If on home page, scroll to section
        if (this.router.url === '/' || this.router.url === '') {
            const element = document.querySelector(target);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        } else {
            // Navigate to home and then scroll
            this.router.navigate(['/']).then(() => {
                setTimeout(() => {
                    const element = document.querySelector(target);
                    if (element) {
                        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }
                }, 100);
            });
        }
    }

    // Notification
    showNotification(message: string): void {
        this.notificationService.show(message);
    }
}
