package com.knowallrates.goldapi.dto;

public class PredictionResponse {
    private String date;
    private Double predicted22k;
    private Double predicted24k;
    private Integer confidence;
    private String trend;

    // Constructors
    public PredictionResponse() {}

    public PredictionResponse(String date, Double predicted22k, Double predicted24k,
                              Integer confidence, String trend) {
        this.date = date;
        this.predicted22k = predicted22k;
        this.predicted24k = predicted24k;
        this.confidence = confidence;
        this.trend = trend;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Double getPredicted22k() { return predicted22k; }
    public void setPredicted22k(Double predicted22k) { this.predicted22k = predicted22k; }

    public Double getPredicted24k() { return predicted24k; }
    public void setPredicted24k(Double predicted24k) { this.predicted24k = predicted24k; }

    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}