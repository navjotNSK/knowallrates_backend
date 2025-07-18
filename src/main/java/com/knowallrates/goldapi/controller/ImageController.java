package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.model.Image;
import com.knowallrates.goldapi.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.core.io.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/uploads/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImageController {
    
    @Autowired
    private ImageService imageService;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Image image = imageService.store(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", image.getId());
            response.put("filename", image.getFilename());
            response.put("originalFilename", image.getOriginalFilename());
            response.put("size", image.getSize());
            response.put("uploadTime", image.getUploadTime());
            response.put("url", "/api/images/" + image.getFilename());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Resource resource = imageService.loadAsResource(filename);
            Optional<Image> image = imageService.getImageByFilename(filename);
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllImages() {
        List<Image> images = imageService.getAllImages();
        List<Map<String, Object>> response = images.stream()
            .map(image -> {
                Map<String, Object> imageData = new HashMap<>();
                imageData.put("id", image.getId());
                imageData.put("filename", image.getFilename());
                imageData.put("originalFilename", image.getOriginalFilename());
                imageData.put("size", image.getSize());
                imageData.put("uploadTime", image.getUploadTime());
                imageData.put("url", "/api/images/" + image.getFilename());
                return imageData;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}