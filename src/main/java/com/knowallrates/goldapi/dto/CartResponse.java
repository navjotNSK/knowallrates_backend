package com.knowallrates.goldapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.knowallrates.goldapi.model.Cart;
import com.knowallrates.goldapi.model.CartItem;

import java.util.List;
import java.util.stream.Collectors;

public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private Double totalAmount;
    private Integer totalItems;

    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String name;

        @JsonProperty("imageUrl")
        private String productImage;
        private String category;
        private String purity;

        @JsonProperty("weight")
        private Double weightInGrams;
        private Integer quantity;

        @JsonProperty("discountPrice")
        private Double unitPrice;
        private Double totalPrice;

        // Constructors
        public CartItemResponse() {}

        public CartItemResponse(CartItem item) {
            this.id = item.getId();
            this.productId = item.getProduct().getId();
            this.name = item.getProduct().getName();
            this.productImage = item.getProduct().getImageUrl();
            this.category = item.getProduct().getCategory();
            this.purity = item.getProduct().getPurity();
            this.weightInGrams = item.getProduct().getWeightInGrams();
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
            this.totalPrice = item.getTotalPrice();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getPurity() { return purity; }
        public void setPurity(String purity) { this.purity = purity; }

        public Double getWeightInGrams() { return weightInGrams; }
        public void setWeightInGrams(Double weightInGrams) { this.weightInGrams = weightInGrams; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }

    // Constructors
    public CartResponse() {}

    public CartResponse(Cart cart) {
        this.id = cart.getId();
        this.items = cart.getItems().stream()
                .map(CartItemResponse::new)
                .toList();
        this.totalAmount = cart.getTotalAmount();
        this.totalItems = cart.getItems().size();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}
