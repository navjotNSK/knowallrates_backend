package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.ProductRequest;
import com.knowallrates.goldapi.dto.ProductResponse;
import com.knowallrates.goldapi.model.Product;
import com.knowallrates.goldapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shop")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
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
                dto.setWeight(product.getWeight());
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
//
//    @GetMapping
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
//                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
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
//
//    @GetMapping("/featured")
//    @CrossOrigin(origins = "*")
//    public ResponseEntity<List<Product>> getFeaturedProducts() {
//        try {
//            List<Product> products = productService.getFeaturedProducts();
//            return ResponseEntity.ok()
//                    .header("Access-Control-Allow-Origin", "*")
//                    .body(products);
//        } catch (Exception e) {
//            System.err.println("Error in getFeaturedProducts: " + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }

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
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @CrossOrigin(origins = "*")
//    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
//        try {
//            Product product = productService.createProduct(request);
//            return ResponseEntity.ok()
//                    .header("Access-Control-Allow-Origin", "*")
//                    .body(product);
//        } catch (Exception e) {
//            System.err.println("Error in createProduct: " + e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.updateProduct(id, request,null,null);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(product);
        } catch (Exception e) {
            System.err.println("Error in updateProduct: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            System.err.println("Error in deleteProduct: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
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
}
