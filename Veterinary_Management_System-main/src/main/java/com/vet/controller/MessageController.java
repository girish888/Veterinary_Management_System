package com.vet.controller;

import com.vet.model.Message;
import com.vet.model.User;
import com.vet.model.Role;
import com.vet.service.MessageService;
import com.vet.service.UserService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/messages/contact-admin")
    @PreAuthorize("hasAnyRole('OWNER', 'VETERINARIAN')")
    public String showContactForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            
            Message message = new Message();
            message.setSenderName(user.getFullName());
            message.setSenderEmail(user.getEmail());
            model.addAttribute("message", message);
            return "messages/contact-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading contact form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/messages/contact-admin")
    @PreAuthorize("hasAnyRole('OWNER', 'VETERINARIAN')")
    public String submitMessage(@Valid @ModelAttribute Message message,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "messages/contact-form";
        }
        
        messageService.saveMessage(message);
        redirectAttributes.addFlashAttribute("successMessage", 
            "Your message has been sent successfully!");
        return "redirect:/messages/contact-admin";
    }

    @GetMapping("/messages/admin/inbox")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminInbox(Model model) {
        model.addAttribute("messages", messageService.getAllMessages());
        return "messages/admin-inbox";
    }

    @GetMapping("/messages/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewMessage(@PathVariable Long id, Model model) {
        model.addAttribute("message", messageService.getMessageById(id));
        return "messages/view";
    }
    
    @GetMapping("/owner/messages")
    @PreAuthorize("hasRole('OWNER')")
    public String showOwnerMessages(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            List<User> veterinarians = userService.findByRole("VETERINARIAN");
            if (veterinarians.isEmpty()) {
                model.addAttribute("error", "No veterinarians available. Please contact the administrator.");
            }
            // Add vetMap for Thymeleaf lookup
            Map<Long, User> vetMap = veterinarians.stream().collect(Collectors.toMap(User::getId, java.util.function.Function.identity()));
            // Received messages: where owner is receiver
            List<Message> receivedMessages = messageService.getMessagesReceivedByUser(user.getId());
            // Sent messages: where owner is sender
            List<Message> sentMessages = messageService.getMessagesSentByUser(user.getId());
            model.addAttribute("receivedMessages", receivedMessages);
            model.addAttribute("sentMessages", sentMessages);
            model.addAttribute("veterinarians", veterinarians);
            model.addAttribute("vetMap", vetMap);
            model.addAttribute("profile", userService.getProfileDTO(user));
            return "owner/messages";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading messages: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/owner/messages/send")
    @PreAuthorize("hasRole('OWNER')")
    public String sendMessageToVet(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam Long recipientId,
                                 @RequestParam String subject,
                                 @RequestParam String content,
                                 RedirectAttributes redirectAttributes) {
        try {
            User sender;
            if (userDetails != null) {
                sender = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                sender = userService.findByUsername(username);
            }
            
            messageService.sendMessage(sender.getId(), recipientId, subject, content);
            redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send message: " + e.getMessage());
        }
        return "redirect:/owner/messages";
    }

    @GetMapping("/vet/messages")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public String showVetMessages(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            List<User> owners = userService.findByRole("OWNER");
            if (owners.isEmpty()) {
                model.addAttribute("error", "No pet owners available.");
            }
            // Received messages: where vet is receiver
            List<Message> receivedMessages = messageService.getMessagesReceivedByUser(user.getId());
            // Sent messages: where vet is sender
            List<Message> sentMessages = messageService.getMessagesSentByUser(user.getId());
            model.addAttribute("receivedMessages", receivedMessages);
            model.addAttribute("sentMessages", sentMessages);
            model.addAttribute("owners", owners);
            model.addAttribute("profile", userService.getProfileDTO(user));
            return "vet/messages";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading messages: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/vet/messages/send")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public String sendMessageToOwner(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestParam Long recipientId,
                                   @RequestParam String subject,
                                   @RequestParam String content,
                                   RedirectAttributes redirectAttributes) {
        try {
            User sender;
            if (userDetails != null) {
                sender = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                sender = userService.findByUsername(username);
            }
            
            messageService.sendMessage(sender.getId(), recipientId, subject, content);
            redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send message: " + e.getMessage());
        }
        return "redirect:/vet/messages";
    }
} 