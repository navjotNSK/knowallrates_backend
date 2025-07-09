package com.knowallrates.goldapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gold_rates")
public class GoldRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", unique = true)
    private LocalDate date;

    @Column(name = "gold_22k")
    private Double gold22k;

    @Column(name = "gold_24k")
    private Double gold24k;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public GoldRate() {}

    public GoldRate(LocalDate date, Double gold22k, Double gold24k) {
        this.date = date;
        this.gold22k = gold22k;
        this.gold24k = gold24k;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getGold22k() { return gold22k; }
    public void setGold22k(Double gold22k) { this.gold22k = gold22k; }

    public Double getGold24k() { return gold24k; }
    public void setGold24k(Double gold24k) { this.gold24k = gold24k; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}