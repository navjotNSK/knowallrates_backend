package com.knowallrates.goldapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowallrates.goldapi.dto.ProductRequest;
import com.knowallrates.goldapi.dto.ProductResponse;
import com.knowallrates.goldapi.dto.UpdateRateRequest;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.GoldRate;
import com.knowallrates.goldapi.model.Product;
import com.knowallrates.goldapi.service.AdminService;
import com.knowallrates.goldapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // For deserializing JSON part of multipart request

    @GetMapping("/assets")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<Asset>> getAvailableAssets() {
        try {
            System.out.println("GET /api/admin/assets - Request received");
            List<Asset> assets = adminService.getAvailableAssets();
            System.out.println("GET /api/admin/assets - Found " + assets.size() + " assets");
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(assets);
        } catch (Exception e) {
            System.err.println("Error in getAvailableAssets: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/rates/update")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<GoldRate> updateTodayRate(@Valid @RequestBody UpdateRateRequest request) {
        try {
            System.out.println("POST /api/admin/rates/update - Request received: " + request);
            GoldRate updatedRate = adminService.updateTodayRate(request);
            System.out.println("POST /api/admin/rates/update - Rate updated: " + updatedRate);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(updatedRate);
        } catch (Exception e) {
            System.err.println("Error in updateTodayRate: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getAdminDashboard() {
        System.out.println("GET /api/admin/dashboard - Request received");
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body("Admin Dashboard Access Granted");
    }

    // Handle preflight requests
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            Product product = productService.createProduct(request, mainImage, additionalImages);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(convertToProductResponse(product));
        } catch (Exception e) {
            System.err.println("Error in createProduct: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            Product product = productService.updateProduct(id, request, mainImage, additionalImages);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(convertToProductResponse(product));
        } catch (Exception e) {
            System.err.println("Error in updateProduct: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
        } catch (Exception e) {
            System.err.println("Error in deleteProduct: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/products/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProductResponse> toggleProductStatus(@PathVariable Long id, @RequestBody Boolean isActive) {
        try {
            Product product = productService.toggleProductStatus(id, isActive);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(convertToProductResponse(product));
        } catch (Exception e) {
            System.err.println("Error in toggleProductStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<ProductResponse>> getAllProductsForAdmin() {
        try {
            List<Product> products = productService.getAllProducts(); // Changed to getAllProducts to include inactive ones for admin view

            List<ProductResponse> response = products.stream()
                    .map(this::convertToProductResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);

        } catch (Exception e) {
            System.err.println("Error in getAllProductsForAdmin: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setAssetName(product.getAsset() != null ? product.getAsset().getDisplayName() : null);
        dto.setBasePrice(product.getBasePrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setPrice(product.getFinalPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setWeight(product.getWeight());
        dto.setPurity(product.getPurity());
        dto.setImageUrl(product.getImageUrl());
        dto.setAdditionalImages(product.getAdditionalImages());
        dto.setActive(product.getIsActive());
        dto.setCategory(product.getCategory());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                return ResponseEntity.ok()
                        .header("Access-Control-Allow-Origin", "*")
                        .body(product.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error in getProduct: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.updateProduct(id, request, null, null);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(product);
        } catch (Exception e) {
            System.err.println("Error in updateProduct: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
