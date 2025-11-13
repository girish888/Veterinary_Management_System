package com.vet.repository;

import com.vet.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
    Optional<Veterinarian> findByEmail(String email);
    boolean existsByEmail(String email);
} 