package com.vet.service.impl;

import com.vet.model.Appointment;
import com.vet.repository.AppointmentRepository;
import com.vet.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> findByOwnerId(Long ownerId) {
        return appointmentRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Appointment> findByVeterinarianId(Long veterinarianId) {
        return appointmentRepository.findByVeterinarianId(veterinarianId);
    }

    @Override
    public List<Appointment> findByPetId(Long petId) {
        return appointmentRepository.findByPetId(petId);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = findById(id);
        if (appointment != null) {
            appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }
    }

    @Override
    public List<Appointment> findByVeterinarianIdAndDateTimeBetween(Long veterinarianId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return appointmentRepository.findByVeterinarianIdAndDateTimeBetween(veterinarianId, start, end);
    }
} 