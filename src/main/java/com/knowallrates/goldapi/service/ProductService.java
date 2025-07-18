package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.ProductRequest;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.Product;
import com.knowallrates.goldapi.repository.AssetRepository;
import com.knowallrates.goldapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private static final String PRODUCT_IMAGE_SUBDIRECTORY = "";

    @Transactional
    public Product createProduct(ProductRequest request, MultipartFile mainImage, List<MultipartFile> additionalImages) throws IOException {
        Asset asset = assetRepository.findByName(request.getAssetName())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getAssetName()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setAsset(asset);
        product.setBasePrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStockQuantity(request.getStockQuantity());
        product.setPurity(request.getPurity());
        product.setIsActive(request.getIsActive());
        product.setCategory(request.getCategory());

        // Parse weight string to Double
        if (request.getWeight() != null && !request.getWeight().isEmpty()) {
            try {
                // Assuming weight comes as "25g", extract number
                String weightStr = request.getWeight().replaceAll("[^\\d.]", "");
                product.setWeight(Double.parseDouble(weightStr));
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse weight string '" + request.getWeight() + "'. Setting to null. Error: " + e.getMessage());
                product.setWeight(null);
            }
        } else {
            product.setWeight(null);
        }

        // Handle main image upload
        if (mainImage != null && !mainImage.isEmpty()) {
            String mainImagePath = fileStorageService.storeFile(mainImage, PRODUCT_IMAGE_SUBDIRECTORY);
            product.setImageUrl(mainImagePath);
        } else {
            throw new IllegalArgumentException("Main image is required for new products.");
        }

        // Handle additional images upload
        List<String> uploadedAdditionalImagePaths = new ArrayList<>();
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (MultipartFile file : additionalImages) {
                if (!file.isEmpty()) {
                    String path = fileStorageService.storeFile(file, PRODUCT_IMAGE_SUBDIRECTORY);
                    uploadedAdditionalImagePaths.add(path);
                }
            }
        }
        product.setAdditionalImages(uploadedAdditionalImagePaths);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request, MultipartFile newMainImage, List<MultipartFile> newAdditionalImages) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        Asset asset = assetRepository.findByName(request.getAssetName())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getAssetName()));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setAsset(asset);
        existingProduct.setBasePrice(request.getPrice());
        existingProduct.setDiscountPercentage(request.getDiscountPercentage());
        existingProduct.setStockQuantity(request.getStockQuantity());
        existingProduct.setPurity(request.getPurity());
        existingProduct.setIsActive(request.getIsActive());
        existingProduct.setCategory(request.getCategory());

        // Parse weight string to Double
        if (request.getWeight() != null && !request.getWeight().isEmpty()) {
            try {
                String weightStr = request.getWeight().replaceAll("[^\\d.]", "");
                existingProduct.setWeight(Double.parseDouble(weightStr));
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse weight string '" + request.getWeight() + "'. Setting to null. Error: " + e.getMessage());
                existingProduct.setWeight(null);
            }
        } else {
            existingProduct.setWeight(null);
        }

        // Handle main image update
        if (newMainImage != null && !newMainImage.isEmpty()) {
            // Delete old main image if it exists
            if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
                fileStorageService.deleteFile(existingProduct.getImageUrl());
            }
            String mainImagePath = fileStorageService.storeFile(newMainImage, PRODUCT_IMAGE_SUBDIRECTORY);
            existingProduct.setImageUrl(mainImagePath);
        } else if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            // If no new main image, but an existing one is provided in request (meaning it was kept)
            existingProduct.setImageUrl(request.getImageUrl());
        } else {
            // If no new main image and no existing one is kept, delete old one and set to null
            if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
                fileStorageService.deleteFile(existingProduct.getImageUrl());
            }
            existingProduct.setImageUrl(null);
        }

        // Handle additional images update
        List<String> currentAdditionalImages = new ArrayList<>();
        // Add existing images that were not removed by the frontend
        if (request.getAdditionalImages() != null) {
            currentAdditionalImages.addAll(request.getAdditionalImages());
        }

        // Add newly uploaded additional images
        if (newAdditionalImages != null && !newAdditionalImages.isEmpty()) {
            for (MultipartFile file : newAdditionalImages) {
                if (!file.isEmpty()) {
                    String path = fileStorageService.storeFile(file, PRODUCT_IMAGE_SUBDIRECTORY);
                    currentAdditionalImages.add(path);
                }
            }
        }

        // Identify images to delete (those in existingProduct.getAdditionalImages() but not in currentAdditionalImages)
        List<String> imagesToDelete = existingProduct.getAdditionalImages().stream()
                .filter(existingPath -> !currentAdditionalImages.contains(existingPath))
                .collect(Collectors.toList());

        for (String path : imagesToDelete) {
            fileStorageService.deleteFile(path);
        }

        existingProduct.setAdditionalImages(currentAdditionalImages);

        return productRepository.save(existingProduct);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Delete main image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(product.getImageUrl());
        }

        // Delete additional images
        if (product.getAdditionalImages() != null) {
            for (String imagePath : product.getAdditionalImages()) {
                fileStorageService.deleteFile(imagePath);
            }
        }

        productRepository.delete(product);
    }

    @Transactional
    public Product toggleProductStatus(Long id, Boolean isActive) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setIsActive(isActive);
        return productRepository.save(product);
    }

    public void initializeSampleProducts() {
        if (productRepository.count() == 0) {
            List<Asset> assets = assetRepository.findByIsActive(true);

            for (Asset asset : assets) {
                switch (asset.getName().toLowerCase()) {
                    case "gold":
                        createSampleGoldProducts(asset);
                        break;
                    case "silver":
                        createSampleSilverProducts(asset);
                        break;
                    case "bitcoin":
                        createSampleBitcoinProducts(asset);
                        break;
                }
            }
            System.out.println("Sample products initialized");
        }
    }

    private void createSampleGoldProducts(Asset asset) {
        productRepository.save(new Product("22K Gold Ring", "Beautiful 22K gold ring with intricate design", asset, 25000.0, 5.0, "22K", "Ring"));
        productRepository.save(new Product("24K Gold Necklace", "Elegant 24K gold necklace", asset, 85000.0, 15.0, "24K", "Necklace"));
        productRepository.save(new Product("Gold Earrings", "Traditional gold earrings", asset, 15000.0, 3.0, "22K", "Earrings"));
    }

    private void createSampleSilverProducts(Asset asset) {
        productRepository.save(new Product("Silver Bracelet", "Stylish silver bracelet", asset, 3000.0, 25.0, "925", "Bracelet"));
        productRepository.save(new Product("Silver Coin", "Pure silver coin", asset, 5000.0, 50.0, "999", "Coin"));
    }

    private void createSampleBitcoinProducts(Asset asset) {
        productRepository.save(new Product("Bitcoin Commemorative Coin", "Physical Bitcoin commemorative coin", asset, 2500.0, 31.1, "Gold Plated", "Coin"));
    }
}

