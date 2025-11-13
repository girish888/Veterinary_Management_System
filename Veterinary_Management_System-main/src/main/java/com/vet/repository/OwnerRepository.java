package com.vet.repository;

import com.vet.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByEmail(String email);
    Optional<Owner> findByEmail(String email);
    Optional<Owner> findByUserId(Long userId);
} 