import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { ContactService } from '../../services/contact.service';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';
import { Product, ContactMessage } from '../../models';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
    // Products
    products = signal<Product[]>([]);

    // Hero Carousel
    currentSlide = 0;
    heroSlides = [
        {
            id: 1,
            image: '/images/hero-bg.png',
            tagline: 'Fresh and Premium Quality Fruits',
            title: 'Fresh Fruits, Fresh Life',
            subtitle: 'Premium quality fruits delivered to your doorstep'
        },
        {
            id: 2,
            image: '/images/hero-bg-2.png',
            tagline: 'Handpicked from the Market',
            title: 'Farm Fresh Selection',
            subtitle: 'Handpicked from the best farms around the world'
        },
        {
            id: 3,
            image: '/images/hero-bg-3.png',
            tagline: 'Variety of Fruits, Rich Flavors',
            title: 'Premium Store Quality',
            subtitle: 'Browse our extensive collection of exotic fruits'
        }
    ];

    // Contact form
    contactForm: ContactMessage = { name: '', email: '', subject: '', message: '' };



    constructor(
        private productService: ProductService,
        private cartService: CartService,
        private contactService: ContactService,
        private notificationService: NotificationService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.productService.getAllProducts().subscribe({
            next: (products) => this.products.set(products),
            error: (err) => console.error('Error loading products:', err)
        });
    }

    // Cart
    addToCart(productId: number): void {
        this.cartService.addToCart(productId, 1).subscribe({
            next: () => {
                const product = this.products().find(p => p.id === productId);
                this.notificationService.show(`${product?.name} added to cart!`);
                // Update cart count
                const userId = this.authService.getCurrentUser()?.userId;
                this.cartService.getCart(userId).subscribe();
            },
            error: (err) => this.notificationService.show('Error adding to cart')
        });
    }

    // Contact form
    handleContactSubmit(): void {
        this.contactService.submitContactForm(this.contactForm).subscribe({
            next: () => {
                this.notificationService.show('Thank you for your message! We will get back to you soon.');
                this.contactForm = { name: '', email: '', subject: '', message: '' };
            },
            error: (err) => this.notificationService.show('Error sending message')
        });
    }

    // Hero Carousel Methods
    nextSlide(): void {
        this.currentSlide = (this.currentSlide + 1) % this.heroSlides.length;
    }

    prevSlide(): void {
        this.currentSlide = this.currentSlide === 0
            ? this.heroSlides.length - 1
            : this.currentSlide - 1;
    }

    goToSlide(index: number): void {
        this.currentSlide = index;
    }

    smoothScroll(target: string): void {
        const element = document.querySelector(target);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
}
