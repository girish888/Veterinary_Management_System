package com.vet.service.impl;

import com.vet.model.Pet;
import com.vet.repository.PetRepository;
import com.vet.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PetServiceImpl implements PetService {

    @Autowired
    private PetRepository petRepository;

    @Override
    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    @Override
    public List<Pet> findAll() {
        return petRepository.findAllWithOwner();
    }

    @Override
    public List<Pet> findByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    @Override
    public long count() {
        return petRepository.count();
    }

    @Override
    public void deletePet(Long id) {
        // If you want to prevent deleting pets with appointments, move this logic to a higher layer (controller/service)
        petRepository.deleteById(id);
    }

    @Override
    public long getTotalPets() {
        return petRepository.count();
    }

    @Override
    public List<Pet> getPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    @Override
    public List<Pet> findAllByIds(List<Long> ids) {
        return petRepository.findAllById(ids);
    }
} 