package com.vet.service;

import com.vet.model.User;
import com.vet.model.Role;
import com.vet.dto.ProfileDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    User registerUser(User user);
    User findByUsername(String username);
    User updateProfile(Long userId, ProfileDTO profileDTO);
    ProfileDTO getProfileDTO(User user);
    User findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    List<User> findByRole(String role);
    long getTotalUsersByRole(Role role);
    User toggleUserBlock(Long userId);
    boolean isUserBlocked(Long userId);
    List<User> findAll();
    void deleteById(Long id);
    User updateVeterinarian(Long id, User vet);
    User findByEmail(String email);
    User save(User user);
    List<User> findAllByIds(List<Long> ids);
    long count();
} 