package com.vet.controller;

import com.vet.model.Owner;
import com.vet.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public String listOwners(Model model) {
        model.addAttribute("owners", ownerService.getAllOwners());
        return "owners/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("owner", new Owner());
        return "owners/form";
    }

    @PostMapping
    public String addOwner(@Valid @ModelAttribute Owner owner, 
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "owners/form";
        }
        if (ownerService.existsByEmail(owner.getEmail())) {
            result.rejectValue("email", "error.owner", "Email already exists");
            return "owners/form";
        }
        ownerService.saveOwner(owner);
        redirectAttributes.addFlashAttribute("successMessage", "Owner added successfully!");
        return "redirect:/owners";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("owner", ownerService.getOwnerById(id));
        return "owners/form";
    }

    @PostMapping("/{id}")
    public String updateOwner(@PathVariable Long id,
                            @Valid @ModelAttribute Owner owner,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "owners/form";
        }
        owner.setId(id);
        ownerService.saveOwner(owner);
        redirectAttributes.addFlashAttribute("successMessage", "Owner updated successfully!");
        return "redirect:/owners";
    }

    @PostMapping("/{id}/delete")
    public String deleteOwner(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        ownerService.deleteOwner(id);
        redirectAttributes.addFlashAttribute("successMessage", "Owner deleted successfully!");
        return "redirect:/owners";
    }
} 