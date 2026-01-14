import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Cart } from '../../models';

@Component({
    selector: 'app-cart-sidebar',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './cart-sidebar.component.html',
    styleUrl: './cart-sidebar.component.scss',
})
export class CartSidebarComponent {
    @Input() isOpen: boolean = false;
    @Input() cart: Cart = { items: [], subtotal: 0, tax: 0, total: 0, itemCount: 0 };

    @Output() onClose = new EventEmitter<void>();
    @Output() onUpdateQuantity = new EventEmitter<{ cartItemId: number; quantity: number }>();
    @Output() onRemove = new EventEmitter<number>();
    @Output() onCheckout = new EventEmitter<void>();

    updateQuantity(cartItemId: number, quantity: number): void {
        this.onUpdateQuantity.emit({ cartItemId, quantity });
    }

    removeItem(cartItemId: number): void {
        this.onRemove.emit(cartItemId);
    }
}
