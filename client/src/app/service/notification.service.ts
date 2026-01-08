import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    private messageSubject = new BehaviorSubject<string>('');
    public message$ = this.messageSubject.asObservable();
    show(message: string, duration: number = 3000): void {
        this.messageSubject.next(message);
        setTimeout(() => this.messageSubject.next(''), duration);
    }

    clear(): void {
        this.messageSubject.next('');
    }
}
