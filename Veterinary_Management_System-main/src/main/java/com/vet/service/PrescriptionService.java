package com.vet.service;

import com.vet.model.Prescription;
import java.util.List;

public interface PrescriptionService {
    Prescription save(Prescription prescription);
    Prescription findById(Long id);
    List<Prescription> findAll();
    List<Prescription> findByVeterinarian(Long veterinarianId);
    List<Prescription> findByVeterinarianId(Long veterinarianId);
    List<Prescription> findByPet(Long petId);
    long count();
    void deletePrescription(Long id);
    long getTotalPrescriptions();
    List<Prescription> getPrescriptionsByVet(Long vetId);
    List<Prescription> getPrescriptionsByOwner(Long ownerId);
    List<Prescription> getPrescriptionsByVeterinarian(Long veterinarianId);
    List<Prescription> getAllPrescriptions();
    Prescription savePrescription(Prescription prescription);
    Prescription getPrescriptionById(Long id);
    Prescription findByAppointmentId(Long appointmentId);
} 