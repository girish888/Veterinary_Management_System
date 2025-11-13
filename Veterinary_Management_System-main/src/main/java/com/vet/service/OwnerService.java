package com.vet.service;

import com.vet.model.Owner;
import java.util.List;

public interface OwnerService {
    List<Owner> getAllOwners();
    Owner getOwnerById(Long id);
    Owner saveOwner(Owner owner);
    void deleteOwner(Long id);
    boolean existsByEmail(String email);
    long count();
    Owner findByEmail(String email);
    /**
     * Finds the Owner entity by the associated User ID.
     * Returns null if not found.
     */
    Owner findByUserId(Long userId);
} 