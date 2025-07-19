package com.knowallrates.goldapi.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AllRatesResponse {
    private String date;
    private List<AssetRateInfo> rates;
    private String timestamp;

    public static class AssetRateInfo {
        private String assetName;
        private String displayName;
        private Double rate22k;
        private Double rate24k;
        private Double ratePerUnit;
        private String unit;
        private Double change;
        private Double changePercent;
        private String trend; // up, down, stable

        // Constructors
        public AssetRateInfo() {}

        public AssetRateInfo(String assetName, String displayName, Double rate22k, Double rate24k) {
            this.assetName = assetName;
            this.displayName = displayName;
            this.rate22k = rate22k;
            this.rate24k = rate24k;
        }

        public AssetRateInfo(String assetName, String displayName, Double ratePerUnit, String unit) {
            this.assetName = assetName;
            this.displayName = displayName;
            this.ratePerUnit = ratePerUnit;
            this.unit = unit;
        }

        // Getters and Setters
        public String getAssetName() { return assetName; }
        public void setAssetName(String assetName) { this.assetName = assetName; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public Double getRate22k() { return rate22k; }
        public void setRate22k(Double rate22k) { this.rate22k = rate22k; }

        public Double getRate24k() { return rate24k; }
        public void setRate24k(Double rate24k) { this.rate24k = rate24k; }

        public Double getRatePerUnit() { return ratePerUnit; }
        public void setRatePerUnit(Double ratePerUnit) { this.ratePerUnit = ratePerUnit; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public Double getChange() { return change; }
        public void setChange(Double change) { this.change = change; }

        public Double getChangePercent() { return changePercent; }
        public void setChangePercent(Double changePercent) { this.changePercent = changePercent; }

        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }

    // Constructors
    public AllRatesResponse() {}

    public AllRatesResponse(String date, List<AssetRateInfo> rates) {
        this.date = date;
        this.rates = rates;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<AssetRateInfo> getRates() { return rates; }
    public void setRates(List<AssetRateInfo> rates) { this.rates = rates; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
