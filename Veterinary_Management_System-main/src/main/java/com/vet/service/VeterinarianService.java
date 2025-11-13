package com.vet.service;

import com.vet.model.Veterinarian;
import java.util.List;

public interface VeterinarianService {
    List<Veterinarian> getAllVeterinarians();
    Veterinarian getVeterinarianById(Long id);
    Veterinarian saveVeterinarian(Veterinarian veterinarian);
    void deleteVeterinarian(Long id);
    boolean existsByEmail(String email);
    long count();
} 