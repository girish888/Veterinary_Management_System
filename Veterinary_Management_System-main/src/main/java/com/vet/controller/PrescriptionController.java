package com.vet.controller;

import com.vet.model.Prescription;
import com.vet.model.User;
import com.vet.model.Role;
import com.vet.model.Pet;
import com.vet.model.Appointment;
import com.vet.model.Owner;
import com.vet.service.PrescriptionService;
import com.vet.service.AppointmentService;
import com.vet.service.UserService;
import com.vet.service.PetService;
import com.vet.repository.UserRepository;
import com.vet.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/vet/prescriptions")
@PreAuthorize("hasRole('VETERINARIAN')")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listPrescriptions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByVet(userDetails.getUser().getId());
        
        // Log for debugging
        System.out.println("Found " + prescriptions.size() + " prescriptions for vet ID: " + userDetails.getUser().getId());
        prescriptions.forEach(p -> {
            System.out.println("Prescription ID: " + p.getId() + ", Pet: " + 
                (p.getPet() != null ? p.getPet().getName() : "null") + 
                ", Owner: " + (p.getOwner() != null ? p.getOwner().getFullName() : "null"));
        });
        
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
        return "vet/prescriptions/list";
    }

    @GetMapping("/create")
    public String showCreateForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("prescription", new Prescription());
        model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
        return "vet/prescriptions/add";
    }

    @PostMapping("/create")
    @Transactional
    public String createPrescription(
            @RequestParam Long petId,
            @RequestParam String symptoms,
            @RequestParam String diagnosis,
            @RequestParam String medications,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User veterinarian = userService.findById(userDetails.getUser().getId());
            Pet pet = petService.findById(petId);
            if (pet == null) {
                redirectAttributes.addFlashAttribute("error", "Selected pet not found");
                return "redirect:/vet/prescriptions/create";
            }
            
            // Get the owner with proper session handling
            Owner owner = pet.getOwner();
            if (owner == null) {
                redirectAttributes.addFlashAttribute("error", "Pet owner not found");
                return "redirect:/vet/prescriptions/create";
            }
            
            User ownerUser = userService.findById(owner.getUserId());
            if (ownerUser == null) {
                redirectAttributes.addFlashAttribute("error", "Pet owner user not found");
                return "redirect:/vet/prescriptions/create";
            }
            
            Appointment appointment = new Appointment();
            appointment.setPetId(pet.getId());
            appointment.setOwnerId(owner.getId());
            appointment.setVeterinarianId(veterinarian.getId());
            appointment.setDateTime(LocalDateTime.now());
            appointment.setStatus("COMPLETED");
            appointment.setReason("Prescription created");
            appointment = appointmentService.save(appointment);
            
            Prescription prescription = new Prescription();
            prescription.setAppointment(appointment);
            prescription.setPet(pet);
            prescription.setVeterinarian(veterinarian);
            prescription.setOwner(ownerUser);
            prescription.setDate(LocalDateTime.now());
            prescription.setSymptoms(symptoms);
            prescription.setDiagnosis(diagnosis);
            prescription.setMedications(medications);
            prescription.setNotes(notes);
            prescriptionService.save(prescription);
            
            redirectAttributes.addFlashAttribute("success", "Prescription created successfully");
            return "redirect:/vet/prescriptions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create prescription: " + e.getMessage());
            return "redirect:/vet/prescriptions/create";
        }
    }

    @GetMapping("/{id}")
    public String viewPrescription(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Prescription prescription = prescriptionService.findById(id);
            if (prescription == null) {
                redirectAttributes.addFlashAttribute("error", "Prescription not found.");
                return "redirect:/vet/prescriptions";
            }
            
            // Check that the logged-in user is the veterinarian who created this prescription
            if (!prescription.getVeterinarian().getId().equals(userDetails.getUser().getId())) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to view this prescription.");
                return "redirect:/vet/prescriptions";
            }
            
            // Add prescription to model with all relationships loaded
            model.addAttribute("prescription", prescription);
            model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
            
            // Log for debugging
            System.out.println("Prescription loaded: " + prescription.getId());
            System.out.println("Pet: " + (prescription.getPet() != null ? prescription.getPet().getName() : "null"));
            System.out.println("Owner: " + (prescription.getOwner() != null ? prescription.getOwner().getFullName() : "null"));
            System.out.println("Vet: " + (prescription.getVeterinarian() != null ? prescription.getVeterinarian().getFullName() : "null"));
            
            return "vet/prescriptions/view";
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            redirectAttributes.addFlashAttribute("error", "Error loading prescription: " + e.getMessage());
            return "redirect:/vet/prescriptions";
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public String viewPrescriptionByAppointment(@PathVariable Long appointmentId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails,
                                              Model model) {
        try {
            User user = userDetails != null ? userDetails.getUser() : null;
            if (user == null) {
                return "redirect:/login";
            }

            Prescription prescription = prescriptionService.findByAppointmentId(appointmentId);
            if (prescription == null) {
                model.addAttribute("errorMessage", "No prescription found for this appointment.");
                return "redirect:/vet/appointments";
            }

            // Check if user has permission to view this prescription
            if (user.getRole() == Role.VETERINARIAN && 
                !prescription.getVeterinarian().getId().equals(user.getId())) {
                return "redirect:/dashboard";
            }
            if (user.getRole() == Role.OWNER && 
                !prescription.getOwner().getId().equals(user.getId())) {
                return "redirect:/dashboard";
            }

            model.addAttribute("prescription", prescription);
            return "vet/prescriptions/view";
        } catch (Exception e) {
            model.addAttribute("error", "Error viewing prescription: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        Prescription prescription = prescriptionService.findById(id);
        model.addAttribute("prescription", prescription);
        model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
        return "vet/prescriptions/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePrescription(@PathVariable Long id,
                                   @ModelAttribute Prescription prescription,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "vet/prescriptions/edit";
        }
        
        try {
            Prescription existingPrescription = prescriptionService.findById(id);
            existingPrescription.setSymptoms(prescription.getSymptoms());
            existingPrescription.setDiagnosis(prescription.getDiagnosis());
            existingPrescription.setMedications(prescription.getMedications());
            existingPrescription.setNotes(prescription.getNotes());
            
            prescriptionService.save(existingPrescription);
            redirectAttributes.addFlashAttribute("success", "Prescription updated successfully");
            return "redirect:/vet/prescriptions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating prescription: " + e.getMessage());
            return "redirect:/vet/prescriptions/" + id + "/edit";
        }
    }
} 