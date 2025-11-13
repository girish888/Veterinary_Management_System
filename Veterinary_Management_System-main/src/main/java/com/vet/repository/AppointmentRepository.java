package com.vet.repository;

import com.vet.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByOwnerId(Long ownerId);
    List<Appointment> findByVeterinarianId(Long veterinarianId);
    List<Appointment> findByPetId(Long petId);
    List<Appointment> findByVeterinarianIdAndDateTimeBetween(Long veterinarianId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDateTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
} 