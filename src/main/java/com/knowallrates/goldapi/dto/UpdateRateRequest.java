package com.knowallrates.goldapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateRateRequest {
    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotNull(message = "22K rate is required")
    @Positive(message = "22K rate must be positive")
    private Double rate22k;

    @NotNull(message = "24K rate is required")
    @Positive(message = "24K rate must be positive")
    private Double rate24k;

    private String date; // Optional, defaults to today

    // Constructors
    public UpdateRateRequest() {}

    public UpdateRateRequest(String assetName, Double rate22k, Double rate24k) {
        this.assetName = assetName;
        this.rate22k = rate22k;
        this.rate24k = rate24k;
    }

    // Getters and Setters
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public Double getRate22k() { return rate22k; }
    public void setRate22k(Double rate22k) { this.rate22k = rate22k; }

    public Double getRate24k() { return rate24k; }
    public void setRate24k(Double rate24k) { this.rate24k = rate24k; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "UpdateRateRequest{" +
                "assetName='" + assetName + '\'' +
                ", rate22k=" + rate22k +
                ", rate24k=" + rate24k +
                ", date='" + date + '\'' +
                '}';
    }
}
