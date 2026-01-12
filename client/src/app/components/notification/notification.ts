import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
    selector: 'app-notification',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './notification.html',
    styleUrl: './notification.scss',
})
export class NotificationComponent {
    @Input() message: string = '';
}
