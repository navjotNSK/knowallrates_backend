package com.knowallrates.goldapi.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String couponCode;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String orderNotes;

    // Constructors
    public OrderRequest() {}

    // Getters and Setters
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
}
