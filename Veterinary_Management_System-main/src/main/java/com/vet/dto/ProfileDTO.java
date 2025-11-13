package com.vet.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
public class ProfileDTO {
    private String fullName;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String mobile;
    
    private String specialization;
    private String workingHours;
    private String address;
    private String profilePhoto;
} 