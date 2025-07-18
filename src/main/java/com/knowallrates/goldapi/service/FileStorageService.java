package com.knowallrates.goldapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String subdirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Normalize file name
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path uploadPath = Paths.get(uploadDir, subdirectory).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // Create directory if it doesn't exist

        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path that can be served by the web server
        return "/" + subdirectory + "/" + fileName;
    }

    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        // Remove leading slash to get relative path from uploadDir
        String relativePath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        Path fileToDelete = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
                System.out.println("Deleted file: " + fileToDelete);
            } else {
                System.out.println("File not found for deletion: " + fileToDelete);
            }
        } catch (IOException e) {
            System.err.println("Could not delete file: " + fileToDelete + ". " + e.getMessage());
        }
    }
}
