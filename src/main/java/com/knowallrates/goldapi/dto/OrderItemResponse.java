package com.knowallrates.goldapi.dto;

import com.knowallrates.goldapi.model.OrderItem;

public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    // Constructors
    public OrderItemResponse() {}

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.productId = orderItem.getProduct().getId();
        this.productName = orderItem.getProduct().getName();
        this.productDescription = orderItem.getProduct().getDescription();
        this.productImageUrl = orderItem.getProduct().getImageUrl();
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
