package com.vet.controller;

import com.vet.model.User;
import com.vet.model.Owner;
import com.vet.model.Appointment;
import com.vet.service.PetService;
import com.vet.service.AppointmentService;
import com.vet.service.PrescriptionService;
import com.vet.service.OwnerService;
import com.vet.service.UserService;
import com.vet.service.AppointmentReminderService;
import com.vet.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequestMapping("/owner")
@PreAuthorize("hasRole('OWNER')")
public class OwnerDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerDashboardController.class);

    @Autowired
    private PetService petService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private OwnerService ownerService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AppointmentReminderService appointmentReminderService;

    @GetMapping("/pets")
    public String listPets(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            if (owner == null) {
                model.addAttribute("errorMessage", "Error loading pets: Owner not found.");
                return "error";
            }
            List<com.vet.model.Pet> ownerPets = petService.getPetsByOwner(owner.getId());
            model.addAttribute("pets", ownerPets);
            model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
            return "owner/pets";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading pets: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/appointments/{id}/edit")
    public String showEditAppointmentForm(@PathVariable Long id,
                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            Appointment appointment = appointmentService.findById(id);
            if (owner == null || appointment == null || !appointment.getOwnerId().equals(owner.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found or not accessible.");
                return "redirect:/owner/appointments";
            }
            if (!"SCHEDULED".equalsIgnoreCase(appointment.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only scheduled appointments can be edited.");
                return "redirect:/owner/appointments";
            }
            model.addAttribute("appointment", appointment);
            model.addAttribute("veterinarians", userService.findByRole("VETERINARIAN"));
            return "owner/reschedule-appointment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to load appointment: " + e.getMessage());
            return "redirect:/owner/appointments";
        }
    }

    @PostMapping("/appointments/{id}/reschedule")
    public String rescheduleAppointment(@PathVariable Long id,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam("newDateTime") String newDateTime,
                                        @RequestParam(value = "newVetId", required = false) Long newVetId,
                                        RedirectAttributes redirectAttributes) {
        try {
            Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            Appointment appointment = appointmentService.findById(id);
            if (owner == null || appointment == null || !appointment.getOwnerId().equals(owner.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found or not accessible.");
                return "redirect:/owner/appointments";
            }
            if (!"SCHEDULED".equalsIgnoreCase(appointment.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only scheduled appointments can be edited.");
                return "redirect:/owner/appointments";
            }

            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(newDateTime);
            Long targetVetId = (newVetId != null ? newVetId : appointment.getVeterinarianId());

            // Check conflicts around the new time for selected vet (exclude this appointment)
            java.time.LocalDateTime start = dt.minusMinutes(29);
            java.time.LocalDateTime end = dt.plusMinutes(29);
            java.util.List<Appointment> overlapping = appointmentService.findByVeterinarianIdAndDateTimeBetween(targetVetId, start, end);
            boolean hasConflict = overlapping.stream()
                    .anyMatch(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()) && !a.getId().equals(appointment.getId()));
            if (hasConflict) {
                redirectAttributes.addFlashAttribute("errorMessage", "This time slot is unavailable. Please choose a different time.");
                return "redirect:/owner/appointments";
            }

            appointment.setDateTime(dt);
            if (newVetId != null) {
                appointment.setVeterinarianId(newVetId);
            }
            appointmentService.save(appointment);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update appointment: " + e.getMessage());
        }
        return "redirect:/owner/appointments";
    }

    @GetMapping("/prescriptions")
    public String listPrescriptions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            if (owner == null) {
                model.addAttribute("errorMessage", "Error loading prescriptions: Owner not found.");
                return "error";
            }
            model.addAttribute("prescriptions", prescriptionService.getPrescriptionsByOwner(owner.getUserId()));
            model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
            return "owner/prescriptions";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading prescriptions: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            if (owner == null) {
                model.addAttribute("errorMessage", "Error loading dashboard: Owner not found.");
                return "error";
            }
            model.addAttribute("owner", owner);
            model.addAttribute("username", userDetails.getUser().getFullName());
            model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
            List<com.vet.model.Pet> ownerPets = petService.getPetsByOwner(owner.getId());
            model.addAttribute("pets", ownerPets);
            List<com.vet.model.Appointment> allAppointments = appointmentService.findByOwnerId(owner.getId());
            List<com.vet.model.Appointment> upcomingAppointments = allAppointments.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED")).toList();
            List<com.vet.model.Appointment> completedAppointments = allAppointments.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("COMPLETED")).toList();
            List<com.vet.model.Appointment> cancelledAppointments = allAppointments.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("CANCELLED")).toList();
            model.addAttribute("upcomingAppointments", upcomingAppointments);
            model.addAttribute("completedAppointments", completedAppointments);
            model.addAttribute("cancelledAppointments", cancelledAppointments);
            model.addAttribute("allAppointments", allAppointments);
            
            // Add missing attributes for the template
            model.addAttribute("petCount", ownerPets.size());
            model.addAttribute("activePrescriptions", prescriptionService.getPrescriptionsByOwner(owner.getUserId()).size());
            model.addAttribute("pendingReminders", 0); // TODO: Implement reminder count
            
            java.util.Map<Long, String> petNames = new java.util.HashMap<>();
            java.util.Map<Long, com.vet.model.User> vets = new java.util.HashMap<>();
            for (com.vet.model.Appointment appointment : allAppointments) {
                com.vet.model.Pet pet = petService.findById(appointment.getPetId());
                if (pet != null) {
                    petNames.put(pet.getId(), pet.getName());
                }
                com.vet.model.User vet = userService.findById(appointment.getVeterinarianId());
                if (vet != null) {
                    vets.put(vet.getId(), vet);
                }
            }
            model.addAttribute("petNames", petNames);
            model.addAttribute("vets", vets);
            
            // Add next appointment if available
            if (!upcomingAppointments.isEmpty()) {
                com.vet.model.Appointment nextAppointment = upcomingAppointments.get(0);
                // Create a map with enriched data for the next appointment
                java.util.Map<String, Object> nextAppointmentData = new java.util.HashMap<>();
                nextAppointmentData.put("id", nextAppointment.getId());
                nextAppointmentData.put("petId", nextAppointment.getPetId());
                nextAppointmentData.put("petName", petNames.get(nextAppointment.getPetId()));
                nextAppointmentData.put("reason", nextAppointment.getReason());
                nextAppointmentData.put("dateTime", nextAppointment.getDateTime());
                nextAppointmentData.put("status", nextAppointment.getStatus());
                model.addAttribute("nextAppointment", nextAppointmentData);
            }
            // Map only SCHEDULED appointments to FullCalendar event objects
            java.util.List<java.util.Map<String, Object>> appointmentEvents = new java.util.ArrayList<>();
            java.time.format.DateTimeFormatter isoFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            for (com.vet.model.Appointment appointment : allAppointments) {
                if (appointment.getStatus() != null && appointment.getStatus().equalsIgnoreCase("SCHEDULED")) {
                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    String petName = petNames.getOrDefault(appointment.getPetId(), "Pet");
                    String vetName = vets.containsKey(appointment.getVeterinarianId()) ? vets.get(appointment.getVeterinarianId()).getFullName() : "Veterinarian";
                    event.put("id", appointment.getId());
                    event.put("title", petName + " with Dr. " + vetName);
                    event.put("start", appointment.getDateTime() != null ? appointment.getDateTime().format(isoFormatter) : "");
                    event.put("description", appointment.getReason());
                    event.put("status", appointment.getStatus());
                    event.put("allDay", false);
                    appointmentEvents.add(event);
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String appointmentEventsJson = objectMapper.writeValueAsString(appointmentEvents);
            model.addAttribute("appointmentEventsJson", appointmentEventsJson);
            return "dashboard/owner";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/appointments")
    public String ownerAppointments(@AuthenticationPrincipal com.vet.security.CustomUserDetails userDetails, Model model) {
        try {
            com.vet.model.Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            if (owner == null) {
                model.addAttribute("errorMessage", "Error loading appointments: Owner not found.");
                return "error";
            }
            List<com.vet.model.Pet> ownerPets = petService.getPetsByOwner(owner.getId());
            model.addAttribute("pets", ownerPets);
            model.addAttribute("veterinarians", userService.findByRole("VETERINARIAN"));

            List<com.vet.model.Appointment> allAppointments = appointmentService.findByOwnerId(owner.getId());
            List<com.vet.model.Appointment> upcomingAppointments = allAppointments.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("SCHEDULED")).toList();
            List<com.vet.model.Appointment> completedAppointments = allAppointments.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("COMPLETED")).toList();
            List<com.vet.model.Appointment> cancelledAppointments = allAppointments.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("CANCELLED")).toList();

            model.addAttribute("upcomingAppointments", upcomingAppointments);
            model.addAttribute("completedAppointments", completedAppointments);
            model.addAttribute("cancelledAppointments", cancelledAppointments);
            model.addAttribute("allAppointments", allAppointments);
            model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
            return "owner/appointments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading appointments: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/appointments/book")
    @PreAuthorize("hasRole('OWNER')")
    public String bookAppointmentOwner(
            @AuthenticationPrincipal com.vet.security.CustomUserDetails userDetails,
            @RequestParam Long petId,
            @RequestParam Long vetId,
            @RequestParam String dateTime,
            @RequestParam String reason,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            com.vet.model.Owner owner = ownerService.findByEmail(userDetails.getUser().getEmail());
            if (owner == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Owner profile not found for this user.");
                return "redirect:/owner/appointments";
            }
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(dateTime);
            // Check for 30-minute slot conflict for the veterinarian
            java.time.LocalDateTime start = dt.minusMinutes(29); // allow 1 min overlap for edge
            java.time.LocalDateTime end = dt.plusMinutes(29);
            java.util.List<com.vet.model.Appointment> overlapping = appointmentService.findByVeterinarianIdAndDateTimeBetween(vetId, start, end);
            boolean hasConflict = overlapping.stream().anyMatch(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()));
            if (hasConflict) {
                redirectAttributes.addFlashAttribute("errorMessage", "This time slot is unavailable. Please choose a slot at least 30 minutes apart from existing appointments.");
                return "redirect:/owner/appointments";
            }
            com.vet.model.Appointment appointment = new com.vet.model.Appointment();
            appointment.setOwnerId(owner.getId());
            appointment.setPetId(petId);
            appointment.setVeterinarianId(vetId);
            appointment.setDateTime(dt);
            appointment.setReason(reason);
            appointment.setStatus("SCHEDULED");
            appointment = appointmentService.save(appointment);
            
            // Send immediate confirmation emails
            try {
                appointmentReminderService.sendImmediateConfirmation(appointment.getId());
                logger.info("Immediate confirmation emails sent for appointment ID: {}", appointment.getId());
            } catch (Exception e) {
                logger.error("Failed to send immediate confirmation emails for appointment ID: {}", appointment.getId(), e);
                // Don't fail the appointment booking if email sending fails
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully! Confirmation emails have been sent.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to book appointment: " + e.getMessage());
        }
        return "redirect:/owner/appointments";
    }

    @GetMapping("/prescriptions/{id}")
    public String viewPrescription(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        try {
            com.vet.model.Prescription prescription = prescriptionService.findById(id);
            if (prescription == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Prescription not found.");
                return "redirect:/owner/prescriptions";
            }
            // Check that the logged-in user is the owner
            if (!prescription.getOwner().getId().equals(userDetails.getUser().getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to view this prescription.");
                return "redirect:/owner/prescriptions";
            }
            model.addAttribute("prescription", prescription);
            return "owner/prescriptions/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading prescription: " + e.getMessage());
            return "redirect:/owner/prescriptions";
        }
    }

    @GetMapping("/doctors")
    public String listDoctors(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("doctors", userService.findByRole("VETERINARIAN"));
        model.addAttribute("profile", userService.getProfileDTO(userDetails.getUser()));
        return "owner/doctors";
    }
} 