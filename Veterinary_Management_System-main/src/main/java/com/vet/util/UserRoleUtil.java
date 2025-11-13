package com.vet.util;

import com.vet.model.Role;
import com.vet.model.User;
import com.vet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userRoleUtil")
public class UserRoleUtil {

    @Autowired
    private UserRepository userRepository;

    public String getRoleDisplay(String email) {
        if (email == null) return "";
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "";
        Role role = user.getRole();
        if (role == Role.OWNER) return "Owner";
        if (role == Role.VETERINARIAN) return "Veterinarian";
        if (role == Role.ADMIN) return "Admin";
        return role.name();
    }
} 