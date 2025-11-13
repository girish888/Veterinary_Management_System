package com.vet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String senderName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String senderEmail;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message content is required")
    @Column(length = 2000)
    private String content;

    private LocalDateTime sentAt;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "receiver_name")
    private String receiverName;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
} 