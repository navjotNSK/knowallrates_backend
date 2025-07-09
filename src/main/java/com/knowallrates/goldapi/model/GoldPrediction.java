package com.knowallrates.goldapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gold_predictions")
public class GoldPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prediction_date")
    private LocalDate predictionDate;

    @Column(name = "predicted_22k")
    private Double predicted22k;

    @Column(name = "predicted_24k")
    private Double predicted24k;

    @Column(name = "confidence")
    private Integer confidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "trend")
    private Trend trend;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum Trend {
        UP, DOWN, STABLE
    }

    // Constructors
    public GoldPrediction() {}

    public GoldPrediction(LocalDate predictionDate, Double predicted22k, Double predicted24k,
                          Integer confidence, Trend trend) {
        this.predictionDate = predictionDate;
        this.predicted22k = predicted22k;
        this.predicted24k = predicted24k;
        this.confidence = confidence;
        this.trend = trend;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDate predictionDate) { this.predictionDate = predictionDate; }

    public Double getPredicted22k() { return predicted22k; }
    public void setPredicted22k(Double predicted22k) { this.predicted22k = predicted22k; }

    public Double getPredicted24k() { return predicted24k; }
    public void setPredicted24k(Double predicted24k) { this.predicted24k = predicted24k; }

    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }

    public Trend getTrend() { return trend; }
    public void setTrend(Trend trend) { this.trend = trend; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}