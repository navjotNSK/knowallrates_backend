package com.knowallrates.goldapi.dto;


public class TodayRateResponse {
    private String date;
    private Double gold22k;
    private Double gold24k;
    private Double change22k;
    private Double change24k;
    private Double changePercent22k;
    private Double changePercent24k;
    private String timestamp;
    private YesterdayRate yesterday;

    public static class YesterdayRate {
        private String date;
        private Double gold22k;
        private Double gold24k;
        private String timestamp;

        // Constructors
        public YesterdayRate() {}

        public YesterdayRate(String date, Double gold22k, Double gold24k, String timestamp) {
            this.date = date;
            this.gold22k = gold22k;
            this.gold24k = gold24k;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public Double getGold22k() { return gold22k; }
        public void setGold22k(Double gold22k) { this.gold22k = gold22k; }

        public Double getGold24k() { return gold24k; }
        public void setGold24k(Double gold24k) { this.gold24k = gold24k; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    // Constructors
    public TodayRateResponse() {}

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Double getGold22k() { return gold22k; }
    public void setGold22k(Double gold22k) { this.gold22k = gold22k; }

    public Double getGold24k() { return gold24k; }
    public void setGold24k(Double gold24k) { this.gold24k = gold24k; }

    public Double getChange22k() { return change22k; }
    public void setChange22k(Double change22k) { this.change22k = change22k; }

    public Double getChange24k() { return change24k; }
    public void setChange24k(Double change24k) { this.change24k = change24k; }

    public Double getChangePercent22k() { return changePercent22k; }
    public void setChangePercent22k(Double changePercent22k) { this.changePercent22k = changePercent22k; }

    public Double getChangePercent24k() { return changePercent24k; }
    public void setChangePercent24k(Double changePercent24k) { this.changePercent24k = changePercent24k; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public YesterdayRate getYesterday() { return yesterday; }
    public void setYesterday(YesterdayRate yesterday) { this.yesterday = yesterday; }
}