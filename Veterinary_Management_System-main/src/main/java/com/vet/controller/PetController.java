package com.vet.controller;

import com.vet.model.Pet;
import com.vet.model.Owner;
import com.vet.service.PetService;
import com.vet.service.OwnerService;
import com.vet.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    public String listPets(Model model) {
        model.addAttribute("pets", petService.getAllPets());
        return "pets/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('OWNER')")
    public String showAddForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            Pet pet = new Pet();
            Owner owner = ownerService.findByUserId(userDetails.getUser().getId());
            if (owner == null) {
                model.addAttribute("error", "Owner profile not found. Please complete your profile first.");
                return "error";
            }
            pet.setOwner(owner);
            model.addAttribute("pet", pet);
            return "pets/form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading pet form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public String addPet(@Valid @ModelAttribute Pet pet,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "pets/form";
        }
        try {
            Owner owner = ownerService.findByUserId(userDetails.getUser().getId());
            if (owner == null) {
                result.reject("error", "Owner profile not found. Please complete your profile first.");
                return "pets/form";
            }
            pet.setOwner(owner);
            petService.savePet(pet);
            redirectAttributes.addFlashAttribute("successMessage", "Pet added successfully!");
            return "redirect:/owner/pets";
        } catch (Exception e) {
            result.reject("error", "Error saving pet: " + e.getMessage());
            return "pets/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('OWNER')")
    public String showEditForm(@PathVariable Long id, 
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        try {
            Pet pet = petService.getPetById(id);
            Owner owner = ownerService.findByUserId(userDetails.getUser().getId());
            if (owner == null || !pet.getOwner().getId().equals(owner.getId())) {
                return "redirect:/error?message=Unauthorized access";
            }
            model.addAttribute("pet", pet);
            return "pets/form";
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public String updatePet(@PathVariable Long id,
                          @Valid @ModelAttribute Pet pet,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "pets/form";
        }
        try {
            Pet existingPet = petService.getPetById(id);
            Owner owner = ownerService.findByUserId(userDetails.getUser().getId());
            if (owner == null || !existingPet.getOwner().getId().equals(owner.getId())) {
                return "redirect:/error?message=Unauthorized access";
            }
            pet.setId(id);
            pet.setOwner(owner);
            petService.savePet(pet);
            redirectAttributes.addFlashAttribute("successMessage", "Pet updated successfully!");
            return "redirect:/owner/pets";
        } catch (Exception e) {
            result.reject("error", "Error updating pet: " + e.getMessage());
            return "pets/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('OWNER')")
    public String deletePet(@PathVariable Long id,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        try {
            Pet pet = petService.getPetById(id);
            Owner owner = ownerService.findByUserId(userDetails.getUser().getId());
            if (owner == null || !pet.getOwner().getId().equals(owner.getId())) {
                return "redirect:/error?message=Unauthorized access";
            }
            petService.deletePet(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pet deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting pet: " + e.getMessage());
        }
        return "redirect:/owner/pets";
    }
} 