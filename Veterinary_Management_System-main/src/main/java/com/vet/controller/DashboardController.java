package com.vet.controller;

import com.vet.model.User;
import com.vet.model.Role;
import com.vet.model.Appointment;
import com.vet.model.Pet;
import com.vet.service.*;
import com.vet.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PetService petService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private MessageService messageService;

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            // Get the User object from CustomUserDetails or SecurityContext
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            
            // Add user to model
            model.addAttribute("user", user);
            
            // Add counts for all users
            model.addAttribute("veterinarianCount", userService.countByRole(Role.VETERINARIAN));
            model.addAttribute("ownerCount", userService.countByRole(Role.OWNER));
            model.addAttribute("petCount", petService.count());
            model.addAttribute("appointmentCount", appointmentService.findAll().size());
            
            // Add role-specific data
            if (user.getRole() == Role.VETERINARIAN) {
                model.addAttribute("upcomingAppointments", 
                    appointmentService.findByVeterinarianId(user.getId()));
            } else if (user.getRole() == Role.OWNER) {
                model.addAttribute("pets", petService.findByOwner(user.getId()));
                model.addAttribute("upcomingAppointments", 
                    appointmentService.findByOwnerId(user.getId()));
            }
            
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard data: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/dashboard/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            // Get the User object from CustomUserDetails or SecurityContext
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            
            model.addAttribute("user", user);
            model.addAttribute("totalPets", petService.getTotalPets());
            model.addAttribute("totalOwners", userService.getTotalUsersByRole(Role.OWNER));
            model.addAttribute("totalVets", userService.getTotalUsersByRole(Role.VETERINARIAN));
            model.addAttribute("totalAppointments", appointmentService.findAll().size());
            model.addAttribute("totalPrescriptions", prescriptionService.getTotalPrescriptions());
            model.addAttribute("latestMessages", messageService.getLatestMessages(5));
            return "dashboard/admin";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading admin dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/dashboard/vet")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public String vetDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user = userDetails.getUser();
            model.addAttribute("user", user);
            model.addAttribute("username", user.getFullName());
            com.vet.dto.ProfileDTO profileDTO = userService.getProfileDTO(user);
            // Debug log for profile photo
            Logger logger = LoggerFactory.getLogger(DashboardController.class);
            logger.info("[Vet Dashboard] profile.profilePhoto for user {}: {}", user.getUsername(), profileDTO.getProfilePhoto());
            model.addAttribute("profile", profileDTO);
            
            java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            java.time.LocalDateTime endOfDay = startOfDay.plusDays(1);
            java.util.List<com.vet.model.Appointment> allAppointments = appointmentService.findByVeterinarianId(user.getId());
            
            // Today's appointments (scheduled for today)
            java.util.List<com.vet.model.Appointment> todayAppointments = allAppointments.stream()
                .filter(a -> a.getDateTime() != null && !a.getDateTime().isBefore(startOfDay) && a.getDateTime().isBefore(endOfDay)
                    && a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED"))
                .toList();
            
            // Today's completed appointments
            java.util.List<com.vet.model.Appointment> todayCompletedAppointments = allAppointments.stream()
                .filter(a -> a.getDateTime() != null && !a.getDateTime().isBefore(startOfDay) && a.getDateTime().isBefore(endOfDay)
                    && a.getStatus() != null && a.getStatus().equalsIgnoreCase("COMPLETED"))
                .toList();
            
            // All completed appointments
            java.util.List<com.vet.model.Appointment> completedAppointments = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("COMPLETED"))
                .toList();
            
            // Upcoming appointments (scheduled and in the future)
            java.util.List<com.vet.model.Appointment> upcomingAppointments = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED") && a.getDateTime() != null && a.getDateTime().isAfter(java.time.LocalDateTime.now()))
                .toList();
            
            // Follow-up appointments (appointments with "FOLLOW_UP" reason or status)
            java.util.List<com.vet.model.Appointment> followUpAppointments = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED") && 
                           (a.getReason() != null && a.getReason().toLowerCase().contains("follow") || 
                            a.getReason() != null && a.getReason().toLowerCase().contains("checkup")))
                .toList();
            
            // Emergency appointments
            java.util.List<com.vet.model.Appointment> emergencyAppointments = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED") && 
                           a.getReason() != null && a.getReason().toLowerCase().contains("emergency"))
                .toList();

            java.util.Map<Long, String> petNames = new java.util.HashMap<>();
            java.util.Map<Long, String> ownerNames = new java.util.HashMap<>();
            for (com.vet.model.Appointment appointment : allAppointments) {
                com.vet.model.Pet pet = petService.findById(appointment.getPetId());
                if (pet != null) petNames.put(pet.getId(), pet.getName());
                com.vet.model.Owner owner = ownerService.getOwnerById(appointment.getOwnerId());
                if (owner != null) ownerNames.put(owner.getId(), owner.getName());
                }
            model.addAttribute("todayAppointments", todayAppointments.size());
            model.addAttribute("completedAppointments", todayCompletedAppointments.size());
            model.addAttribute("upcomingAppointments", upcomingAppointments.size());
            model.addAttribute("petNames", petNames);
            model.addAttribute("ownerNames", ownerNames);
            
            // Add dynamic counts for the template
            model.addAttribute("pendingSurgeries", 0); // TODO: Implement surgery count
            model.addAttribute("followUps", followUpAppointments.size());
            model.addAttribute("emergencies", emergencyAppointments.size());
            // Calendar events: all upcoming appointments (SCHEDULED and in the future)
            java.util.List<java.util.Map<String, Object>> appointmentEvents = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED") && a.getDateTime() != null && a.getDateTime().isAfter(java.time.LocalDateTime.now()))
                .map(appointment -> {
                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    event.put("title", petNames.get(appointment.getPetId()) + " with " + ownerNames.get(appointment.getOwnerId()));
                    event.put("start", appointment.getDateTime().toString());
                    event.put("description", appointment.getReason());
                    event.put("status", appointment.getStatus());
                    event.put("allDay", false);
                    return event;
                })
                .toList();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String appointmentEventsJson = objectMapper.writeValueAsString(appointmentEvents);
                model.addAttribute("appointmentEventsJson", appointmentEventsJson);
            } catch (Exception e) {
                model.addAttribute("appointmentEventsJson", "[]");
            }
            return "dashboard/vet";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading vet dashboard: " + e.getMessage());
            return "error";
        }
    }
} 