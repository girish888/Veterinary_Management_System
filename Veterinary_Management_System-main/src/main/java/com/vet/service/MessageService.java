package com.vet.service;

import com.vet.model.Message;
import java.util.List;

public interface MessageService {
    Message saveMessage(Message message);
    Message getMessageById(Long id);
    List<Message> getAllMessages();
    List<Message> getLatestMessages(int count);
    List<Message> getUnreadMessages(Long userId);
    void markAsRead(Long messageId);
    List<Message> getMessagesByOwner(Long ownerId);
    void sendMessage(Long senderId, Long recipientId, String subject, String content);
    List<Message> getMessagesReceivedByUser(Long userId);
    List<Message> getMessagesSentByUser(Long userId);
    void deleteMessage(Long id);
} 