import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ContactMessage } from '../models';

@Injectable({
    providedIn: 'root',
})
export class ContactService {
    private readonly API_URL = 'https://xj9iw8ynrf.execute-api.us-east-1.amazonaws.com/api/contact';

    constructor(private http: HttpClient) { }

    submitContactForm(message: ContactMessage): Observable<ContactMessage> {
        return this.http.post<ContactMessage>(`${this.API_URL}`, message);
    }

    getAllMessages(): Observable<ContactMessage[]> {
        return this.http.get<ContactMessage[]>(`${this.API_URL}`);
    }

    getUnreadMessages(): Observable<ContactMessage[]> {
        return this.http.get<ContactMessage[]>(`${this.API_URL}/unread`);
    }

    markAsRead(messageId: number): Observable<ContactMessage> {
        return this.http.patch<ContactMessage>(`${this.API_URL}/${messageId}/read`, null);
    }

}

