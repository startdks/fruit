package com.fruit.server.contact;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactMessage> saveMessage(@RequestBody ContactRequest contactRequest) {
        ContactMessage message = contactService.saveMessage(contactRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        List<ContactMessage> messages = contactService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<ContactMessage>> getUnreadMessages() {
        List<ContactMessage> unreadMessages = contactService.getUnreadMessages();
        return ResponseEntity.ok(unreadMessages);
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<ContactMessage> markAsRead(@PathVariable Long messageId) {
        ContactMessage message = contactService.markAsRead(messageId);
        return ResponseEntity.ok(message);
    }
}
