package com.vet.service;

import com.vet.model.Appointment;
import java.util.List;

public interface AppointmentService {
    Appointment save(Appointment appointment);
    List<Appointment> findByOwnerId(Long ownerId);
    List<Appointment> findByVeterinarianId(Long veterinarianId);
    List<Appointment> findByPetId(Long petId);
    List<Appointment> findAll();
    Appointment findById(Long id);
    void cancelAppointment(Long id);
    List<Appointment> findByVeterinarianIdAndDateTimeBetween(Long veterinarianId, java.time.LocalDateTime start, java.time.LocalDateTime end);
} 