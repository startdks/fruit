package com.fruit.server.contact;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fruit.server.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactMessageRepository contactMessageRepository;

    @Transactional
    public ContactMessage saveMessage(ContactRequest contactRequest) {
        ContactMessage message = new ContactMessage();
        message.setName(contactRequest.name());
        message.setEmail(contactRequest.email());
        message.setSubject(contactRequest.subject());
        message.setMessage(contactRequest.message());
        message.setIsRead(false);
        return contactMessageRepository.save(message);
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ContactMessage> getUnreadMessages() {
        return contactMessageRepository.findByIsReadFalse();
    }

    @Transactional
    public ContactMessage markAsRead(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", id));
        message.setIsRead(true);
        return contactMessageRepository.save(message);
    }
}
