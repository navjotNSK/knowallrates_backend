package com.knowallrates.goldapi.dto;

import java.util.List;

public class HistoryResponse {
    private List<HistoryRate> rates;

    public static class HistoryRate {
        private String date;
        private Double gold22k;
        private Double gold24k;

        // Constructors
        public HistoryRate() {}

        public HistoryRate(String date, Double gold22k, Double gold24k) {
            this.date = date;
            this.gold22k = gold22k;
            this.gold24k = gold24k;
        }

        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public Double getGold22k() { return gold22k; }
        public void setGold22k(Double gold22k) { this.gold22k = gold22k; }

        public Double getGold24k() { return gold24k; }
        public void setGold24k(Double gold24k) { this.gold24k = gold24k; }
    }

    // Constructors
    public HistoryResponse() {}

    public HistoryResponse(List<HistoryRate> rates) {
        this.rates = rates;
    }

    // Getters and Setters
    public List<HistoryRate> getRates() { return rates; }
    public void setRates(List<HistoryRate> rates) { this.rates = rates; }
}