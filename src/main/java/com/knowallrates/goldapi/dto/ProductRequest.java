package com.knowallrates.goldapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;


public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private Double price; // This is basePrice from frontend

    @PositiveOrZero(message = "Discount percentage must be non-negative")
    private Double discountPercentage = 0.0;

    @PositiveOrZero(message = "Stock quantity must be non-negative")
    private Integer stockQuantity = 0;

    // Changed to String to match frontend input, will be parsed in service
    private String weight;

    private String purity;

    private Boolean isActive = false;

    @NotBlank(message = "Category is required")
    private String category;

    // Existing image URLs to retain if no new files are uploaded
    private String imageUrl; // Path to existing main image
    private java.util.List<String> additionalImages; // Paths to existing additional images

    // Constructors
    public ProductRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getPurity() { return purity; }
    public void setPurity(String purity) { this.purity = purity; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public java.util.List<String> getAdditionalImages() { return additionalImages; }
    public void setAdditionalImages(java.util.List<String> additionalImages) { this.additionalImages = additionalImages; }
}
