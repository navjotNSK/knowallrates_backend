package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.model.Image;
import com.knowallrates.goldapi.repository.ImageRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }
    
    public Image store(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (!isValidImageType(contentType)) {
                throw new RuntimeException("Invalid file type. Only images are allowed.");
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path destinationFile = Paths.get(uploadDir).resolve(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            
            // Save to database
            Image image = new Image(
                filename,
                originalFilename,
                contentType,
                file.getSize(),
                destinationFile.toString()
            );
            
            return imageRepository.save(image);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
    
    public Resource loadAsResource(String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
    
    public List<Image> getAllImages() {
        return imageRepository.findByOrderByUploadTimeDesc();
    }
    
    public Optional<Image> getImageByFilename(String filename) {
        return imageRepository.findByFilename(filename);
    }
    
    public void deleteImage(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isPresent()) {
            try {
                Files.deleteIfExists(Paths.get(image.get().getFilePath()));
                imageRepository.deleteById(id);
            } catch (IOException e) {
                throw new RuntimeException("Could not delete file", e);
            }
        }
    }
    
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif") ||
            contentType.equals("image/webp")
        );
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}