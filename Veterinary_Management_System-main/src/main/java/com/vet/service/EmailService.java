package com.vet.service;

import com.vet.model.Appointment;

/**
 * Service interface for sending email messages
 */
public interface EmailService {
    
    /**
     * Send email with retry mechanism
     */
    void sendEmailWithRetry(String to, String subject, String content, int maxRetries);
    
    /**
     * Send immediate confirmation email to pet owner
     */
    void sendOwnerConfirmation(Appointment appointment, String ownerEmail, String ownerName,
                              String veterinarianName, String petName);
    
    /**
     * Send immediate confirmation email to veterinarian
     */
    void sendVeterinarianConfirmation(Appointment appointment, String veterinarianEmail,
                                     String veterinarianName, String ownerName, String petName);
    
    /**
     * Send reminder email to pet owner
     */
    void sendOwnerReminder(Appointment appointment, String ownerEmail, String ownerName,
                          String veterinarianName, String petName);
    
    /**
     * Send reminder email to veterinarian
     */
    void sendVeterinarianReminder(Appointment appointment, String veterinarianEmail,
                                 String veterinarianName, String ownerName, String petName);
}
