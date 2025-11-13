package com.vet.repository;

import com.vet.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByVeterinarianId(Long veterinarianId);
    List<Prescription> findByPetId(Long petId);
    List<Prescription> findByOwnerId(Long ownerId);
    Prescription findByAppointmentId(Long appointmentId);
    
    @Query("SELECT p FROM Prescription p WHERE p.veterinarian.id = ?1 ORDER BY p.date DESC")
    List<Prescription> findRecentByVeterinarian(Long veterinarianId);
} 