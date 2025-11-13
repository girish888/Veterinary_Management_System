package com.vet.service.impl;

import com.vet.model.Prescription;
import com.vet.repository.PrescriptionRepository;
import com.vet.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Override
    public Prescription save(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public Prescription findById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        // Initialize relationships to prevent lazy loading issues
        if (prescription.getPet() != null) prescription.getPet().getName();
        if (prescription.getOwner() != null) prescription.getOwner().getFullName();
        if (prescription.getVeterinarian() != null) prescription.getVeterinarian().getFullName();
        return prescription;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findAll() {
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findByVeterinarian(Long veterinarianId) {
        List<Prescription> prescriptions = prescriptionRepository.findByVeterinarianId(veterinarianId);
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findByVeterinarianId(Long veterinarianId) {
        List<Prescription> prescriptions = prescriptionRepository.findByVeterinarianId(veterinarianId);
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findByPet(Long petId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPetId(petId);
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    public long count() {
        return prescriptionRepository.count();
    }

    @Override
    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }

    @Override
    public long getTotalPrescriptions() {
        return prescriptionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByVet(Long vetId) {
        List<Prescription> prescriptions = prescriptionRepository.findByVeterinarianId(vetId);
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByOwner(Long ownerId) {
        List<Prescription> prescriptions = prescriptionRepository.findByOwnerId(ownerId);
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByVeterinarian(Long veterinarianId) {
        List<Prescription> prescriptions = prescriptionRepository.findByVeterinarianId(veterinarianId);
        // Initialize relationships
        prescriptions.forEach(p -> {
            if (p.getPet() != null) p.getPet().getName();
            if (p.getOwner() != null) p.getOwner().getFullName();
            if (p.getVeterinarian() != null) p.getVeterinarian().getFullName();
        });
        return prescriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getAllPrescriptions() {
        return findAll();
    }

    @Override
    public Prescription savePrescription(Prescription prescription) {
        return save(prescription);
    }

    @Override
    public Prescription getPrescriptionById(Long id) {
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Prescription findByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId);
        if (prescription != null) {
            // Initialize relationships
            if (prescription.getPet() != null) prescription.getPet().getName();
            if (prescription.getOwner() != null) prescription.getOwner().getFullName();
            if (prescription.getVeterinarian() != null) prescription.getVeterinarian().getFullName();
        }
        return prescription;
    }
} 