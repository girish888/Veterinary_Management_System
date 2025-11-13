package com.vet.service;

import com.vet.model.Pet;
import java.util.List;

public interface PetService {
    Pet save(Pet pet);
    Pet findById(Long id);
    List<Pet> findAll();
    List<Pet> findByOwner(Long ownerId);
    long count();
    void deletePet(Long id);
    long getTotalPets();
    List<Pet> getPetsByOwner(Long ownerId);
    List<Pet> getAllPets();
    Pet savePet(Pet pet);
    Pet getPetById(Long id);
    List<Pet> findAllByIds(List<Long> ids);
} 