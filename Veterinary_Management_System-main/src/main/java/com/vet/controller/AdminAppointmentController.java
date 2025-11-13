package com.vet.controller;

import com.vet.model.Appointment;
import com.vet.model.User;
import com.vet.model.Pet;
import com.vet.service.AppointmentService;
import com.vet.service.UserService;
import com.vet.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PetService petService;

    @GetMapping
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        
        // Fetch all related entities
        List<Long> ownerIds = appointments.stream()
            .map(Appointment::getOwnerId)
            .distinct()
            .collect(Collectors.toList());
            
        List<Long> vetIds = appointments.stream()
            .map(Appointment::getVeterinarianId)
            .distinct()
            .collect(Collectors.toList());
            
        List<Long> petIds = appointments.stream()
            .map(Appointment::getPetId)
            .distinct()
            .collect(Collectors.toList());
            
        // Create maps for quick lookup
        Map<Long, User> owners = userService.findAllByIds(ownerIds).stream()
            .collect(Collectors.toMap(User::getId, owner -> owner));
            
        Map<Long, User> vets = userService.findAllByIds(vetIds).stream()
            .collect(Collectors.toMap(User::getId, vet -> vet));
            
        Map<Long, Pet> pets = petService.findAllByIds(petIds).stream()
            .collect(Collectors.toMap(Pet::getId, pet -> pet));
            
        // Add data to model
        model.addAttribute("appointments", appointments);
        model.addAttribute("owners", owners);
        model.addAttribute("vets", vets);
        model.addAttribute("pets", pets);
        
        return "admin/appointments";
    }
} 