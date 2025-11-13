package com.vet.service.impl;

import com.vet.model.Veterinarian;
import com.vet.repository.VeterinarianRepository;
import com.vet.service.VeterinarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VeterinarianServiceImpl implements VeterinarianService {

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Override
    public List<Veterinarian> getAllVeterinarians() {
        return veterinarianRepository.findAll();
    }

    @Override
    public Veterinarian getVeterinarianById(Long id) {
        return veterinarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinarian not found"));
    }

    @Override
    public Veterinarian saveVeterinarian(Veterinarian veterinarian) {
        return veterinarianRepository.save(veterinarian);
    }

    @Override
    public void deleteVeterinarian(Long id) {
        veterinarianRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return veterinarianRepository.existsByEmail(email);
    }

    @Override
    public long count() {
        return veterinarianRepository.count();
    }
} 