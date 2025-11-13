package com.vet.repository;

import com.vet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwnerId(Long ownerId);
    List<Pet> findByOwnerEmail(String ownerEmail);
    
    @Query("SELECT p FROM Pet p WHERE p.owner.id = ?1 ORDER BY p.name")
    List<Pet> findAllByOwnerIdOrderByName(Long ownerId);

    @Query("SELECT p FROM Pet p JOIN FETCH p.owner")
    List<Pet> findAllWithOwner();
} 