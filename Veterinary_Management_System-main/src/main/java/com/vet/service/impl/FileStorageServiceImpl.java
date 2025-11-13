package com.vet.service.impl;

import com.vet.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file, String directory) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir + "/" + directory).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Debug logging
            logger.info("[File Upload] Directory: {}", uploadPath);
            logger.info("[File Upload] Original Filename: {}", originalFileName);
            logger.info("[File Upload] Generated Filename: {}", fileName);

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(fileName);
            logger.info("[File Upload] Target Location: {}", targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            logger.error("[File Upload] Could not store file: {}", ex.getMessage(), ex);
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public void deleteFile(String fileName, String directory) {
        try {
            Path filePath = getFilePath(fileName, directory);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file. Please try again!", ex);
        }
    }

    @Override
    public Path getFilePath(String fileName, String directory) {
        return Paths.get(uploadDir + "/" + directory).resolve(fileName).normalize();
    }
} 