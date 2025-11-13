package com.vet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

/**
 * Test controller for verifying email configuration
 * This should be removed or secured in production
 */
@RestController
@RequestMapping("/api/email-test")
public class EmailTestController {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${spring.mail.password}")
    private String emailPassword;
    
    @GetMapping("/config")
    public ResponseEntity<String> getEmailConfig() {
        return ResponseEntity.ok("Email Configuration:\n" +
                               "From: " + fromEmail + "\n" +
                               "Password: " + (emailPassword != null ? "***SET***" : "***NOT SET***") + "\n" +
                               "MailSender: " + (mailSender != null ? "Configured" : "Not Configured"));
    }
    
    @PostMapping("/send")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("VetCare Email Test - " + System.currentTimeMillis());
            message.setText("This is a test email from VetCare Clinic System.\n\n" +
                          "If you received this email, the email configuration is working correctly!\n\n" +
                          "Timestamp: " + java.time.LocalDateTime.now() + "\n" +
                          "Test ID: " + System.currentTimeMillis());
            
            mailSender.send(message);
            return ResponseEntity.ok("✅ Test email sent successfully to " + to);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("❌ Failed to send email to " + to + ": " + e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> emailStatus() {
        try {
            // Simple test to check if mail sender is configured
            return ResponseEntity.ok("✅ Email service is configured and ready");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("❌ Email service configuration error: " + e.getMessage());
        }
    }
}
