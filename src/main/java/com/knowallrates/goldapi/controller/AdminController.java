package com.knowallrates.goldapi.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;


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

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.createProduct(request);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(product);
        } catch (Exception e) {
            System.err.println("Error in createProduct: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<ProductResponse>> getAllProductsForAdmin() {
        try {
            List<Product> products = productService.getAllActiveProducts();

            List<ProductResponse> response = products.stream().map(product -> {
                ProductResponse dto = new ProductResponse();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setDescription(product.getDescription());
                dto.setAssetName(product.getAsset() != null ? product.getAsset().getDisplayName() : null);
                dto.setBasePrice(product.getBasePrice());
                dto.setDiscountPercentage(product.getDiscountPercentage());
                dto.setPrice(product.getFinalPrice());
                dto.setStockQuantity(product.getStockQuantity());
                dto.setWeight(product.getWeightInGrams());
                dto.setPurity(product.getPurity());
                dto.setImageUrl(product.getImageUrl());
                dto.setAdditionalImages(product.getAdditionalImages());
                dto.setActive(product.getIsActive());
                dto.setCategory(product.getCategory());
                dto.setCreatedAt(product.getCreatedAt());
                dto.setUpdatedAt(product.getUpdatedAt());
                return dto;
            }).collect(Collectors.toList());

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


//    @GetMapping("/products")
//    @PreAuthorize("hasRole('ADMIN')")
//    @CrossOrigin(origins = "*")
//    public ResponseEntity<List<ProductResponse>> getAllProductsForAdmin() {
//        try {
//            List<Product> products = productService.getAllActiveProducts();
//
//            List<ProductResponse> response = new ArrayList<>();
//            for(Product p: products){
//            response.add(ProductResponse.builder()
//                    .id(p.getId())
//                    .name(p.getName())
//                    .description(p.getDescription())
//                    .assetName(p.getAsset().getDisplayName())
//                    .basePrice(p.getBasePrice())
//                    .discountPercentage(p.getDiscountPercentage())
//                    .finalPrice(p.getFinalPrice())
//                    .stockQuantity(p.getStockQuantity())
//                    .weightInGrams(p.getWeightInGrams())
//                    .purity(p.getPurity())
//                    .imageUrl(p.getImageUrl())
//                    .additionalImages(p.getAdditionalImages())
//                    .isActive(p.getIsActive())
//                    .isFeatured(p.getIsFeatured())
//                    .category(p.getCategory())
//                    .createdAt(p.getCreatedAt())
//                    .updatedAt(p.getUpdatedAt())
//                    .build());
//            }
////
////
////                    products.stream()
////                    .map(product -> ProductResponse.builder()
////                            .id(product.getId())
////                            .name(product.getName())
////                            .description(product.getDescription())
////                            .assetName(product.getAsset().getDisplayName())
////                            .basePrice(product.getBasePrice())
////                            .discountPercentage(product.getDiscountPercentage())
////                            .finalPrice(product.getFinalPrice())
////                            .stockQuantity(product.getStockQuantity())
////                            .weightInGrams(product.getWeightInGrams())
////                            .purity(product.getPurity())
////                            .imageUrl(product.getImageUrl())
////                            .additionalImages(product.getAdditionalImages())
////                            .isActive(product.getIsActive())
////                            .isFeatured(product.getIsFeatured())
////                            .category(product.getCategory())
////                            .createdAt(product.getCreatedAt())
////                            .updatedAt(product.getUpdatedAt())
////                            .build())
////                    .toList(); // ✅ Use toList() instead of collect(Collectors.toList())
//
//            return ResponseEntity.ok()
//                    .header("Access-Control-Allow-Origin", "*")
//                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
//                    .header("Access-Control-Allow-Headers", "*")
//                    .body(response);
//
//        } catch (Exception e) {
//            System.err.println("Error in getAllProductsForAdmin: " + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }


//
//    @GetMapping("/products")
//    @CrossOrigin(origins = "*")
//    public ResponseEntity<Page<Product>> getProducts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "12") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir,
//            @RequestParam(required = false) String asset,
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) String search) {
//        try {
//            Sort sort = sortDir.equalsIgnoreCase("desc") ?
//                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//            Pageable pageable = PageRequest.of(page, size, sort);
//
//            Page<Product> products;
//            if (search != null && !search.trim().isEmpty()) {
//                products = productService.searchProducts(search, pageable);
//            } else if (asset != null && !asset.trim().isEmpty()) {
//                products = productService.getProductsByAsset(asset, pageable);
//            } else if (category != null && !category.trim().isEmpty()) {
//                products = productService.getProductsByCategory(category, pageable);
//            } else {
//                products = productService.searchProducts("", pageable);
//            }
//
//            return ResponseEntity.ok()
//                    .header("Access-Control-Allow-Origin", "*")
//                    .body(products);
//        } catch (Exception e) {
//            System.err.println("Error in getProducts: " + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }

}
