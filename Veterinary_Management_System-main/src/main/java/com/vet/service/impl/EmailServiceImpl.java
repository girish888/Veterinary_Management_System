package com.vet.service.impl;

import com.vet.model.Appointment;
import com.vet.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of EmailService using Gmail SMTP
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.clinic.name:VetCare Clinic}")
    private String clinicName;
    
    @Value("${app.clinic.address:456 Veterinary Avenue, Medical District, City, State 12345}")
    private String clinicAddress;
    
    @Value("${app.clinic.phone:+1-555-VET-CARE}")
    private String clinicPhone;
    
    @Override
    public void sendEmailWithRetry(String to, String subject, String content, int maxRetries) {
        int attempts = 0;
        boolean sent = false;
        
        logger.info("📧 Attempting to send email to: {} | Subject: {}", to, subject);
        
        while (attempts < maxRetries && !sent) {
            attempts++;
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(content);
                
                mailSender.send(message);
                sent = true;
                logger.info("✅ Email sent successfully to {} on attempt {}/{}", to, attempts, maxRetries);
                
            } catch (Exception e) {
                logger.error("❌ Failed to send email to {} on attempt {}/{}: {}", to, attempts, maxRetries, e.getMessage());
                
                if (attempts < maxRetries) {
                    try {
                        // Wait before retry (exponential backoff)
                        long waitTime = 1000 * attempts;
                        logger.info("⏳ Waiting {} ms before retry {} for email to {}", waitTime, attempts + 1, to);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.warn("⚠️ Email sending interrupted for {}", to);
                        break;
                    }
                }
            }
        }
        
        if (!sent) {
            logger.error("💥 Failed to send email to {} after {} attempts - giving up", to, maxRetries);
        } else {
            logger.info("🎉 Email delivery completed successfully to {}", to);
        }
    }
    
    @Override
    public void sendOwnerConfirmation(Appointment appointment, String ownerEmail, String ownerName,
                                    String veterinarianName, String petName) {
        String subject = "Appointment Confirmation - " + petName + " scheduled for " +
                        appointment.getDateTime().format(DATE_FORMATTER) + " at " +
                        appointment.getDateTime().format(TIME_FORMATTER);
        
        String content = generateOwnerConfirmationContent(appointment, ownerName, veterinarianName, petName);
        
        // Send email asynchronously with enhanced logging
        CompletableFuture.runAsync(() -> {
            try {
                sendEmailWithRetry(ownerEmail, subject, content, 3);
                logger.info("✅ Owner confirmation email successfully sent to {} for appointment ID: {}", 
                           ownerEmail, appointment.getId());
            } catch (Exception e) {
                logger.error("❌ Failed to send owner confirmation email to {} for appointment ID: {}", 
                           ownerEmail, appointment.getId(), e);
            }
        });
        
        logger.info("📧 Owner confirmation email queued for sending to {} for appointment ID: {}", 
                   ownerEmail, appointment.getId());
    }
    
    @Override
    public void sendVeterinarianConfirmation(Appointment appointment, String veterinarianEmail,
                                           String veterinarianName, String ownerName, String petName) {
        String subject = "New Appointment Confirmation - " + petName + " with " + ownerName;
        
        String content = generateVeterinarianConfirmationContent(appointment, veterinarianName, ownerName, petName);
        
        // Send email asynchronously with enhanced logging
        CompletableFuture.runAsync(() -> {
            try {
                sendEmailWithRetry(veterinarianEmail, subject, content, 3);
                logger.info("✅ Veterinarian confirmation email successfully sent to {} for appointment ID: {}", 
                           veterinarianEmail, appointment.getId());
            } catch (Exception e) {
                logger.error("❌ Failed to send veterinarian confirmation email to {} for appointment ID: {}", 
                           veterinarianEmail, appointment.getId(), e);
            }
        });
        
        logger.info("📧 Veterinarian confirmation email queued for sending to {} for appointment ID: {}", 
                   veterinarianEmail, appointment.getId());
    }
    
    @Override
    public void sendOwnerReminder(Appointment appointment, String ownerEmail, String ownerName,
                                String veterinarianName, String petName) {
        String subject = "Appointment Reminder - " + petName + " in 1 hour";
        
        String content = generateOwnerReminderContent(appointment, ownerName, veterinarianName, petName);
        
        // Send email asynchronously
        CompletableFuture.runAsync(() -> {
            sendEmailWithRetry(ownerEmail, subject, content, 3);
        });
        
        logger.info("Owner reminder email sent to {} for appointment ID: {}", ownerEmail, appointment.getId());
    }
    
    @Override
    public void sendVeterinarianReminder(Appointment appointment, String veterinarianEmail,
                                       String veterinarianName, String ownerName, String petName) {
        String subject = "Appointment Reminder - " + petName + " with " + ownerName + " in 1 hour";
        
        String content = generateVeterinarianReminderContent(appointment, veterinarianName, ownerName, petName);
        
        // Send email asynchronously
        CompletableFuture.runAsync(() -> {
            sendEmailWithRetry(veterinarianEmail, subject, content, 3);
        });
        
        logger.info("Veterinarian reminder email sent to {} for appointment ID: {}", veterinarianEmail, appointment.getId());
    }
    
    /**
     * Generate confirmation email content for pet owner
     */
    private String generateOwnerConfirmationContent(Appointment appointment, String ownerName,
                                                  String veterinarianName, String petName) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear ").append(ownerName).append(",\n\n");
        content.append("🎉 Your appointment has been successfully confirmed!\n\n");
        content.append("Thank you for choosing ").append(clinicName).append(" for your pet's care.\n\n");
        
        content.append("📋 APPOINTMENT DETAILS:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("📅 Date: ").append(appointment.getDateTime().format(DATE_FORMATTER)).append("\n");
        content.append("🕐 Time: ").append(appointment.getDateTime().format(TIME_FORMATTER)).append("\n");
        content.append("🐾 Pet: ").append(petName).append("\n");
        content.append("👨‍⚕️ Veterinarian: Dr. ").append(veterinarianName).append("\n");
        content.append("📝 Reason: ").append(appointment.getReason()).append("\n");
        content.append("📍 Location: ").append(clinicName).append("\n");
        content.append("   ").append(clinicAddress).append("\n\n");
        
        content.append("⚠️  IMPORTANT REMINDERS:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("• Please arrive 10 minutes before your scheduled appointment time\n");
        content.append("• Bring any relevant medical records or previous prescriptions\n");
        content.append("• If you need to reschedule or cancel, please contact us at least 24 hours in advance\n\n");
        
        content.append("📞 CONTACT INFORMATION:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("Phone: ").append(clinicPhone).append("\n");
        content.append("Email: ").append(fromEmail).append("\n\n");
        
        content.append("We look forward to providing excellent care for ").append(petName).append("! 🐕🐱\n\n");
        content.append("Best regards,\n");
        content.append("The ").append(clinicName).append(" Team\n");
        content.append("Providing compassionate care for your beloved pets");
        
        return content.toString();
    }
    
    /**
     * Generate confirmation email content for veterinarian
     */
    private String generateVeterinarianConfirmationContent(Appointment appointment, String veterinarianName,
                                                         String ownerName, String petName) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear Dr. ").append(veterinarianName).append(",\n\n");
        content.append("📋 A new appointment has been confirmed and added to your schedule.\n\n");
        
        content.append("📋 APPOINTMENT DETAILS:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("📅 Date: ").append(appointment.getDateTime().format(DATE_FORMATTER)).append("\n");
        content.append("🕐 Time: ").append(appointment.getDateTime().format(TIME_FORMATTER)).append("\n");
        content.append("🐾 Pet: ").append(petName).append("\n");
        content.append("👤 Owner: ").append(ownerName).append("\n");
        content.append("📝 Reason: ").append(appointment.getReason()).append("\n");
        content.append("📍 Location: ").append(clinicName).append("\n");
        content.append("   ").append(clinicAddress).append("\n\n");
        
        content.append("📞 CONTACT INFORMATION:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("Phone: ").append(clinicPhone).append("\n");
        content.append("Email: ").append(fromEmail).append("\n\n");
        
        content.append("Please review the appointment details and prepare accordingly.\n\n");
        content.append("Best regards,\n");
        content.append("The ").append(clinicName).append(" Team");
        
        return content.toString();
    }
    
    /**
     * Generate reminder email content for pet owner
     */
    private String generateOwnerReminderContent(Appointment appointment, String ownerName,
                                              String veterinarianName, String petName) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear ").append(ownerName).append(",\n\n");
        content.append("⏰ APPOINTMENT REMINDER\n\n");
        content.append("This is a friendly reminder that ").append(petName).append("'s appointment is in 1 hour.\n\n");
        
        content.append("📋 APPOINTMENT DETAILS:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("📅 Date: ").append(appointment.getDateTime().format(DATE_FORMATTER)).append("\n");
        content.append("🕐 Time: ").append(appointment.getDateTime().format(TIME_FORMATTER)).append("\n");
        content.append("👨‍⚕️ Veterinarian: Dr. ").append(veterinarianName).append("\n");
        content.append("📍 Location: ").append(clinicName).append("\n");
        content.append("   ").append(clinicAddress).append("\n\n");
        
        content.append("⚠️  REMINDER:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("• Please arrive 10 minutes before your scheduled appointment time\n");
        content.append("• Bring any relevant medical records or previous prescriptions\n\n");
        
        content.append("📞 CONTACT INFORMATION:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("Phone: ").append(clinicPhone).append("\n");
        content.append("Email: ").append(fromEmail).append("\n\n");
        
        content.append("We look forward to seeing you and ").append(petName).append("!\n\n");
        content.append("Best regards,\n");
        content.append("The ").append(clinicName).append(" Team");
        
        return content.toString();
    }
    
    /**
     * Generate reminder email content for veterinarian
     */
    private String generateVeterinarianReminderContent(Appointment appointment, String veterinarianName,
                                                     String ownerName, String petName) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear Dr. ").append(veterinarianName).append(",\n\n");
        content.append("⏰ APPOINTMENT REMINDER\n\n");
        content.append("You have an appointment in 1 hour.\n\n");
        
        content.append("📋 APPOINTMENT DETAILS:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("📅 Date: ").append(appointment.getDateTime().format(DATE_FORMATTER)).append("\n");
        content.append("🕐 Time: ").append(appointment.getDateTime().format(TIME_FORMATTER)).append("\n");
        content.append("🐾 Pet: ").append(petName).append("\n");
        content.append("👤 Owner: ").append(ownerName).append("\n");
        content.append("📝 Reason: ").append(appointment.getReason()).append("\n");
        content.append("📍 Location: ").append(clinicName).append("\n");
        content.append("   ").append(clinicAddress).append("\n\n");
        
        content.append("📞 CONTACT INFORMATION:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("Phone: ").append(clinicPhone).append("\n");
        content.append("Email: ").append(fromEmail).append("\n\n");
        
        content.append("Please prepare for the appointment accordingly.\n\n");
        content.append("Best regards,\n");
        content.append("The ").append(clinicName).append(" Team");
        
        return content.toString();
    }
}
