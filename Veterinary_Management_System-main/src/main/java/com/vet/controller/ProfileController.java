package com.vet.controller;

import com.vet.dto.ProfileDTO;
import com.vet.model.User;
import com.vet.service.UserService;
import com.vet.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import com.vet.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            
            model.addAttribute("user", user);
            model.addAttribute("profile", userService.getProfileDTO(user));
            return "profile/view";
        } catch (Exception e) {
            logger.error("[Profile View] Error loading profile: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }
            
            model.addAttribute("user", user);
            model.addAttribute("profile", userService.getProfileDTO(user));
            return "profile/edit";
        } catch (Exception e) {
            logger.error("[Profile Edit] Error loading profile edit form: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @ModelAttribute("profile") ProfileDTO profileDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile) {
        if (result.hasErrors()) {
            logger.warn("[Profile Update] Validation errors: {}", result.getAllErrors());
            return "profile/edit";
        }
        
        User user;
        if (userDetails != null) {
            user = userDetails.getUser();
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            user = userService.findByUsername(username);
        }
        
        // Handle profile photo upload
        if (profilePhotoFile != null && !profilePhotoFile.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(profilePhotoFile, "profile-photos");
                logger.info("[Profile Update] Uploaded new profile photo: {} for user: {}", fileName, user.getUsername());
                profileDTO.setProfilePhoto(fileName);
            } catch (Exception e) {
                logger.error("[Profile Update] Error uploading profile photo: {}", e.getMessage(), e);
                result.rejectValue("profilePhoto", "error.profile", "Failed to upload profile photo: " + e.getMessage());
                return "profile/edit";
            }
        } else {
            // Keep existing photo if not uploading a new one
            logger.info("[Profile Update] No new photo uploaded, keeping existing: {} for user: {}", 
                       user.getProfilePhoto(), user.getUsername());
            profileDTO.setProfilePhoto(user.getProfilePhoto());
        }
        
        try {
            userService.updateProfile(user.getId(), profileDTO);
            logger.info("[Profile Update] Profile updated successfully for user: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile updated successfully!");
            
            // Redirect back to appropriate dashboard based on user role
            if (user.getRole().name().equals("OWNER")) {
                return "redirect:/owner/dashboard";
            } else if (user.getRole().name().equals("VETERINARIAN")) {
                return "redirect:/dashboard/vet";
            } else {
                return "redirect:/profile";
            }
        } catch (RuntimeException e) {
            logger.error("[Profile Update] Error updating profile for user {}: {}", user.getUsername(), e.getMessage(), e);
            result.rejectValue("email", "error.profile", e.getMessage());
            return "profile/edit";
        }
    }

    /**
     * Test endpoint for debugging photo upload
     */
    @GetMapping("/photo-test")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testPhotoUpload() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Photo upload endpoint is accessible");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * REST endpoint for updating profile photo only
     */
    @PostMapping("/photo")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfilePhoto(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("profilePhotoFile") MultipartFile profilePhotoFile) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }

            logger.info("[Profile Photo Update] Starting photo upload for user: {}", user.getUsername());

            // Validate file
            if (profilePhotoFile == null || profilePhotoFile.isEmpty()) {
                logger.warn("[Profile Photo Update] No file selected for user: {}", user.getUsername());
                response.put("success", false);
                response.put("message", "No file selected");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("[Profile Photo Update] File details - Name: {}, Size: {}, Content-Type: {}", 
                       profilePhotoFile.getOriginalFilename(), 
                       profilePhotoFile.getSize(), 
                       profilePhotoFile.getContentType());

            // Validate file size (5MB max)
            if (profilePhotoFile.getSize() > 5 * 1024 * 1024) {
                logger.warn("[Profile Photo Update] File too large for user: {} - Size: {}", 
                           user.getUsername(), profilePhotoFile.getSize());
                response.put("success", false);
                response.put("message", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type
            String contentType = profilePhotoFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("[Profile Photo Update] Invalid file type for user: {} - Content-Type: {}", 
                           user.getUsername(), contentType);
                response.put("success", false);
                response.put("message", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // Store file
            String fileName = fileStorageService.storeFile(profilePhotoFile, "profile-photos");
            logger.info("[Profile Photo Update] File stored successfully: {} for user: {}", fileName, user.getUsername());
            
            // Update user profile with new photo
            ProfileDTO profileDTO = userService.getProfileDTO(user);
            profileDTO.setProfilePhoto(fileName);
            userService.updateProfile(user.getId(), profileDTO);

            logger.info("[Profile Photo Update] Photo updated successfully for user: {} with file: {}", 
                       user.getUsername(), fileName);

            response.put("success", true);
            response.put("message", "Profile photo updated successfully");
            response.put("photoUrl", "/uploads/profile-photos/" + fileName);
            response.put("fileName", fileName);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("[Profile Photo Update] Error updating profile photo for user: {}", 
                        userDetails != null ? userDetails.getUsername() : "unknown", e);
            response.put("success", false);
            response.put("message", "Failed to update profile photo: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * REST endpoint for getting profile information
     */
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProfileInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user;
            if (userDetails != null) {
                user = userDetails.getUser();
            } else {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                user = userService.findByUsername(username);
            }

            ProfileDTO profile = userService.getProfileDTO(user);
            
            response.put("success", true);
            response.put("profile", profile);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("[Profile Info] Error getting profile info: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to get profile information: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 