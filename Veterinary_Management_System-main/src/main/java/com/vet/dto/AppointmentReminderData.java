package com.vet.dto;

import com.vet.model.Appointment;
import java.time.LocalDateTime;

/**
 * DTO containing all necessary data for sending appointment reminders
 */
public class AppointmentReminderData {
    
    private Long appointmentId;
    private Long ownerId;
    private Long veterinarianId;
    private Long petId;
    private LocalDateTime dateTime;
    private String reason;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private String veterinarianName;
    private String veterinarianEmail;
    private String veterinarianPhone;
    private String petName;
    private String petSpecies;
    
    // Constructors
    public AppointmentReminderData() {}
    
    public AppointmentReminderData(Appointment appointment) {
        this.appointmentId = appointment.getId();
        this.ownerId = appointment.getOwnerId();
        this.veterinarianId = appointment.getVeterinarianId();
        this.petId = appointment.getPetId();
        this.dateTime = appointment.getDateTime();
        this.reason = appointment.getReason();
    }
    
    // Getters and Setters
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    public Long getVeterinarianId() { return veterinarianId; }
    public void setVeterinarianId(Long veterinarianId) { this.veterinarianId = veterinarianId; }
    
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    
    public String getVeterinarianName() { return veterinarianName; }
    public void setVeterinarianName(String veterinarianName) { this.veterinarianName = veterinarianName; }
    
    public String getVeterinarianEmail() { return veterinarianEmail; }
    public void setVeterinarianEmail(String veterinarianEmail) { this.veterinarianEmail = veterinarianEmail; }
    
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    
    public String getVeterinarianPhone() { return veterinarianPhone; }
    public void setVeterinarianPhone(String veterinarianPhone) { this.veterinarianPhone = veterinarianPhone; }
    
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    
    public String getPetSpecies() { return petSpecies; }
    public void setPetSpecies(String petSpecies) { this.petSpecies = petSpecies; }
}
