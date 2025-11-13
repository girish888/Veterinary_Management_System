package com.vet.scheduler;

import com.vet.service.AppointmentReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Scheduler for automatically sending appointment reminders
 */
@Component
public class AppointmentReminderScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderScheduler.class);
    
    @Autowired
    private AppointmentReminderService appointmentReminderService;
    
    @Value("${app.email.reminder.enabled:true}")
    private boolean remindersEnabled;
    
    @Value("${app.email.reminder.time:08:00}")
    private String reminderTime;
    
    /**
     * Scheduled job to send email appointment reminders every hour
     * This ensures timely reminders for appointments scheduled in the next hour
     */
    @Scheduled(cron = "${app.email.reminder.hourly.cron:0 0 * * * ?}")
    public void sendHourlyEmailReminders() {
        if (!remindersEnabled) {
            logger.info("Email appointment reminders are disabled. Skipping hourly reminder job.");
            return;
        }
        
        logger.info("Starting hourly email appointment reminder job");
        
        try {
            LocalDate today = LocalDate.now();
            appointmentReminderService.processRemindersForDate(today);
            logger.info("Hourly email appointment reminder job completed successfully for date: {}", today);
            
        } catch (Exception e) {
            logger.error("Failed to process hourly email appointment reminders", e);
        }
    }
    

    
    /**
     * Manual trigger method for testing purposes
     */
    public void triggerRemindersManually() {
        logger.info("Manually triggering email appointment reminders");
        appointmentReminderService.processTodaysReminders();
    }
}

