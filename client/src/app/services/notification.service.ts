import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private messageSubject = new BehaviorSubject<string>('');
    private timeoutId: any = null;

    public message$ = this.messageSubject.asObservable();

    show(message: string, duration: number = 3000): void {
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.messageSubject.next(message);
        this.timeoutId = setTimeout(() => this.messageSubject.next(''), duration);
    }

    clear(): void {
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }
        this.messageSubject.next('');
    }
}
