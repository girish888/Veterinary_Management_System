package com.vet.service.impl;

import com.vet.model.Message;
import com.vet.model.User;
import com.vet.repository.MessageRepository;
import com.vet.repository.UserRepository;
import com.vet.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderBySentAtDesc();
    }

    @Override
    public List<Message> getLatestMessages(int count) {
        return messageRepository.findAllByOrderBySentAtDesc()
                .subList(0, Math.min(count, (int) messageRepository.count()));
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long messageId) {
        Message message = getMessageById(messageId);
        message.setRead(true);
        messageRepository.save(message);
    }
    
    @Override
    public List<Message> getMessagesByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        return messageRepository.findMessagesByOwner(ownerId, owner.getEmail());
    }
    
    @Override
    public void sendMessage(Long senderId, Long recipientId, String subject, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
                
        Message message = new Message();
        message.setSenderName(sender.getFullName());
        message.setSenderEmail(sender.getEmail());
        message.setSubject(subject);
        message.setContent(content);
        message.setReceiverId(recipientId);
        message.setReceiverName(recipient.getFullName());
        
        messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesReceivedByUser(Long userId) {
        return messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
    }

    @Override
    public List<Message> getMessagesSentByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.findBySenderEmailOrderBySentAtDesc(user.getEmail());
    }

    @Override
    public void deleteMessage(Long id) {
        Message message = getMessageById(id);
        messageRepository.delete(message);
    }
} 