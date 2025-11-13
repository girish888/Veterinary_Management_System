package com.vet.controller;

import com.vet.model.User;
import com.vet.model.Message;
import com.vet.service.UserService;
import com.vet.service.PetService;
import com.vet.service.AppointmentService;
import com.vet.service.PrescriptionService;
import com.vet.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

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

    @PostMapping("/users/{id}/toggle-block")
    public String toggleUserBlock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserBlock(id);
            String message = user.isBlocked() ? "User has been blocked" : "User has been unblocked";
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/pets")
    public String managePets(Model model) {
        model.addAttribute("pets", petService.findAll());
        return "admin/pets";
    }

    @GetMapping("/prescriptions")
    public String managePrescriptions(Model model) {
        model.addAttribute("prescriptions", prescriptionService.findAll());
        return "admin/prescriptions";
    }

    @GetMapping("/prescriptions/{id}/view")
    public String viewPrescription(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("prescription", prescriptionService.findById(id));
            return "admin/prescription-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Prescription not found: " + e.getMessage());
            return "redirect:/admin/prescriptions";
        }
    }

    @GetMapping("/messages")
    public String manageMessages(Model model) {
        model.addAttribute("messages", messageService.getAllMessages());
        return "admin/messages";
    }

    @GetMapping("/messages/{id}")
    public String viewMessage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Message message = messageService.getMessageById(id);
            if (!message.isRead()) {
                message.setRead(true);
                messageService.saveMessage(message);
            }
            model.addAttribute("message", message);
            return "admin/message-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Message not found: " + e.getMessage());
            return "redirect:/admin/messages";
        }
    }

    @PostMapping("/messages/reply")
    public String replyToMessage(@RequestParam Long messageId, 
                               @RequestParam String subject,
                               @RequestParam String content,
                               RedirectAttributes redirectAttributes) {
        try {
            Message originalMessage = messageService.getMessageById(messageId);
            Message reply = new Message();
            reply.setSubject(subject);
            reply.setContent(content);
            reply.setSenderName("Admin");
            reply.setSenderEmail("admin@vet.com");
            reply.setReceiverId(originalMessage.getReceiverId());
            reply.setReceiverName(originalMessage.getSenderName());
            reply.setSentAt(LocalDateTime.now());
            reply.setRead(false);
            
            messageService.saveMessage(reply);
            redirectAttributes.addFlashAttribute("success", "Reply sent successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send reply: " + e.getMessage());
        }
        return "redirect:/admin/messages";
    }

    @GetMapping("/veterinarians")
    public String manageVeterinarians(Model model) {
        model.addAttribute("veterinarians", userService.findByRole("VETERINARIAN"));
        return "admin/veterinarians";
    }

    @GetMapping("/veterinarians/add")
    public String showAddVeterinarianForm(Model model) {
        model.addAttribute("veterinarian", new com.vet.model.User());
        return "admin/veterinarian-form";
    }

    @PostMapping("/veterinarians/add")
    public String addVeterinarian(@ModelAttribute("veterinarian") com.vet.model.User vet, RedirectAttributes redirectAttributes, Model model) {
        try {
            vet.setRole(com.vet.model.Role.VETERINARIAN);
            userService.registerUser(vet);
            redirectAttributes.addFlashAttribute("message", "Veterinarian added successfully.");
            return "redirect:/admin/veterinarians";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("veterinarian", vet);
            return "admin/veterinarian-form";
        }
    }

    @GetMapping("/veterinarians/{id}/edit")
    public String showEditVeterinarianForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            com.vet.model.User vet = userService.findById(id);
            if (vet.getRole() != com.vet.model.Role.VETERINARIAN) {
                redirectAttributes.addFlashAttribute("error", "User is not a veterinarian.");
                return "redirect:/admin/veterinarians";
            }
            model.addAttribute("veterinarian", vet);
            return "admin/veterinarian-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Veterinarian not found: " + e.getMessage());
            return "redirect:/admin/veterinarians";
        }
    }

    @PostMapping("/veterinarians/{id}/edit")
    public String editVeterinarian(@PathVariable Long id, @ModelAttribute("veterinarian") com.vet.model.User vet, RedirectAttributes redirectAttributes, Model model) {
        try {
            vet.setRole(com.vet.model.Role.VETERINARIAN);
            userService.updateVeterinarian(id, vet);
            redirectAttributes.addFlashAttribute("message", "Veterinarian updated successfully.");
            return "redirect:/admin/veterinarians";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("veterinarian", vet);
            return "admin/veterinarian-form";
        }
    }

    @PostMapping("/veterinarians/{id}/delete")
    public String deleteVeterinarian(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            com.vet.model.User vet = userService.findById(id);
            if (vet.getRole() != com.vet.model.Role.VETERINARIAN) {
                redirectAttributes.addFlashAttribute("error", "User is not a veterinarian.");
                return "redirect:/admin/veterinarians";
            }
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Veterinarian deleted successfully.");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete veterinarian: Please remove or reassign all related appointments and prescriptions first.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete veterinarian: " + e.getMessage());
        }
        return "redirect:/admin/veterinarians";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.count());
        model.addAttribute("totalPets", petService.count());
        model.addAttribute("totalAppointments", appointmentService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    @PostMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/users";
            }
            
            // Preserve important fields
            existingUser.setUsername(user.getUsername());
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setMobile(user.getMobile());
            existingUser.setRole(user.getRole());
            
            userService.save(existingUser);
            redirectAttributes.addFlashAttribute("message", "User updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
} 