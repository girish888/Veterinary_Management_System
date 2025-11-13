package com.vet.controller;

import com.vet.model.Message;
import com.vet.model.User;
import com.vet.service.MessageService;
import com.vet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        return "contact";
    }

    @PostMapping("/contact/send")
    public String sendContactMessage(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {
        try {
            // Find admin user
            User admin = userService.findByRole("ADMIN").stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No admin user found"));

            // Create and save message
            Message contactMessage = new Message();
            contactMessage.setSenderName(name);
            contactMessage.setSenderEmail(email);
            contactMessage.setSubject(subject);
            contactMessage.setContent(message);
            contactMessage.setReceiverId(admin.getId());
            contactMessage.setReceiverName(admin.getFullName());

            messageService.saveMessage(contactMessage);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Your message has been sent successfully! We'll get back to you soon.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to send message: " + e.getMessage());
        }
        return "redirect:/contact";
    }
} 