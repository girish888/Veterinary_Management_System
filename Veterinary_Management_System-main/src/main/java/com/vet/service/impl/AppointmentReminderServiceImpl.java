package com.vet.service.impl;

import com.vet.dto.AppointmentReminderData;
import com.vet.model.Appointment;
import com.vet.model.User;
import com.vet.model.Pet;
import com.vet.repository.AppointmentRepository;
import com.vet.repository.UserRepository;
import com.vet.repository.PetRepository;
import com.vet.service.AppointmentReminderService;
import com.vet.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AppointmentReminderService for managing appointment reminders
 */
@Service
@Transactional
public class AppointmentReminderServiceImpl implements AppointmentReminderService {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderServiceImpl.class);
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PetRepository petRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void processRemindersForDate(LocalDate date) {
        logger.info("Processing reminders for date: {}", date);
        
        List<AppointmentReminderData> appointments = getAppointmentsForDate(date);
        
        if (appointments.isEmpty()) {
            logger.info("No appointments found for date: {}", date);
            return;
        }
        
        logger.info("Found {} appointments for date: {}", appointments.size(), date);
        
        for (AppointmentReminderData appointmentData : appointments) {
            try {
                if (!areRemindersAlreadySent(appointmentData.getAppointmentId(), date)) {
                    sendReminderForAppointment(appointmentData.getAppointmentId());
                    markRemindersAsSent(appointmentData.getAppointmentId(), date);
                    logger.info("Reminders sent successfully for appointment ID: {}", appointmentData.getAppointmentId());
                } else {
                    logger.info("Reminders already sent for appointment ID: {} on date: {}", 
                              appointmentData.getAppointmentId(), date);
                }
            } catch (Exception e) {
                logger.error("Failed to process reminder for appointment ID: {}", 
                           appointmentData.getAppointmentId(), e);
            }
        }
    }
    
    @Override
    public void processTodaysReminders() {
        processRemindersForDate(LocalDate.now());
    }
    
    @Override
    public void sendReminderForAppointment(Long appointmentId) {
        AppointmentReminderData appointmentData = getAppointmentReminderData(appointmentId);
        
        if (appointmentData == null) {
            logger.error("Could not find appointment data for ID: {}", appointmentId);
            return;
        }
        
        // Send reminder to pet owner
        if (appointmentData.getOwnerEmail() != null && !appointmentData.getOwnerEmail().trim().isEmpty()) {
            emailService.sendOwnerReminder(
                createAppointmentFromData(appointmentData),
                appointmentData.getOwnerEmail(),
                appointmentData.getOwnerName(),
                appointmentData.getVeterinarianName(),
                appointmentData.getPetName()
            );
        } else {
            logger.warn("No owner email found for appointment ID: {}", appointmentId);
        }
        
        // Send reminder to veterinarian
        if (appointmentData.getVeterinarianEmail() != null && !appointmentData.getVeterinarianEmail().trim().isEmpty()) {
            emailService.sendVeterinarianReminder(
                createAppointmentFromData(appointmentData),
                appointmentData.getVeterinarianEmail(),
                appointmentData.getVeterinarianName(),
                appointmentData.getOwnerName(),
                appointmentData.getPetName()
            );
        } else {
            logger.warn("No veterinarian email found for appointment ID: {}", appointmentId);
        }
    }
    
    @Override
    public List<AppointmentReminderData> getAppointmentsForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        List<Appointment> appointments = appointmentRepository.findByDateTimeBetweenAndStatus(
            startOfDay, endOfDay, "SCHEDULED");
        
        return appointments.stream()
            .map(this::enrichAppointmentData)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean areRemindersAlreadySent(Long appointmentId, LocalDate date) {
        // This is a simplified implementation. In a production system, you might want to
        // track reminder history in a separate table to avoid duplicate sends.
        // For now, we'll assume reminders are sent once per day per appointment.
        return false;
    }
    
    @Override
    public void markRemindersAsSent(Long appointmentId, LocalDate date) {
        // This is a simplified implementation. In a production system, you might want to
        // log reminder history in a separate table.
        logger.info("Marked reminders as sent for appointment ID: {} on date: {}", appointmentId, date);
    }
    
    @Override
    public void sendImmediateConfirmation(Long appointmentId) {
        logger.info("Sending immediate confirmation for appointment ID: {}", appointmentId);
        
        AppointmentReminderData appointmentData = getAppointmentReminderData(appointmentId);
        
        if (appointmentData == null) {
            logger.error("Could not find appointment data for ID: {}", appointmentId);
            return;
        }
        
        // Send confirmation to pet owner
        if (appointmentData.getOwnerEmail() != null && !appointmentData.getOwnerEmail().trim().isEmpty()) {
            emailService.sendOwnerConfirmation(
                createAppointmentFromData(appointmentData),
                appointmentData.getOwnerEmail(),
                appointmentData.getOwnerName(),
                appointmentData.getVeterinarianName(),
                appointmentData.getPetName()
            );
        } else {
            logger.warn("No owner email found for appointment ID: {}", appointmentId);
        }
        
        // Send confirmation to veterinarian
        if (appointmentData.getVeterinarianEmail() != null && !appointmentData.getVeterinarianEmail().trim().isEmpty()) {
            emailService.sendVeterinarianConfirmation(
                createAppointmentFromData(appointmentData),
                appointmentData.getVeterinarianEmail(),
                appointmentData.getVeterinarianName(),
                appointmentData.getOwnerName(),
                appointmentData.getPetName()
            );
        } else {
            logger.warn("No veterinarian email found for appointment ID: {}", appointmentId);
        }
        
        logger.info("Immediate confirmation emails sent successfully for appointment ID: {}", appointmentId);
    }
    
    /**
     * Enrich appointment data with user and pet information
     */
    private AppointmentReminderData enrichAppointmentData(Appointment appointment) {
        AppointmentReminderData data = new AppointmentReminderData(appointment);
        
        // Get owner information
        User owner = userRepository.findById(appointment.getOwnerId()).orElse(null);
        if (owner != null) {
            data.setOwnerName(owner.getFullName());
            data.setOwnerEmail(owner.getEmail());
            data.setOwnerPhone(owner.getMobile()); // User model has mobile field
        }
        
        // Get veterinarian information
        User veterinarian = userRepository.findById(appointment.getVeterinarianId()).orElse(null);
        if (veterinarian != null) {
            data.setVeterinarianName(veterinarian.getFullName());
            data.setVeterinarianEmail(veterinarian.getEmail());
            data.setVeterinarianPhone(veterinarian.getMobile()); // User model has mobile field
        }
        
        // Get pet information
        Pet pet = petRepository.findById(appointment.getPetId()).orElse(null);
        if (pet != null) {
            data.setPetName(pet.getName());
            data.setPetSpecies(pet.getSpecies());
        }
        
        return data;
    }
    
    /**
     * Get enriched appointment data for a specific appointment ID
     */
    private AppointmentReminderData getAppointmentReminderData(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            return null;
        }
        return enrichAppointmentData(appointment);
    }
    
    /**
     * Create an Appointment object from AppointmentReminderData
     */
    private Appointment createAppointmentFromData(AppointmentReminderData data) {
        Appointment appointment = new Appointment();
        appointment.setId(data.getAppointmentId());
        appointment.setOwnerId(data.getOwnerId());
        appointment.setVeterinarianId(data.getVeterinarianId());
        appointment.setPetId(data.getPetId());
        appointment.setDateTime(data.getDateTime());
        appointment.setReason(data.getReason());
        appointment.setStatus("SCHEDULED");
        return appointment;
    }
}
