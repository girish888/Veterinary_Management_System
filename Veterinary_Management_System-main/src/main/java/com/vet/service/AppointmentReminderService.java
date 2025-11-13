package com.vet.service;

import com.vet.dto.AppointmentReminderData;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing appointment reminders
 */
public interface AppointmentReminderService {
    
    /**
     * Process and send reminders for all appointments scheduled for a specific date
     * @param date The date to process appointments for
     */
    void processRemindersForDate(LocalDate date);
    
    /**
     * Process and send reminders for today's appointments
     */
    void processTodaysReminders();
    
    /**
     * Send reminder for a specific appointment
     * @param appointmentId The ID of the appointment to send reminders for
     */
    void sendReminderForAppointment(Long appointmentId);
    
    /**
     * Get all appointments scheduled for a specific date
     * @param date The date to get appointments for
     * @return List of appointments for the specified date
     */
    List<AppointmentReminderData> getAppointmentsForDate(LocalDate date);
    
    /**
     * Check if reminders have already been sent for an appointment on a specific date
     * @param appointmentId The appointment ID
     * @param date The date to check
     * @return true if reminders were already sent, false otherwise
     */
    boolean areRemindersAlreadySent(Long appointmentId, LocalDate date);
    
    /**
     * Mark reminders as sent for an appointment on a specific date
     * @param appointmentId The appointment ID
     * @param date The date reminders were sent
     */
    void markRemindersAsSent(Long appointmentId, LocalDate date);
    
    /**
     * Send immediate confirmation SMS for a newly booked appointment
     * @param appointmentId The appointment ID
     */
    void sendImmediateConfirmation(Long appointmentId);
}
