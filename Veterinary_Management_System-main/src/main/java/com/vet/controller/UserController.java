package com.vet.controller;

import com.vet.model.User;
import com.vet.dto.ProfileDTO;
import com.vet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userService.findByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
        model.addAttribute("user", user);
        model.addAttribute("profile", userService.getProfileDTO(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profile") ProfileDTO profileDTO,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "profile";
        }
        User user = userService.findByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
        userService.updateProfile(user.getId(), profileDTO);
        return "redirect:/user/profile?updated";
    }
} 