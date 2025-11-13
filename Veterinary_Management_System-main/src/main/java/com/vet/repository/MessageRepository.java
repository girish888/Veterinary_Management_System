package com.vet.repository;

import com.vet.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
    
    @Query("SELECT m FROM Message m WHERE m.receiverId = ?1 OR m.senderEmail = ?2 ORDER BY m.sentAt DESC")
    List<Message> findLatestMessagesByUserId(Long userId, String senderEmail, int limit);

    List<Message> findAllByOrderBySentAtDesc();
    
    @Query("SELECT m FROM Message m WHERE m.receiverId = ?1 OR m.senderEmail = ?2 ORDER BY m.sentAt DESC")
    List<Message> findMessagesByOwner(Long ownerId, String ownerEmail);

    List<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId);
    List<Message> findBySenderEmailOrderBySentAtDesc(String senderEmail);
} 