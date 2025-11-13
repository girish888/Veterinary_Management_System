package com.vet.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, String directory);
    void deleteFile(String fileName, String directory);
    Path getFilePath(String fileName, String directory);
} 