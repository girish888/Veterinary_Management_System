package com.vet.controller;

import com.vet.dto.AppointmentReminderData;
import com.vet.service.AppointmentReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing appointment reminders
 */
@RestController
@RequestMapping("/api/reminders")
@PreAuthorize("hasRole('ADMIN')")
public class AppointmentReminderController {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderController.class);
    
    @Autowired
    private AppointmentReminderService appointmentReminderService;
    
    /**
     * Manually trigger reminders for today's appointments
     */
    @PostMapping("/trigger/today")
    public ResponseEntity<Map<String, Object>> triggerTodaysReminders() {
        logger.info("Admin manually triggered today's appointment reminders");
        
        try {
            appointmentReminderService.processTodaysReminders();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Today's appointment reminders have been processed successfully");
            response.put("timestamp", LocalDate.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to trigger today's reminders", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to process today's reminders: " + e.getMessage());
            response.put("timestamp", LocalDate.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Manually trigger reminders for a specific date
     */
    @PostMapping("/trigger/date/{date}")
    public ResponseEntity<Map<String, Object>> triggerRemindersForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("Admin manually triggered appointment reminders for date: {}", date);
        
        try {
            appointmentReminderService.processRemindersForDate(date);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment reminders for " + date + " have been processed successfully");
            response.put("date", date);
            response.put("timestamp", LocalDate.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to trigger reminders for date: {}", date, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to process reminders for " + date + ": " + e.getMessage());
            response.put("date", date);
            response.put("timestamp", LocalDate.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get appointments for a specific date (for reminder preview)
     */
    @GetMapping("/appointments/{date}")
    public ResponseEntity<Map<String, Object>> getAppointmentsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            List<AppointmentReminderData> appointments = appointmentReminderService.getAppointmentsForDate(date);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("date", date);
            response.put("appointmentCount", appointments.size());
            response.put("appointments", appointments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get appointments for date: {}", date, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve appointments for " + date + ": " + e.getMessage());
            response.put("date", date);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get today's appointments (for reminder preview)
     */
    @GetMapping("/appointments/today")
    public ResponseEntity<Map<String, Object>> getTodaysAppointments() {
        return getAppointmentsForDate(LocalDate.now());
    }
    
    /**
     * Send reminder for a specific appointment
     */
    @PostMapping("/send/{appointmentId}")
    public ResponseEntity<Map<String, Object>> sendReminderForAppointment(@PathVariable Long appointmentId) {
        logger.info("Admin manually triggered reminder for appointment ID: {}", appointmentId);
        
        try {
            appointmentReminderService.sendReminderForAppointment(appointmentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reminder sent successfully for appointment ID: " + appointmentId);
            response.put("appointmentId", appointmentId);
            response.put("timestamp", LocalDate.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to send reminder for appointment ID: {}", appointmentId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send reminder for appointment ID " + appointmentId + ": " + e.getMessage());
            response.put("appointmentId", appointmentId);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get reminder system status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getReminderSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("system", "Appointment Reminder System");
        status.put("status", "Active");
        status.put("lastCheck", LocalDate.now());
        status.put("scheduledJobs", List.of(
            "Daily reminders at 8:00 AM",
            "Hourly checks for same-day appointments"
        ));
        status.put("features", List.of(
            "Automatic daily reminders",
            "Manual trigger support",
            "Retry mechanism with exponential backoff",
            "Comprehensive logging"
        ));
        
        return ResponseEntity.ok(status);
    }
}
