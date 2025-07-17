package com.knowallrates.goldapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_rates", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"asset_id", "date"})
})
public class AssetRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "rate_22k")
    private Double rate22k;

    @Column(name = "rate_24k")
    private Double rate24k;

    @Column(name = "rate_per_unit") // For Bitcoin, Silver etc.
    private Double ratePerUnit;

    @Column(name = "unit") // gram, ounce, coin etc.
    private String unit = "gram";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public AssetRate() {}

    public AssetRate(Asset asset, LocalDate date, Double rate22k, Double rate24k) {
        this.asset = asset;
        this.date = date;
        this.rate22k = rate22k;
        this.rate24k = rate24k;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public AssetRate(Asset asset, LocalDate date, Double ratePerUnit, String unit) {
        this.asset = asset;
        this.date = date;
        this.ratePerUnit = ratePerUnit;
        this.unit = unit;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getRate22k() { return rate22k; }
    public void setRate22k(Double rate22k) { this.rate22k = rate22k; }

    public Double getRate24k() { return rate24k; }
    public void setRate24k(Double rate24k) { this.rate24k = rate24k; }

    public Double getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(Double ratePerUnit) { this.ratePerUnit = ratePerUnit; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
