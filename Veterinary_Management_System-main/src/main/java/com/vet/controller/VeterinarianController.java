package com.vet.controller;

import com.vet.model.Veterinarian;
import com.vet.service.VeterinarianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.vet.model.Appointment;
import com.vet.model.Pet;
import com.vet.model.Owner;
import com.vet.model.User;
import com.vet.service.AppointmentService;
import com.vet.service.PetService;
import com.vet.service.OwnerService;
import com.vet.service.UserService;
import com.vet.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class VeterinarianController {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PetService petService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private UserService userService;

    @GetMapping("/veterinarians")
    public String listVeterinarians(Model model) {
        model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
        return "veterinarians/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("veterinarian", new Veterinarian());
        return "veterinarians/form";
    }

    @PostMapping
    public String addVeterinarian(@Valid @ModelAttribute Veterinarian veterinarian,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "veterinarians/form";
        }
        if (veterinarianService.existsByEmail(veterinarian.getEmail())) {
            result.rejectValue("email", "error.veterinarian", "Email already exists");
            return "veterinarians/form";
        }
        veterinarianService.saveVeterinarian(veterinarian);
        redirectAttributes.addFlashAttribute("successMessage", "Veterinarian added successfully!");
        return "redirect:/veterinarians";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("veterinarian", veterinarianService.getVeterinarianById(id));
        return "veterinarians/form";
    }

    @PostMapping("/{id}")
    public String updateVeterinarian(@PathVariable Long id,
                                   @Valid @ModelAttribute Veterinarian veterinarian,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "veterinarians/form";
        }
        veterinarian.setId(id);
        veterinarianService.saveVeterinarian(veterinarian);
        redirectAttributes.addFlashAttribute("successMessage", "Veterinarian updated successfully!");
        return "redirect:/veterinarians";
    }

    @PostMapping("/{id}/delete")
    public String deleteVeterinarian(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            veterinarianService.deleteVeterinarian(id);
            redirectAttributes.addFlashAttribute("successMessage", "Veterinarian deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting veterinarian: " + e.getMessage());
        }
        return "redirect:/veterinarians";
    }

    @GetMapping("/vet/appointments")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public String vetAppointments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User vet = userDetails.getUser();
            List<Appointment> allAppointments = appointmentService.findByVeterinarianId(vet.getId());
            // Map pet and owner names using only new Appointment fields
            Map<Long, String> petNames = new HashMap<>();
            Map<Long, String> ownerNames = new HashMap<>();
            for (Appointment appointment : allAppointments) {
                Pet pet = petService.findById(appointment.getPetId());
                if (pet != null) petNames.put(pet.getId(), pet.getName());
                Owner owner = ownerService.getOwnerById(appointment.getOwnerId());
                if (owner != null) ownerNames.put(owner.getId(), owner.getName());
            }
            // Filter completed and cancelled appointments
            List<Appointment> completedAppointments = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("COMPLETED")).toList();
            List<Appointment> cancelledAppointments = allAppointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("CANCELLED")).toList();
            model.addAttribute("allAppointments", allAppointments);
            model.addAttribute("completedAppointments", completedAppointments);
            model.addAttribute("cancelledAppointments", cancelledAppointments);
            model.addAttribute("petNames", petNames);
            model.addAttribute("ownerNames", ownerNames);
            model.addAttribute("profile", userService.getProfileDTO(vet));
            return "vet/appointments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading appointments: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/vet/appointments/{id}/complete")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public String completeAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.findById(id);
            if (appointment != null && "SCHEDULED".equalsIgnoreCase(appointment.getStatus())) {
                appointment.setStatus("COMPLETED");
                appointmentService.save(appointment);
                redirectAttributes.addFlashAttribute("successMessage", "Appointment marked as completed.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found or not in scheduled state.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error completing appointment: " + e.getMessage());
        }
        return "redirect:/vet/appointments";
    }

    @PostMapping("/vet/appointments/{id}/cancel")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling appointment: " + e.getMessage());
        }
        return "redirect:/vet/appointments";
    }
} 