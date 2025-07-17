package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.ProductRequest;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.Product;
import com.knowallrates.goldapi.repository.AssetRepository;
import com.knowallrates.goldapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AssetRepository assetRepository;

    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByIsActiveTrueAndIsFeaturedTrue();
    }

    public Page<Product> getProductsByAsset(String assetName, Pageable pageable) {
        Optional<Asset> assetOpt = assetRepository.findByName(assetName);
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found: " + assetName);
        }
        return productRepository.findByAsset(assetOpt.get(), pageable);
    }

    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.findBySearchTerm(searchTerm, pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductRequest request) {
        Optional<Asset> assetOpt = assetRepository.findByName(request.getAssetName());
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found: " + request.getAssetName());
        }

        Product product = new Product(
            request.getName(),
            request.getDescription(),
            assetOpt.get(),
            request.getPrice(),
            request.getWeight(),
            request.getPurity(),
            request.getCategory()
        );

        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setAdditionalImages(request.getAdditionalImages());
        product.setIsFeatured(request.getIsActive());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        
        Optional<Asset> assetOpt = assetRepository.findByName(request.getAssetName());
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found: " + request.getAssetName());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setAsset(assetOpt.get());
        product.setBasePrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStockQuantity(request.getStockQuantity());
        product.setWeightInGrams(request.getWeight());
        product.setPurity(request.getPurity());
        product.setImageUrl(request.getImageUrl());
        product.setAdditionalImages(request.getAdditionalImages());
        product.setIsFeatured(request.getIsActive());
        product.setCategory(request.getCategory());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        product.setIsActive(false);
        productRepository.save(product);
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
