package com.vet.controller;

import com.vet.model.Prescription;
import com.vet.model.User;
import com.vet.service.PrescriptionService;
import com.vet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/admin/prescription-management")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listAllPrescriptions(
            Model model) {
        List<Prescription> prescriptions = prescriptionService.findAll();
        model.addAttribute("prescriptions", prescriptions);
        return "admin/prescriptions";
    }
} 