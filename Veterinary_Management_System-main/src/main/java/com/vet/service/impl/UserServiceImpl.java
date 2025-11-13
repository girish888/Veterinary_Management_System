package com.vet.service.impl;

import com.vet.model.User;
import com.vet.model.Role;
import com.vet.model.Owner;
import com.vet.dto.ProfileDTO;
import com.vet.repository.UserRepository;
import com.vet.service.UserService;
import com.vet.service.OwnerService;
import com.vet.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OwnerService ownerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        return new CustomUserDetails(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        // Only set role to OWNER if not already set (for public registration)
        if (user.getRole() == null) {
            user.setRole(Role.OWNER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("[REGISTER_USER] username={}, fullName={}, email={}, mobile={}, address={}, role={}", user.getUsername(), user.getFullName(), user.getEmail(), user.getMobile(), user.getAddress(), user.getRole());
        User savedUser = userRepository.save(user);
        // Create owner profile ONLY if user is registering as an owner
        if (user.getRole() == Role.OWNER) {
            // Check if owner already exists for this user
            Owner existingOwner = ownerService.findByUserId(savedUser.getId());
            if (existingOwner == null) {
                Owner owner = new Owner();
                owner.setName(user.getFullName());
                owner.setEmail(user.getEmail());
                owner.setPhone(user.getMobile());
                owner.setAddress(user.getAddress());
                owner.setUserId(savedUser.getId()); // Link owner to user
                logger.info("[REGISTER_OWNER] name={}, email={}, phone={}, address={}, userId={}", owner.getName(), owner.getEmail(), owner.getPhone(), owner.getAddress(), owner.getUserId());
                ownerService.saveOwner(owner);
            } else {
                logger.info("[REGISTER_OWNER] Owner already exists for userId={}", savedUser.getId());
            }
        } else {
            // Do NOT create Owner profile for veterinarians or admins
        }
        return savedUser;
    }

    @Override
    public User updateProfile(Long userId, ProfileDTO profileDTO) {
        User user = findById(userId);
        if (!user.getEmail().equals(profileDTO.getEmail()) && userRepository.existsByEmail(profileDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setFullName(profileDTO.getFullName());
        user.setEmail(profileDTO.getEmail());
        user.setMobile(profileDTO.getMobile());
        user.setProfilePhoto(profileDTO.getProfilePhoto());
        if (user.getRole() == Role.VETERINARIAN) {
            user.setSpecialization(profileDTO.getSpecialization());
            user.setWorkingHours(profileDTO.getWorkingHours());
        }
        if (user.getRole() == Role.OWNER) {
            user.setAddress(profileDTO.getAddress());
            Owner owner = ownerService.findByEmail(user.getEmail());
            if (owner != null) {
                owner.setName(profileDTO.getFullName());
                owner.setPhone(profileDTO.getMobile());
                owner.setAddress(profileDTO.getAddress());
                ownerService.saveOwner(owner);
            }
        }
        return userRepository.save(user);
    }

    @Override
    public ProfileDTO getProfileDTO(User user) {
        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setProfilePhoto(user.getProfilePhoto());
        if (user.getRole() == Role.VETERINARIAN) {
            dto.setSpecialization(user.getSpecialization());
            dto.setWorkingHours(user.getWorkingHours());
        }
        if (user.getRole() == Role.OWNER) {
            dto.setAddress(user.getAddress());
        }
        return dto;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    public List<User> findByRole(String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(roleEnum);
            logger.info("Found {} users with role {}", users.size(), role);
            return users;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role: {}", role);
            throw new RuntimeException("Invalid role: " + role);
        }
    }

    @Override
    public long getTotalUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    public User toggleUserBlock(Long userId) {
        User user = findById(userId);
        user.setBlocked(!user.isBlocked());
        logger.info("User {} has been {}", user.getUsername(), user.isBlocked() ? "blocked" : "unblocked");
        return userRepository.save(user);
    }

    @Override
    public boolean isUserBlocked(Long userId) {
        User user = findById(userId);
        return user.isBlocked();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logger.debug("Starting deletion of user with ID: {}", id);
        User user = findById(id);
        logger.debug("Found user to delete: {}", user.getUsername());
        
        if (user.getRole() == Role.OWNER) {
            logger.debug("User is an owner, deleting associated owner profile");
            // Delete associated owner profile
            Owner owner = ownerService.findByEmail(user.getEmail());
            if (owner != null) {
                logger.debug("Found associated owner profile, deleting it");
                ownerService.deleteOwner(owner.getId());
            }
        }
        
        logger.debug("Deleting user from database");
        userRepository.deleteById(id);
        logger.info("Successfully deleted user: {}", user.getUsername());
    }

    @Override
    public User updateVeterinarian(Long id, User vet) {
        User existingVet = findById(id);
        if (existingVet.getRole() != Role.VETERINARIAN) {
            throw new RuntimeException("User is not a veterinarian.");
        }
        if (!existingVet.getEmail().equals(vet.getEmail()) && userRepository.existsByEmail(vet.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        existingVet.setFullName(vet.getFullName());
        existingVet.setEmail(vet.getEmail());
        existingVet.setMobile(vet.getMobile());
        existingVet.setSpecialization(vet.getSpecialization());
        existingVet.setWorkingHours(vet.getWorkingHours());
        // Only update password if provided and not blank
        if (vet.getPassword() != null && !vet.getPassword().isBlank()) {
            existingVet.setPassword(passwordEncoder.encode(vet.getPassword()));
        }
        return userRepository.save(existingVet);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
} 