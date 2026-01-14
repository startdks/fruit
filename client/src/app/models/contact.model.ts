export interface ContactMessage {
    id?: number;
    name: string;
    email: string;
    subject: string;
    message: string;
    isRead?: boolean;
    created?: Date;
}
